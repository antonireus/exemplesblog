package org.sample.fileServer;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Llista els fitxers dins el directori de treball, i permet pujar-hi fitxers amb un formulari que defineixi
 * un camp de tipus file i nom file.
 *
 * Location (8.1.5) pot ser un directori absolut, o un directori relatiu. Si és relatiu o serà respecte
 * el directori temporal proporcinat pel contenidor a cada mòdul web: javax.servlet.context.tempdir (4.8.1).
 * Els fitxers es guardaran temporalment a disc a partir de 16k (16384 bytes)
 */
@WebServlet(name = "fileServlet", urlPatterns = "/file", loadOnStartup = 1)
@MultipartConfig(location = FileServlet.UPLOAD_LOCATION, fileSizeThreshold = 16384)
public class FileServlet extends HttpServlet {

    private static final long serialVersionUID = 7613141480400005252L;

    private static final Logger log = Logger.getLogger(FileServlet.class.getName());

    public static final String UPLOAD_LOCATION = "fileServer";
    
    private File uploadDir;

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("init");
        File contextTempDir = (File) config.getServletContext().getAttribute("javax.servlet.context.tempdir");
        log.info("contexTempDir: " + contextTempDir.getAbsolutePath());

        uploadDir = new File(contextTempDir, FileServlet.UPLOAD_LOCATION);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new UnavailableException("No s'ha pogut crear el directori " + uploadDir.getAbsolutePath());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("doGet");
        request.setCharacterEncoding("UTF-8");
        
        String filename = request.getParameter("download");
        if (filename == null || filename.isEmpty()) {
            File[] fileArray = uploadDir.listFiles();
            List<File> files = fileArray != null ? Arrays.asList(fileArray) : new ArrayList<>();
            request.setAttribute("files", files);
            request.getRequestDispatcher("/files.jsp").forward(request, response);
        } else {
            log.info("Download: " + filename);
            File downloadFile = new File(uploadDir, filename);
            
            
            String safefilename = charsetSafe(filename, StandardCharsets.ISO_8859_1, "?");
            String utf8filename = rfc5987_encode(filename);
            
            /*
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            */
                      
            response.setHeader("Content-Disposition", "attachment"
                    + "; filename=\"" + safefilename + "\""
                    + "; filename*=UTF-8''" + utf8filename);
            
            response.setContentType("application/octet-stream");
            response.setContentLengthLong(downloadFile.length());
            Files.copy(downloadFile.toPath(), response.getOutputStream());
            response.getOutputStream().close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        log.info("doPost");
        request.setCharacterEncoding("UTF-8");

        Part part = request.getPart("file");

        log.info("SubmittedFileName: " + part.getSubmittedFileName());
        log.info("ContentType: " + part.getContentType());
        log.info("Size: " + part.getSize());

        if (part.getSize() > 0) {
            File savedFile = new File(uploadDir, part.getSubmittedFileName());
            try (InputStream is = part.getInputStream()) {
                Files.copy(is, savedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            part.delete();
        }

        response.sendRedirect(request.getContextPath() + "/file");
    }
    
    public static String rfc5987_encode(final String s) {
        final byte[] s_bytes = s.getBytes(StandardCharsets.UTF_8);
        final int len = s_bytes.length;
        final StringBuilder sb = new StringBuilder(len << 1);
        
        // digits hexadecimals
        final char[] digits = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        
        // caràcters permesos segons RFC5987: attr-char, ordenats per optimitzar la cerca
        final byte[] attr_char = {'!','#','$','&','+','-','.','0','1','2','3','4','5','6','7','8','9', 
                'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','^','_','`',
                'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','|', '~'};
        
        for (int i = 0; i < len; ++i) {
            byte b = s_bytes[i];
            if (Arrays.binarySearch(attr_char, b) >= 0) {
                // si el byte està dins els caràcters pemesos l'afegim
                sb.append((char) b);
            } else {
                // sinó el codificam amb "%", veure https://tools.ietf.org/html/rfc3986#section-2.1
                sb.append('%');
                sb.append(digits[0x0f & (b >>> 4)]); // els 4 bits de l'esquerra
                sb.append(digits[b & 0x0f]); // els 4 bits de la dreta
            }
        }

        return sb.toString();
    }
    
    public static String charsetSafe(String string, Charset charset, String replacement) {
        CharsetEncoder encoder = charset.newEncoder();
        if (encoder.canEncode(string)) {
            return string;
        }
        
        encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        encoder.replaceWith(replacement.getBytes(charset));
        try {
            ByteBuffer buffer = encoder.encode(CharBuffer.wrap(string));
            return new String(buffer.array(), charset);
        } catch (CharacterCodingException e) {
            // Realment no es pot produir mai perquè hem fixat CodingErrorAction.REPLACE
            throw new IllegalStateException("encoder.onUnmappableCharacter hauria de ser REPLACE!!", e);
        }
    }
}
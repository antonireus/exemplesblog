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
import java.io.InputStream;
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

            String safefilename = EncoderUtils.charsetSafeEncode(filename, StandardCharsets.ISO_8859_1, "_");
            String utf8filename = EncoderUtils.headerEncode(filename, StandardCharsets.UTF_8);

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
    
}
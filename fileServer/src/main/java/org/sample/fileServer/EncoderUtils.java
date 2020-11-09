package org.sample.fileServer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.BitSet;

public class EncoderUtils {

    private EncoderUtils() {}
    
    private static final BitSet RFC5987_ATTR_CHAR = new BitSet(256);

    static {
        /*
         attr-char     = ALPHA / DIGIT
                   / "!" / "#" / "$" / "&" / "+" / "-" / "."
                   / "^" / "_" / "`" / "|" / "~"
         */
        for (int i = 'a'; i <= 'z'; i++) {
            RFC5987_ATTR_CHAR.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            RFC5987_ATTR_CHAR.set(i);
        }
        for (int i = '0'; i <= '9'; i++) {
            RFC5987_ATTR_CHAR.set(i);
        }
        RFC5987_ATTR_CHAR.set('!');
        RFC5987_ATTR_CHAR.set('#');
        RFC5987_ATTR_CHAR.set('$');
        RFC5987_ATTR_CHAR.set('&');
        RFC5987_ATTR_CHAR.set('+');
        RFC5987_ATTR_CHAR.set('-');
        RFC5987_ATTR_CHAR.set('.');
        RFC5987_ATTR_CHAR.set('^');
        RFC5987_ATTR_CHAR.set('_');
        RFC5987_ATTR_CHAR.set('`');
        RFC5987_ATTR_CHAR.set('|');
        RFC5987_ATTR_CHAR.set('~');
    }
    
    public static String headerEncode(String value, Charset charset) {
        byte[] bytes = value.getBytes(charset);
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        
        for (byte b: bytes) {
            if (RFC5987_ATTR_CHAR.get(b & 0xFF)) {
                // si el byte està dins els caràcters pemesos l'afegim
                builder.append((char) b);
            } else {
                // sinó el codificam amb "%", veure https://tools.ietf.org/html/rfc3986#section-2.1
                builder.append('%');
                builder.append(Character.forDigit( (b >> 4) & 0x0F, 16)); // els 4 bits de l'esquerra
                builder.append(Character.forDigit( b & 0x0F, 16)); // els 4 bits de la dreta
            }
        }

        return builder.toString();
    }
        
    
    public static String charsetSafeEncode(String string, Charset charset, String replacement) {
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

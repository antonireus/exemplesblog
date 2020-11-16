package org.example.filehash;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int BUFFER_SIZE = 8192 * 8;

    public static void main(String... args) throws NoSuchAlgorithmException {

        File file = new File("ENORME.mp4");
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte[] buffer = new byte[BUFFER_SIZE];
        //ByteBuffer bBuffer = ByteBuffer.wrap(buffer);
        //ByteBuffer bBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        //ByteBuffer bBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        //System.out.println("Is direct: " + bBuffer.isDirect());

        long startTime = System.nanoTime();

/*
        try (FileChannel fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            while (fileChannel.read(bBuffer) != -1) {
                bBuffer.flip();
                messageDigest.update(bBuffer);
                bBuffer.clear();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
*/

        try (InputStream is = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE)) {
            int i;
            while ((i = is.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, i);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        long duration = System.nanoTime() - startTime;
        System.out.printf("Calculat en: %d ms%n", TimeUnit.NANOSECONDS.toMillis(duration));

        byte[] digest = messageDigest.digest();
        System.out.println(bytesToHex(digest));
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            builder.append(Character.forDigit((b >> 4) & 0x0F, 16));
            builder.append(Character.forDigit(b & 0x0F, 16));
        }
        return builder.toString();
    }
}

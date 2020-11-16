package org.sample.filehash;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class BufferedFileInputStreamHashProducerImpl extends FileHashProducer {

    private final byte[] buffer;

    public BufferedFileInputStreamHashProducerImpl(String algorithm, int bufferSize) {
        super(algorithm);
        this.buffer = new byte[bufferSize];
    }

    @Override
    protected void doCalculateHash(Path path) {
        try (InputStream is = new FileInputStream(path.toFile());
             BufferedInputStream bis = new BufferedInputStream(is, buffer.length)) {
            int r;
            while ( (r = bis.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, r);
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}

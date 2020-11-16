package org.sample.filehash;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class FileInputStreamHashProducerImpl extends FileHashProducer {

    private final byte[] buffer;

    public FileInputStreamHashProducerImpl(String algorithm, int bufferSize) {
        super(algorithm);
        buffer = new byte[bufferSize];
    }

    @Override
    protected void doCalculateHash(Path path) {
        try (InputStream is = new FileInputStream(path.toFile())) {
            int r;
            while ( (r = is.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, r);
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}

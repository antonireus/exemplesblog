package org.sample.filehash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileChannelHashProducerImpl extends FileHashProducer {

    private final ByteBuffer buffer;

    public FileChannelHashProducerImpl(String algorithm, int bufferSize) {
        this(algorithm, bufferSize, false);
    }

    public FileChannelHashProducerImpl(String algorithm, int bufferSize, boolean directBuffer) {
        super(algorithm);
        this.buffer = directBuffer
                ? ByteBuffer.allocateDirect(bufferSize)
                : ByteBuffer.allocate(bufferSize);
    }

    @Override
    protected void doCalculateHash(Path path) {
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            while (fileChannel.read(buffer) != -1) {
                buffer.flip();
                messageDigest.update(buffer);
                buffer.clear();
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}

package org.sample.filehash;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class FileHashProducer {

    protected MessageDigest messageDigest;

    public FileHashProducer(String algorithm) {
        try {
            this.messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Algortime no soportat: " + algorithm);
        }
    }

    public String calculateHash(String filename) {
        Path path = Paths.get(filename);
        if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new IllegalArgumentException("El fitxer " + filename + " no es pot llegir");
        }
        doCalculateHash(path);
        byte[] hash = messageDigest.digest();
        return HexUtils.bytesToHex(hash);
    }

    protected abstract void doCalculateHash(Path path);

}

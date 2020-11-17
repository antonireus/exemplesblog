package org.sample.filehash;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String... args) {

        String algorithm = "SHA-256";
        String filename = args[0];
        int bufferSize = 8192;

        List<FileHashProducer> producers = new ArrayList<>();
        producers.add(new FileInputStreamHashProducerImpl(algorithm, bufferSize));
        producers.add(new BufferedFileInputStreamHashProducerImpl(algorithm, bufferSize));
        producers.add(new FileChannelHashProducerImpl(algorithm, bufferSize * 8));
        producers.add(new FileChannelHashProducerImpl(algorithm, bufferSize * 8, true));
        producers.add(new FileInputStreamHashProducerImpl(algorithm, bufferSize));

        for (FileHashProducer producer : producers) {

            long startTime = System.nanoTime();

            String result = producer.calculateHash(filename);

            long duration = System.nanoTime() - startTime;

            System.out.printf("%s: %s (%d ms)%n",
                    producer.getClass().getSimpleName(),
                    result,
                    TimeUnit.NANOSECONDS.toMillis(duration));
        }

    }

}

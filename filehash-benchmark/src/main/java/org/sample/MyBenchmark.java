/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sample;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.sample.filehash.FileChannelHashProducerImpl;
import org.sample.filehash.FileHashProducer;
import org.sample.filehash.FileInputStreamHashProducerImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@State(Scope.Thread)
public class MyBenchmark {

    private static final String ALGORITHM = "SHA-256";

    private static final String TESTFILE = "TESTFILE";

    static {
        File file = new File(TESTFILE);
        if (!file.exists()) {
            System.out.printf("Creant %s%n", TESTFILE);

            Random random = new Random(1L);
            byte[] buffer = new byte[1_048_576];
            try (OutputStream os = new FileOutputStream(file)){
                for (int i = 0; i < 100; i++) {
                    random.nextBytes(buffer);
                    os.write(buffer);
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

        }
    }

    // 8K, 64K, 512K
    @Param({"8192", "65536", "524288"})
    public int bufferSize;

    private FileHashProducer classicIOProducer;

    private FileHashProducer newIOProducer;

    private FileHashProducer newIODirectProducer;

    @Setup(Level.Trial)
    public void setup() {
        classicIOProducer = new FileInputStreamHashProducerImpl(ALGORITHM, bufferSize);
        newIOProducer = new FileChannelHashProducerImpl(ALGORITHM, bufferSize, false);
        newIODirectProducer = new FileChannelHashProducerImpl(ALGORITHM, bufferSize, true);
    }

    @Benchmark
    public String testClassicIO() {
        return classicIOProducer.calculateHash(TESTFILE);
    }

    @Benchmark
    public String testNewIO() {
        return newIOProducer.calculateHash(TESTFILE);
    }

    @Benchmark
    public String testNewIODirect() {
        return newIODirectProducer.calculateHash(TESTFILE);
    }

}

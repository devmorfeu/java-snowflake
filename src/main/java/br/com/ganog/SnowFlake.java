package br.com.ganog;

import java.io.ByteArrayInputStream;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.time.Instant.*;
import static java.util.stream.Collectors.joining;

public class SnowFlake {

    private static final int NODE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    private static final int maxNodeId = (int) (Math.pow(2, NODE_ID_BITS) - 1);

    // Custom Epoch (January 1, 2015 Midnight UTC = 2015-01-01T00:00:00Z)
    private static final long CUSTOM_EPOCH = 1420070400000L;
    private static final AtomicLong lastTimestamp = new AtomicLong(0L);

    private final int threadId;

    public SnowFlake() {
        this.threadId = criarThreadId();
    }

    public synchronized long nextId() {
        long currentTimestamp = timestamp();
        long timestamp;

        while (currentTimestamp <= lastTimestamp.get()) {
            // Loop para garantir a ordem sequencial dos IDs
        }
        lastTimestamp.set(currentTimestamp);



        long id = currentTimestamp << (NODE_ID_BITS + SEQUENCE_BITS);
        System.out.println("id: " + id);

        id |= ((long) threadId << SEQUENCE_BITS);
        System.out.println("id2: " + id);

        id |= new SecureRandom().nextInt(10);
        System.out.println("id3: " + id);

        return id;
    }

    // Get current timestamp in milliseconds, adjust for the custom epoch.
    private static long timestamp() {
        return now().toEpochMilli() - CUSTOM_EPOCH;
    }

    private int criarThreadId() {
        int threadId;
        try {
            final var thread = Thread.currentThread();

            System.out.println("thread name: " + thread.getName());

            byte[] threadBytes = thread.getName().getBytes();

            System.out.println("thread bytes: " + threadBytes);

            var inputStream = new ByteArrayInputStream(threadBytes);

            final var intStream = IntStream.generate(inputStream::read).limit(inputStream.available());

            final var result = intStream.mapToObj(b -> format("%02X",(byte) b )).collect(joining());

            threadId = result.hashCode();

            System.out.println("thread hashcode: " + threadBytes);
        } catch (Exception ex) {
            threadId = (new SecureRandom().nextInt());
        }
        threadId = threadId & maxNodeId;

        System.out.println("thread id: " + threadId);

        return threadId;
    }
}

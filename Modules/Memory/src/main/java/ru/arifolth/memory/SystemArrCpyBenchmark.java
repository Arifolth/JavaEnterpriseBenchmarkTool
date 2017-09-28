package ru.arifolth.memory;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arifolth.benchmark.*;

public class SystemArrCpyBenchmark implements AbstractMemoryBenchmark {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemArrCpyBenchmark.class);
    private BenchmarkItem benchmarkItem = new BenchmarkItem("System.arraycopy");

    @Override
    public BenchmarkItem call() {
        LOGGER.info("Benchmarking System.arraycopy...");

        try {
            for (int mb : new int[]{10 * 1024 * 1024, 25 * 1024 * 1024, 50 * 1024 * 1024, 75 * 1024 * 1024}) {
                copyBytes(RandomStringUtils.randomAlphabetic(mb).getBytes(), mb);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        } finally {
            LOGGER.info("Stop Benchmarking System.arraycopy");
        }

        return benchmarkItem;
    }

    private void copyBytes(byte[] original, int size) {
        Timer timer = new Timer();

        byte[] copy = new byte [size];
        System.arraycopy(original, 0, copy, 0, size);

        long elapsedTime = timer.getElapsedMillis();
        LOGGER.debug("System.arraycopy: " + size/(1024 * 1024) + "Mb file copy: " + size/elapsedTime/1000 + " MB/s");
        benchmarkItem.getBenchmarkResults().add(new BenchmarkResult(size/(1024 * 1024) + "Mb file copy", size/elapsedTime/1000, MeasureEnum.MBPS));
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}

package ru.arifolth.memory;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arifolth.benchmark.*;

import java.util.Arrays;

public class ArraysCpyBenchmark implements AbstractMemoryBenchmark{
    private static final Logger LOGGER = LoggerFactory.getLogger(ArraysCpyBenchmark.class);
    private BenchmarkItem benchmarkItem = new BenchmarkItem("Arrays.copyOf");

    @Override
    public BenchmarkItem call() {
        LOGGER.info("Benchmarking Arrays.copyOf...");

        try {
            for (int mb : new int[]{10 * 1024 * 1024, 25 * 1024 * 1024, 50 * 1024 * 1024, 75 * 1024 * 1024}) {
                copyBytes(RandomStringUtils.randomAlphabetic(mb).getBytes(), mb);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        } finally {
            LOGGER.info("Stop Benchmarking Arrays.copyOf");
        }

        return benchmarkItem;
    }

    private void copyBytes(byte[] original, int size) {
        Timer timer = new Timer();

        Arrays.copyOf(original, size);

        long elapsedTime = timer.getElapsedMillis();
        LOGGER.debug("Arrays.copyOf: " + size/(1024 * 1024) + "Mb file copy: " + size/elapsedTime/1000 + " MB/s");
        benchmarkItem.getBenchmarkResults().add(new BenchmarkResult(size/(1024 * 1024) + "Mb file copy", size/elapsedTime/1000, MeasureEnum.MBPS));
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}

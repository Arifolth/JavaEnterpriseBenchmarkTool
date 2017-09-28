package ru.arifolth.benchmark;

import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractBenchmarkImpl<T extends ConcreteBenchmark> implements Benchmark {
    protected ExecutorService executorService = Executors.newFixedThreadPool(1);
    protected BenchmarkItem benchmarkItem;
    private Class<T> clazz;

    public AbstractBenchmarkImpl(String name, Class<T> clazz) {
        benchmarkItem = new BenchmarkItem(name);
        this.clazz = clazz;
    }

    @Override
    public BenchmarkItem call() throws ExecutionException, InterruptedException {
        try {
            for (T concreteBenchmark : SpringFactoriesLoader.loadFactories(clazz, null)) {
                Future<BenchmarkItem> benchmarkItemFuture = executorService.submit(concreteBenchmark);

                benchmarkItem.getBenchmarkItems().add(benchmarkItemFuture.get());
            }
        } finally {
            executorService.shutdownNow();
        }

        return benchmarkItem;
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}
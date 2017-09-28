package ru.arifolth.benchmark;

import java.util.concurrent.Callable;

public interface ConcreteBenchmark extends Callable<BenchmarkItem> {
}

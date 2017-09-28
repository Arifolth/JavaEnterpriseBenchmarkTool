package ru.arifolth.benchmark;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Created by ANilov on 08.05.2017.
 */
public interface Benchmark extends Callable<BenchmarkItem> {
}

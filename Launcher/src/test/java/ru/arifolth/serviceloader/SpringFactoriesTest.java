package ru.arifolth.serviceloader;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.support.SpringFactoriesLoader;
import ru.arifolth.benchmark.Benchmark;

import java.util.List;


public class SpringFactoriesTest {

    @Test
    public void LoadModulesTest() {
        List<Benchmark> benchmarks = SpringFactoriesLoader.loadFactories(Benchmark.class, null);
        Assert.assertNotNull(benchmarks);
        Assert.assertTrue(benchmarks.size() == 6);
    }
}

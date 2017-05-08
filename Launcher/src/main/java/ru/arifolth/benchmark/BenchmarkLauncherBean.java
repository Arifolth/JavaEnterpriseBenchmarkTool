/**
 *  Java Enterprise BenchmarkImpl Tool
 *  Copyright (C) 2017  Alexander Nilov arifolth@gmail.com 
 */


/**
 * 
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package ru.arifolth.benchmark;

/**
 * Created by ANilov on 09.02.2017.
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Component
public class BenchmarkLauncherBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkLauncherBean.class);
    private static final List<Future<BenchmarkItem>> futures = new ArrayList<Future<BenchmarkItem>>();

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ObjectMapper objectMapper;

    private void generateReport() throws ExecutionException, InterruptedException, IOException {
        BenchmarkReport benchmarkReport = new BenchmarkReport();

        for (Future<BenchmarkItem> future : futures) {
            benchmarkReport.getBenchmarks().add(future.get());
        }

        LOGGER.info("Generating Benchmark Report...");
        FileUtils.writeStringToFile(
                new File("BenchmarkReport.xml"),
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(benchmarkReport),
                Charset.defaultCharset());
    }

    public void perform() throws InterruptedException, ExecutionException, IOException {
        try {
            for(Benchmark benchmark : SpringFactoriesLoader.loadFactories(Benchmark.class, null)) {
                futures.add(executorService.submit(benchmark));
            }
        } finally {
            generateReport();

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        }
    }
}
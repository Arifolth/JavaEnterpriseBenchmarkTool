/**
 *  Java Enterprise Benchmark Tool
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

package ru.arifolth.filestorage;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arifolth.benchmark.BenchmarkItem;
import ru.arifolth.benchmark.BenchmarkResult;
import ru.arifolth.benchmark.MeasureEnum;
import ru.arifolth.benchmark.Timer;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by ANilov on 11.02.2017.
 */
public class Benchmark implements Callable<BenchmarkItem> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Benchmark.class);
    private BenchmarkItem benchmarkItem = new BenchmarkItem("File Storage");

    @Override
    public BenchmarkItem call() throws Exception {
        LOGGER.info("Benchmarking File Storage...");

        Timer timer = new Timer();
        try {
            for (int mb : new int[]{50, 100, 250, 500})
                testFileSize(mb);

            LOGGER.debug("File Storage: took " + timer.getElapsedTime() + " seconds");
        } finally {
            LOGGER.info("Stop Benchmarking File Storage.");
        }

        return benchmarkItem;
    }

    private void testFileSize(int mb) throws IOException {

        File file = null;
        try {
            file = File.createTempFile(RandomStringUtils.randomAlphabetic(5), "tmp");

            char[] chars = new char[1024];
            Arrays.fill(chars, 'A');
            String longLine = new String(chars);
            Timer timerWrite = new Timer();

            PrintWriter printWriter = null;
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file);
                printWriter = new PrintWriter(fileWriter);
                for (int i = 0; i < mb * 1024; i++) {
                    printWriter.println(longLine);
                }
            } finally {
                if(printWriter != null)
                    printWriter.close();
                if(fileWriter != null)
                    fileWriter.close();
            }

            long writeTime = timerWrite.getElapsedNanos();
            LOGGER.debug(String.format("Took %.3f seconds to write to a %d MB, file rate: %.1f MB/s%n",
                    writeTime / 1e9, file.length() >> 20, file.length() * 1000.0 / writeTime));
            benchmarkItem.getBenchmarkResults().add(new BenchmarkResult("Write " + mb + " MB file", file.length() * 1000.0 / writeTime, MeasureEnum.MBPS));

            Timer timerRead = new Timer();
            BufferedReader bufferedReader = null;
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);

                for (String line; (line = bufferedReader.readLine()) != null; ) {
                }
            } finally {
                if(bufferedReader != null)
                    bufferedReader.close();
                if(fileReader != null)
                    fileReader.close();
            }

            long readTime = timerRead.getElapsedNanos();
            LOGGER.debug(String.format("Took %.3f seconds to read to a %d MB file, rate: %.1f MB/s%n",
                    readTime / 1e9, file.length() >> 20, file.length() * 1000.0 / readTime));
            benchmarkItem.getBenchmarkResults().add(new BenchmarkResult("Read " + mb + " MB file", file.length() * 1000.0 / readTime, MeasureEnum.MBPS));
        } finally {
            if(file != null)
                file.delete();
        }
    }
}

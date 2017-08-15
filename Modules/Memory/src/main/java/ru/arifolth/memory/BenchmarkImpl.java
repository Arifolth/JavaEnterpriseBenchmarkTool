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

package ru.arifolth.memory;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arifolth.benchmark.*;

import java.io.File;
import java.util.Arrays;

/**
 * Created by ANilov on 16.08.2017.
 */
public class BenchmarkImpl implements Benchmark {
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkImpl.class);
    private BenchmarkItem benchmarkItem = new BenchmarkItem("MemCpy");

    @Override
    public BenchmarkItem call() throws Exception {
        LOGGER.info("Benchmarking MemCpy...");
        File file = null;

        try {
            for (int mb : new int[]{10 * 1024 * 1024, 25 * 1024 * 1024, 50 * 1024 * 1024, 75 * 1024 * 1024}) {
                copyBytes(RandomStringUtils.randomAlphabetic(mb).getBytes(), mb);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        } finally {
            try {
                if(file != null) {
                    file.delete();
                }
            } catch (SecurityException e) {
                // ignore
            }
            LOGGER.info("Stop Benchmarking MemCpy.");
        }

        return benchmarkItem;
    }

    private void copyBytes(byte[] original, int size) {
        Timer timer = new Timer();

        Arrays.copyOf(original, size);

        long elapsedTime = timer.getElapsedMillis();
        LOGGER.debug("MemCpy: " + size/(1024 * 1024) + "Mb file copy: " + size/elapsedTime/1000 + " MB/s");
        benchmarkItem.getBenchmarkResults().add(new BenchmarkResult(size/(1024 * 1024) + "Mb file copy", size/elapsedTime/1000, MeasureEnum.MBPS));
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}

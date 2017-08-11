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

package ru.arifolth.benchmark;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by ANilov on 16.02.2017.
 */
public class BenchmarkHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkHelper.class);

    private BenchmarkHelper() {
    }

    public static File generateFixedSizeFile(int size) throws IOException {
        File file = null;
        RandomAccessFile fh = null;

        LOGGER.trace("Generating File size=" + size + "...");

        try {
            file = File.createTempFile(RandomStringUtils.randomAlphabetic(5), ".tmp");
            fh = new RandomAccessFile (file, "rw");

            fh.writeBytes(RandomStringUtils.randomAlphabetic(size));

            LOGGER.trace("File size=" + size + " generated.");
        } catch (Exception ex) {
            LOGGER.error("Error Generating File size=" + size + "...", ex);

            if(fh != null) {
                fh.close();
            }

            try {
                if(file != null) {
                    file.delete();
                }
            } catch (SecurityException e) {
                // ignore
            }
        }

        return file;
    }
}

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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by ANilov on 16.02.2017.
 */
public class BenchmarkHelper {

    private BenchmarkHelper() {
    }

    public static File generateFile() throws IOException {
        return generateFixedSizeFile(1024 * 1024 * 10);
    }

    public static File generateFixedSizeFile(int size) throws IOException {
        File file = File.createTempFile(RandomStringUtils.randomAlphabetic(5), "tmp");

        FileUtils.writeStringToFile(file, RandomStringUtils.randomAlphabetic(size), Charset.defaultCharset());

        return file;
    }
}

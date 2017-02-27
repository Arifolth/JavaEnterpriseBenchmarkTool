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

import java.io.*;

/**
 * Created by ANilov on 16.02.2017.
 */
public class BenchmarkHelper {

    private BenchmarkHelper() {
    }

    public static File generateFixedSizeFile(int size) throws IOException {
        FileOutputStream fileOutputStream = null;
        OutputStream os = null;

        //fix ru.arifolth.benchmark.BenchmarkHelper.generateFixedSizeFile(BenchmarkHelper.java:48)
        //org.apache.commons.io.FileUtils.writeStringToFile(FileUtils.java:1947)
        File file = File.createTempFile(RandomStringUtils.randomAlphabetic(5), "tmp");
        try {
            fileOutputStream = new FileOutputStream(file);
            os = new BufferedOutputStream(fileOutputStream);

            os.write(RandomStringUtils.randomAlphabetic(size).getBytes());

            os.flush();
        } catch (Throwable th) {
            th.printStackTrace();
        }
        finally {

            if(null != fileOutputStream)
                fileOutputStream.close();

            if(null != os)
                os.close();
        }

        return file;
    }
}

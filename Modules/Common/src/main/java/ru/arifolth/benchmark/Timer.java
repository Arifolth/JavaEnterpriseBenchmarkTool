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

import java.util.concurrent.TimeUnit;

/**
 * Created by ANilov on 11.02.2017.
 */
public class Timer {
    long startTime = System.nanoTime();

    public long getElapsedTime() {
        return TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);
    }

    public long getElapsedMillis() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    }

    public long getElapsedNanos() {
        return System.nanoTime() - startTime;
    }
}

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

import javax.lang.model.element.NestingKind;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by ANilov on 18.02.2017.
 */
public class BenchmarkReport {
    private String nameOS = System.getProperty("os.name");
    private String versionOS = System.getProperty("os.version");
    private String architectureOS = System.getProperty("os.arch");
    private String processors = String.valueOf(Runtime.getRuntime().availableProcessors());
    private String freeMemory = String.valueOf(Runtime.getRuntime().freeMemory());
    long maxMemory = Runtime.getRuntime().maxMemory();
    private String maximumMemory = (maxMemory == Long.MAX_VALUE ? "no limit" : String.valueOf(maxMemory));
    private String totalMemory  = String.valueOf(Runtime.getRuntime().totalMemory());
    private String usedMemory = String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    private Collection<BenchmarkItem> benchmarks = new ArrayList<BenchmarkItem>();

    public String getUsedMemory() {
        return usedMemory;
    }

    public String getNameOS() {
        return nameOS;
    }

    public String getVersionOS() {
        return versionOS;
    }

    public String getArchitectureOS() {
        return architectureOS;
    }

    public String getProcessors() {
        return processors;
    }

    public String getFreeMemory() {
        return freeMemory;
    }

    public String getMaximumMemory() {
        return maximumMemory;
    }

    public String getTotalMemory() {
        return totalMemory;
    }

    public Collection<BenchmarkItem> getBenchmarks() {
        return benchmarks;
    }
}

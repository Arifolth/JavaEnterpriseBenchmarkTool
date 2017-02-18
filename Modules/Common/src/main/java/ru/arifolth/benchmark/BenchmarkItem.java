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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by ANilov on 18.02.2017.
 */
public class BenchmarkItem {
    private String name;
    private Collection<BenchmarkResult> benchmarkResults = new ArrayList<BenchmarkResult>();

    public BenchmarkItem(String name) {
        this.name = name;
    }

    public BenchmarkItem(String name, Collection<BenchmarkResult> benchmarkResults) {
        this.name = name;
        this.benchmarkResults = benchmarkResults;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<BenchmarkResult> getBenchmarkResults() {
        return benchmarkResults;
    }

    public void setBenchmarkResults(Collection<BenchmarkResult> benchmarkResults) {
        this.benchmarkResults = benchmarkResults;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BenchmarkItem benchmarkItem = (BenchmarkItem) o;

        if (name != null ? !name.equals(benchmarkItem.name) : benchmarkItem.name != null) return false;
        return benchmarkResults != null ? benchmarkResults.equals(benchmarkItem.benchmarkResults) : benchmarkItem.benchmarkResults == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (benchmarkResults != null ? benchmarkResults.hashCode() : 0);
        return result;
    }
}

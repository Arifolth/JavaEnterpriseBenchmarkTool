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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by ANilov on 11.02.2017.
 */
public class BenchmarkResult {
    private String description;
    private BigDecimal value;
    private MeasureEnum unit;

    public BenchmarkResult() {
    }

    public BenchmarkResult(String description, double value, MeasureEnum unit) {
        this.description = description;
        this.value = BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP);
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP);
    }

    public MeasureEnum getUnit() {
        return unit;
    }

    public void setUnit(MeasureEnum unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BenchmarkResult that = (BenchmarkResult) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return unit == that.unit;

    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }
}

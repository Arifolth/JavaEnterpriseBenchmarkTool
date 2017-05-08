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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BenchmarkMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkMain.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        LOGGER.info("Java Enterprise Benchmark Tool");
        LOGGER.info("Copyright (C) 2017 Alexander Nilov");

        ApplicationContext ctx = new ClassPathXmlApplicationContext("application-context.xml");
        LOGGER.info("Start Benchmarking...");

        BenchmarkLauncherBean bean = ctx.getBean(BenchmarkLauncherBean.class);
        bean.perform();

        ((ClassPathXmlApplicationContext) (ctx)).close();
        LOGGER.info("Benchmarking complete.");
    }
}
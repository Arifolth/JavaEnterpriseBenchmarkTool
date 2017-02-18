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

package ru.arifolth.xslt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arifolth.benchmark.BenchmarkItem;
import ru.arifolth.benchmark.BenchmarkResult;
import ru.arifolth.benchmark.MeasureEnum;
import ru.arifolth.benchmark.Timer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;

/**
 * Created by ANilov on 11.02.2017.
 */
public class Benchmark implements Callable<BenchmarkItem> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Benchmark.class);
    private BenchmarkItem benchmarkItem = new BenchmarkItem("XSLT");

    private final static String[] xsltProcessors = new String[]{
            "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl",
            "net.sf.saxon.TransformerFactoryImpl",
            "org.apache.xalan.processor.TransformerFactoryImpl"
        };

    @Override
    public BenchmarkItem call() throws Exception {
        LOGGER.info("Benchmarking XSLT...");

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {

            for(String xsltProcessor : xsltProcessors) {
                TransformerFactory transformerFactory = TransformerFactory.newInstance(xsltProcessor, null);
                Timer xsltTimer = new Timer();

                Transformer transformer = transformerFactory.newTransformer(new javax.xml.transform.stream.StreamSource(classLoader.getResourceAsStream("xsltInput.xsl")));

                transformer.transform(new javax.xml.transform.stream.StreamSource(classLoader.getResourceAsStream("xsltInput.xml")), new javax.xml.transform.stream.StreamResult(new FileOutputStream("xsltResult.html")));

                long elapsedMillis = xsltTimer.getElapsedMillis();
                LOGGER.debug("xsltProcessor: " + xsltProcessor + ", Transformation took " + elapsedMillis + " milliseconds");
                benchmarkItem.getBenchmarkResults().add(new BenchmarkResult(xsltProcessor, elapsedMillis, MeasureEnum.MILLISECONDS));
            }

            return benchmarkItem;
        } finally {
            LOGGER.info("Stop Benchmarking XSLT.");
        }
    }
}

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

package ru.arifolth.webservice;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arifolth.WebServiceImpl;
import ru.arifolth.benchmark.*;
import ru.arifolth.ws.ServiceFault;
import ru.arifolth.ws.ServiceRequest;
import ru.arifolth.ws.WebService;

import javax.xml.ws.Endpoint;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by ANilov on 11.02.2017.
 */
public class Benchmark implements Callable<BenchmarkItem> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Benchmark.class);
    private BenchmarkItem benchmarkItem = new BenchmarkItem("WebService[SOAP]");
    private ExecutorService executorService;
    private final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public BenchmarkItem call() throws Exception {
        LOGGER.info("Benchmarking WS...");
        executorService = Executors.newCachedThreadPool();

        try {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    LOGGER.debug("Publishing WS Endpoint at http://127.0.0.1:8080/WebService?wsdl");
                    Endpoint endpoint = Endpoint.publish("http://127.0.0.1:8080/WebService?wsdl", new WebServiceImpl());
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        endpoint.stop();
                    }
                }
            });

            executorService.submit(new Runnable() {
                private File file;
                private WebService webService = new WebService();

                @Override
                public void run() {
                    try {
                        LOGGER.debug("Sending WS Request");

                        for (int mb : new int[]{10 * 1024 * 1024, 25 * 1024 * 1024, 50 * 1024 * 1024, 75 * 1024 * 1024})
                            sendFile(mb);

                        latch.countDown();
                    } catch (ServiceFault serviceFault) {
                        LOGGER.error("Error calling WebService", serviceFault);
                    } catch (IOException e) {
                        LOGGER.error("IO Exception occured ", e);
                    } finally {
                        file.delete();
                    }
                }

                private void sendFile(int size) throws IOException, ServiceFault {
                    try {
                        file = BenchmarkHelper.generateFixedSizeFile(size);

                        Timer timer = new Timer();

                        ServiceRequest serviceRequest = new ServiceRequest();
                        serviceRequest.setRequestBody(FileUtils.readFileToByteArray(file));

                        webService.getWebServicePort().getData(serviceRequest);

                        long elapsedTime = timer.getElapsedMillis();
                        LOGGER.debug("WebService[SOAP]: " + size/(1024 * 1024) + "Mb file transfer: " + size/elapsedTime/1000 + " MB/s");
                        benchmarkItem.getBenchmarkResults().add(new BenchmarkResult(size/(1024 * 1024) + "Mb file transfer", size/elapsedTime/1000, MeasureEnum.MBPS));
                    } finally {
                        file.delete();
                    }
                }
            });

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        } finally {
            latch.await();
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
            LOGGER.info("Stop Benchmarking WS.");
        }

        return benchmarkItem;
    }
}

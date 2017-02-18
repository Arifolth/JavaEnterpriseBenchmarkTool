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

package ru.arifolth.http;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arifolth.benchmark.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.*;

/**
 * Created by ANilov on 18.02.2017.
 */
public class Benchmark implements Callable<BenchmarkItem> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Benchmark.class);
    private BenchmarkItem benchmarkItem = new BenchmarkItem("EmbeddedJetty [HTTP]");
    private final CountDownLatch latch = new CountDownLatch(1);
    private Server server = new Server();

    @Override
    public BenchmarkItem call() throws Exception {
        LOGGER.info("Benchmarking EmbeddedJetty...");
        ExecutorService executorService = Executors.newCachedThreadPool();

        try {
            executorService.submit(new Runnable() {
                private File file;

                @Override
                public void run() {
                    LOGGER.debug("Starting EmbeddedJetty on port 8082");
                    try {
                        Connector connector = new SelectChannelConnector();
                        connector.setPort(8082);
                        server.addConnector(connector);

                        server.setHandler(new AbstractHandler(){
                            @Override
                            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
                                    throws IOException, ServletException {
                                response.setContentType("text/html;charset=utf-8");
                                response.setStatus(HttpServletResponse.SC_OK);
                                baseRequest.setHandled(true);

                                sendFile(response, Integer.valueOf(baseRequest.getParameter("size")));
                            }

                            private void sendFile(HttpServletResponse response, int size) throws IOException {
                                file = BenchmarkHelper.generateFixedSizeFile(size);
                                LOGGER.trace("Server [HTTP] Sending file...");
                                Timer timer = new Timer();
                                response.getWriter().println(FileUtils.readFileToString(file, Charset.defaultCharset()));
                                response.getWriter().flush();

                                LOGGER.trace("Server [HTTP] File Sent....");
                                long elapsedTime = timer.getElapsedMillis();
                                LOGGER.debug("Server[HTTP]: " + size / (1024 * 1024) + "Mb file transfer: " + size / elapsedTime / 1000 + " MB/s");
                                benchmarkItem.getBenchmarkResults().add(new BenchmarkResult(size / (1024 * 1024) + "Mb file transfer", size / elapsedTime / 1000, MeasureEnum.MBPS));
                            }
                        });

                        LOGGER.trace("Starting Jetty...");
                        server.start();
                        latch.await();
                        LOGGER.trace("Stopping Jetty.");
                        server.stop();
                    } catch (Exception e) {
                        LOGGER.error("Jetty Error: ", e);
                    } finally {
                        server.destroy();
                        file.delete();
                    }
                }
            });
            executorService.submit(new Runnable() {
                private final static String serverIP = "http://localhost:8082";
                private BufferedReader in;
                private InputStreamReader inputStreamReader;

                @Override
                public void run() {
                    for (int mb : new int[]{10 * 1024 * 1024, 25 * 1024 * 1024, 50 * 1024 * 1024, 75 * 1024 * 1024})
                        requestFile(mb);

                    latch.countDown();
                }

                private void requestFile(int size) {
                    HttpURLConnection urlConnection = null;
                    try {
                        URL url = new URL(serverIP + "?size=" + size);
                        LOGGER.trace("HTTPClient Connecting to " + serverIP + ", file " + size);
                        urlConnection = (HttpURLConnection) url.openConnection();

                        inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                        this.in = new BufferedReader(inputStreamReader);
                        String inputLine;
                        do {
                            inputLine = this.in.readLine();
                        } while(inputLine != null);

                        LOGGER.trace("HTTPClient File recieved.");
                    } catch (MalformedURLException e) {
                        LOGGER.error("MalformedURLException", e);
                    } catch (IOException e) {
                        LOGGER.error("IOException", e);
                    } finally {
                        if(urlConnection != null)
                            urlConnection.disconnect();
                        try {
                            inputStreamReader.close();
                            in.close();
                        } catch (IOException e) {
                            LOGGER.error("IOException", e);
                        }
                    }
                }
            });


        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        } finally {
            latch.await();

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
            LOGGER.info("Stop Benchmarking EmbeddedJetty.");
        }

        return benchmarkItem;
    }
}

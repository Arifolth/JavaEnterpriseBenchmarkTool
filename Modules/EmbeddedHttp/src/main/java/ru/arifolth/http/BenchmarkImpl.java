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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arifolth.benchmark.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by ANilov on 18.02.2017.
 */
public class BenchmarkImpl implements Benchmark {
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkImpl.class);
    private BenchmarkItem benchmarkItem = new BenchmarkItem("EmbeddedHttp [HTTP]");
    private final CountDownLatch latch = new CountDownLatch(1);
    private HttpServer server;

    @Override
    public BenchmarkItem call() throws Exception {
        LOGGER.info("Benchmarking EmbeddedHttp...");
        ExecutorService executorService = Executors.newCachedThreadPool();

        try {
            executorService.submit(new Runnable() {
                private File file;

                @Override
                public void run() {
                    LOGGER.debug("Starting EmbeddedHttp on port 8082");
                    try {
                        server = HttpServer.create(new InetSocketAddress(8082), 0);

                        server.setExecutor(Executors.newSingleThreadExecutor());
                        server.createContext("/", new HttpHandler(){
                            @Override
                            public void handle(HttpExchange exchange) throws IOException {
                                String query = exchange.getRequestURI().getQuery();
                                Map<String, String> parameters = queryToMap(StringUtils.isBlank(query) ? StringUtils.EMPTY : query);

                                sendFile(exchange, Integer.valueOf(parameters.get("size")));
                            }

                            private void sendFile(HttpExchange exchange, int size) throws IOException {
                                try {
                                    file = BenchmarkHelper.generateFixedSizeFile(size);

                                    LOGGER.trace("Server [HTTP] Sending file...");
                                    Timer timer = new Timer();

                                    writeResponse(exchange, FileUtils.readFileToString(file, Charset.defaultCharset()));

                                    LOGGER.trace("Server [HTTP] File Sent....");
                                    long elapsedTime = timer.getElapsedMillis();
                                    LOGGER.debug("Server[HTTP]: " + size / (1024 * 1024) + "Mb file transfer: " + size / elapsedTime / 1000 + " MB/s");
                                    benchmarkItem.getBenchmarkResults().add(new BenchmarkResult(size / (1024 * 1024) + "Mb file transfer", size / elapsedTime / 1000, MeasureEnum.MBPS));
                                } finally {
                                    file.delete();
                                }
                            }
                        });

                        LOGGER.trace("Starting Http Server...");
                        server.start();
                    } catch (Exception e) {
                        LOGGER.error("Http Server Error: ", e);
                    }
                }

                public void writeResponse(HttpExchange httpExchange, String response) throws IOException {
                    Headers responseHeaders = httpExchange.getResponseHeaders();
                    responseHeaders.set("Content-Type", "text/html;charset=utf-8");
                    httpExchange.sendResponseHeaders(200, response.length());

                    OutputStream os = httpExchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }

                public Map<String, String> queryToMap(String query){
                    Map<String, String> result = new HashMap<String, String>();
                    for (String param : query.split("&")) {
                        String pair[] = param.split("=");
                        if (pair.length>1) {
                            result.put(pair[0], pair[1]);
                        }else{
                            result.put(pair[0], "");
                        }
                    }
                    return result;
                }
            });
            executorService.submit(new Runnable() {
                private static final  String HOST = "http://127.0.0.1:8082/";
                private BufferedReader in;
                private InputStreamReader inputStreamReader;

                @Override
                public void run() {
                    try {
                        for (int mb : new int[]{10 * 1024 * 1024, 25 * 1024 * 1024, 50 * 1024 * 1024, 75 * 1024 * 1024})
                            requestFile(mb);
                    } finally {
                        LOGGER.trace("Stopping Http Server.");
                        server.stop(1);
                        ((ExecutorService)server.getExecutor()).shutdown();

                        latch.countDown();
                    }

                }

                private void requestFile(int size) {
                    HttpURLConnection urlConnection = null;
                    try {
                        URL url = new URL(HOST + "?size=" + size);
                        LOGGER.trace("HTTPClient Connecting to " + HOST + ", file " + size);
                        urlConnection = (HttpURLConnection) url.openConnection();

                        int status = urlConnection.getResponseCode();
                        if(status != 200) {
                            LOGGER.error("Error: ResponseCode ", status);
                        }
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
            latch.await(1, TimeUnit.MINUTES);

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.SECONDS);
            LOGGER.info("Stop Benchmarking EmbeddedHttp.");
        }

        return benchmarkItem;
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}

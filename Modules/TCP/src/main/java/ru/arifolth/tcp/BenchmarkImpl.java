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

package ru.arifolth.tcp;


import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arifolth.benchmark.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.*;

/**
 * Created by ANilov on 11.02.2017.
 */
public class BenchmarkImpl implements Benchmark {
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkImpl.class);
    public static final int BYTES = 1024 * 1024 * 10;
    private final CountDownLatch countDownLatch = new CountDownLatch(2);
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private BenchmarkItem benchmarkItem = new BenchmarkItem("TCPServer");

    @Override
    public BenchmarkItem call() throws Exception {
        LOGGER.info("Benchmarking TCP...");
        try {
            executorService.submit(new Runnable() {
                private File file;
                private ServerSocket serverSocket;
                private PrintWriter out;

                @Override
                public void run() {
                    try {
                        serverSocket = new ServerSocket(3248);

                        Socket connectionSocket = serverSocket.accept();
                        LOGGER.trace("Accepted connection : " + connectionSocket);

                        try {
                            out = new PrintWriter(connectionSocket.getOutputStream(), true);
                            for (int mb : new int[]{10 * 1024 * 1024, 25 * 1024 * 1024, 50 * 1024 * 1024, 75 * 1024 * 1024}) {
                                sendFile(mb);
                            }
                        } catch (IOException ex) {
                            LOGGER.error(ex.getMessage());
                        } catch (Throwable th) {
                            LOGGER.error(th.getMessage());
                        } finally {
                            out.close();
                            connectionSocket.close();
                        }

                    } catch (IOException ex) {
                        LOGGER.error(ex.getMessage());
                    } finally {
                        try {
                            serverSocket.close();
                        } catch (IOException ex) {
                            LOGGER.error(ex.getMessage());
                        }

                        LOGGER.trace("Server Terminating.");
                        countDownLatch.countDown();
                    }
                }

                private void sendFile(int size) throws IOException {
                    file = BenchmarkHelper.generateFixedSizeFile(size);

                    Timer timer = new Timer();
                    LOGGER.trace("Server File Sending....");
                    out.println(FileUtils.readFileToString(file, Charset.defaultCharset()));

                    LOGGER.trace("Server File Sent....");
                    long elapsedTime = timer.getElapsedMillis();
                    LOGGER.debug("TCPServer: " + size / (1024 * 1024) + "Mb file transfer: " + size / elapsedTime / 1000 + " MB/s");
                    benchmarkItem.getBenchmarkResults().add(new BenchmarkResult(size / (1024 * 1024) + "Mb file transfer", size / elapsedTime / 1000, MeasureEnum.MBPS));
                }
            });
            executorService.submit(new Runnable() {
                private InputStreamReader inputStreamReader;
                private final static String serverIP = "127.0.0.1";
                private final static int serverPort = 3248;
                private Socket clientSocket;

                @Override
                public void run() {
                    try {
                        clientSocket = new Socket(serverIP, serverPort);

                        LOGGER.trace("Client connected....");

                        LOGGER.trace("Client File Recieving....");
                        BufferedReader bufferedReader = null;
                        try {
                            inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                            bufferedReader = new BufferedReader(inputStreamReader);

                            for (String line; (line = bufferedReader.readLine()) != null; ) {
                            }
                        } finally {
                            if(bufferedReader != null)
                                bufferedReader.close();
                            if(inputStreamReader != null)
                                inputStreamReader.close();
                        }

                        LOGGER.trace("Client File Recieved.");

                    } catch (IOException ex) {
                        LOGGER.error(ex.getMessage());
                    } finally {
                        try {
                            if (clientSocket != null)
                                clientSocket.close();
                        } catch (IOException ex) {
                            LOGGER.error(ex.getMessage());
                        }

                        LOGGER.trace("Client Terminating.");
                        countDownLatch.countDown();
                    }
                }
            });
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        } finally {
            countDownLatch.await(1, TimeUnit.MINUTES);
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.SECONDS);
            LOGGER.info("Stop Benchmarking TCP.");
        }

        return benchmarkItem;
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}

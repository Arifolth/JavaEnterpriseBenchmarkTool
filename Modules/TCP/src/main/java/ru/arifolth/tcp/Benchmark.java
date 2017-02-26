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

package ru.arifolth.tcp;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arifolth.benchmark.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Created by ANilov on 11.02.2017.
 */
public class Benchmark implements Callable<BenchmarkItem> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Benchmark.class);
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
                private BufferedOutputStream bufferedOutputStream;

                @Override
                public void run() {
                    try {
                        serverSocket = new ServerSocket(3248);

                        Socket connectionSocket = serverSocket.accept();
                        LOGGER.trace("Accepted connection : " + connectionSocket);
                        bufferedOutputStream = new BufferedOutputStream(connectionSocket.getOutputStream());

                        try {
                            for (int mb : new int[]{10 * 1024 * 1024, 25 * 1024 * 1024, 50 * 1024 * 1024, 75 * 1024 * 1024})
                                sendFile(mb);

                        } catch (IOException ex) {
                            LOGGER.error(ex.getMessage());
                        } finally {
                            bufferedOutputStream.close();
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
                    try {
                        file = BenchmarkHelper.generateFixedSizeFile(size);

                        byte[] bytes = FileUtils.readFileToByteArray(file);

                        Timer timer = new Timer();
                        LOGGER.trace("Server File Sending....");
                        bufferedOutputStream.write(bytes, 0, bytes.length);
                        bufferedOutputStream.flush();

                        LOGGER.trace("Server File Sent....");
                        long elapsedTime = timer.getElapsedMillis();
                        LOGGER.debug("TCPServer: " + bytes.length / (1024 * 1024) + "Mb file transfer: " + bytes.length / elapsedTime / 1000 + " MB/s");
                        benchmarkItem.getBenchmarkResults().add(new BenchmarkResult(bytes.length / (1024 * 1024) + "Mb file transfer", bytes.length / elapsedTime / 1000, MeasureEnum.MBPS));
                    } finally {
                        if(file != null)
                            file.delete();
                    }
                }
            });
            executorService.submit(new Runnable() {
                private File file = File.createTempFile(RandomStringUtils.randomAlphabetic(5), "tmp");
                private final static String serverIP = "127.0.0.1";
                private final static int serverPort = 3248;
                private Socket clientSocket;
                private FileOutputStream fos;

                @Override
                public void run() {
                    byte[] aByte = new byte[32*1024];
                    int bytesRead;

                    try {
                        clientSocket = new Socket(serverIP, serverPort);

                        clientSocket.setReceiveBufferSize(BYTES);
                        InputStream is = clientSocket.getInputStream();
                        LOGGER.trace("Client connected....");

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        if (is != null) {
                            BufferedOutputStream bos;

                            fos = new FileOutputStream(file);
                            bos = new BufferedOutputStream(fos);
                            LOGGER.trace("Client File Recieving....");
                            bytesRead = is.read(aByte, 0, aByte.length);

                            do {
                                baos.write(aByte);
                                bytesRead = is.read(aByte);
                            } while (bytesRead != -1);

                            bos.write(baos.toByteArray());
                            bos.flush();

                            bos.close();
                            baos.close();
                            is.close();

                            LOGGER.trace("Client File Recieved.");
                        }
                    } catch (IOException ex) {
                        LOGGER.error(ex.getMessage());
                    } finally {
                        try {
                            if (fos != null)
                                fos.close();

                            if (clientSocket != null)
                                clientSocket.close();

                        } catch (IOException ex) {
                            LOGGER.error(ex.getMessage());
                        }

                        file.delete();

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
}

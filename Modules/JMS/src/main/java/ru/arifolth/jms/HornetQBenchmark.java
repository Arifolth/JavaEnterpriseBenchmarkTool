package ru.arifolth.jms;

import org.apache.commons.lang3.RandomStringUtils;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.core.config.impl.FileConfiguration;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.hornetq.integration.transports.netty.NettyConnectorFactory;
import org.hornetq.integration.transports.netty.TransportConstants;
import org.hornetq.jms.server.JMSServerManager;
import org.hornetq.jms.server.impl.JMSServerManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arifolth.benchmark.*;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HornetQBenchmark implements AbstractJMSBenchmark {
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkImpl.class);
    public static final int JMS_MESSAGES_AMOUNT = 300;
    public static final String TRANSPORT_QUEUE = "transportQueue";

    private final BenchmarkItem benchmarkItem = new BenchmarkItem("HornetQ");

    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final CountDownLatch countDownLatch = new CountDownLatch(JMS_MESSAGES_AMOUNT);

    private FileConfiguration configuration;
    private JMSServerManager jmsServerManager;

    private void startServer() {
        try {
            configuration = new FileConfiguration();
            configuration.setConfigurationUrl("hornetq-configuration.xml");
            configuration.start();

            HornetQServer server = HornetQServers.newHornetQServer(configuration);
            jmsServerManager = new JMSServerManagerImpl(server, "hornetq-jms.xml");
            //if you want to use JNDI, simple inject a context here or don't call this method and make sure the JNDI parameters are set.
            jmsServerManager.setContext(null);
            jmsServerManager.start();
            LOGGER.trace("JMS server started");
        } catch (Exception e) {
            LOGGER.error("Error Benchmarking HornetQ:", e);
        }
    }

    private void stopServer() {
        try {
            LOGGER.trace("Stopping HornetQ server");
            jmsServerManager.stop();

            configuration.stop();
        } catch (Exception e) {
            LOGGER.error("Error Benchmarking HornetQ:", e);
        }
    }

    @Override
    public BenchmarkItem call() throws JMSException, InterruptedException {
        LOGGER.info("Benchmarking HornetQ...");
        try {
            startServer();

            LOGGER.debug("start processing HornetQ queue...");
            Timer timer = new Timer();

            for (int i = 0; i < JMS_MESSAGES_AMOUNT; i++) {
                sendJMSMessage();
                receiveJMSMessage();
            }

            long elapsedTime = timer.getElapsedMillis();
            LOGGER.debug("HornetQ processed " + JMS_MESSAGES_AMOUNT + " messages in " + TimeUnit.MILLISECONDS.toSeconds(elapsedTime) + " Seconds");

            benchmarkItem.getBenchmarkResults().add(new BenchmarkResult("HornetQ processed " + JMS_MESSAGES_AMOUNT + " messages in", TimeUnit.MILLISECONDS.toSeconds(elapsedTime), MeasureEnum.SECONDS));
        } finally {
            countDownLatch.await(1, TimeUnit.MINUTES);

            executorService.shutdownNow();

            stopServer();
            LOGGER.info("Stop Benchmarking HornetQ.");
        }

        return benchmarkItem;
    }

    private void sendJMSMessage() throws JMSException {
        executorService.submit(new Runnable() {
            private MessageProducer producer;

            private Session session;
            private Connection connection;
            private Queue transportQueue;

            {
                // Step 1. Directly instantiate the JMS Queue object.
                transportQueue = HornetQJMSClient.createQueue(TRANSPORT_QUEUE);

                // Step 2. Instantiate the TransportConfiguration object which
                // contains the knowledge of what transport to use,
                // The server port etc.

                Map<String, Object> connectionParams = new HashMap<String, Object>();
                connectionParams.put(TransportConstants.PORT_PROP_NAME, 5445);

                TransportConfiguration transportConfiguration = new TransportConfiguration(
                        NettyConnectorFactory.class.getName(), connectionParams);

                // Step 3 Directly instantiate the JMS ConnectionFactory object
                // using that TransportConfiguration
                ConnectionFactory cf = HornetQJMSClient.createConnectionFactory(transportConfiguration);

                // Step 4.Create a JMS Connection
                connection = cf.createConnection();

                // Step 5. Create a JMS Session
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Step 10. Start the Connection
                connection.start();
            }

            @Override
            public void run() {
                Thread.currentThread().setName("Producer-" + countDownLatch.getCount());

                // Step 6. Create a JMS Message Producer
                try {
                    producer = session.createProducer(transportQueue);

                    // Step 7. Create a Text Message
                    TextMessage message = session.createTextMessage(RandomStringUtils.randomAlphabetic(255));

                    // Step 8. Send the Message
                    producer.send(message);

                    LOGGER.trace("Sent message: " + message.getText());
                } catch (JMSException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                } finally {
                    try {
                        if (producer != null) {
                            producer.close();
                        }

                        if (null != session) {
                            session.close();
                        }
                        if (null != connection) {
                            connection.stop();

                            connection.close();
                        }
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    }
                }
            }
        });
    }

    private void receiveJMSMessage() throws JMSException {
        executorService.submit(new Runnable() {
            private MessageConsumer consumer;

            private Session session;
            private Connection connection;
            private Queue transportQueue;

            {
                // Step 1. Directly instantiate the JMS Queue object.
                transportQueue = HornetQJMSClient.createQueue(TRANSPORT_QUEUE);

                // Step 2. Instantiate the TransportConfiguration object which
                // contains the knowledge of what transport to use,
                // The server port etc.

                Map<String, Object> connectionParams = new HashMap<String, Object>();
                connectionParams.put(TransportConstants.PORT_PROP_NAME, 5445);

                TransportConfiguration transportConfiguration = new TransportConfiguration(
                        NettyConnectorFactory.class.getName(), connectionParams);

                // Step 3 Directly instantiate the JMS ConnectionFactory object
                // using that TransportConfiguration
                ConnectionFactory cf = HornetQJMSClient.createConnectionFactory(transportConfiguration);

                // Step 4.Create a JMS Connection
                connection = cf.createConnection();

                // Step 5. Create a JMS Session
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Step 10. Start the Connection
                connection.start();
            }

            @Override
            public void run() {
                Thread.currentThread().setName("Consumer-" + countDownLatch.getCount());

                try {
                    // Step 9. Create a JMS Message Consumer
                    consumer = session.createConsumer(transportQueue);


                    // Step 11. Receive the message
                    TextMessage messageReceived = (TextMessage) consumer.receive(5000);

                    LOGGER.trace("Received message: " + messageReceived.getText());

                    countDownLatch.countDown();
                } catch (JMSException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                } finally {
                    try {
                        if (consumer != null) {
                            consumer.close();
                        }

                        if (null != session) {
                            session.close();
                        }
                        if (null != connection) {
                            connection.stop();

                            connection.close();
                        }
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    }
                }
            }
        });
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}
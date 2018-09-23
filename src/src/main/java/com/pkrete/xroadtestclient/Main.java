package com.pkrete.xroadtestclient;

import org.niis.xrd4j.client.serializer.ServiceRequestSerializer;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.util.MessageHelper;
import com.pkrete.xroadtestclient.request.RequestFactory;
import com.pkrete.xroadtestclient.request.thread.Worker;
import com.pkrete.xroadtestclient.serializer.TestServiceRequestSerializer;
import com.pkrete.xroadtestclient.util.ApplicationHelper;
import com.pkrete.xroadtestclient.util.PropertiesLoader;
import com.pkrete.xroadtestclient.util.StatisticsCollector;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains the main method of the test client application.
 *
 * @author Petteri Kivimäki
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public Main() {
        ApplicationHelper.configureLog4j();
    }

    public static void main(String[] args) {
        new Main().start();
    }

    public void start() {
        // Read properties from files
        Properties settings = PropertiesLoader.loadGeneralSettings();
        Properties clients = PropertiesLoader.loadClientSettings();
        // Get settings for the test run
        int threadExecutorCount = MessageHelper.strToInt(settings.getProperty("thread.executor.count"));
        int threadCount = MessageHelper.strToInt(settings.getProperty("thread.count"));
        String url = settings.getProperty("proxy.url");
        int sleep = MessageHelper.strToInt(settings.getProperty("thread.sleep"));
        int maxRequestCount = MessageHelper.strToInt(settings.getProperty("thread.request.count"));
        int maxTime = MessageHelper.strToInt(settings.getProperty("thread.request.maxtime"));

        logger.info("Thread executor count : {}", threadExecutorCount);
        logger.info("Thread count : {}", threadCount);
        logger.info("Proxy URL : \"{}\"", url);
        logger.info("Thread sleep time : {}", sleep);
        logger.info("Max request count per thread : {}", maxRequestCount);
        logger.info("Max run time per thread : {}", maxTime);

        // Create serializer for requests
        ServiceRequestSerializer serializer = new TestServiceRequestSerializer();
        // Generate the request object
        ServiceRequest request = RequestFactory.getRequest(clients);

        if (request == null) {
            logger.error("Configuring the client failed. Exit...");
            return;
        }

        logger.info("Start the test.");
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(threadExecutorCount);
        for (int i = 0; i < threadCount; i++) {
            logger.debug("Starting thread #{}.", i);
            // Clone the request - all the threads update the id, which causes
            // concurrecny issues if the object is not cloned
            request = ApplicationHelper.clone(request);
            Runnable worker = new Worker(request, url, sleep, maxRequestCount, maxTime, i, serializer);
            executor.execute(worker);
        }
        // The shutdown() method doesn’t cause an immediate destruction 
        // of the ExecutorService. It will make the ExecutorService stop 
        // accepting new tasks and shut down after all running threads 
        // finish their current work.
        executor.shutdown();

        try {
            // Blocks until all tasks have completed execution after a shutdown 
            // request, or the timeout occurs, or the current thread is 
            // interrupted, whichever happens first. Returns true if this 
            // executor is terminated and false if the timeout elapsed 
            // before termination.
            while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                // Wait for executor to be terminated
                logger.trace("Waiting for ExecutorService to be terminated.");
            }
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }
        long duration = System.currentTimeMillis() - startTime;
        logger.info("The test was succesfully finished.");
        logger.info("##################################");
        logger.info("RESULTS:");
        logger.info("Test duration: {}", ApplicationHelper.millisecondsToString(duration));
        logger.info("Total number of queries: {}", StatisticsCollector.getStatisticsCollector().getResults().size());
        logger.info("Successful queries #: {}", StatisticsCollector.getStatisticsCollector().getSuccessCount());
        logger.info("Failed queries #: {}", StatisticsCollector.getStatisticsCollector().getFailureCount());
        logger.info("Fastest query: {} ms", StatisticsCollector.getStatisticsCollector().getMinThroughput());
        logger.info("Slowest query: {} ms", StatisticsCollector.getStatisticsCollector().getMaxThroughput());
        logger.info("Median: {} ms", StatisticsCollector.getStatisticsCollector().getMedian());
    }
}

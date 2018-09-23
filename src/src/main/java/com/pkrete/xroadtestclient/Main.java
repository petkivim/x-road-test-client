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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class contains the main method of the test client application.
 *
 * @author Petteri Kivimäki
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public Main() {
        ApplicationHelper.configureLog4j();
    }

    public static void main(String[] args) {
        new Main().start();
    }

    /**
     * Run configured tests.
     */
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

        LOG.info("Thread executor count : {}", threadExecutorCount);
        LOG.info("Thread count : {}", threadCount);
        LOG.info("Proxy URL : \"{}\"", url);
        LOG.info("Thread sleep time : {}", sleep);
        LOG.info("Max request count per thread : {}", maxRequestCount);
        LOG.info("Max run time per thread : {}", maxTime);

        // Create serializer for requests
        ServiceRequestSerializer serializer = new TestServiceRequestSerializer();
        // Generate the request object
        ServiceRequest request = RequestFactory.getRequest(clients);

        if (request == null) {
            LOG.error("Configuring the client failed. Exit...");
            return;
        }

        LOG.info("Start the test.");
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(threadExecutorCount);
        for (int i = 0; i < threadCount; i++) {
            LOG.debug("Starting thread #{}.", i);
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
                LOG.trace("Waiting for ExecutorService to be terminated.");
            }
        } catch (InterruptedException ex) {
            LOG.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }
        long duration = System.currentTimeMillis() - startTime;
        LOG.info("The test was succesfully finished.");
        LOG.info("##################################");
        LOG.info("RESULTS:");
        LOG.info("Test duration: {}", ApplicationHelper.millisecondsToString(duration));
        LOG.info("Total number of queries: {}", StatisticsCollector.getStatisticsCollector().getResults().size());
        LOG.info("Successful queries #: {}", StatisticsCollector.getStatisticsCollector().getSuccessCount());
        LOG.info("Failed queries #: {}", StatisticsCollector.getStatisticsCollector().getFailureCount());
        LOG.info("Fastest query: {} ms", StatisticsCollector.getStatisticsCollector().getMinThroughput());
        LOG.info("Slowest query: {} ms", StatisticsCollector.getStatisticsCollector().getMaxThroughput());
        LOG.info("Median: {} ms", StatisticsCollector.getStatisticsCollector().getMedian());
    }
}

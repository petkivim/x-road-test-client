package com.pkrete.xrd4j.tools.test_client;

import com.pkrete.xrd4j.client.serializer.ServiceRequestSerializer;
import com.pkrete.xrd4j.common.message.ServiceRequest;
import com.pkrete.xrd4j.common.util.MessageHelper;
import com.pkrete.xrd4j.tools.test_client.request.RequestFactory;
import com.pkrete.xrd4j.tools.test_client.request.thread.Worker;
import com.pkrete.xrd4j.tools.test_client.serializer.TestServiceRequestSerializer;
import com.pkrete.xrd4j.tools.test_client.util.ApplicationHelper;
import com.pkrete.xrd4j.tools.test_client.util.PropertiesLoader;
import com.pkrete.xrd4j.tools.test_client.util.StatisticsCollector;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        new Main().start(args);
    }

    public void start(String[] args) {
        Properties settings = PropertiesLoader.loadGeneralSettings();
        Properties clients = PropertiesLoader.loadClientSettings();
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

        ServiceRequestSerializer serializer = new TestServiceRequestSerializer();
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
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        long endTime = System.currentTimeMillis() - startTime;
        logger.info("The test was succesfully finished.");
        logger.info("##################################");
        logger.info("RESULTS:");
        logger.info("Test duration: {} s", (int) ((endTime / 1000) % 60));
        logger.info("Total number of queries: {}", StatisticsCollector.getStatisticsCollector().getResults().size());
        logger.info("Successful queries #: {}", StatisticsCollector.getStatisticsCollector().getSuccessCount());
        logger.info("Failed queries #: {}", StatisticsCollector.getStatisticsCollector().getFailureCount());
        logger.info("Fastest query: {} ms", StatisticsCollector.getStatisticsCollector().getMinThroughput());
        logger.info("Slowest query: {} ms", StatisticsCollector.getStatisticsCollector().getMaxThroughput());
        logger.info("Median: {} ms", StatisticsCollector.getStatisticsCollector().getMedian());
    }
}

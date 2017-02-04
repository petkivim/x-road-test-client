package com.pkrete.xrd4j.tools.testclient.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class collects statistics about the test execution.
 *
 * @author Petteri Kivimäki
 */
public class StatisticsCollector {

    /**
     * Variable for the fastest request throughput.
     */
    private long minThroughput;
    /**
     * Variable for the slowest request throughput.
     */
    private long maxThroughput;
    /**
     * Variable that counts the number of successful requests.
     */
    private int successCount;
    /**
     * Variable that counts the number of failed requests.
     */
    private int failureCount;
    /**
     * Variable for the throughput time of all the requests.
     */
    private final BlockingQueue<Long> results;
    /**
     * Lock object for minThroughput.
     */
    private final Object minLock = new Object();
    /**
     * Lock object for maxThroughput.
     */
    private final Object maxLock = new Object();
    /**
     * Lock object for successCount.
     */
    private final Object successLock = new Object();
    /**
     * Lock object for failureCount.
     */
    private final Object failureLock = new Object();
    /**
     * Reference to the singleton object.
     */
    private static StatisticsCollector ref;

    /**
     * Returns a reference to the singleton object of this class.
     *
     * @return reference to the singleton object of this class
     */
    public static StatisticsCollector getStatisticsCollector() {
        if (ref == null) {
            ref = new StatisticsCollector();
        }
        return ref;
    }

    /**
     * Constructs and initializes a new StatisticsCollector.
     */
    private StatisticsCollector() {
        this.minThroughput = 0;
        this.maxThroughput = 0;
        this.successCount = 0;
        this.failureCount = 0;
        this.results = new LinkedBlockingQueue<Long>();
    }

    /**
     * Returns the smallest throughput value.
     *
     * @return smallest throughput value
     */
    public long getMinThroughput() {
        synchronized (minLock) {
            return minThroughput;
        }
    }

    /**
     * Sets the smallest throughput value.
     *
     * @param minThroughput new value
     */
    public void setMinThroughput(long minThroughput) {
        synchronized (minLock) {
            if (this.minThroughput == 0) {
                this.minThroughput = minThroughput;
            } else if (minThroughput < this.minThroughput) {
                this.minThroughput = minThroughput;
            }
        }
    }

    /**
     * Returns the biggest throughput value.
     *
     * @return biggest throughput value
     */
    public long getMaxThroughput() {
        synchronized (maxLock) {
            return maxThroughput;
        }
    }

    /**
     * Sets the biggest throughput value.
     *
     * @param maxThroughput new value
     */
    public void setMaxThroughput(long maxThroughput) {
        synchronized (maxLock) {
            if (this.maxThroughput == 0) {
                this.maxThroughput = maxThroughput;
            } else if (maxThroughput > this.maxThroughput) {
                this.maxThroughput = maxThroughput;
            }
        }
    }

    /**
     * Returns the results list.
     *
     * @return results list
     */
    public BlockingQueue<Long> getResults() {
        return results;
    }

    /**
     * Returns the results list sorted ascending.
     *
     * @return results list sorted ascending
     */
    public List<Long> getSortedResults() {
        List<Long> sortedResults = new ArrayList(Arrays.asList(this.results.toArray()));
        Collections.sort(sortedResults);
        return sortedResults;
    }

    /**
     * Returns the median of the results.
     *
     * @return median of the results
     */
    public Double getMedian() {
        List<Long> sortedResults = this.getSortedResults();
        int middle = sortedResults.size() / 2;
        if (sortedResults.size() % 2 == 1) {
            return (double) sortedResults.get(middle);
        } else {
            return (sortedResults.get(middle - 1) + sortedResults.get(middle)) / 2.0;
        }
    }

    /**
     * Adds a new result to the results list.
     *
     * @param result new value
     */
    public void addResult(Long result) {
        this.results.add(result);
    }

    /**
     * Returns the number of successful queries.
     *
     * @return number of successful queries
     */
    public int getSuccessCount() {
        return successCount;
    }

    /**
     * Increases the number of successful queries by one.
     *
     */
    public void increseSuccessCount() {
        synchronized (successLock) {
            this.successCount++;
        }
    }

    /**
     * Returns the number of failed queries.
     *
     * @return number of failed queries
     */
    public int getFailureCount() {
        return failureCount;
    }

    /**
     * Increases the number of failed queries by one.
     *
     */
    public void increaseFailureCount() {
        synchronized (failureLock) {
            this.failureCount++;
        }
    }
}

package com.hansun.server.metrics;


import com.google.common.base.Preconditions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class InfluxDBClient {

    private final InfluxDB influxDB;
    private final String influxDBName;
    private final String retentionPolicy;
    private final String influxDBUrl;
    private static final String LOCAL_HOST = "localhost";
    private static final String UNKONOWN = "unknown";


    private static final Logger log = LoggerFactory.getLogger(InfluxDBClient.class);

    public InfluxDBClient(String influxDBUrl, String influxDBUserName, String influxDBPassword, String influxDBName, String retentionPolicy, int batchSize, int maxTimeBeforeBatchFlush) {
        Preconditions.checkArgument(influxDBUrl != null, "Invalid influxDBUrl");
        Preconditions.checkArgument(influxDBUserName != null, "Invalid influxDBUserName");
        Preconditions.checkArgument(influxDBPassword != null, "Invalid influxDBPassword");
        Preconditions.checkArgument(influxDBName != null, "Invalid influxDBName");
        influxDB = InfluxDBFactory.connect(influxDBUrl, influxDBUserName, influxDBPassword);
        this.influxDBName = influxDBName;
        this.retentionPolicy = retentionPolicy;
        this.influxDBUrl = influxDBUrl;
        influxDB.enableBatch(batchSize, maxTimeBeforeBatchFlush, TimeUnit.MILLISECONDS);
        System.out.print(ping());
    }

    /**
     * Method to ping influxDB.
     *
     * @return Pong
     */
    public Pong ping() {
        return influxDB.ping();
    }

    /**
     * Method to query influxDB.
     *
     * @param query
     * @return QueryResult
     */
    public QueryResult getQueryResult(String query) {
        //if influxDB is not present in local environment then return null
        if (!checkIfLocalInfluxDBExist()) {
            return null;
        }
        QueryResult queryResult = influxDB.query(new Query(query, influxDBName));
        return queryResult;
    }


    /**
     * Method to add single Point to influxDB
     *
     * @param measurement
     * @param tagsMap
     * @param fieldsMap
     */
    public void writeSinglePoint(String measurement, Map<String, String> tagsMap, Map<String, Object> fieldsMap) {
        try {
            //if influxDB is not present in local environment then don't write
            if (!checkIfLocalInfluxDBExist()) {
                return;
            }
            Point point = createPoint(measurement, tagsMap, fieldsMap);
            influxDB.write(influxDBName, retentionPolicy, point);
        } catch (Exception exception) {
            log.warn("Error in writing single point to influxDB", exception);
        }
    }

    /**
     * Method to add single Point to influxDB
     *
     * @param measurement
     * @param tagsMap
     * @param fieldsMap
     * @param timestamp
     */
    public void writeSinglePoint(String measurement, Map<String, String> tagsMap, Map<String, Object> fieldsMap, long timestamp) {
        try {
            //if influxDB is not present in local environment then don't write
            if (!checkIfLocalInfluxDBExist()) {
                return;
            }
            Point point = createPoint(measurement, tagsMap, fieldsMap, timestamp);
            influxDB.write(influxDBName, retentionPolicy, point);
        } catch (Exception exception) {
            log.warn("Error in writing single point to influxDB", exception);
        }
    }


    /**
     * Method to create BatchPoint
     *
     * @param measurement
     * @param pointMap
     */
    public void evaluateAndWriteBatchPoints(String measurement, Map<Map<String, String>, Map<String, Object>> pointMap) {
        Set<Point> influxDBPointSet = new HashSet<>();
        pointMap.entrySet().forEach(entry -> influxDBPointSet.add(createPoint(measurement, entry.getKey(), entry.getValue())));
        writeBatchPoints(influxDBPointSet);
    }


    /**
     * Method to write BatchPoint to influxDB
     *
     * @param influxDBPointSet
     */
    private void writeBatchPoints(Set<Point> influxDBPointSet) {
        try {
            //if influxDB is not present in local environment then don't write
            if (!checkIfLocalInfluxDBExist()) {
                return;
            }
            BatchPoints batchPoints = BatchPoints.database(influxDBName).retentionPolicy(retentionPolicy).build();
            influxDBPointSet.stream().forEach(point -> influxDB.write(batchPoints.point(point)));
        } catch (Exception exception) {
            log.warn("Error in writing batch points to influxDB", exception);
        }

    }


    /**
     * Method to stop executar thread present in Batch processer
     */
    public void close() {
        // disable the batch which closes the scheduled executor which runs on
        // non-daemon threads.
        influxDB.disableBatch();
    }

    /**
     * Helper method to create single Point
     *
     * @param tagsMap
     * @param fieldsMap
     * @return Point
     */
    public Point createPoint(String measurement, Map<String, String> tagsMap, Map<String, Object> fieldsMap) {
        Point.Builder pointBuilder = Point.measurement(measurement)
                //Currently using System current time(UTC) for storing,
                //it can be configured using properties file along with time unit
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .fields(fieldsMap).tag(tagsMap);
        return pointBuilder.build();
    }

    /**
     * Helper method to create single Point
     *
     * @param tagsMap
     * @param fieldsMap
     * @param timestamp
     * @return Point
     */
    public Point createPoint(String measurement, Map<String, String> tagsMap, Map<String, Object> fieldsMap, long timestamp) {
        Point.Builder pointBuilder = Point.measurement(measurement)
                .time(timestamp, TimeUnit.MILLISECONDS)
                .fields(fieldsMap).tag(tagsMap);
        return pointBuilder.build();
    }


    //For test purpose

    /**
     * Method to create database
     */
    public void createInfluxDatabase(String influxDBName) {
        influxDB.createDatabase(influxDBName);
    }

    /**
     * Method to describe database
     */
    public List<String> describeInfluxDatabase() {
        return influxDB.describeDatabases();
    }


    /**
     * Method to delete database
     */
    public void deleteInfluxDatabase(String influxDBName) {
        influxDB.deleteDatabase(influxDBName);
    }


    private boolean checkIfLocalInfluxDBExist() {
        try {
            if (influxDBUrl.contains(LOCAL_HOST) &&
                    (ping() == null || ping().getVersion().equalsIgnoreCase(UNKONOWN))) {
                return false;
            }
        } catch (Exception exception) {
            log.debug("Exception due to influxDB is not present in local environment", exception);
            return false;
        }
        return true;
    }

}
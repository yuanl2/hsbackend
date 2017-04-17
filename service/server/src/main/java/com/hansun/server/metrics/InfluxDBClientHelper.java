package com.hansun.server.metrics;

import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanl2 on 2017/4/12.
 */
@Component
public class InfluxDBClientHelper {

    private static final Logger log = LoggerFactory.getLogger(InfluxDBClientHelper.class);

    //    private MetricHelper metricHelper;
    private InfluxDBClient client;


    @Autowired
    public InfluxDBClientHelper(InfluxDBClient client) {
        this.client = client;
//        this.metricHelper = metricHelper;
    }

    //For Unit Test only
    public InfluxDBClientHelper() {
    }


    /**
     * Method to describe influxDB
     *
     * @return List
     */
    public List<String> describeInfluxDB() {
        return client.describeInfluxDatabase();
    }

    /**
     * Method to query influxDB.
     *
     * @param query
     * @return QueryResult
     */
    public QueryResult getInfluxDBQueryResult(String query) {
        return client.getQueryResult(query);
    }


    public void sendIncrementToInfluxDB(String measurement, String fieldKey) {
        Map<String, String> tagsMap = new HashMap();
        Map<String, Object> fieldsMap = new HashMap();
        fieldsMap.put(fieldKey, new Integer(1));
        writeSinglePointToInfluxDB(measurement, tagsMap, fieldsMap);
    }

    public void sendIncrementByCountToInfluxDB(String measurement, String fieldKey, int count) {
        Map<String, String> tagsMap = new HashMap();
        Map<String, Object> fieldsMap = new HashMap();
        fieldsMap.put(fieldKey, count);
        writeSinglePointToInfluxDB(measurement, tagsMap, fieldsMap);
    }


    public void sendGaugeToInfluxDB(String measurement, String fieldKey, Long fieldValue) {
        Map<String, String> tagsMap = new HashMap();
        Map<String, Object> fieldsMap = new HashMap();
        fieldsMap.put(fieldKey, fieldValue);
        writeSinglePointToInfluxDB(measurement, tagsMap, fieldsMap);
    }


    public void sendIncrementAndGaugeToInfluxDB(String measurement, Map<String, Object> fieldsMap) {
        Map<String, String> tagsMap = new HashMap();
        writeSinglePointToInfluxDB(measurement, tagsMap, fieldsMap);
    }

    /**
     * Method to write single Point to influxDB with multiple custom tags and fields when request is synchronous only
     *
     * @param measurement
     * @param tagsMap
     * @param fieldsMap
     */
    public void writeSinglePointToInfluxDB(String measurement, Map<String, String> tagsMap, Map<String, Object> fieldsMap) {
        //For synchronous request populate authInfo and userAgent from ThreadLocals class
        // Note for asynchronous request, ThreadLocals will not work here
        try {
            Map<String, String> mutableTagsMap = new HashMap<>(tagsMap);
            Map<String, Object> mutableFieldsMap = new HashMap<>(fieldsMap);
//            metricHelper.addAllDefaultMetricProps(mutableTagsMap, mutableFieldsMap, ThreadLocals.getAuthInfo(), ThreadLocals.getUserAgent());
            client.writeSinglePoint(measurement, mutableTagsMap, mutableFieldsMap);
        } catch (Exception exception) {
            log.warn("Error in sending single point to influxDB when request is synchronous", exception);
        }
    }


    public void writeSinglePointToInfluxDB(String measurement, Map<String, String> tagsMap, Map<String, Object> fieldsMap, long timestamp) {
        //For synchronous request populate authInfo and userAgent from ThreadLocals class
        // Note for asynchronous request, ThreadLocals will not work here
        try {
            Map<String, String> mutableTagsMap = new HashMap<>(tagsMap);
            Map<String, Object> mutableFieldsMap = new HashMap<>(fieldsMap);
//            metricHelper.addAllDefaultMetricProps(mutableTagsMap, mutableFieldsMap, ThreadLocals.getAuthInfo(), ThreadLocals.getUserAgent());
            client.writeSinglePoint(measurement, mutableTagsMap, mutableFieldsMap, timestamp);
        } catch (Exception exception) {
            log.warn("Error in sending single point to influxDB when request is synchronous", exception);
        }
    }

    /**
     * Method to write single Point to influxDB with multiple custom tags and fields when request is asynchronous or writing in multiple threads
     *
     * @param measurement
     * @param tagsMap
     * @param fieldsMap
     */
    public void writeSinglePointToInfluxDBAsync(String measurement, Map<String, String> tagsMap, Map<String, Object> fieldsMap) {
        try {
            Map<String, String> mutableTagsMap = new HashMap<>(tagsMap);
            Map<String, Object> mutableFieldsMap = new HashMap<>(fieldsMap);
//            metricHelper.addAllDefaultMetricProps(mutableTagsMap, mutableFieldsMap, authInfo, userAgent);
            client.writeSinglePoint(measurement, mutableTagsMap, mutableFieldsMap);
        } catch (Exception exception) {
            log.warn("Error in sending single point to influxDB", exception);
        }
    }


//    /**
//     * Method to write BatchPoints with multiple custom tags and fields
//     *
//     * @param measurement
//     * @param influxDBPointMap
//     */
//    public void writeBatchPointsToInfluxDB(String measurement, Map<Map<String, String>, Map<String, Object>> influxDBPointMap, AuthInfo authInfo, String userAgent) {
//        try {
//            Map<Map<String, String>, Map<String, Object>> mutableInfluxDBPointMap = new HashMap<>();
//            for (Map.Entry<Map<String, String>, Map<String, Object>> entry : influxDBPointMap.entrySet()) {
//                mutableInfluxDBPointMap.put(new HashMap<>(entry.getKey()), new HashMap<>(entry.getValue()));
//            }
//            mutableInfluxDBPointMap.entrySet().stream().forEach((entry) -> metricHelper.addAllDefaultMetricProps(entry.getKey(), entry.getValue(), authInfo, userAgent));
//            client.evaluateAndWriteBatchPoints(measurement, mutableInfluxDBPointMap);
//        } catch (Exception exception) {
//            log.warn("Error in sending batch points to influxDB", exception);
//        }
//    }

    public void emitMetrics(InfluxDBMetrics metrics) {
        try {
            if (metrics.getTimestamp() != null) {
                writeSinglePointToInfluxDB(metrics.getMeasurement(), metrics.getTagMap(), metrics.getFieldMap(), metrics.getTimestamp().toEpochMilli());
            } else {
                writeSinglePointToInfluxDB(metrics.getMeasurement(), metrics.getTagMap(), metrics.getFieldMap());
            }
        } catch (Exception exception) {
            log.info("Error in emitting metrics data.", exception);
        }
    }

    /**
     * Method to stop executar thread present in Batch processer
     */
    public void shutdown() {
        client.close();
    }


}

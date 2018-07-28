package com.hansun.server.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.hansun.server.common.HSServiceProperties;
import com.hansun.server.common.ServerException;
import com.hansun.server.db.ConnectionPoolManager;
import com.hansun.server.db.DataStore;
import com.hansun.server.metrics.InfluxDBClient;
import com.hansun.server.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by yuanl2 on 2017/3/30.
 */
@Configuration
//@EnableWebMvc
@ComponentScan(basePackages = "com.hansun.server")
public class HSServiceConfig {

    @Autowired
    private Environment env;

    @Bean
    public HSServiceProperties hsServiceProperties() {
        return new HSServiceProperties(env);
    }

    @Bean
    public ConnectionPoolManager connectionPoolManager() {
        return new ConnectionPoolManager(hsServiceProperties());
    }

    @Bean
    public DataStore dataStore() {
        return new DataStore();
    }

    @Bean
    public DeviceService deviceService() {
        return new DeviceService();
    }

    @Bean
    public InfluxDBClient influxDBClient() {
        try {
            return new InfluxDBClient(hsServiceProperties().getInfluxDBUrl(),
                    hsServiceProperties().getInfluxDBUserName(),
                    hsServiceProperties().getInfluxDBPassword(),
                    hsServiceProperties().getInfluxDBName(),
                    hsServiceProperties().getInfluxDbRetentionPolicy(),
                    hsServiceProperties().getBatchSize(),
                    hsServiceProperties().getBatchTimeout());
        } catch (Exception e) {
            throw new ServerException("Unable to start influxDB client", e);
        }
    }

    @Bean(name = "mapperObject")
    public ObjectMapper getObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        om.registerModule(javaTimeModule);
        return om;
    }
}

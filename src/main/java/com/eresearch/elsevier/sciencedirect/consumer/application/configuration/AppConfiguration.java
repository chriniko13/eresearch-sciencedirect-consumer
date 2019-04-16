package com.eresearch.elsevier.sciencedirect.consumer.application.configuration;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.eresearch.elsevier.sciencedirect.consumer.deserializer.InstantDeserializer;
import com.eresearch.elsevier.sciencedirect.consumer.serializer.InstantSerializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.netty.handler.ssl.SslContextBuilder;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.*;

@EnableScheduling
@EnableAspectJAutoProxy

@Configuration
public class AppConfiguration implements SchedulingConfigurer {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Instant.class, new InstantSerializer());
        javaTimeModule.addDeserializer(Instant.class, new InstantDeserializer());
        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }

    @Bean
    @Qualifier("elsevierObjectMapper")
    public ObjectMapper elsevierObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true); //for elsevier api.
        //objectMapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Instant.class, new InstantSerializer());
        javaTimeModule.addDeserializer(Instant.class, new InstantDeserializer());
        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }

    @Bean
    @Qualifier("consumerRestTemplate")
    public RestTemplate restTemplate() throws SSLException {
        Netty4ClientHttpRequestFactory nettyFactory = new Netty4ClientHttpRequestFactory();
        nettyFactory.setSslContext(SslContextBuilder.forClient().build());

        return new RestTemplate(nettyFactory);
    }

    /*
     * Handling (front) asynchronous communications.
     */
    @Bean(destroyMethod = "shutdownNow")
    @Qualifier("scidirConsumerExecutor")
    public ExecutorService scidirConsumerExecutor() {
        return new ThreadPoolExecutor(
                20, 120,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(300, true),
                new ThreadFactoryBuilder().setNameFormat("scidir-consumer-thread-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /*
     * Handling worker (threads) operations.
     */
    @Bean(destroyMethod = "shutdownNow")
    @Qualifier("workerOperationsExecutor")
    public ExecutorService workerOperationsExecutor() {
        return new ThreadPoolExecutor(
                20, 120,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(300, true),
                new ThreadFactoryBuilder().setNameFormat("worker-operations-thread-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /*
     * Handling db operations.
     */
    @Bean(destroyMethod = "shutdownNow")
    @Qualifier("dbOperationsExecutor")
    public ExecutorService dbOperationsExecutor() {
        return new ThreadPoolExecutor(
                20, 120,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(300, true),
                new ThreadFactoryBuilder().setNameFormat("db-operations-thread-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Value("${service.zone.id}")
    private ZoneId zoneId;

    @Bean
    public Clock clock() {
        return Clock.system(zoneId);
    }

    @Bean
    @Qualifier("basicRetryPolicy")
    public RetryPolicy retryPolicy() {

        return new RetryPolicy()
                .retryOn(RestClientException.class)
                .withMaxRetries(3)
                .withDelay(10, TimeUnit.SECONDS)
                .withJitter(7, TimeUnit.SECONDS);
    }

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Bean(destroyMethod = "close")
    @Qualifier("hikariDataSource")
    public HikariDataSource hikariDataSource() {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(dbUrl);
        config.setUsername(username);
        config.setPassword(password);

        config.setMetricRegistry(this.metricRegistry());
        config.setHealthCheckRegistry(this.healthCheckRegistry());

        return new HikariDataSource(config);
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(this.hikariDataSource());
    }

    @Qualifier("transactionTemplate")
    @Bean
    public TransactionTemplate transactionTemplate() {

        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager());

        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        return transactionTemplate;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(this.hikariDataSource());
    }

    @Bean
    @Qualifier("appMetricRegistry")
    public MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }


    @Bean
    public HealthCheckRegistry healthCheckRegistry() {
        return new HealthCheckRegistry();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newSingleThreadScheduledExecutor());
    }
}

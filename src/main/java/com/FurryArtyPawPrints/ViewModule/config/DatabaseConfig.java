package com.FurryArtyPawPrints.ViewModule.config;

import com.FurryArtyPawPrints.ViewModule.utils.AwsIamAuthUtility;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.client.SSLMode;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.time.Duration;

@Slf4j
@Configuration
@EnableR2dbcRepositories(basePackages = "com.FurryArtyPawPrints.ViewModule.repositories")
public class DatabaseConfig extends AbstractR2dbcConfiguration {

    @Autowired
    private AwsIamAuthUtility awsIamAuthUtility;

    @Value("${aws.rds.host}")
    private String rdsHost;

    @Value("${aws.rds.port:5432}")
    private int rdsPort;

    @Value("${aws.rds.username}")
    private String rdsUsername;

    @Value("${aws.rds.database:furryartsypawprints}")
    private String databaseName;

    @Value("${aws.rds.connection-timeout:30}")
    private int connectionTimeoutSeconds;

    @Bean
    @Primary
    @Override
    public ConnectionFactory connectionFactory() {
        try {
            String authToken = awsIamAuthUtility.generateAuthToken();

            PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder()
                    .host(rdsHost)
                    .port(rdsPort)
                    .database(databaseName)
                    .username(rdsUsername)
                    .password(authToken)
                    .connectTimeout(Duration.ofSeconds(connectionTimeoutSeconds))
                    .sslMode(SSLMode.REQUIRE) // Enable SSL for Aurora
                    .build();

            log.info("Configured Aurora PostgreSQL connection to: {}:{}/{}", rdsHost, rdsPort, databaseName);
            return new PostgresqlConnectionFactory(config);

        } catch (Exception e) {
            log.error("Failed to create database connection factory: {}", e.getMessage(), e);
            throw new RuntimeException("Database connection configuration failed", e);
        }
    }
}

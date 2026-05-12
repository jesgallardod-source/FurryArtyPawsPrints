package com.FurryArtyPawPrints.ViewModule.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsUtilities;
import software.amazon.awssdk.services.rds.model.GenerateAuthenticationTokenRequest;

@Slf4j
@Component
public class AwsIamAuthUtility {

    @Value("${aws.rds.host}")
    private String rdsHost;

    @Value("${aws.rds.port:5432}")
    private int rdsPort;

    @Value("${aws.rds.region:us-east-1}")
    private String rdsRegion;

    @Value("${aws.rds.username}")
    private String rdsUsername;

    /**
     * Generate IAM authentication token for Aurora PostgreSQL
     * @return Authentication token to use as password
     */
    public String generateAuthToken() {
        try {
            AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();

            RdsUtilities utilities = RdsUtilities.builder()
                    .credentialsProvider(credentialsProvider)
                    .region(Region.of(rdsRegion))
                    .build();

            GenerateAuthenticationTokenRequest request = GenerateAuthenticationTokenRequest.builder()
                    .hostname(rdsHost)
                    .port(rdsPort)
                    .username(rdsUsername)
                    .build();

            String token = utilities.generateAuthenticationToken(request);
            log.debug("Successfully generated IAM authentication token for user: {}", rdsUsername);
            return token;

        } catch (Exception e) {
            log.error("Failed to generate IAM authentication token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate IAM authentication token", e);
        }
    }

    /**
     * Get the database connection URL for Aurora PostgreSQL
     * @return JDBC URL for Aurora PostgreSQL
     */
    public String getDatabaseUrl() {
        return String.format("jdbc:postgresql://%s:%d/%s", rdsHost, rdsPort, getDatabaseName());
    }

    /**
     * Get the R2DBC connection URL for Aurora PostgreSQL
     * @return R2DBC URL for Aurora PostgreSQL
     */
    public String getR2dbcUrl() {
        return String.format("r2dbc:postgresql://%s:%d/%s", rdsHost, rdsPort, getDatabaseName());
    }

    /**
     * Extract database name from host (assuming format: cluster-name.region.rds.amazonaws.com)
     * @return Database name
     */
    private String getDatabaseName() {
        // You can configure this as a property or extract from host
        // For now, using a default - update as needed
        return "furryartsypawprints";
    }
}

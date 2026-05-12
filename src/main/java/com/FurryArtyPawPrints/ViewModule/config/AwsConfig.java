package com.FurryArtyPawPrints.ViewModule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.RdsClientBuilder;

@Configuration
public class AwsConfig {

    @Bean
    public RdsClient rdsClient() {
        RdsClientBuilder builder = RdsClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(System.getenv().getOrDefault("AWS_REGION", "us-east-1")));

        return builder.build();
    }
}

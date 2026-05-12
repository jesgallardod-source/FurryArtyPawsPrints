package com.FurryArtyPawPrints.ViewModule.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dsql.DsqlClient;
import software.amazon.awssdk.services.dsql.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.dsql.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.dsql.model.Field;
import software.amazon.awssdk.services.dsql.model.SqlParameter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuroraDsqlConnectionUtility {

    @Value("${aws.dsql.cluster-arn}")
    private String clusterArn;

    @Value("${aws.dsql.region:us-east-2}")
    private String region;

    @Value("${aws.dsql.database:furryArtsyPawsPrint}")
    private String database;

    private DsqlClient dsqlClient;

    public AuroraDsqlConnectionUtility() {
        this.dsqlClient = DsqlClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Execute a SELECT query and return results
     */
    public List<Map<String, Object>> executeQuery(String sql, Map<String, Object> parameters) {
        try {
            ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                    .clusterIdentifier(clusterArn)
                    .database(database)
                    .sql(sql)
                    .parameters(buildSqlParameters(parameters))
                    .build();

            ExecuteStatementResponse response = dsqlClient.executeStatement(request);

            return response.records().stream()
                    .map(record -> {
                        Map<String, Object> row = new java.util.HashMap<>();
                        for (int i = 0; i < record.size(); i++) {
                            Field field = record.get(i);
                            String columnName = response.columnMetadata().get(i).name();
                            row.put(columnName, extractFieldValue(field));
                        }
                        return row;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error executing query: {}", e.getMessage(), e);
            throw new RuntimeException("Query execution failed", e);
        }
    }

    /**
     * Execute an INSERT, UPDATE, or DELETE statement
     */
    public int executeUpdate(String sql, Map<String, Object> parameters) {
        try {
            ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                    .clusterIdentifier(clusterArn)
                    .database(database)
                    .sql(sql)
                    .parameters(buildSqlParameters(parameters))
                    .build();

            ExecuteStatementResponse response = dsqlClient.executeStatement(request);
            return response.numberOfRecordsUpdated();

        } catch (Exception e) {
            log.error("Error executing update: {}", e.getMessage(), e);
            throw new RuntimeException("Update execution failed", e);
        }
    }

    /**
     * Execute DDL statements (CREATE, ALTER, DROP)
     */
    public void executeDdl(String sql) {
        try {
            ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                    .clusterIdentifier(clusterArn)
                    .database(database)
                    .sql(sql)
                    .build();

            dsqlClient.executeStatement(request);
            log.info("DDL executed successfully: {}", sql);

        } catch (Exception e) {
            log.error("Error executing DDL: {}", e.getMessage(), e);
            throw new RuntimeException("DDL execution failed", e);
        }
    }

    private List<SqlParameter> buildSqlParameters(Map<String, Object> parameters) {
        return parameters.entrySet().stream()
                .map(entry -> SqlParameter.builder()
                        .name(entry.getKey())
                        .value(convertToField(entry.getValue()))
                        .build())
                .collect(Collectors.toList());
    }

    private Field convertToField(Object value) {
        if (value == null) {
            return Field.builder().isNull(true).build();
        }

        if (value instanceof String) {
            return Field.stringValue((String) value);
        } else if (value instanceof Integer) {
            return Field.longValue(((Integer) value).longValue());
        } else if (value instanceof Long) {
            return Field.longValue((Long) value);
        } else if (value instanceof Boolean) {
            return Field.booleanValue((Boolean) value);
        } else if (value instanceof java.time.LocalDateTime) {
            return Field.stringValue(value.toString());
        }

        return Field.stringValue(value.toString());
    }

    private Object extractFieldValue(Field field) {
        if (field.isNull()) {
            return null;
        }

        if (field.stringValue() != null) {
            return field.stringValue();
        } else if (field.longValue() != null) {
            return field.longValue();
        } else if (field.booleanValue() != null) {
            return field.booleanValue();
        }

        return field.toString();
    }

    public void close() {
        if (dsqlClient != null) {
            dsqlClient.close();
        }
    }
}

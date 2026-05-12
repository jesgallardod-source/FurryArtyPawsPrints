package com.FurryArtyPawPrints.ViewModule.services;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
public class DatabaseInitializationService implements CommandLineRunner {

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public void run(String... args) throws Exception {
        initializeSchema()
                .doOnSuccess(result -> log.info("Database schema initialized successfully"))
                .doOnError(error -> log.error("Failed to initialize database schema: {}", error.getMessage()))
                .subscribe();
    }

    private Mono<Void> initializeSchema() {
        return Mono.fromCallable(() -> {
            try {
                ClassPathResource resource = new ClassPathResource("schema.sql");
                String sql = new String(Files.readAllBytes(Paths.get(resource.getURI())));

                // Split SQL commands and execute them
                String[] statements = sql.split(";");
                for (String statement : statements) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        log.debug("Executing: {}", trimmed);
                        r2dbcEntityTemplate.getDatabaseClient()
                                .sql(trimmed)
                                .then()
                                .block(); // Use block() for synchronous execution during startup
                    }
                }
                return null;
            } catch (Exception e) {
                log.error("Error reading or executing schema.sql: {}", e.getMessage());
                throw new RuntimeException("Schema initialization failed", e);
            }
        });
    }
}

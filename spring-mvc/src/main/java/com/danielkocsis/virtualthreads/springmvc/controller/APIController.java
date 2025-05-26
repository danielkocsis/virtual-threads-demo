package com.danielkocsis.virtualthreads.springmvc.controller;

import com.danielkocsis.virtualthreads.shared.ServiceConfiguration;
import com.danielkocsis.virtualthreads.springmvc.domain.AuditEntry;
import com.danielkocsis.virtualthreads.springmvc.repository.AuditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/demo/mvc")
public class APIController {

    private final RestClient restClient;
    private final AuditRepository repository;
    private final ServiceConfiguration configuration;

    public APIController(RestClient.Builder restClientBuilder,
                         AuditRepository repository,
                         ServiceConfiguration configuration) {

        this.restClient = restClientBuilder.baseUrl(configuration.getUri()).build();
        this.repository = repository;
        this.configuration = configuration;
    }

    @GetMapping
    Long getEntriesCount() {
        return repository.count();
    }

    @PostMapping
    public String doBlockingOperation() {
        try {
            var result = callExternalWebAPI();
            log.info("API request completed");

            repository.save(
                    AuditEntry.builder()
                            .responseCode(result.getStatusCode().value())
                            .actionTime(Instant.now())
                            .uri("https://httpbin.org/delay/1")
                            .build());

            log.info("Database call completed");
        } catch (Exception e) {
            log.error("Error during processing: {}", e.getMessage());
        }

        return "Request processed successfully";
    }

    private ResponseEntity<Void> callExternalWebAPI() {
        return restClient.get()
                .uri("/delay/" + configuration.getDelay())
                .retrieve()
                .toBodilessEntity();
    }
}
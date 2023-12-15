package com.danielkocsis.virtualthreads.loom.controller;

import com.danielkocsis.virtualthreads.loom.domain.AuditEntry;
import com.danielkocsis.virtualthreads.loom.repository.AuditRepository;
import com.danielkocsis.virtualthreads.shared.ServiceConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@RestController
@RequestMapping("/api/demo/loom")
@Slf4j
public class APIController {

    private final RestClient restClient;
    private final AuditRepository repository;
    private final ServiceConfiguration serviceConfiguration;

    public APIController(RestClient.Builder restClientBuilder,
                         AuditRepository repository,
                         ServiceConfiguration serviceConfiguration) {

        this.restClient = restClientBuilder.baseUrl(serviceConfiguration.getUri()).build();
        this.repository = repository;
        this.serviceConfiguration = serviceConfiguration;
    }

    @GetMapping
    Long getEntriesCount() {
        return repository.count();
    }

    @PostMapping
    public String doBlockingOperation() {
        ResponseEntity<Void> response = callExternalWebAPI();
        saveNewAuditEntry(response.getStatusCode());

        return "Request processed successfully";
    }

    private AuditEntry saveNewAuditEntry(HttpStatusCode code) {
        AuditEntry auditEntry = repository.save(
                AuditEntry.builder()
                        .responseCode(code.value())
                        .actionTime(Instant.now())
                        .uri("https://httpbin.org/delay/1")
                        .build());
        log.info("Database call completed on thread {}", Thread.currentThread());
        return auditEntry;
    }

    private ResponseEntity<Void> callExternalWebAPI() {
        ResponseEntity<Void> response = restClient.get()
                .uri(STR."/delay/\{serviceConfiguration.getDelay()}")
                .retrieve()
                .toBodilessEntity();

        log.info("API request completed on thread {}", Thread.currentThread());
        return response;
    }
}
package com.danielkocsis.virtualthreads.webflux.controller;

import com.danielkocsis.virtualthreads.shared.ServiceConfiguration;
import com.danielkocsis.virtualthreads.webflux.domain.AuditEntry;
import com.danielkocsis.virtualthreads.webflux.repository.AuditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/demo/webflux")
public class APIController {

    private final ServiceConfiguration configuration;
    private final WebClient webClient;
    private final AuditRepository repository;

    public APIController(AuditRepository repository, ServiceConfiguration configuration) {
        this.webClient = WebClient.create(configuration.getUri());
        this.repository = repository;
        this.configuration = configuration;
    }

    @PostMapping
    public Mono<String> doBlockingOperation() {
        return callExternalWebAPI()
                .doOnNext(_ -> log.info("API request completed"))
                .map(r -> AuditEntry.builder()
                            .uri(configuration.getUri())
                            .responseCode(r.getStatusCode().value())
                            .actionTime(Instant.now())
                            .build())
                .flatMap(repository::save)
                .doOnNext(_ -> log.info("Database call completed"))
                .doOnError(e -> log.error("Error during processing: {}", e.getMessage()))
                .map(_ -> "Request processed successfully");
    }

    @GetMapping
    Mono<Long> getEntriesCount() {
        return repository.count();
    }

    private Mono<ResponseEntity<Void>> callExternalWebAPI() {
        return webClient.method(HttpMethod.GET)
                .uri(STR."/delay/\{configuration.getDelay()}")
                .retrieve()
                .toBodilessEntity();
    }
}
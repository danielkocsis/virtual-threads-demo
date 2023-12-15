package com.danielkocsis.virtualthreadstomcat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.concurrent.StructuredTaskScope;

@RestController
@RequestMapping("/httpbin")
@Slf4j
public class HttpBinController {

    private final RestClient restClient;

    public HttpBinController(RestClient.Builder restClientBuilder) {
        restClient = restClientBuilder.baseUrl("https://httpbin.org/").build();
    }

    @GetMapping("/block")
    public String delay(@RequestParam int seconds, @RequestParam int times) throws InterruptedException {
        try (var scope = new StructuredTaskScope<>()) {
            for (int i = 0; i < times; i++) {
                scope.fork(() -> callExternalWebAPI(seconds));
            }

            scope.join();
        }

        String response = String.format("Called delay API %d times on %s", times, Thread.currentThread());
        log.info(response);

        return response;
    }

    private ResponseEntity<Void> callExternalWebAPI(int seconds) {
        ResponseEntity<Void> result = null;

        try {
            result = restClient.get()
                    .uri("/delay/" + seconds)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Response code {} on {}", result.getStatusCode(), Thread.currentThread());
        }
        catch (Throwable t) {
            log.error("Error", t);
        }

        return result;
    }
}
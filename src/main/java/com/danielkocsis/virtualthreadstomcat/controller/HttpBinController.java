package com.danielkocsis.virtualthreadstomcat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/httpbin")
@Slf4j
public class HttpBinController {

    private final RestClient restClient;

    public HttpBinController(RestClient.Builder restClientBuilder) {
        restClient = restClientBuilder.baseUrl("https://httpbin.org/").build();
    }

    @GetMapping("/block/{seconds}")
    public String delay(@PathVariable int seconds) {
        ResponseEntity<Void> result = null;

        try {
            result = restClient.get()
                    .uri("/delay/" + seconds)
                     .retrieve()
                    .toBodilessEntity();

            log.info("{} on {}", result.getStatusCode(), Thread.currentThread());
        }
        catch (Throwable t) {
            log.error("Error", t);
        }

        return Thread.currentThread().toString();
    }
}
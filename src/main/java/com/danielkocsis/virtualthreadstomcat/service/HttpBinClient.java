package com.danielkocsis.virtualthreadstomcat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Slf4j
public class HttpBinClient {

    private final RestClient restClient;

    public HttpBinClient(RestClient.Builder restClientBuilder) {
        restClient = restClientBuilder.baseUrl("https://httpbin.org/").build();
    }

    @Async
    public CompletableFuture<ResponseEntity<Void>> callExternalWebAPI(int seconds) {
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

        return CompletableFuture.completedFuture(result);
    }
}

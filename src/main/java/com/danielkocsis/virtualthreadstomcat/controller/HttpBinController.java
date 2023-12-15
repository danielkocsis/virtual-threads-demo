package com.danielkocsis.virtualthreadstomcat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/httpbin")
@Slf4j
public class HttpBinController {

    private final WebClient webClient = WebClient.create("https://httpbin.org/");

    @GetMapping("/block")
    public Mono<String> delay(@RequestParam int seconds, @RequestParam int times) {
        return Flux.just(1)
                .repeat(times)
                .flatMap(i -> callExternalWebAPI(seconds))
                .doOnNext(r -> log.info("Response code {} on {}", r.getStatusCode(), Thread.currentThread()))
                .collectList()
                .map(r -> String.format("Called delay API %d times on %s", times, Thread.currentThread()));
    }

    private Mono<ResponseEntity<Void>> callExternalWebAPI(int seconds) {
        return webClient.method(HttpMethod.GET)
                .uri("/delay/" + seconds)
                .retrieve()
                .toBodilessEntity();
    }
}
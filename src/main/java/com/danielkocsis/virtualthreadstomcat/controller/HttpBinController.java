package com.danielkocsis.virtualthreadstomcat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/httpbin")
@Slf4j
public class HttpBinController {

    private final WebClient webClient = WebClient.create("https://httpbin.org/");

    @GetMapping("/block/{seconds}")
    public Mono<String> delay(@PathVariable int seconds) {

        return webClient.method(HttpMethod.GET)
                .uri("/delay/" + seconds)
                .retrieve()
                .toBodilessEntity()
                .doOnNext(result -> log.info("{} on {}", result.getStatusCode(), Thread.currentThread()))
                .map(result -> "Thread.currentThread().toString()");
    }
}
package com.danielkocsis.virtualthreadstomcat.controller;

import com.danielkocsis.virtualthreadstomcat.service.HttpBinClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/httpbin")
@Slf4j
public class HttpBinController {

    @Resource
    private HttpBinClient httpBinClient;

    @GetMapping("/block")
    public String delay(@RequestParam int seconds, @RequestParam int times) throws ExecutionException, InterruptedException {
        List<CompletableFuture<ResponseEntity<Void>>> responseEntityFutures = new ArrayList<>();

        for (int i = 0; i < times; i++) {
            responseEntityFutures.add(httpBinClient.callExternalWebAPI(seconds));
        }

        responseEntityFutures.forEach(CompletableFuture::join);

        String response = String.format("Called delay API %d times on %s", times, Thread.currentThread());
        log.info(response);

        return response;
    }
}
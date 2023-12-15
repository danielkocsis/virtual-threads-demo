package com.danielkocsis.virtualthreads.shared;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConfigurationProperties(prefix = "service")
public class ServiceConfiguration {

    private final String uri;
    private final int delay;

    @ConstructorBinding
    public ServiceConfiguration(@DefaultValue(value = "https://httpbin.org/") String uri,
                                @DefaultValue(value = "1") int delay) {

        this.uri = uri;
        this.delay = delay;
    }
}

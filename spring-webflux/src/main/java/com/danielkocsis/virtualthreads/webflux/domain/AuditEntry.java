package com.danielkocsis.virtualthreads.webflux.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audit")
public class AuditEntry {

    private @Id Long id;
    private String uri;
    private int responseCode;
    private Instant actionTime;
}

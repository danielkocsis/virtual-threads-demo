package com.danielkocsis.virtualthreads.loom.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEntry {

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    private String uri;
    private int responseCode;
    private Instant actionTime;
}

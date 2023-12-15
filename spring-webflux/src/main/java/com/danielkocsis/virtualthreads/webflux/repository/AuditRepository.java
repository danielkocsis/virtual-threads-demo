package com.danielkocsis.virtualthreads.webflux.repository;

import com.danielkocsis.virtualthreads.webflux.domain.AuditEntry;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AuditRepository extends ReactiveCrudRepository<AuditEntry, Long> {
}

package com.danielkocsis.virtualthreads.loom.repository;

import com.danielkocsis.virtualthreads.loom.domain.AuditEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditEntry, Long> {
}

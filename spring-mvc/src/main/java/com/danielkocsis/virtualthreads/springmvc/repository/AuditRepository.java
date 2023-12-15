package com.danielkocsis.virtualthreads.springmvc.repository;

import com.danielkocsis.virtualthreads.springmvc.domain.AuditEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditEntry, Long> {
}

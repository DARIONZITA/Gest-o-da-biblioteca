package com.example.bibliotecaapi.repository;

import com.example.bibliotecaapi.model.OcrLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcrLogRepository extends JpaRepository<OcrLog, Long> {
}

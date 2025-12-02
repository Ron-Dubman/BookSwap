package com.BookSwap.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "audit_logs")

public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // e.g., "SWAP_ACCEPTED", "LOGIN_FAILED"

    @Column(nullable = false)
    private String username;

    private LocalDateTime timestamp = LocalDateTime.now();

    private String ipAddress; // Captured from HttpServletRequest
}

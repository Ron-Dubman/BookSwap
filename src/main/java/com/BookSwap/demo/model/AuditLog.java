package com.BookSwap.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String username;

    private LocalDateTime timestamp;

    private String ipAddress;
    
    private String details;

    public AuditLog(String action, String username, String ipAddress) {
        this.action = action;
        this.username = username;
        this.ipAddress = ipAddress;
        this.timestamp = LocalDateTime.now();
    }
}

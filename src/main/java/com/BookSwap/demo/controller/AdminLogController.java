package com.BookSwap.demo.controller;

import com.BookSwap.demo.model.AuditLog;
import com.BookSwap.demo.repository.AuditLogRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminLogController {

    private static final String LOG_FILE_PATH = "logs/application.log";
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final AuditLogRepository auditLogRepository;

    public AdminLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewLogs(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            Model model) {

        // DB audit logs with simple in-memory filtering
        List<AuditLog> dbLogs = auditLogRepository.findAllByOrderByTimestampDesc().stream()
                .filter(a -> username == null || username.isBlank() || username.equalsIgnoreCase(a.getUsername()))
                .filter(a -> action == null || action.isBlank() || action.equalsIgnoreCase(a.getAction()))
                .filter(a -> parse(from) == null || !a.getTimestamp().isBefore(parse(from)))
                .filter(a -> parse(to) == null || !a.getTimestamp().isAfter(parse(to)))
                .sorted(Comparator.comparing(AuditLog::getTimestamp).reversed())
                .collect(Collectors.toList());

        // App log tail (last 200 lines)
        List<String> fileLogs = readLastLines(Paths.get(LOG_FILE_PATH), 200);

        model.addAttribute("dbLogs", dbLogs);
        model.addAttribute("fileLogs", fileLogs);

        // Echo filters back to the form
        model.addAttribute("fUsername", username);
        model.addAttribute("fAction", action);
        model.addAttribute("fFrom", from);
        model.addAttribute("fTo", to);

        return "admin-logs";
    }

    private static LocalDateTime parse(String val) {
        if (val == null || val.isBlank()) return null;
        try { return LocalDateTime.parse(val, DT); } catch (Exception e) { return null; }
    }

    private static List<String> readLastLines(Path path, int lines) {
        try {
            if (!Files.exists(path)) return List.of("Log file not found at: " + path);
            List<String> all = Files.readAllLines(path);
            int fromIndex = Math.max(0, all.size() - lines);
            return all.subList(fromIndex, all.size());
        } catch (IOException e) {
            return List.of("Error reading log file: " + e.getMessage());
        }
    }
}

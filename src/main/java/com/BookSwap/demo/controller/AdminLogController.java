package com.BookSwap.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminLogController {
    
    private static final String LOG_FILE_PATH = "logs/application.log";

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewLogs(Model model){
        try {
            Path path = Paths.get(LOG_FILE_PATH);
            if(Files.exists(path)){
                List<String> logLines = Files.readAllLines(path);
                List<String> recentLogs = logLines.stream()
                        .skip(Math.max(0, logLines.size() - 50))
                        .collect(Collectors.toList());
                model.addAttribute("logs", recentLogs);
            } else {
                model.addAttribute("logs", List.of("Log file not found at: " + LOG_FILE_PATH));
            }
        }
        catch (IOException e) {
            model.addAttribute("logs", List.of("Error reading log file: " + e.getMessage()));
        }
        return "admin-logs";
    }

}

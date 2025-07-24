package com.hrapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 🩺 Health Check Controller
 * 
 * Bu controller uygulamanın çalışıp çalışmadığını test etmek için.
 * Spring Boot'ta @RestController annotation'ı:
 * - @Controller + @ResponseBody birleşimi
 * - JSON response döndürür
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * 🎯 Basit health check endpoint
     * 
     * @GetMapping - HTTP GET isteklerini karşılar
     * ResponseEntity - HTTP response'u customize etmek için
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "HR Management System");
        response.put("version", "1.0.0");
        response.put("message", "🎉 Sistem çalışıyor!");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 🧪 Test endpoint - Spring Boot öğrenirken kullanışlı
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("✅ Test başarılı! Spring Boot çalışıyor.");
    }
} 
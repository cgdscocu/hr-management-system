package com.hrapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 🚀 HR Management System Ana Uygulama Sınıfı
 * 
 * @SpringBootApplication annotation'ı 3 önemli şeyi yapar:
 * 1. @Configuration - Bu sınıfın configuration olduğunu belirtir
 * 2. @EnableAutoConfiguration - Spring Boot'un otomatik ayarlarını aktifleştirir
 * 3. @ComponentScan - Bu package ve alt package'lerdeki componentleri tarar
 */
@SpringBootApplication
public class HrManagementSystemApplication {

    /**
     * 🎯 Ana method - Uygulamanın başlangıç noktası
     * 
     * Spring Boot uygulaması bu method ile başlar.
     * SpringApplication.run() methodu:
     * - Embedded Tomcat sunucusunu başlatır
     * - Application context'i oluşturur
     * - Auto-configuration'ları çalıştırır
     */
    public static void main(String[] args) {
        SpringApplication.run(HrManagementSystemApplication.class, args);
        
        // Uygulama başladığında konsola bilgi yazdır
        System.out.println("🎉 HR Management System başarıyla başlatıldı!");
        System.out.println("📊 Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("🔧 Actuator: http://localhost:8080/actuator");
    }
} 
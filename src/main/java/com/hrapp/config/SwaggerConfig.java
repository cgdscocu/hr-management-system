package com.hrapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 📚 Swagger/OpenAPI Configuration
 * 
 * Bu sınıf API dokümantasyonu için gerekli.
 * @Configuration - Spring'e bu sınıfın bean tanımları içerdiğini söyler
 * @Bean - Method'un döndürdüğü objeyi Spring Container'a ekler
 */
@Configuration
public class SwaggerConfig {

    /**
     * 📖 OpenAPI Bean tanımlaması
     * 
     * Bu bean Swagger UI'da görünecek bilgileri içerir
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🏢 HR Management System API")
                        .description("Kapsamlı İnsan Kaynakları Yönetim Sistemi REST API'leri")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("HR App Development Team")
                                .email("info@hrapp.com")
                                .url("https://github.com/hrapp"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }
} 
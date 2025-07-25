package com.hrapp.controller;

import com.hrapp.entity.User;
import com.hrapp.service.AuthService;
import com.hrapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 🔐 Auth Controller - Kimlik Doğrulama API'ları
 * 
 * Login, Register, Password değiştirme endpoint'leri
 * 
 * @RestController - REST API controller'ı olarak işaretler
 * @RequestMapping - Base path tanımlar
 * @RequiredArgsConstructor - Lombok: final field'lar için constructor
 * @Slf4j - Lombok: Logger
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 🔓 Kullanıcı Girişi
     * 
     * POST /auth/login
     * Body: {"email": "user@example.com", "password": "123456"}
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        log.info("Giriş denemesi - Email: {}", loginRequest.getEmail());
        
        try {
            Optional<User> userOpt = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            
            if (userOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Geçersiz email veya şifre");
                
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            User user = userOpt.get();
            
            // Son giriş zamanını güncelle
            userService.updateLastLogin(user.getEmail());
            
            // Başarılı response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Giriş başarılı");
            response.put("user", createUserResponse(user));
            
            log.info("Başarılı giriş - User ID: {}, Email: {}", user.getId(), user.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Giriş hatası - Email: {}, Hata: {}", loginRequest.getEmail(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Giriş sırasında hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📝 Kullanıcı Kaydı
     * 
     * POST /auth/register
     * Body: {"email": "user@example.com", "password": "123456", "firstName": "Ali", "lastName": "Veli"}
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest registerRequest) {
        log.info("Kayıt denemesi - Email: {}", registerRequest.getEmail());
        
        try {
            // User entity oluştur
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setPhone(registerRequest.getPhone());
            // Tenant bilgisi şimdilik null (ileride eklenir)
            
            // Kaydet (default "USER" rolü ile)
            User savedUser = authService.register(user, "USER");
            
            // Başarılı response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Kayıt başarılı");
            response.put("user", createUserResponse(savedUser));
            
            log.info("Başarılı kayıt - User ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            log.warn("Kayıt hatası - Email: {}, Hata: {}", registerRequest.getEmail(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Kayıt hatası - Email: {}, Hata: {}", registerRequest.getEmail(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Kayıt sırasında hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🔒 Şifre Değiştirme
     * 
     * POST /auth/change-password
     * Body: {"email": "user@example.com", "oldPassword": "123456", "newPassword": "newpass"}
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody ChangePasswordRequest request) {
        log.info("Şifre değiştirme denemesi - Email: {}", request.getEmail());
        
        try {
            boolean success = authService.changePassword(request.getEmail(), 
                    request.getOldPassword(), request.getNewPassword());
            
            Map<String, Object> response = new HashMap<>();
            
            if (success) {
                response.put("success", true);
                response.put("message", "Şifre başarıyla değiştirildi");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Eski şifre hatalı veya kullanıcı bulunamadı");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
        } catch (Exception e) {
            log.error("Şifre değiştirme hatası - Email: {}, Hata: {}", request.getEmail(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Şifre değiştirme sırasında hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * ✅ Email Doğrulama
     * 
     * POST /auth/verify-email
     * Body: {"email": "user@example.com"}
     */
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestBody VerifyEmailRequest request) {
        log.info("Email doğrulama - Email: {}", request.getEmail());
        
        try {
            boolean success = authService.verifyEmail(request.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            
            if (success) {
                response.put("success", true);
                response.put("message", "Email başarıyla doğrulandı");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Kullanıcı bulunamadı");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            log.error("Email doğrulama hatası - Email: {}, Hata: {}", request.getEmail(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Email doğrulama sırasında hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ===============================
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 👤 User response objesi oluştur (şifre hariç)
     */
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("email", user.getEmail());
        userResponse.put("firstName", user.getFirstName());
        userResponse.put("lastName", user.getLastName());
        userResponse.put("fullName", user.getFullName());
        userResponse.put("phone", user.getPhone());
        userResponse.put("active", user.getActive());
        userResponse.put("emailVerified", user.getEmailVerified());
        userResponse.put("roles", user.getRoles().stream()
                .map(role -> role.getName()).toList());
        
        return userResponse;
    }

    // ===============================
    // 📄 Request DTOs
    // ===============================

    public static class LoginRequest {
        private String email;
        private String password;
        
        // Getters & Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String phone;
        
        // Getters & Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class ChangePasswordRequest {
        private String email;
        private String oldPassword;
        private String newPassword;
        
        // Getters & Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class VerifyEmailRequest {
        private String email;
        
        // Getters & Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
} 
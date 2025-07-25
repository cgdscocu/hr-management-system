package com.hrapp.controller;

import com.hrapp.entity.User;
import com.hrapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 👤 User Controller - Kullanıcı Yönetimi API'ları
 * 
 * Kullanıcı CRUD işlemleri, rol atamaları
 * Admin yetkileri gerekir
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 📋 Tüm kullanıcıları getir
     * 
     * GET /users
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        log.debug("Tüm kullanıcılar isteniyor");
        
        try {
            List<User> users = userService.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Kullanıcılar başarıyla getirildi");
            response.put("data", users.stream()
                    .map(this::createUserResponse).toList());
            response.put("count", users.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Kullanıcı listesi getirme hatası: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Kullanıcılar getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🆔 ID ile kullanıcı getir
     * 
     * GET /users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        log.debug("Kullanıcı isteniyor - ID: {}", id);
        
        try {
            Optional<User> userOpt = userService.findById(id);
            
            if (userOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Kullanıcı bulunamadı");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Kullanıcı başarıyla getirildi");
            response.put("data", createUserResponse(userOpt.get()));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Kullanıcı getirme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Kullanıcı getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📧 Email ile kullanıcı getir
     * 
     * GET /users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, Object>> getUserByEmail(@PathVariable String email) {
        log.debug("Kullanıcı isteniyor - Email: {}", email);
        
        try {
            Optional<User> userOpt = userService.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Kullanıcı bulunamadı");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Kullanıcı başarıyla getirildi");
            response.put("data", createUserResponse(userOpt.get()));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Kullanıcı getirme hatası - Email: {}, Hata: {}", email, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Kullanıcı getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🎭 Role göre kullanıcıları getir
     * 
     * GET /users/role/{roleName}
     */
    @GetMapping("/role/{roleName}")
    public ResponseEntity<Map<String, Object>> getUsersByRole(@PathVariable String roleName) {
        log.debug("Rol kullanıcıları isteniyor - Rol: {}", roleName);
        
        try {
            List<User> users = userService.findByRole(roleName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rol kullanıcıları başarıyla getirildi");
            response.put("data", users.stream()
                    .map(this::createUserResponse).toList());
            response.put("count", users.size());
            response.put("role", roleName);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Rol kullanıcıları getirme hatası - Rol: {}, Hata: {}", roleName, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Rol kullanıcıları getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🔄 Kullanıcı güncelle
     * 
     * PUT /users/{id}
     * Body: {"firstName": "Yeni Ad", "lastName": "Yeni Soyad", ...}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, 
                                                          @RequestBody UpdateUserRequest request) {
        log.info("Kullanıcı güncelleniyor - ID: {}", id);
        
        try {
            // Request'ten User entity oluştur
            User userDetails = new User();
            userDetails.setFirstName(request.getFirstName());
            userDetails.setLastName(request.getLastName());
            userDetails.setPhone(request.getPhone());
            userDetails.setPassword(request.getPassword()); // null ise güncellenmez
            
            User updatedUser = userService.updateUser(id, userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Kullanıcı başarıyla güncellendi");
            response.put("data", createUserResponse(updatedUser));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Kullanıcı güncelleme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Kullanıcı güncelleme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Kullanıcı güncellenirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🎭 Kullanıcıya rol ata
     * 
     * POST /users/{id}/roles/{roleName}
     */
    @PostMapping("/{id}/roles/{roleName}")
    public ResponseEntity<Map<String, Object>> assignRole(@PathVariable Long id, 
                                                          @PathVariable String roleName) {
        log.info("Rol atanıyor - User ID: {}, Rol: {}", id, roleName);
        
        try {
            User updatedUser = userService.assignRole(id, roleName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rol başarıyla atandı");
            response.put("data", createUserResponse(updatedUser));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Rol atama hatası - User ID: {}, Rol: {}, Hata: {}", id, roleName, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Rol atama hatası - User ID: {}, Rol: {}, Hata: {}", id, roleName, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Rol atanırken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🎭 Kullanıcıdan rol kaldır
     * 
     * DELETE /users/{id}/roles/{roleName}
     */
    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<Map<String, Object>> removeRole(@PathVariable Long id, 
                                                          @PathVariable String roleName) {
        log.info("Rol kaldırılıyor - User ID: {}, Rol: {}", id, roleName);
        
        try {
            User updatedUser = userService.removeRole(id, roleName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rol başarıyla kaldırıldı");
            response.put("data", createUserResponse(updatedUser));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Rol kaldırma hatası - User ID: {}, Rol: {}, Hata: {}", id, roleName, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Rol kaldırma hatası - User ID: {}, Rol: {}, Hata: {}", id, roleName, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Rol kaldırılırken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * ✅ Kullanıcı durumunu değiştir (aktif/pasif)
     * 
     * PATCH /users/{id}/toggle-status
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Map<String, Object>> toggleUserStatus(@PathVariable Long id) {
        log.info("Kullanıcı durumu değiştiriliyor - ID: {}", id);
        
        try {
            User updatedUser = userService.toggleUserStatus(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Kullanıcı durumu başarıyla değiştirildi");
            response.put("data", createUserResponse(updatedUser));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Kullanıcı durum değiştirme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Kullanıcı durum değiştirme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Kullanıcı durumu değiştirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🗑️ Kullanıcı sil
     * 
     * DELETE /users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        log.info("Kullanıcı siliniyor - ID: {}", id);
        
        try {
            userService.deleteUser(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Kullanıcı başarıyla silindi");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Kullanıcı silme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Kullanıcı silme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Kullanıcı silinirken hata oluştu");
            
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
        userResponse.put("lastLogin", user.getLastLogin());
        userResponse.put("createdAt", user.getCreatedAt());
        userResponse.put("roles", user.getRoles().stream()
                .map(role -> Map.of(
                        "id", role.getId(),
                        "name", role.getName(),
                        "description", role.getDescription()
                )).toList());
        
        // Department bilgisi (eğer varsa)
        if (user.getDepartment() != null) {
            userResponse.put("department", Map.of(
                    "id", user.getDepartment().getId(),
                    "name", user.getDepartment().getName()
            ));
        }
        
        // Position bilgisi (eğer varsa)
        if (user.getPosition() != null) {
            userResponse.put("position", Map.of(
                    "id", user.getPosition().getId(),
                    "title", user.getPosition().getTitle(),
                    "level", user.getPosition().getLevel()
            ));
        }
        
        return userResponse;
    }

    // ===============================
    // 📄 Request DTOs
    // ===============================

    public static class UpdateUserRequest {
        private String firstName;
        private String lastName;
        private String phone;
        private String password; // Opsiyonel
        
        // Getters & Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
} 
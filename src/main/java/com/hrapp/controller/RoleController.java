package com.hrapp.controller;

import com.hrapp.entity.Role;
import com.hrapp.service.RoleService;
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
 * 🎭 Role Controller - Rol Yönetimi API'ları
 * 
 * Rol CRUD işlemleri, izin atamaları
 * Sadece Admin yetkileri
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    /**
     * 📋 Tüm rolleri getir
     * 
     * GET /roles
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        log.debug("Tüm roller isteniyor");
        
        try {
            List<Role> roles = roleService.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Roller başarıyla getirildi");
            response.put("data", roles.stream()
                    .map(this::createRoleResponse).toList());
            response.put("count", roles.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Rol listesi getirme hatası: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Roller getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * ✅ Aktif rolleri getir
     * 
     * GET /roles/active
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveRoles() {
        log.debug("Aktif roller isteniyor");
        
        try {
            List<Role> roles = roleService.findActiveRoles();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Aktif roller başarıyla getirildi");
            response.put("data", roles.stream()
                    .map(this::createRoleResponse).toList());
            response.put("count", roles.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Aktif rol listesi getirme hatası: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Aktif roller getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🌐 Global rolleri getir
     * 
     * GET /roles/global
     */
    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> getGlobalRoles() {
        log.debug("Global roller isteniyor");
        
        try {
            List<Role> roles = roleService.findGlobalRoles();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Global roller başarıyla getirildi");
            response.put("data", roles.stream()
                    .map(this::createRoleResponse).toList());
            response.put("count", roles.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Global rol listesi getirme hatası: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Global roller getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🆔 ID ile rol getir
     * 
     * GET /roles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoleById(@PathVariable Long id) {
        log.debug("Rol isteniyor - ID: {}", id);
        
        try {
            Optional<Role> roleOpt = roleService.findById(id);
            
            if (roleOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Rol bulunamadı");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rol başarıyla getirildi");
            response.put("data", createRoleResponse(roleOpt.get()));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Rol getirme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Rol getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📝 Yeni rol oluştur
     * 
     * POST /roles
     * Body: {"name": "NEW_ROLE", "description": "Yeni rol açıklaması"}
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRole(@RequestBody CreateRoleRequest request) {
        log.info("Yeni rol oluşturuluyor - İsim: {}", request.getName());
        
        try {
            // Role entity oluştur
            Role role = new Role();
            role.setName(request.getName());
            role.setDescription(request.getDescription());
            // Tenant bilgisi şimdilik null (global rol)
            
            Role savedRole = roleService.createRole(role);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rol başarıyla oluşturuldu");
            response.put("data", createRoleResponse(savedRole));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            log.warn("Rol oluşturma hatası - İsim: {}, Hata: {}", request.getName(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Rol oluşturma hatası - İsim: {}, Hata: {}", request.getName(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Rol oluşturulurken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🔄 Rol güncelle
     * 
     * PUT /roles/{id}
     * Body: {"description": "Güncellenmiş açıklama"}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRole(@PathVariable Long id, 
                                                          @RequestBody UpdateRoleRequest request) {
        log.info("Rol güncelleniyor - ID: {}", id);
        
        try {
            // Role entity oluştur
            Role roleDetails = new Role();
            roleDetails.setDescription(request.getDescription());
            
            Role updatedRole = roleService.updateRole(id, roleDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rol başarıyla güncellendi");
            response.put("data", createRoleResponse(updatedRole));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Rol güncelleme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Rol güncelleme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Rol güncellenirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🔑 Role izin ekle
     * 
     * POST /roles/{id}/permissions/{permissionName}
     */
    @PostMapping("/{id}/permissions/{permissionName}")
    public ResponseEntity<Map<String, Object>> addPermissionToRole(@PathVariable Long id, 
                                                                   @PathVariable String permissionName) {
        log.info("Role izin ekleniyor - Role ID: {}, İzin: {}", id, permissionName);
        
        try {
            Role updatedRole = roleService.addPermissionToRole(id, permissionName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "İzin başarıyla eklendi");
            response.put("data", createRoleResponse(updatedRole));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("İzin ekleme hatası - Role ID: {}, İzin: {}, Hata: {}", id, permissionName, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("İzin ekleme hatası - Role ID: {}, İzin: {}, Hata: {}", id, permissionName, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "İzin eklenirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🔑 Rolden izin kaldır
     * 
     * DELETE /roles/{id}/permissions/{permissionName}
     */
    @DeleteMapping("/{id}/permissions/{permissionName}")
    public ResponseEntity<Map<String, Object>> removePermissionFromRole(@PathVariable Long id, 
                                                                        @PathVariable String permissionName) {
        log.info("Rolden izin kaldırılıyor - Role ID: {}, İzin: {}", id, permissionName);
        
        try {
            Role updatedRole = roleService.removePermissionFromRole(id, permissionName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "İzin başarıyla kaldırıldı");
            response.put("data", createRoleResponse(updatedRole));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("İzin kaldırma hatası - Role ID: {}, İzin: {}, Hata: {}", id, permissionName, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("İzin kaldırma hatası - Role ID: {}, İzin: {}, Hata: {}", id, permissionName, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "İzin kaldırılırken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * ✅ Rol durumunu değiştir (aktif/pasif)
     * 
     * PATCH /roles/{id}/toggle-status
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Map<String, Object>> toggleRoleStatus(@PathVariable Long id) {
        log.info("Rol durumu değiştiriliyor - ID: {}", id);
        
        try {
            Role updatedRole = roleService.toggleRoleStatus(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rol durumu başarıyla değiştirildi");
            response.put("data", createRoleResponse(updatedRole));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Rol durum değiştirme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Rol durum değiştirme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Rol durumu değiştirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🗑️ Rol sil
     * 
     * DELETE /roles/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRole(@PathVariable Long id) {
        log.info("Rol siliniyor - ID: {}", id);
        
        try {
            roleService.deleteRole(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rol başarıyla silindi");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Rol silme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Rol silme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Rol silinirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ===============================
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 🎭 Role response objesi oluştur
     */
    private Map<String, Object> createRoleResponse(Role role) {
        Map<String, Object> roleResponse = new HashMap<>();
        roleResponse.put("id", role.getId());
        roleResponse.put("name", role.getName());
        roleResponse.put("description", role.getDescription());
        roleResponse.put("active", role.getActive());
        roleResponse.put("isSystemRole", role.getIsSystemRole());
        roleResponse.put("isGlobal", role.isGlobalRole());
        roleResponse.put("createdAt", role.getCreatedAt());
        
        // İzinler
        roleResponse.put("permissions", role.getPermissions().stream()
                .map(permission -> Map.of(
                        "id", permission.getId(),
                        "name", permission.getName(),
                        "description", permission.getDescription(),
                        "resource", permission.getResource(),
                        "action", permission.getAction()
                )).toList());
        
        // Tenant bilgisi (eğer varsa)
        if (role.getTenant() != null) {
            roleResponse.put("tenant", Map.of(
                    "id", role.getTenant().getId(),
                    "name", role.getTenant().getName()
            ));
        }
        
        return roleResponse;
    }

    // ===============================
    // 📄 Request DTOs
    // ===============================

    public static class CreateRoleRequest {
        private String name;
        private String description;
        
        // Getters & Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class UpdateRoleRequest {
        private String description;
        
        // Getters & Setters
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
} 
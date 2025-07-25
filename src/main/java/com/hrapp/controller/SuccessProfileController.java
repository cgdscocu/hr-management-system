package com.hrapp.controller;

import com.hrapp.entity.SuccessProfile;
import com.hrapp.entity.SuccessProfileDimension;
import com.hrapp.service.SuccessProfileService;
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
 * 🎯 Success Profile Controller - Başarı Profili Yönetimi API'ları
 * 
 * Pozisyon başarı profilleri ve dimension ilişkileri CRUD işlemleri
 * Admin ve HR Manager yetkileri gerekir
 */
@RestController
@RequestMapping("/success-profiles")
@RequiredArgsConstructor
@Slf4j
public class SuccessProfileController {

    private final SuccessProfileService successProfileService;

    /**
     * 📋 Tüm success profilleri getir
     * 
     * GET /success-profiles
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProfiles() {
        log.debug("Tüm success profiller isteniyor");
        
        try {
            List<SuccessProfile> profiles = successProfileService.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Success profiller başarıyla getirildi");
            response.put("data", profiles.stream()
                    .map(this::createProfileResponse).toList());
            response.put("count", profiles.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Success profil listesi getirme hatası: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Success profiller getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * ✅ Aktif success profilleri getir
     * 
     * GET /success-profiles/active
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveProfiles() {
        log.debug("Aktif success profiller isteniyor");
        
        try {
            List<SuccessProfile> profiles = successProfileService.findActiveProfiles();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Aktif success profiller başarıyla getirildi");
            response.put("data", profiles.stream()
                    .map(this::createProfileResponse).toList());
            response.put("count", profiles.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Aktif success profil listesi getirme hatası: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Aktif success profiller getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🏢 Tenant'a göre success profilleri getir
     * 
     * GET /success-profiles/tenant/{tenantId}
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Map<String, Object>> getProfilesByTenant(@PathVariable Long tenantId) {
        log.debug("Tenant success profilleri isteniyor - Tenant ID: {}", tenantId);
        
        try {
            List<SuccessProfile> profiles = successProfileService.findByTenant(tenantId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tenant success profilleri başarıyla getirildi");
            response.put("data", profiles.stream()
                    .map(this::createProfileResponse).toList());
            response.put("count", profiles.size());
            response.put("tenantId", tenantId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Tenant success profil listesi getirme hatası - Tenant ID: {}, Hata: {}", tenantId, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Tenant success profilleri getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 💼 Pozisyona göre success profilleri getir
     * 
     * GET /success-profiles/position/{positionId}
     */
    @GetMapping("/position/{positionId}")
    public ResponseEntity<Map<String, Object>> getProfilesByPosition(@PathVariable Long positionId) {
        log.debug("Pozisyon success profilleri isteniyor - Position ID: {}", positionId);
        
        try {
            List<SuccessProfile> profiles = successProfileService.findByPosition(positionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pozisyon success profilleri başarıyla getirildi");
            response.put("data", profiles.stream()
                    .map(this::createProfileResponse).toList());
            response.put("count", profiles.size());
            response.put("positionId", positionId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Pozisyon success profil listesi getirme hatası - Position ID: {}, Hata: {}", positionId, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Pozisyon success profilleri getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🆔 ID ile success profil getir
     * 
     * GET /success-profiles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProfileById(@PathVariable Long id) {
        log.debug("Success profil isteniyor - ID: {}", id);
        
        try {
            Optional<SuccessProfile> profileOpt = successProfileService.findById(id);
            
            if (profileOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Success profil bulunamadı");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Success profil başarıyla getirildi");
            response.put("data", createProfileResponse(profileOpt.get()));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Success profil getirme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Success profil getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📊 Success profile dimension'larını getir
     * 
     * GET /success-profiles/{id}/dimensions
     */
    @GetMapping("/{id}/dimensions")
    public ResponseEntity<Map<String, Object>> getProfileDimensions(@PathVariable Long id) {
        log.debug("Success profil dimension'ları isteniyor - Profile ID: {}", id);
        
        try {
            List<SuccessProfileDimension> dimensions = successProfileService.getProfileDimensions(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profil dimension'ları başarıyla getirildi");
            response.put("data", dimensions.stream()
                    .map(this::createProfileDimensionResponse).toList());
            response.put("count", dimensions.size());
            response.put("profileId", id);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Profil dimension listesi getirme hatası - Profile ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Profil dimension'ları getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📝 Yeni success profil oluştur
     * 
     * POST /success-profiles
     * Body: {"name": "Yazılım Geliştirici Profili", "description": "...", "profileType": "POSITION_SPECIFIC", ...}
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProfile(@RequestBody CreateProfileRequest request) {
        log.info("Yeni success profil oluşturuluyor - İsim: {}", request.getName());
        
        try {
            // SuccessProfile entity oluştur
            SuccessProfile profile = new SuccessProfile();
            profile.setName(request.getName());
            profile.setDescription(request.getDescription());
            if (request.getProfileType() != null) {
                profile.setProfileType(SuccessProfile.ProfileType.valueOf(request.getProfileType().toUpperCase()));
            }
            profile.setMinSuccessScore(request.getMinSuccessScore());
            profile.setTargetSuccessScore(request.getTargetSuccessScore());
            
            // Tenant, Position, Department bilgileri şimdilik null (ileride JWT'den alınacak)
            
            SuccessProfile savedProfile = successProfileService.createProfile(profile);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Success profil başarıyla oluşturuldu");
            response.put("data", createProfileResponse(savedProfile));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Success profil oluşturma hatası - Geçersiz parametre: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Geçersiz parametre: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (RuntimeException e) {
            log.warn("Success profil oluşturma hatası - İsim: {}, Hata: {}", request.getName(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Success profil oluşturma hatası - İsim: {}, Hata: {}", request.getName(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Success profil oluşturulurken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📊 Profile dimension ekle
     * 
     * POST /success-profiles/{id}/dimensions
     * Body: {"dimensionId": 1, "weight": 15.0, "minScore": 3.0, "targetScore": 4.0, "isCritical": false}
     */
    @PostMapping("/{id}/dimensions")
    public ResponseEntity<Map<String, Object>> addDimensionToProfile(@PathVariable Long id, 
                                                                     @RequestBody AddDimensionRequest request) {
        log.info("Profile dimension ekleniyor - Profile ID: {}, Dimension ID: {}", id, request.getDimensionId());
        
        try {
            SuccessProfile updatedProfile = successProfileService.addDimensionToProfile(
                id, request.getDimensionId(), request.getWeight(), 
                request.getMinScore(), request.getTargetScore(), request.getIsCritical()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Dimension başarıyla profile eklendi");
            response.put("data", createProfileResponse(updatedProfile));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Profile dimension ekleme hatası - Profile ID: {}, Dimension ID: {}, Hata: {}", 
                    id, request.getDimensionId(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Profile dimension ekleme hatası - Profile ID: {}, Dimension ID: {}, Hata: {}", 
                    id, request.getDimensionId(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Dimension profile eklenirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📊 Profile dimension kaldır
     * 
     * DELETE /success-profiles/{profileId}/dimensions/{dimensionId}
     */
    @DeleteMapping("/{profileId}/dimensions/{dimensionId}")
    public ResponseEntity<Map<String, Object>> removeDimensionFromProfile(@PathVariable Long profileId, 
                                                                          @PathVariable Long dimensionId) {
        log.info("Profile'dan dimension kaldırılıyor - Profile ID: {}, Dimension ID: {}", profileId, dimensionId);
        
        try {
            SuccessProfile updatedProfile = successProfileService.removeDimensionFromProfile(profileId, dimensionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Dimension başarıyla profile'dan kaldırıldı");
            response.put("data", createProfileResponse(updatedProfile));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Profile dimension kaldırma hatası - Profile ID: {}, Dimension ID: {}, Hata: {}", 
                    profileId, dimensionId, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Profile dimension kaldırma hatası - Profile ID: {}, Dimension ID: {}, Hata: {}", 
                    profileId, dimensionId, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Dimension profile'dan kaldırılırken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🔄 Success profil güncelle
     * 
     * PUT /success-profiles/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProfile(@PathVariable Long id, 
                                                             @RequestBody UpdateProfileRequest request) {
        log.info("Success profil güncelleniyor - ID: {}", id);
        
        try {
            SuccessProfile profileDetails = new SuccessProfile();
            profileDetails.setName(request.getName());
            profileDetails.setDescription(request.getDescription());
            if (request.getProfileType() != null) {
                profileDetails.setProfileType(SuccessProfile.ProfileType.valueOf(request.getProfileType().toUpperCase()));
            }
            profileDetails.setMinSuccessScore(request.getMinSuccessScore());
            profileDetails.setTargetSuccessScore(request.getTargetSuccessScore());
            
            SuccessProfile updatedProfile = successProfileService.updateProfile(id, profileDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Success profil başarıyla güncellendi");
            response.put("data", createProfileResponse(updatedProfile));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Success profil güncelleme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Success profil güncelleme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Success profil güncellenirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * ✅ Success profil durumunu değiştir (aktif/pasif)
     * 
     * PATCH /success-profiles/{id}/toggle-status
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Map<String, Object>> toggleProfileStatus(@PathVariable Long id) {
        log.info("Success profil durumu değiştiriliyor - ID: {}", id);
        
        try {
            SuccessProfile updatedProfile = successProfileService.toggleProfileStatus(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Success profil durumu başarıyla değiştirildi");
            response.put("data", createProfileResponse(updatedProfile));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Success profil durum değiştirme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Success profil durum değiştirme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Success profil durumu değiştirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🗑️ Success profil sil
     * 
     * DELETE /success-profiles/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProfile(@PathVariable Long id) {
        log.info("Success profil siliniyor - ID: {}", id);
        
        try {
            successProfileService.deleteProfile(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Success profil başarıyla silindi");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Success profil silme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Success profil silme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Success profil silinirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🔍 Success profil arama
     * 
     * GET /success-profiles/search?name=yazılım
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProfiles(@RequestParam String name) {
        log.debug("Success profil arama - İsim: {}", name);
        
        try {
            List<SuccessProfile> profiles = successProfileService.searchByName(name);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Success profil arama tamamlandı");
            response.put("data", profiles.stream()
                    .map(this::createProfileResponse).toList());
            response.put("count", profiles.size());
            response.put("searchTerm", name);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Success profil arama hatası - İsim: {}, Hata: {}", name, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Success profil arama sırasında hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ===============================
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 📊 SuccessProfile response objesi oluştur
     */
    private Map<String, Object> createProfileResponse(SuccessProfile profile) {
        Map<String, Object> profileResponse = new HashMap<>();
        profileResponse.put("id", profile.getId());
        profileResponse.put("name", profile.getName());
        profileResponse.put("description", profile.getDescription());
        profileResponse.put("profileType", profile.getProfileType().name());
        profileResponse.put("profileTypeDisplayName", profile.getProfileTypeDisplayName());
        profileResponse.put("minSuccessScore", profile.getMinSuccessScore());
        profileResponse.put("targetSuccessScore", profile.getTargetSuccessScore());
        profileResponse.put("active", profile.getActive());
        profileResponse.put("isSystemProfile", profile.getIsSystemProfile());
        profileResponse.put("totalWeight", profile.getTotalWeight());
        profileResponse.put("activeDimensionCount", profile.getActiveDimensionCount());
        profileResponse.put("profileSummary", profile.getProfileSummary());
        profileResponse.put("createdAt", profile.getCreatedAt());
        profileResponse.put("updatedAt", profile.getUpdatedAt());
        
        // Tenant bilgisi
        if (profile.getTenant() != null) {
            profileResponse.put("tenant", Map.of(
                    "id", profile.getTenant().getId(),
                    "name", profile.getTenant().getName()
            ));
        }
        
        // Position bilgisi
        if (profile.getPosition() != null) {
            profileResponse.put("position", Map.of(
                    "id", profile.getPosition().getId(),
                    "title", profile.getPosition().getTitle()
            ));
        }
        
        // Department bilgisi
        if (profile.getDepartment() != null) {
            profileResponse.put("department", Map.of(
                    "id", profile.getDepartment().getId(),
                    "name", profile.getDepartment().getName()
            ));
        }
        
        // Oluşturan kullanıcı bilgisi
        if (profile.getCreatedBy() != null) {
            profileResponse.put("createdBy", Map.of(
                    "id", profile.getCreatedBy().getId(),
                    "fullName", profile.getCreatedBy().getFullName()
            ));
        }
        
        return profileResponse;
    }

    /**
     * 📊 SuccessProfileDimension response objesi oluştur
     */
    private Map<String, Object> createProfileDimensionResponse(SuccessProfileDimension spd) {
        Map<String, Object> spdResponse = new HashMap<>();
        spdResponse.put("id", spd.getId());
        spdResponse.put("weight", spd.getWeight());
        spdResponse.put("minScore", spd.getMinScore());
        spdResponse.put("targetScore", spd.getTargetScore());
        spdResponse.put("isCritical", spd.getIsCritical());
        spdResponse.put("active", spd.getActive());
        spdResponse.put("notes", spd.getNotes());
        spdResponse.put("displayOrder", spd.getDisplayOrder());
        spdResponse.put("combinationName", spd.getCombinationName());
        
        // Dimension bilgisi
        if (spd.getDimension() != null) {
            spdResponse.put("dimension", Map.of(
                    "id", spd.getDimension().getId(),
                    "name", spd.getDimension().getName(),
                    "category", spd.getDimension().getCategory().name(),
                    "scaleType", spd.getDimension().getScaleType().name()
            ));
        }
        
        return spdResponse;
    }

    // ===============================
    // 📄 Request DTOs
    // ===============================

    public static class CreateProfileRequest {
        private String name;
        private String description;
        private String profileType = "POSITION_SPECIFIC";
        private Double minSuccessScore = 70.0;
        private Double targetSuccessScore = 85.0;
        
        // Getters & Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getProfileType() { return profileType; }
        public void setProfileType(String profileType) { this.profileType = profileType; }
        public Double getMinSuccessScore() { return minSuccessScore; }
        public void setMinSuccessScore(Double minSuccessScore) { this.minSuccessScore = minSuccessScore; }
        public Double getTargetSuccessScore() { return targetSuccessScore; }
        public void setTargetSuccessScore(Double targetSuccessScore) { this.targetSuccessScore = targetSuccessScore; }
    }

    public static class UpdateProfileRequest {
        private String name;
        private String description;
        private String profileType;
        private Double minSuccessScore;
        private Double targetSuccessScore;
        
        // Getters & Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getProfileType() { return profileType; }
        public void setProfileType(String profileType) { this.profileType = profileType; }
        public Double getMinSuccessScore() { return minSuccessScore; }
        public void setMinSuccessScore(Double minSuccessScore) { this.minSuccessScore = minSuccessScore; }
        public Double getTargetSuccessScore() { return targetSuccessScore; }
        public void setTargetSuccessScore(Double targetSuccessScore) { this.targetSuccessScore = targetSuccessScore; }
    }

    public static class AddDimensionRequest {
        private Long dimensionId;
        private Double weight = 10.0;
        private Double minScore = 3.0;
        private Double targetScore = 4.0;
        private Boolean isCritical = false;
        
        // Getters & Setters
        public Long getDimensionId() { return dimensionId; }
        public void setDimensionId(Long dimensionId) { this.dimensionId = dimensionId; }
        public Double getWeight() { return weight; }
        public void setWeight(Double weight) { this.weight = weight; }
        public Double getMinScore() { return minScore; }
        public void setMinScore(Double minScore) { this.minScore = minScore; }
        public Double getTargetScore() { return targetScore; }
        public void setTargetScore(Double targetScore) { this.targetScore = targetScore; }
        public Boolean getIsCritical() { return isCritical; }
        public void setIsCritical(Boolean critical) { isCritical = critical; }
    }
} 
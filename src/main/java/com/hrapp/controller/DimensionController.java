package com.hrapp.controller;

import com.hrapp.entity.Dimension;
import com.hrapp.service.DimensionService;
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
 * 📊 Dimension Controller - Boyut Yönetimi API'ları
 * 
 * Performans değerlendirme boyutları CRUD işlemleri
 * Admin yetkileri gerekir
 */
@RestController
@RequestMapping("/dimensions")
@RequiredArgsConstructor
@Slf4j
public class DimensionController {

    private final DimensionService dimensionService;

    /**
     * 📋 Tüm boyutları getir
     * 
     * GET /dimensions
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDimensions() {
        log.debug("Tüm boyutlar isteniyor");
        
        try {
            List<Dimension> dimensions = dimensionService.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Boyutlar başarıyla getirildi");
            response.put("data", dimensions.stream()
                    .map(this::createDimensionResponse).toList());
            response.put("count", dimensions.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Boyut listesi getirme hatası: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Boyutlar getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * ✅ Aktif boyutları getir
     * 
     * GET /dimensions/active
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveDimensions() {
        log.debug("Aktif boyutlar isteniyor");
        
        try {
            List<Dimension> dimensions = dimensionService.findActiveDimensions();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Aktif boyutlar başarıyla getirildi");
            response.put("data", dimensions.stream()
                    .map(this::createDimensionResponse).toList());
            response.put("count", dimensions.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Aktif boyut listesi getirme hatası: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Aktif boyutlar getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🏢 Tenant'a göre boyutları getir
     * 
     * GET /dimensions/tenant/{tenantId}
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Map<String, Object>> getDimensionsByTenant(@PathVariable Long tenantId) {
        log.debug("Tenant boyutları isteniyor - Tenant ID: {}", tenantId);
        
        try {
            List<Dimension> dimensions = dimensionService.findByTenantOrderByDisplayOrder(tenantId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tenant boyutları başarıyla getirildi");
            response.put("data", dimensions.stream()
                    .map(this::createDimensionResponse).toList());
            response.put("count", dimensions.size());
            response.put("tenantId", tenantId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Tenant boyut listesi getirme hatası - Tenant ID: {}, Hata: {}", tenantId, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Tenant boyutları getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📊 Kategoriye göre boyutları getir
     * 
     * GET /dimensions/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getDimensionsByCategory(@PathVariable String category) {
        log.debug("Kategori boyutları isteniyor - Kategori: {}", category);
        
        try {
            Dimension.DimensionCategory dimensionCategory = Dimension.DimensionCategory.valueOf(category.toUpperCase());
            List<Dimension> dimensions = dimensionService.findByCategory(dimensionCategory);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Kategori boyutları başarıyla getirildi");
            response.put("data", dimensions.stream()
                    .map(this::createDimensionResponse).toList());
            response.put("count", dimensions.size());
            response.put("category", category);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Geçersiz kategori - Kategori: {}", category);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Geçersiz kategori: " + category);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Kategori boyut listesi getirme hatası - Kategori: {}, Hata: {}", category, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Kategori boyutları getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🆔 ID ile boyut getir
     * 
     * GET /dimensions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDimensionById(@PathVariable Long id) {
        log.debug("Boyut isteniyor - ID: {}", id);
        
        try {
            Optional<Dimension> dimensionOpt = dimensionService.findById(id);
            
            if (dimensionOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Boyut bulunamadı");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Boyut başarıyla getirildi");
            response.put("data", createDimensionResponse(dimensionOpt.get()));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Boyut getirme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Boyut getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📝 Yeni boyut oluştur
     * 
     * POST /dimensions
     * Body: {"name": "Yeni Boyut", "description": "Açıklama", "category": "TECHNICAL", ...}
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDimension(@RequestBody CreateDimensionRequest request) {
        log.info("Yeni boyut oluşturuluyor - İsim: {}", request.getName());
        
        try {
            // Dimension entity oluştur
            Dimension dimension = new Dimension();
            dimension.setName(request.getName());
            dimension.setDescription(request.getDescription());
            dimension.setCategory(Dimension.DimensionCategory.valueOf(request.getCategory().toUpperCase()));
            dimension.setScaleType(Dimension.ScaleType.valueOf(request.getScaleType().toUpperCase()));
            dimension.setWeight(request.getWeight());
            dimension.setDisplayOrder(request.getDisplayOrder());
            
            // Tenant bilgisi şimdilik null (ileride JWT'den alınacak)
            
            Dimension savedDimension = dimensionService.createDimension(dimension);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Boyut başarıyla oluşturuldu");
            response.put("data", createDimensionResponse(savedDimension));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Boyut oluşturma hatası - Geçersiz parametre: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Geçersiz parametre: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (RuntimeException e) {
            log.warn("Boyut oluşturma hatası - İsim: {}, Hata: {}", request.getName(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Boyut oluşturma hatası - İsim: {}, Hata: {}", request.getName(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Boyut oluşturulurken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🔄 Boyut güncelle
     * 
     * PUT /dimensions/{id}
     * Body: {"name": "Güncellenmiş İsim", "description": "Yeni açıklama", ...}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDimension(@PathVariable Long id, 
                                                               @RequestBody UpdateDimensionRequest request) {
        log.info("Boyut güncelleniyor - ID: {}", id);
        
        try {
            // Dimension entity oluştur
            Dimension dimensionDetails = new Dimension();
            dimensionDetails.setName(request.getName());
            dimensionDetails.setDescription(request.getDescription());
            if (request.getCategory() != null) {
                dimensionDetails.setCategory(Dimension.DimensionCategory.valueOf(request.getCategory().toUpperCase()));
            }
            if (request.getScaleType() != null) {
                dimensionDetails.setScaleType(Dimension.ScaleType.valueOf(request.getScaleType().toUpperCase()));
            }
            dimensionDetails.setWeight(request.getWeight());
            dimensionDetails.setDisplayOrder(request.getDisplayOrder());
            dimensionDetails.setMinValue(request.getMinValue());
            dimensionDetails.setMaxValue(request.getMaxValue());
            dimensionDetails.setScaleDescriptions(request.getScaleDescriptions());
            
            Dimension updatedDimension = dimensionService.updateDimension(id, dimensionDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Boyut başarıyla güncellendi");
            response.put("data", createDimensionResponse(updatedDimension));
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Boyut güncelleme hatası - Geçersiz parametre: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Geçersiz parametre: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (RuntimeException e) {
            log.warn("Boyut güncelleme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Boyut güncelleme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Boyut güncellenirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * ✅ Boyut durumunu değiştir (aktif/pasif)
     * 
     * PATCH /dimensions/{id}/toggle-status
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Map<String, Object>> toggleDimensionStatus(@PathVariable Long id) {
        log.info("Boyut durumu değiştiriliyor - ID: {}", id);
        
        try {
            Dimension updatedDimension = dimensionService.toggleDimensionStatus(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Boyut durumu başarıyla değiştirildi");
            response.put("data", createDimensionResponse(updatedDimension));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Boyut durum değiştirme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Boyut durum değiştirme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Boyut durumu değiştirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📊 Display order güncelle
     * 
     * PATCH /dimensions/{id}/order
     * Body: {"displayOrder": 5}
     */
    @PatchMapping("/{id}/order")
    public ResponseEntity<Map<String, Object>> updateDisplayOrder(@PathVariable Long id, 
                                                                  @RequestBody UpdateOrderRequest request) {
        log.info("Boyut sıralaması güncelleniyor - ID: {}, Yeni Sıra: {}", id, request.getDisplayOrder());
        
        try {
            Dimension updatedDimension = dimensionService.updateDisplayOrder(id, request.getDisplayOrder());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Boyut sıralaması başarıyla güncellendi");
            response.put("data", createDimensionResponse(updatedDimension));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Boyut sıralama güncelleme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Boyut sıralama güncelleme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Boyut sıralaması güncellenirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🗑️ Boyut sil
     * 
     * DELETE /dimensions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDimension(@PathVariable Long id) {
        log.info("Boyut siliniyor - ID: {}", id);
        
        try {
            dimensionService.deleteDimension(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Boyut başarıyla silindi");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Boyut silme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Boyut silme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Boyut silinirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🔍 Boyut arama
     * 
     * GET /dimensions/search?name=teknik
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchDimensions(@RequestParam String name) {
        log.debug("Boyut arama - İsim: {}", name);
        
        try {
            List<Dimension> dimensions = dimensionService.searchByName(name);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Boyut arama tamamlandı");
            response.put("data", dimensions.stream()
                    .map(this::createDimensionResponse).toList());
            response.put("count", dimensions.size());
            response.put("searchTerm", name);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Boyut arama hatası - İsim: {}, Hata: {}", name, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Boyut arama sırasında hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ===============================
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 📊 Dimension response objesi oluştur
     */
    private Map<String, Object> createDimensionResponse(Dimension dimension) {
        Map<String, Object> dimensionResponse = new HashMap<>();
        dimensionResponse.put("id", dimension.getId());
        dimensionResponse.put("name", dimension.getName());
        dimensionResponse.put("description", dimension.getDescription());
        dimensionResponse.put("category", dimension.getCategory().name());
        dimensionResponse.put("categoryDisplayName", dimension.getCategoryDisplayName());
        dimensionResponse.put("scaleType", dimension.getScaleType().name());
        dimensionResponse.put("minValue", dimension.getMinValue());
        dimensionResponse.put("maxValue", dimension.getMaxValue());
        dimensionResponse.put("scaleDescriptions", dimension.getScaleDescriptions());
        dimensionResponse.put("weight", dimension.getWeight());
        dimensionResponse.put("displayOrder", dimension.getDisplayOrder());
        dimensionResponse.put("active", dimension.getActive());
        dimensionResponse.put("isSystemDimension", dimension.getIsSystemDimension());
        dimensionResponse.put("createdAt", dimension.getCreatedAt());
        dimensionResponse.put("updatedAt", dimension.getUpdatedAt());
        
        // Tenant bilgisi (eğer varsa)
        if (dimension.getTenant() != null) {
            dimensionResponse.put("tenant", Map.of(
                    "id", dimension.getTenant().getId(),
                    "name", dimension.getTenant().getName()
            ));
        }
        
        // Oluşturan kullanıcı bilgisi (eğer varsa)
        if (dimension.getCreatedBy() != null) {
            dimensionResponse.put("createdBy", Map.of(
                    "id", dimension.getCreatedBy().getId(),
                    "fullName", dimension.getCreatedBy().getFullName()
            ));
        }
        
        return dimensionResponse;
    }

    // ===============================
    // 📄 Request DTOs
    // ===============================

    public static class CreateDimensionRequest {
        private String name;
        private String description;
        private String category = "CORE_COMPETENCY";
        private String scaleType = "LIKERT_5";
        private Double weight = 10.0;
        private Integer displayOrder;
        
        // Getters & Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getScaleType() { return scaleType; }
        public void setScaleType(String scaleType) { this.scaleType = scaleType; }
        public Double getWeight() { return weight; }
        public void setWeight(Double weight) { this.weight = weight; }
        public Integer getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    }

    public static class UpdateDimensionRequest {
        private String name;
        private String description;
        private String category;
        private String scaleType;
        private Double weight;
        private Integer displayOrder;
        private Double minValue;
        private Double maxValue;
        private String scaleDescriptions;
        
        // Getters & Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getScaleType() { return scaleType; }
        public void setScaleType(String scaleType) { this.scaleType = scaleType; }
        public Double getWeight() { return weight; }
        public void setWeight(Double weight) { this.weight = weight; }
        public Integer getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
        public Double getMinValue() { return minValue; }
        public void setMinValue(Double minValue) { this.minValue = minValue; }
        public Double getMaxValue() { return maxValue; }
        public void setMaxValue(Double maxValue) { this.maxValue = maxValue; }
        public String getScaleDescriptions() { return scaleDescriptions; }
        public void setScaleDescriptions(String scaleDescriptions) { this.scaleDescriptions = scaleDescriptions; }
    }

    public static class UpdateOrderRequest {
        private Integer displayOrder;
        
        // Getters & Setters
        public Integer getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    }
} 
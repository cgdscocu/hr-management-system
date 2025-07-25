package com.hrapp.controller;

import com.hrapp.entity.Survey;
import com.hrapp.entity.Question;
import com.hrapp.service.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 📋 Survey Controller - Anket Yönetimi API'ları
 * 
 * Anket CRUD işlemleri ve soru yönetimi
 * Admin yetkileri gerekir
 */
@RestController
@RequestMapping("/surveys")
@RequiredArgsConstructor
@Slf4j
public class SurveyController {

    private final SurveyService surveyService;

    /**
     * 📋 Tüm anketleri getir
     * 
     * GET /surveys
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSurveys() {
        log.debug("Tüm anketler isteniyor");
        
        try {
            List<Survey> surveys = surveyService.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Anketler başarıyla getirildi");
            response.put("data", surveys.stream()
                    .map(this::createSurveyResponse).toList());
            response.put("count", surveys.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Anket listesi getirme hatası: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Anketler getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * ✅ Aktif anketleri getir
     * 
     * GET /surveys/active
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveSurveys() {
        log.debug("Aktif anketler isteniyor");
        
        try {
            List<Survey> surveys = surveyService.findCurrentlyActiveSurveys();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Aktif anketler başarıyla getirildi");
            response.put("data", surveys.stream()
                    .map(this::createSurveyResponse).toList());
            response.put("count", surveys.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Aktif anket listesi getirme hatası: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Aktif anketler getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🏢 Tenant'a göre anketleri getir
     * 
     * GET /surveys/tenant/{tenantId}
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Map<String, Object>> getSurveysByTenant(@PathVariable Long tenantId) {
        log.debug("Tenant anketleri isteniyor - Tenant ID: {}", tenantId);
        
        try {
            List<Survey> surveys = surveyService.findByTenant(tenantId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tenant anketleri başarıyla getirildi");
            response.put("data", surveys.stream()
                    .map(this::createSurveyResponse).toList());
            response.put("count", surveys.size());
            response.put("tenantId", tenantId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Tenant anket listesi getirme hatası - Tenant ID: {}, Hata: {}", tenantId, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Tenant anketleri getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🆔 ID ile anket getir
     * 
     * GET /surveys/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSurveyById(@PathVariable Long id) {
        log.debug("Anket isteniyor - ID: {}", id);
        
        try {
            Optional<Survey> surveyOpt = surveyService.findById(id);
            
            if (surveyOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Anket bulunamadı");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Anket başarıyla getirildi");
            response.put("data", createSurveyResponse(surveyOpt.get()));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Anket getirme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Anket getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📊 Anket sorularını getir
     * 
     * GET /surveys/{id}/questions
     */
    @GetMapping("/{id}/questions")
    public ResponseEntity<Map<String, Object>> getSurveyQuestions(@PathVariable Long id) {
        log.debug("Anket soruları isteniyor - Survey ID: {}", id);
        
        try {
            List<Question> questions = surveyService.getSurveyQuestions(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Anket soruları başarıyla getirildi");
            response.put("data", questions.stream()
                    .map(this::createQuestionResponse).toList());
            response.put("count", questions.size());
            response.put("surveyId", id);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Anket soru listesi getirme hatası - Survey ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Anket soruları getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📊 Anket istatistikleri getir
     * 
     * GET /surveys/{id}/statistics
     */
    @GetMapping("/{id}/statistics")
    public ResponseEntity<Map<String, Object>> getSurveyStatistics(@PathVariable Long id) {
        log.debug("Anket istatistikleri isteniyor - Survey ID: {}", id);
        
        try {
            Map<String, Object> statistics = surveyService.getSurveyStatistics(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Anket istatistikleri başarıyla getirildi");
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Anket istatistikleri getirme hatası - Survey ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Anket istatistikleri getirme hatası - Survey ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Anket istatistikleri getirilirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📝 Yeni anket oluştur
     * 
     * POST /surveys
     * Body: {"title": "Yeni Anket", "description": "...", "surveyType": "PERFORMANCE", ...}
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSurvey(@RequestBody CreateSurveyRequest request) {
        log.info("Yeni anket oluşturuluyor - Başlık: {}", request.getTitle());
        
        try {
            // Survey entity oluştur
            Survey survey = new Survey();
            survey.setTitle(request.getTitle());
            survey.setDescription(request.getDescription());
            if (request.getSurveyType() != null) {
                survey.setSurveyType(Survey.SurveyType.valueOf(request.getSurveyType().toUpperCase()));
            }
            if (request.getTargetGroup() != null) {
                survey.setTargetGroup(Survey.TargetGroup.valueOf(request.getTargetGroup().toUpperCase()));
            }
            survey.setEstimatedDuration(request.getEstimatedDuration());
            survey.setIsAnonymous(request.getIsAnonymous());
            survey.setIsRepeatable(request.getIsRepeatable());
            survey.setMaxResponses(request.getMaxResponses());
            survey.setSendEmailNotification(request.getSendEmailNotification());
            
            if (request.getStartDate() != null) {
                survey.setStartDate(request.getStartDate());
            }
            if (request.getEndDate() != null) {
                survey.setEndDate(request.getEndDate());
            }
            
            // Tenant bilgisi şimdilik null (ileride JWT'den alınacak)
            
            Survey savedSurvey = surveyService.createSurvey(survey);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Anket başarıyla oluşturuldu");
            response.put("data", createSurveyResponse(savedSurvey));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Anket oluşturma hatası - Geçersiz parametre: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Geçersiz parametre: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (RuntimeException e) {
            log.warn("Anket oluşturma hatası - Başlık: {}, Hata: {}", request.getTitle(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Anket oluşturma hatası - Başlık: {}, Hata: {}", request.getTitle(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Anket oluşturulurken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📊 Ankete soru ekle
     * 
     * POST /surveys/{id}/questions
     * Body: {"questionText": "Soru metni?", "questionType": "LIKERT_5", "required": true, ...}
     */
    @PostMapping("/{id}/questions")
    public ResponseEntity<Map<String, Object>> addQuestionToSurvey(@PathVariable Long id, 
                                                                   @RequestBody AddQuestionRequest request) {
        log.info("Ankete soru ekleniyor - Survey ID: {}", id);
        
        try {
            Question question = new Question();
            question.setQuestionText(request.getQuestionText());
            question.setHelpText(request.getHelpText());
            if (request.getQuestionType() != null) {
                question.setQuestionType(Question.QuestionType.valueOf(request.getQuestionType().toUpperCase()));
            }
            question.setRequired(request.getRequired());
            question.setDisplayOrder(request.getDisplayOrder());
            question.setWeight(request.getWeight());
            
            Question savedQuestion = surveyService.addQuestionToSurvey(id, question);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Soru başarıyla ankete eklendi");
            response.put("data", createQuestionResponse(savedQuestion));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            log.warn("Anket soru ekleme hatası - Survey ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Anket soru ekleme hatası - Survey ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Soru eklenirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 📊 Anket durumunu güncelle
     * 
     * PATCH /surveys/{id}/status
     * Body: {"status": "ACTIVE"}
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateSurveyStatus(@PathVariable Long id, 
                                                                  @RequestBody UpdateStatusRequest request) {
        log.info("Anket durumu güncelleniyor - Survey ID: {}, Yeni Durum: {}", id, request.getStatus());
        
        try {
            Survey.SurveyStatus newStatus = Survey.SurveyStatus.valueOf(request.getStatus().toUpperCase());
            Survey updatedSurvey = surveyService.updateSurveyStatus(id, newStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Anket durumu başarıyla güncellendi");
            response.put("data", createSurveyResponse(updatedSurvey));
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Anket durum güncelleme hatası - Geçersiz durum: {}", request.getStatus());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Geçersiz anket durumu: " + request.getStatus());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (RuntimeException e) {
            log.warn("Anket durum güncelleme hatası - Survey ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Anket durum güncelleme hatası - Survey ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Anket durumu güncellenirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🗑️ Anket sil
     * 
     * DELETE /surveys/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSurvey(@PathVariable Long id) {
        log.info("Anket siliniyor - ID: {}", id);
        
        try {
            surveyService.deleteSurvey(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Anket başarıyla silindi");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.warn("Anket silme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Anket silme hatası - ID: {}, Hata: {}", id, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Anket silinirken hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 🔍 Anket arama
     * 
     * GET /surveys/search?title=performans
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchSurveys(@RequestParam String title) {
        log.debug("Anket arama - Başlık: {}", title);
        
        try {
            List<Survey> surveys = surveyService.searchSurveys(title);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Anket arama tamamlandı");
            response.put("data", surveys.stream()
                    .map(this::createSurveyResponse).toList());
            response.put("count", surveys.size());
            response.put("searchTerm", title);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Anket arama hatası - Başlık: {}, Hata: {}", title, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Anket arama sırasında hata oluştu");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ===============================
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 📊 Survey response objesi oluştur
     */
    private Map<String, Object> createSurveyResponse(Survey survey) {
        Map<String, Object> surveyResponse = new HashMap<>();
        surveyResponse.put("id", survey.getId());
        surveyResponse.put("title", survey.getTitle());
        surveyResponse.put("description", survey.getDescription());
        surveyResponse.put("surveyType", survey.getSurveyType().name());
        surveyResponse.put("surveyTypeDisplayName", survey.getSurveyTypeDisplayName());
        surveyResponse.put("status", survey.getStatus().name());
        surveyResponse.put("statusDisplayName", survey.getStatusDisplayName());
        surveyResponse.put("targetGroup", survey.getTargetGroup().name());
        surveyResponse.put("targetGroupDisplayName", survey.getTargetGroupDisplayName());
        surveyResponse.put("startDate", survey.getStartDate());
        surveyResponse.put("endDate", survey.getEndDate());
        surveyResponse.put("estimatedDuration", survey.getEstimatedDuration());
        surveyResponse.put("isAnonymous", survey.getIsAnonymous());
        surveyResponse.put("isRepeatable", survey.getIsRepeatable());
        surveyResponse.put("active", survey.getActive());
        surveyResponse.put("isSystemSurvey", survey.getIsSystemSurvey());
        surveyResponse.put("maxResponses", survey.getMaxResponses());
        surveyResponse.put("sendEmailNotification", survey.getSendEmailNotification());
        surveyResponse.put("questionCount", survey.getQuestionCount());
        surveyResponse.put("responseCount", survey.getResponseCount());
        surveyResponse.put("completionRate", survey.getCompletionRate());
        surveyResponse.put("isCurrentlyActive", survey.isCurrentlyActive());
        surveyResponse.put("isFull", survey.isFull());
        surveyResponse.put("remainingDays", survey.getRemainingDays());
        surveyResponse.put("surveySummary", survey.getSurveySummary());
        surveyResponse.put("createdAt", survey.getCreatedAt());
        surveyResponse.put("updatedAt", survey.getUpdatedAt());
        
        // Tenant bilgisi
        if (survey.getTenant() != null) {
            surveyResponse.put("tenant", Map.of(
                    "id", survey.getTenant().getId(),
                    "name", survey.getTenant().getName()
            ));
        }
        
        // Oluşturan kullanıcı bilgisi
        if (survey.getCreatedBy() != null) {
            surveyResponse.put("createdBy", Map.of(
                    "id", survey.getCreatedBy().getId(),
                    "fullName", survey.getCreatedBy().getFullName()
            ));
        }
        
        return surveyResponse;
    }

    /**
     * ❓ Question response objesi oluştur
     */
    private Map<String, Object> createQuestionResponse(Question question) {
        Map<String, Object> questionResponse = new HashMap<>();
        questionResponse.put("id", question.getId());
        questionResponse.put("questionText", question.getQuestionText());
        questionResponse.put("helpText", question.getHelpText());
        questionResponse.put("questionType", question.getQuestionType().name());
        questionResponse.put("questionTypeDisplayName", question.getQuestionTypeDisplayName());
        questionResponse.put("displayOrder", question.getDisplayOrder());
        questionResponse.put("required", question.getRequired());
        questionResponse.put("active", question.getActive());
        questionResponse.put("options", question.getOptions());
        questionResponse.put("optionsArray", question.getOptionsArray());
        questionResponse.put("minValue", question.getMinValue());
        questionResponse.put("maxValue", question.getMaxValue());
        questionResponse.put("stepValue", question.getStepValue());
        questionResponse.put("weight", question.getWeight());
        questionResponse.put("hasOptions", question.hasOptions());
        questionResponse.put("requiresNumericValue", question.requiresNumericValue());
        questionResponse.put("requiresTextValue", question.requiresTextValue());
        questionResponse.put("isConditional", question.isConditional());
        questionResponse.put("questionSummary", question.getQuestionSummary());
        
        // Dimension bilgisi
        if (question.getDimension() != null) {
            questionResponse.put("dimension", Map.of(
                    "id", question.getDimension().getId(),
                    "name", question.getDimension().getName(),
                    "category", question.getDimension().getCategory().name()
            ));
        }
        
        return questionResponse;
    }

    // ===============================
    // 📄 Request DTOs
    // ===============================

    public static class CreateSurveyRequest {
        private String title;
        private String description;
        private String surveyType = "PERFORMANCE";
        private String targetGroup = "ALL_EMPLOYEES";
        private Integer estimatedDuration = 10;
        private Boolean isAnonymous = false;
        private Boolean isRepeatable = false;
        private Integer maxResponses;
        private Boolean sendEmailNotification = true;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        
        // Getters & Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSurveyType() { return surveyType; }
        public void setSurveyType(String surveyType) { this.surveyType = surveyType; }
        public String getTargetGroup() { return targetGroup; }
        public void setTargetGroup(String targetGroup) { this.targetGroup = targetGroup; }
        public Integer getEstimatedDuration() { return estimatedDuration; }
        public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }
        public Boolean getIsAnonymous() { return isAnonymous; }
        public void setIsAnonymous(Boolean anonymous) { isAnonymous = anonymous; }
        public Boolean getIsRepeatable() { return isRepeatable; }
        public void setIsRepeatable(Boolean repeatable) { isRepeatable = repeatable; }
        public Integer getMaxResponses() { return maxResponses; }
        public void setMaxResponses(Integer maxResponses) { this.maxResponses = maxResponses; }
        public Boolean getSendEmailNotification() { return sendEmailNotification; }
        public void setSendEmailNotification(Boolean sendEmailNotification) { this.sendEmailNotification = sendEmailNotification; }
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    }

    public static class AddQuestionRequest {
        private String questionText;
        private String helpText;
        private String questionType = "MULTIPLE_CHOICE";
        private Boolean required = true;
        private Integer displayOrder;
        private Double weight = 1.0;
        
        // Getters & Setters
        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public String getHelpText() { return helpText; }
        public void setHelpText(String helpText) { this.helpText = helpText; }
        public String getQuestionType() { return questionType; }
        public void setQuestionType(String questionType) { this.questionType = questionType; }
        public Boolean getRequired() { return required; }
        public void setRequired(Boolean required) { this.required = required; }
        public Integer getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
        public Double getWeight() { return weight; }
        public void setWeight(Double weight) { this.weight = weight; }
    }

    public static class UpdateStatusRequest {
        private String status;
        
        // Getters & Setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
} 
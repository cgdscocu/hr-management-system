package com.hrapp.service;

import com.hrapp.entity.*;
import com.hrapp.repository.SurveyRepository;
import com.hrapp.repository.QuestionRepository;
import com.hrapp.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 📋 Survey Service - Anket İş Mantığı
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final SurveyResponseRepository surveyResponseRepository;

    /**
     * 📋 Tüm anketleri getir
     */
    public List<Survey> findAll() {
        log.debug("Tüm anketler getiriliyor");
        return surveyRepository.findAll();
    }

    /**
     * ✅ Aktif anketleri getir
     */
    public List<Survey> findActiveSurveys() {
        log.debug("Aktif anketler getiriliyor");
        return surveyRepository.findByActiveTrue();
    }

    /**
     * 🏢 Tenant'a göre anketleri getir
     */
    public List<Survey> findByTenant(Long tenantId) {
        log.debug("Tenant anketleri getiriliyor - Tenant ID: {}", tenantId);
        return surveyRepository.findByTenantIdAndActiveTrue(tenantId);
    }

    /**
     * 📊 Anket türüne göre anketleri getir
     */
    public List<Survey> findBySurveyType(Survey.SurveyType surveyType) {
        log.debug("Anket türü anketleri getiriliyor - Tür: {}", surveyType);
        return surveyRepository.findBySurveyType(surveyType);
    }

    /**
     * 📊 Anket durumuna göre anketleri getir
     */
    public List<Survey> findByStatus(Survey.SurveyStatus status) {
        log.debug("Durum anketleri getiriliyor - Durum: {}", status);
        return surveyRepository.findByStatus(status);
    }

    /**
     * ⏰ Şu anda aktif anketleri getir
     */
    public List<Survey> findCurrentlyActiveSurveys() {
        log.debug("Şu anda aktif anketler getiriliyor");
        return surveyRepository.findCurrentlyActivesurveys(LocalDateTime.now());
    }

    /**
     * 🆔 ID ile anket bul
     */
    public Optional<Survey> findById(Long id) {
        log.debug("Anket aranıyor - ID: {}", id);
        return surveyRepository.findById(id);
    }

    /**
     * 🏷️ Başlıkla anket bul
     */
    public Optional<Survey> findByTitle(String title) {
        log.debug("Anket aranıyor - Başlık: {}", title);
        return surveyRepository.findByTitle(title);
    }

    /**
     * 🏢 Tenant ve başlıkla anket bul
     */
    public Optional<Survey> findByTitleAndTenant(String title, Long tenantId) {
        log.debug("Anket aranıyor - Başlık: {}, Tenant ID: {}", title, tenantId);
        return surveyRepository.findByTitleAndTenantId(title, tenantId);
    }

    /**
     * 📝 Yeni anket oluştur
     */
    @Transactional
    public Survey createSurvey(Survey survey) {
        log.info("Yeni anket oluşturuluyor - Başlık: {}", survey.getTitle());
        
        // Başlık kontrolü (tenant bazında)
        if (surveyRepository.findByTitleAndTenantId(survey.getTitle(), survey.getTenant().getId()).isPresent()) {
            throw new RuntimeException("Bu anket başlığı zaten kullanılıyor: " + survey.getTitle());
        }
        
        // Default değerler
        survey.setActive(true);
        survey.setIsSystemSurvey(false);
        survey.setStatus(Survey.SurveyStatus.DRAFT);
        
        if (survey.getEstimatedDuration() == null) {
            survey.setEstimatedDuration(10);
        }
        
        Survey savedSurvey = surveyRepository.save(survey);
        log.info("Anket başarıyla oluşturuldu - ID: {}, Başlık: {}", savedSurvey.getId(), savedSurvey.getTitle());
        
        return savedSurvey;
    }

    /**
     * 🔄 Anket güncelle
     */
    @Transactional
    public Survey updateSurvey(Long id, Survey surveyDetails) {
        log.info("Anket güncelleniyor - ID: {}", id);
        
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anket bulunamadı - ID: " + id));
        
        // Sistem anketi kontrolü
        if (survey.getIsSystemSurvey()) {
            throw new RuntimeException("Sistem anketleri güncellenemez - ID: " + id);
        }
        
        // Başlık kontrolü (eğer değiştiriliyorsa)
        if (!survey.getTitle().equals(surveyDetails.getTitle())) {
            if (surveyRepository.findByTitleAndTenantId(surveyDetails.getTitle(), survey.getTenant().getId()).isPresent()) {
                throw new RuntimeException("Bu anket başlığı zaten kullanılıyor: " + surveyDetails.getTitle());
            }
        }
        
        // Güncellenebilir alanlar
        survey.setTitle(surveyDetails.getTitle());
        survey.setDescription(surveyDetails.getDescription());
        survey.setSurveyType(surveyDetails.getSurveyType());
        survey.setTargetGroup(surveyDetails.getTargetGroup());
        survey.setTargetDepartment(surveyDetails.getTargetDepartment());
        survey.setTargetPosition(surveyDetails.getTargetPosition());
        survey.setStartDate(surveyDetails.getStartDate());
        survey.setEndDate(surveyDetails.getEndDate());
        survey.setEstimatedDuration(surveyDetails.getEstimatedDuration());
        survey.setIsAnonymous(surveyDetails.getIsAnonymous());
        survey.setIsRepeatable(surveyDetails.getIsRepeatable());
        survey.setMaxResponses(surveyDetails.getMaxResponses());
        survey.setSendEmailNotification(surveyDetails.getSendEmailNotification());
        
        return surveyRepository.save(survey);
    }

    /**
     * 📊 Anket durumunu değiştir
     */
    @Transactional
    public Survey updateSurveyStatus(Long id, Survey.SurveyStatus newStatus) {
        log.info("Anket durumu güncelleniyor - ID: {}, Yeni Durum: {}", id, newStatus);
        
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anket bulunamadı - ID: " + id));
        
        // Durum geçiş kontrolü
        validateStatusTransition(survey.getStatus(), newStatus);
        
        survey.setStatus(newStatus);
        
        // Durum özel işlemleri
        switch (newStatus) {
            case ACTIVE:
                if (survey.getStartDate() == null) {
                    survey.setStartDate(LocalDateTime.now());
                }
                break;
            case COMPLETED:
                if (survey.getEndDate() == null) {
                    survey.setEndDate(LocalDateTime.now());
                }
                break;
            case CANCELLED:
                survey.setActive(false);
                break;
        }
        
        return surveyRepository.save(survey);
    }

    /**
     * 📊 Ankete soru ekle
     */
    @Transactional
    public Question addQuestionToSurvey(Long surveyId, Question question) {
        log.info("Ankete soru ekleniyor - Survey ID: {}", surveyId);
        
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Anket bulunamadı - ID: " + surveyId));
        
        // Anket durumu kontrolü (DRAFT veya PUBLISHED'da eklenebilir)
        if (survey.getStatus() == Survey.SurveyStatus.ACTIVE || 
            survey.getStatus() == Survey.SurveyStatus.COMPLETED) {
            throw new RuntimeException("Aktif veya tamamlanmış ankete soru eklenemez");
        }
        
        question.setSurvey(survey);
        question.setActive(true);
        
        // Display order ayarla
        if (question.getDisplayOrder() == null || question.getDisplayOrder() == 0) {
            question.setDisplayOrder(questionRepository.getNextDisplayOrder(surveyId));
        }
        
        // Soru türüne göre default değerler
        if (question.getQuestionType().name().startsWith("LIKERT") || 
            question.getQuestionType() == Question.QuestionType.YES_NO ||
            question.getQuestionType() == Question.QuestionType.RATING_STARS) {
            question.setDefaultLikertOptions();
        }
        
        return questionRepository.save(question);
    }

    /**
     * 📊 Anketin sorularını getir
     */
    public List<Question> getSurveyQuestions(Long surveyId) {
        log.debug("Anket soruları getiriliyor - Survey ID: {}", surveyId);
        return questionRepository.findBySurveyIdOrderByDisplayOrder(surveyId);
    }

    /**
     * 📊 Anketin yanıtlarını getir
     */
    public List<SurveyResponse> getSurveyResponses(Long surveyId) {
        log.debug("Anket yanıtları getiriliyor - Survey ID: {}", surveyId);
        return surveyResponseRepository.findBySurveyId(surveyId);
    }

    /**
     * 📊 Anketin teslim edilmiş yanıtlarını getir
     */
    public List<SurveyResponse> getSubmittedResponses(Long surveyId) {
        log.debug("Teslim edilmiş yanıtlar getiriliyor - Survey ID: {}", surveyId);
        return surveyResponseRepository.findBySurveyIdAndResponseStatusOrderBySubmittedAtDesc(
                surveyId, SurveyResponse.ResponseStatus.SUBMITTED);
    }

    /**
     * 📊 Anket istatistikleri
     */
    public Map<String, Object> getSurveyStatistics(Long surveyId) {
        log.debug("Anket istatistikleri hesaplanıyor - Survey ID: {}", surveyId);
        
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Anket bulunamadı - ID: " + surveyId));
        
        long totalQuestions = questionRepository.countActiveBySurveyId(surveyId);
        long totalResponses = surveyResponseRepository.countBySurveyId(surveyId);
        long submittedResponses = surveyResponseRepository.countSubmittedBySurveyId(surveyId);
        long completedResponses = surveyResponseRepository.countCompletedBySurveyId(surveyId);
        
        Double avgCompletionTime = surveyResponseRepository.getAverageCompletionTimeBySurveyId(surveyId);
        Double avgScore = surveyResponseRepository.getAverageScoreBySurveyId(surveyId);
        Double avgCompletionPercentage = surveyResponseRepository.getAverageCompletionPercentageBySurveyId(surveyId);
        
        Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("survey", survey);
        statistics.put("totalQuestions", totalQuestions);
        statistics.put("totalResponses", totalResponses);
        statistics.put("submittedResponses", submittedResponses);
        statistics.put("completedResponses", completedResponses);
        statistics.put("responseRate", survey.getCompletionRate());
        statistics.put("averageCompletionTime", avgCompletionTime);
        statistics.put("averageScore", avgScore);
        statistics.put("averageCompletionPercentage", avgCompletionPercentage);
        statistics.put("isCurrentlyActive", survey.isCurrentlyActive());
        statistics.put("isFull", survey.isFull());
        statistics.put("remainingDays", survey.getRemainingDays());
        
        return statistics;
    }

    /**
     * 🎯 Kullanıcı için uygun anketleri getir
     */
    public List<Survey> getAvailableSurveysForUser(Long tenantId, Long departmentId, Long positionId) {
        log.debug("Kullanıcı için uygun anketler getiriliyor - Tenant: {}, Department: {}, Position: {}", 
                tenantId, departmentId, positionId);
        return surveyRepository.findAvailableSurveysForUser(tenantId, departmentId, positionId);
    }

    /**
     * ✅ Anketi aktif/pasif yap
     */
    @Transactional
    public Survey toggleSurveyStatus(Long id) {
        log.info("Anket durumu değiştiriliyor - ID: {}", id);
        
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anket bulunamadı - ID: " + id));
        
        // Sistem anketi kontrolü
        if (survey.getIsSystemSurvey()) {
            throw new RuntimeException("Sistem anketleri deaktive edilemez - ID: " + id);
        }
        
        survey.setActive(!survey.getActive());
        return surveyRepository.save(survey);
    }

    /**
     * 🗑️ Anket sil
     */
    @Transactional
    public void deleteSurvey(Long id) {
        log.info("Anket siliniyor - ID: {}", id);
        
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anket bulunamadı - ID: " + id));
        
        // Sistem anketi kontrolü
        if (survey.getIsSystemSurvey()) {
            throw new RuntimeException("Sistem anketleri silinemez - ID: " + id);
        }
        
        // Aktif anket kontrolü
        if (survey.getStatus() == Survey.SurveyStatus.ACTIVE) {
            throw new RuntimeException("Aktif anket silinemez. Önce durumu değiştirin - ID: " + id);
        }
        
        // İlişkili yanıtları da sil
        surveyResponseRepository.deleteBySurveyId(id);
        
        surveyRepository.delete(survey);
    }

    /**
     * 🔍 Anket arama
     */
    public List<Survey> searchSurveys(String title) {
        log.debug("Anket arama - Başlık: {}", title);
        return surveyRepository.findByTitleContaining(title);
    }

    /**
     * 📊 Tenant'taki anket sayısı
     */
    public long countByTenant(Long tenantId) {
        return surveyRepository.countActiveSurveysByTenant(tenantId);
    }

    /**
     * 📊 Durum geçiş kontrolü
     */
    private void validateStatusTransition(Survey.SurveyStatus currentStatus, Survey.SurveyStatus newStatus) {
        // İzin verilen geçişler
        switch (currentStatus) {
            case DRAFT:
                if (newStatus != Survey.SurveyStatus.PUBLISHED && 
                    newStatus != Survey.SurveyStatus.CANCELLED) {
                    throw new RuntimeException("DRAFT'tan sadece PUBLISHED veya CANCELLED'a geçilebilir");
                }
                break;
            case PUBLISHED:
                if (newStatus != Survey.SurveyStatus.ACTIVE && 
                    newStatus != Survey.SurveyStatus.CANCELLED) {
                    throw new RuntimeException("PUBLISHED'dan sadece ACTIVE veya CANCELLED'a geçilebilir");
                }
                break;
            case ACTIVE:
                if (newStatus != Survey.SurveyStatus.PAUSED && 
                    newStatus != Survey.SurveyStatus.COMPLETED && 
                    newStatus != Survey.SurveyStatus.CANCELLED) {
                    throw new RuntimeException("ACTIVE'den sadece PAUSED, COMPLETED veya CANCELLED'a geçilebilir");
                }
                break;
            case PAUSED:
                if (newStatus != Survey.SurveyStatus.ACTIVE && 
                    newStatus != Survey.SurveyStatus.CANCELLED) {
                    throw new RuntimeException("PAUSED'dan sadece ACTIVE veya CANCELLED'a geçilebilir");
                }
                break;
            case COMPLETED:
                if (newStatus != Survey.SurveyStatus.ARCHIVED) {
                    throw new RuntimeException("COMPLETED'dan sadece ARCHIVED'a geçilebilir");
                }
                break;
            case ARCHIVED:
                throw new RuntimeException("ARCHIVED durumundan geçiş yapılamaz");
            case CANCELLED:
                throw new RuntimeException("CANCELLED durumundan geçiş yapılamaz");
        }
    }
} 
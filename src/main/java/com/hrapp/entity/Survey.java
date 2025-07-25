package com.hrapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 📋 Survey Entity - Anket Sistemi
 * 
 * HR süreçlerinde kullanılan anketler:
 * - Performans Değerlendirme Anketleri
 * - 360 Derece Geri Bildirim
 * - Çalışan Memnuniyet Anketleri
 * - Exit Interview Anketleri
 * vb.
 */
@Entity
@Table(name = "surveys", indexes = {
    @Index(name = "idx_survey_tenant", columnList = "tenant_id"),
    @Index(name = "idx_survey_type", columnList = "survey_type"),
    @Index(name = "idx_survey_status", columnList = "status"),
    @Index(name = "idx_survey_dates", columnList = "start_date, end_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Survey {

    /**
     * 🆔 Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 🏷️ Anket Adı
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 📝 Açıklama
     */
    @Column(length = 2000)
    private String description;

    /**
     * 🏢 Tenant - Bu anket hangi şirkete ait
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /**
     * 📊 Anket Türü (PERFORMANCE, FEEDBACK_360, SATISFACTION, EXIT_INTERVIEW vb.)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "survey_type", nullable = false)
    private SurveyType surveyType = SurveyType.PERFORMANCE;

    /**
     * 🎯 Hedef Grup (ALL_EMPLOYEES, DEPARTMENT, POSITION, CUSTOM)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_group", nullable = false)
    private TargetGroup targetGroup = TargetGroup.ALL_EMPLOYEES;

    /**
     * 🏗️ Hedef Departman (eğer DEPARTMENT seçilmişse)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_department_id")
    private Department targetDepartment;

    /**
     * 💼 Hedef Pozisyon (eğer POSITION seçilmişse)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_position_id")
    private Position targetPosition;

    /**
     * 📋 Anket Soruları
     */
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private Set<Question> questions = new HashSet<>();

    /**
     * 📝 Anket Yanıtları
     */
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SurveyResponse> responses = new HashSet<>();

    /**
     * 📊 Anket Durumu (DRAFT, PUBLISHED, ACTIVE, PAUSED, COMPLETED, ARCHIVED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SurveyStatus status = SurveyStatus.DRAFT;

    /**
     * 📅 Başlangıç Tarihi
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * 📅 Bitiş Tarihi
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * ⏱️ Tahmini Süre (dakika)
     */
    @Column(name = "estimated_duration")
    private Integer estimatedDuration = 10;

    /**
     * 🔒 Anonim mi?
     */
    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = false;

    /**
     * 🔄 Tekrarlanabilir mi?
     */
    @Column(name = "is_repeatable", nullable = false)
    private Boolean isRepeatable = false;

    /**
     * ✅ Aktif mi?
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * 🔒 Sistem anketi mi? (Silinebilir değil)
     */
    @Column(name = "is_system_survey", nullable = false)
    private Boolean isSystemSurvey = false;

    /**
     * 📊 Maksimum Yanıt Sayısı (null ise sınırsız)
     */
    @Column(name = "max_responses")
    private Integer maxResponses;

    /**
     * 📧 Email bildirimi gönderilsin mi?
     */
    @Column(name = "send_email_notification", nullable = false)
    private Boolean sendEmailNotification = true;

    /**
     * 📅 Oluşturulma tarihi
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 📅 Güncellenme tarihi
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 👤 Oluşturan kullanıcı
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    // ===============================
    // 📊 Survey Type Enum
    // ===============================

    /**
     * Anket Türleri
     */
    public enum SurveyType {
        PERFORMANCE,            // Performans Değerlendirme
        FEEDBACK_360,           // 360 Derece Geri Bildirim
        SATISFACTION,           // Çalışan Memnuniyeti
        EXIT_INTERVIEW,         // İşten Ayrılış Görüşmesi
        ENGAGEMENT,             // Çalışan Bağlılığı
        TRAINING_EVALUATION,    // Eğitim Değerlendirme
        CLIMATE_SURVEY,         // İş Ortamı Anketi
        PULSE_SURVEY,           // Hızlı Nabız Yoklama
        ONBOARDING,             // İşe Alım Süreci
        COMPETENCY,             // Yetkinlik Değerlendirme
        LEADERSHIP,             // Liderlik Değerlendirme
        CUSTOM                  // Özel Anket
    }

    // ===============================
    // 🎯 Target Group Enum
    // ===============================

    /**
     * Hedef Gruplar
     */
    public enum TargetGroup {
        ALL_EMPLOYEES,          // Tüm Çalışanlar
        DEPARTMENT,             // Belirli Departman
        POSITION,               // Belirli Pozisyon
        ROLE,                   // Belirli Rol
        MANAGER_LEVEL,          // Yönetici Seviyesi
        NEW_EMPLOYEES,          // Yeni Çalışanlar
        REMOTE_EMPLOYEES,       // Uzaktan Çalışanlar
        CUSTOM                  // Özel Seçim
    }

    // ===============================
    // 📊 Survey Status Enum
    // ===============================

    /**
     * Anket Durumları
     */
    public enum SurveyStatus {
        DRAFT,                  // Taslak
        PUBLISHED,              // Yayınlandı
        ACTIVE,                 // Aktif (Yanıt alınıyor)
        PAUSED,                 // Duraklatıldı
        COMPLETED,              // Tamamlandı
        ARCHIVED,               // Arşivlendi
        CANCELLED               // İptal Edildi
    }

    // ===============================
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 📊 Soru ekle
     */
    public void addQuestion(Question question) {
        question.setSurvey(this);
        this.questions.add(question);
    }

    /**
     * 📊 Soru kaldır
     */
    public void removeQuestion(Question question) {
        this.questions.remove(question);
        question.setSurvey(null);
    }

    /**
     * 📊 Toplam soru sayısı
     */
    public int getQuestionCount() {
        return questions.size();
    }

    /**
     * 📊 Toplam yanıt sayısı
     */
    public int getResponseCount() {
        return responses.size();
    }

    /**
     * 📊 Tamamlanma oranı
     */
    public Double getCompletionRate() {
        if (maxResponses == null || maxResponses == 0) {
            return null; // Sınırsız anket
        }
        return (double) getResponseCount() / maxResponses * 100.0;
    }

    /**
     * ⏰ Anket aktif mi?
     */
    public boolean isCurrentlyActive() {
        if (!active || status != SurveyStatus.ACTIVE) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        if (startDate != null && now.isBefore(startDate)) {
            return false; // Henüz başlamamış
        }
        
        if (endDate != null && now.isAfter(endDate)) {
            return false; // Süresi dolmuş
        }
        
        return true;
    }

    /**
     * 📊 Anket dolu mu? (Maksimum yanıt sayısına ulaşıldı mı?)
     */
    public boolean isFull() {
        return maxResponses != null && getResponseCount() >= maxResponses;
    }

    /**
     * ⏱️ Kalan süre (gün)
     */
    public Long getRemainingDays() {
        if (endDate == null) return null;
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endDate)) return 0L;
        
        return java.time.Duration.between(now, endDate).toDays();
    }

    /**
     * 📊 Anket türü görüntü adı
     */
    public String getSurveyTypeDisplayName() {
        switch (surveyType) {
            case PERFORMANCE: return "Performans Değerlendirme";
            case FEEDBACK_360: return "360 Derece Geri Bildirim";
            case SATISFACTION: return "Çalışan Memnuniyeti";
            case EXIT_INTERVIEW: return "İşten Ayrılış Görüşmesi";
            case ENGAGEMENT: return "Çalışan Bağlılığı";
            case TRAINING_EVALUATION: return "Eğitim Değerlendirme";
            case CLIMATE_SURVEY: return "İş Ortamı Anketi";
            case PULSE_SURVEY: return "Hızlı Nabız Yoklama";
            case ONBOARDING: return "İşe Alım Süreci";
            case COMPETENCY: return "Yetkinlik Değerlendirme";
            case LEADERSHIP: return "Liderlik Değerlendirme";
            case CUSTOM: return "Özel Anket";
            default: return surveyType.name();
        }
    }

    /**
     * 📊 Durum görüntü adı
     */
    public String getStatusDisplayName() {
        switch (status) {
            case DRAFT: return "Taslak";
            case PUBLISHED: return "Yayınlandı";
            case ACTIVE: return "Aktif";
            case PAUSED: return "Duraklatıldı";
            case COMPLETED: return "Tamamlandı";
            case ARCHIVED: return "Arşivlendi";
            case CANCELLED: return "İptal Edildi";
            default: return status.name();
        }
    }

    /**
     * 🎯 Hedef grup görüntü adı
     */
    public String getTargetGroupDisplayName() {
        switch (targetGroup) {
            case ALL_EMPLOYEES: return "Tüm Çalışanlar";
            case DEPARTMENT: return "Belirli Departman";
            case POSITION: return "Belirli Pozisyon";
            case ROLE: return "Belirli Rol";
            case MANAGER_LEVEL: return "Yönetici Seviyesi";
            case NEW_EMPLOYEES: return "Yeni Çalışanlar";
            case REMOTE_EMPLOYEES: return "Uzaktan Çalışanlar";
            case CUSTOM: return "Özel Seçim";
            default: return targetGroup.name();
        }
    }

    /**
     * 📝 Anket özeti
     */
    public String getSurveySummary() {
        return String.format("%s - %d Soru, %d Yanıt (%s)", 
                title, getQuestionCount(), getResponseCount(), getStatusDisplayName());
    }
} 
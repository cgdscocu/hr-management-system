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
 * 📝 Survey Response Entity - Anket Yanıtları
 * 
 * Kullanıcıların anketlere verdiği yanıtlar.
 * Her yanıt bir kullanıcı ve anket kombinasyonuna aittir.
 */
@Entity
@Table(name = "survey_responses", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"survey_id", "respondent_id"}),
       indexes = {
    @Index(name = "idx_survey_response_survey", columnList = "survey_id"),
    @Index(name = "idx_survey_response_user", columnList = "respondent_id"),
    @Index(name = "idx_survey_response_status", columnList = "response_status"),
    @Index(name = "idx_survey_response_date", columnList = "submitted_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponse {

    /**
     * 🆔 Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 📋 Anket
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    /**
     * 👤 Yanıtlayan Kullanıcı (Anonim anketlerde null olabilir)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respondent_id")
    private User respondent;

    /**
     * 📊 Yanıt Durumu (STARTED, IN_PROGRESS, COMPLETED, SUBMITTED)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "response_status", nullable = false)
    private ResponseStatus responseStatus = ResponseStatus.STARTED;

    /**
     * 📅 Başlangıç Tarihi
     */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    /**
     * 📅 Teslim Tarihi
     */
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    /**
     * ⏱️ Tamamlanma Süresi (dakika)
     */
    @Column(name = "completion_time")
    private Integer completionTime;

    /**
     * 📊 Tamamlanma Yüzdesi (0-100)
     */
    @Column(name = "completion_percentage")
    private Double completionPercentage = 0.0;

    /**
     * 💻 Yanıtlama Platformu (WEB, MOBILE, TABLET)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private Platform platform = Platform.WEB;

    /**
     * 🌐 IP Adresi (Güvenlik için)
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * 🖥️ User Agent (Tarayıcı bilgisi)
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * 🔢 Toplam Puan (Hesaplanmış)
     */
    @Column(name = "total_score")
    private Double totalScore;

    /**
     * 📊 Ağırlıklı Puan (Dimension ağırlıklarına göre)
     */
    @Column(name = "weighted_score")
    private Double weightedScore;

    /**
     * 📝 Yorumlar (Genel yorum)
     */
    @Column(length = 2000)
    private String comments;

    /**
     * 🏷️ Etiketler (JSON format)
     */
    @Column(columnDefinition = "TEXT")
    private String tags;

    /**
     * 📊 Kalite Puanı (1-5, yanıtın kalitesi)
     */
    @Column(name = "quality_score")
    private Integer qualityScore;

    /**
     * 🔒 Anonim Yanıt mı?
     */
    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = false;

    /**
     * 📧 Email ile bildirim isteniyor mu?
     */
    @Column(name = "wants_email_notification", nullable = false)
    private Boolean wantsEmailNotification = false;

    /**
     * 📱 SMS ile bildirim isteniyor mu?
     */
    @Column(name = "wants_sms_notification", nullable = false)
    private Boolean wantsSmsNotification = false;

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

    // ===============================
    // 📊 Response Status Enum
    // ===============================

    /**
     * Yanıt Durumları
     */
    public enum ResponseStatus {
        STARTED,                // Başlatıldı
        IN_PROGRESS,            // Devam Ediyor
        COMPLETED,              // Tamamlandı (henüz teslim edilmedi)
        SUBMITTED,              // Teslim Edildi
        EXPIRED,                // Süresi Doldu
        CANCELLED               // İptal Edildi
    }

    // ===============================
    // 💻 Platform Enum
    // ===============================

    /**
     * Yanıtlama Platformları
     */
    public enum Platform {
        WEB,                    // Web Tarayıcı
        MOBILE,                 // Mobil Uygulama
        TABLET,                 // Tablet
        DESKTOP_APP,            // Masaüstü Uygulama
        EMAIL,                  // Email içinde
        SMS,                    // SMS ile
        OTHER                   // Diğer
    }

    // ===============================
    // 🛠️ Helper Methods
    // ===============================

    /**
     * ✅ Tamamlandı mı?
     */
    public boolean isCompleted() {
        return responseStatus == ResponseStatus.COMPLETED || 
               responseStatus == ResponseStatus.SUBMITTED;
    }

    /**
     * 📝 Teslim edildi mi?
     */
    public boolean isSubmitted() {
        return responseStatus == ResponseStatus.SUBMITTED;
    }

    /**
     * ⏰ Süresi doldu mu?
     */
    public boolean isExpired() {
        return responseStatus == ResponseStatus.EXPIRED;
    }

    /**
     * ⏱️ Yanıtlama süresini hesapla (dakika)
     */
    public Integer calculateResponseTime() {
        if (startedAt == null) return null;
        
        LocalDateTime endTime = submittedAt != null ? submittedAt : LocalDateTime.now();
        
        return (int) java.time.Duration.between(startedAt, endTime).toMinutes();
    }

    /**
     * 📊 Tamamlanma yüzdesini güncelle
     */
    public void updateCompletionPercentage(int totalQuestions, int answeredQuestions) {
        if (totalQuestions == 0) {
            this.completionPercentage = 0.0;
        } else {
            this.completionPercentage = (double) answeredQuestions / totalQuestions * 100.0;
        }
    }

    /**
     * 📝 Yanıtı başlat
     */
    public void startResponse() {
        this.responseStatus = ResponseStatus.STARTED;
        this.startedAt = LocalDateTime.now();
        this.completionPercentage = 0.0;
    }

    /**
     * 💾 Yanıtı kaydet (devam etmek için)
     */
    public void saveProgress() {
        this.responseStatus = ResponseStatus.IN_PROGRESS;
    }

    /**
     * ✅ Yanıtı tamamla
     */
    public void completeResponse() {
        this.responseStatus = ResponseStatus.COMPLETED;
        this.completionPercentage = 100.0;
        this.completionTime = calculateResponseTime();
    }

    /**
     * 📤 Yanıtı teslim et
     */
    public void submitResponse() {
        this.responseStatus = ResponseStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
        this.completionPercentage = 100.0;
        this.completionTime = calculateResponseTime();
    }

    /**
     * ⏰ Süre doldu olarak işaretle
     */
    public void expireResponse() {
        this.responseStatus = ResponseStatus.EXPIRED;
        this.completionTime = calculateResponseTime();
    }

    /**
     * ❌ Yanıtı iptal et
     */
    public void cancelResponse() {
        this.responseStatus = ResponseStatus.CANCELLED;
        this.completionTime = calculateResponseTime();
    }

    /**
     * 🏷️ Etiket ekle
     */
    public void addTag(String tag) {
        if (tags == null || tags.trim().isEmpty()) {
            tags = tag;
        } else {
            tags += "," + tag;
        }
    }

    /**
     * 🏷️ Etiketleri liste olarak getir
     */
    public String[] getTagsArray() {
        if (tags == null || tags.trim().isEmpty()) {
            return new String[0];
        }
        return tags.split(",");
    }

    /**
     * 📊 Kalite durumu
     */
    public String getQualityStatus() {
        if (qualityScore == null) return "Değerlendirilmemiş";
        
        switch (qualityScore) {
            case 1: return "Çok Düşük";
            case 2: return "Düşük";
            case 3: return "Orta";
            case 4: return "İyi";
            case 5: return "Mükemmel";
            default: return "Bilinmiyor";
        }
    }

    /**
     * 📊 Durum görüntü adı
     */
    public String getStatusDisplayName() {
        switch (responseStatus) {
            case STARTED: return "Başlatıldı";
            case IN_PROGRESS: return "Devam Ediyor";
            case COMPLETED: return "Tamamlandı";
            case SUBMITTED: return "Teslim Edildi";
            case EXPIRED: return "Süresi Doldu";
            case CANCELLED: return "İptal Edildi";
            default: return responseStatus.name();
        }
    }

    /**
     * 💻 Platform görüntü adı
     */
    public String getPlatformDisplayName() {
        switch (platform) {
            case WEB: return "Web Tarayıcı";
            case MOBILE: return "Mobil Uygulama";
            case TABLET: return "Tablet";
            case DESKTOP_APP: return "Masaüstü Uygulama";
            case EMAIL: return "Email";
            case SMS: return "SMS";
            case OTHER: return "Diğer";
            default: return platform.name();
        }
    }

    /**
     * 📝 Yanıt özeti
     */
    public String getResponseSummary() {
        String userInfo = isAnonymous ? "Anonim" : 
                         (respondent != null ? respondent.getFullName() : "Bilinmiyor");
        
        return String.format("%s - %.1f%% (%s)", 
                userInfo, completionPercentage, getStatusDisplayName());
    }
} 
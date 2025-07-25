package com.hrapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 🔗 Success Profile Dimension Entity - Başarı Profili ve Dimension İlişki Tablosu
 * 
 * SuccessProfile ile Dimension arasındaki çoka-çok ilişkiyi yönetir.
 * Her ilişki için ağırlık, minimum skor ve hedef skor bilgilerini tutar.
 */
@Entity
@Table(name = "success_profile_dimensions", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"success_profile_id", "dimension_id"}),
       indexes = {
    @Index(name = "idx_spd_success_profile", columnList = "success_profile_id"),
    @Index(name = "idx_spd_dimension", columnList = "dimension_id"),
    @Index(name = "idx_spd_active", columnList = "active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessProfileDimension {

    /**
     * 🆔 Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 🎯 Başarı Profili
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "success_profile_id", nullable = false)
    private SuccessProfile successProfile;

    /**
     * 📊 Dimension
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dimension_id", nullable = false)
    private Dimension dimension;

    /**
     * ⚖️ Ağırlık (Bu dimension'ın bu profildeki önemi, 0-100 arası)
     */
    @Column(nullable = false)
    private Double weight = 10.0;

    /**
     * 🎯 Minimum Skor (Bu dimension için kabul edilebilir minimum skor)
     */
    @Column(name = "min_score")
    private Double minScore = 3.0;

    /**
     * 🎯 Hedef Skor (Bu dimension için hedeflenen skor)
     */
    @Column(name = "target_score")
    private Double targetScore = 4.0;

    /**
     * 🔥 Kritik mi? (Bu dimension kritik öneme sahip mi?)
     */
    @Column(name = "is_critical", nullable = false)
    private Boolean isCritical = false;

    /**
     * ✅ Aktif mi?
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * 📝 Notlar (Bu dimension için özel notlar)
     */
    @Column(length = 500)
    private String notes;

    /**
     * 📊 Display Order (Görüntüleme sırası)
     */
    @Column(name = "display_order")
    private Integer displayOrder = 0;

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
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 🔍 Skor aralığında mı kontrol et
     */
    public boolean isScoreInRange(Double score) {
        if (score == null) return false;
        return score >= dimension.getMinValue() && score <= dimension.getMaxValue();
    }

    /**
     * ✅ Minimum skorunu karşılıyor mu?
     */
    public boolean meetsMinimumScore(Double score) {
        return score != null && score >= minScore;
    }

    /**
     * 🎯 Hedef skorunu karşılıyor mu?
     */
    public boolean meetsTargetScore(Double score) {
        return score != null && score >= targetScore;
    }

    /**
     * 📊 Skor yüzdesini hesapla (dimension'ın min/max değerine göre)
     */
    public Double calculateScorePercentage(Double score) {
        if (score == null || !isScoreInRange(score)) return null;
        
        return dimension.valueToPercentage(score);
    }

    /**
     * ⚖️ Ağırlıklı skor hesapla
     */
    public Double calculateWeightedScore(Double score) {
        if (score == null || !isScoreInRange(score)) return 0.0;
        
        return score * (weight / 100.0);
    }

    /**
     * 📊 Performans durumu
     */
    public PerformanceStatus getPerformanceStatus(Double score) {
        if (score == null || !isScoreInRange(score)) {
            return PerformanceStatus.INVALID;
        }
        
        if (score >= targetScore) {
            return PerformanceStatus.EXCEEDS_TARGET;
        } else if (score >= minScore) {
            return PerformanceStatus.MEETS_MINIMUM;
        } else {
            return PerformanceStatus.BELOW_MINIMUM;
        }
    }

    /**
     * 🎯 Hedef gap'i hesapla
     */
    public Double getTargetGap(Double score) {
        if (score == null || !isScoreInRange(score)) return null;
        
        return Math.max(0, targetScore - score);
    }

    /**
     * 🔍 Kritik başarısızlık mı? (Kritik dimension'da minimum skor altında)
     */
    public boolean isCriticalFailure(Double score) {
        return isCritical && score != null && score < minScore;
    }

    // ===============================
    // 📊 Performance Status Enum
    // ===============================

    /**
     * Performans Durumları
     */
    public enum PerformanceStatus {
        INVALID,            // Geçersiz skor
        BELOW_MINIMUM,      // Minimum altında
        MEETS_MINIMUM,      // Minimum karşılıyor
        EXCEEDS_TARGET,     // Hedef aşıyor
        OUTSTANDING         // Olağanüstü
    }

    /**
     * 📊 Performans durumu görüntü adı
     */
    public String getPerformanceStatusDisplayName(Double score) {
        PerformanceStatus status = getPerformanceStatus(score);
        switch (status) {
            case INVALID: return "Geçersiz";
            case BELOW_MINIMUM: return "Yetersiz";
            case MEETS_MINIMUM: return "Yeterli";
            case EXCEEDS_TARGET: return "Hedef Üstü";
            case OUTSTANDING: return "Mükemmel";
            default: return status.name();
        }
    }

    /**
     * 📈 İyileştirme önerisi
     */
    public String getImprovementSuggestion(Double score) {
        if (score == null) return "Değerlendirme yapılmamış";
        
        PerformanceStatus status = getPerformanceStatus(score);
        switch (status) {
            case BELOW_MINIMUM:
                return String.format("Bu boyutta gelişim gerekli. Hedef: %.1f", minScore);
            case MEETS_MINIMUM:
                return String.format("İyi performans. Hedef için %.1f puan gerekli", getTargetGap(score));
            case EXCEEDS_TARGET:
                return "Mükemmel performans! Diğer boyutlara odaklanabilirsiniz";
            default:
                return "Değerlendirme sonucu belirsiz";
        }
    }

    /**
     * 🎯 Profil-Dimension kombinasyonu adı
     */
    public String getCombinationName() {
        return String.format("%s - %s", 
                successProfile != null ? successProfile.getName() : "N/A",
                dimension != null ? dimension.getName() : "N/A");
    }
} 
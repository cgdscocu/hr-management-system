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
 * 📊 Dimension Entity - Performans Değerlendirme Boyutları
 * 
 * HR süreçlerinde kullanılan değerlendirme boyutları:
 * - Teknik Yetkinlik
 * - Liderlik
 * - İletişim
 * - Problem Çözme
 * - Takım Çalışması
 * vb.
 */
@Entity
@Table(name = "dimensions", indexes = {
    @Index(name = "idx_dimension_tenant", columnList = "tenant_id"),
    @Index(name = "idx_dimension_name", columnList = "name"),
    @Index(name = "idx_dimension_category", columnList = "category")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dimension {

    /**
     * 🆔 Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 🏷️ Boyut Adı (Teknik Yetkinlik, Liderlik vb.)
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 📝 Açıklama
     */
    @Column(length = 1000)
    private String description;

    /**
     * 🏢 Tenant - Bu boyut hangi şirkete ait
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /**
     * 📊 Kategori (TECHNICAL, BEHAVIORAL, LEADERSHIP, CORE_COMPETENCY vb.)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DimensionCategory category = DimensionCategory.CORE_COMPETENCY;

    /**
     * 📏 Ölçek Türü (LIKERT_5, LIKERT_7, PERCENTAGE, NUMERIC vb.)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "scale_type", nullable = false)
    private ScaleType scaleType = ScaleType.LIKERT_5;

    /**
     * 🎯 Minimum Değer
     */
    @Column(name = "min_value")
    private Double minValue = 1.0;

    /**
     * 🎯 Maksimum Değer
     */
    @Column(name = "max_value")
    private Double maxValue = 5.0;

    /**
     * 📋 Ölçek Açıklamaları (JSON format)
     * Örnek: {"1": "Yetersiz", "2": "Gelişmeli", "3": "Yeterli", "4": "İyi", "5": "Mükemmel"}
     */
    @Column(name = "scale_descriptions", columnDefinition = "TEXT")
    private String scaleDescriptions;

    /**
     * ⚖️ Ağırlık (Değerlendirmedeki önemi, 0-100 arası)
     */
    @Column
    private Double weight = 10.0;

    /**
     * ✅ Aktif mi?
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * 🔒 Sistem boyutu mu? (Silinebilir değil)
     */
    @Column(name = "is_system_dimension", nullable = false)
    private Boolean isSystemDimension = false;

    /**
     * 📊 Sıralama (Görüntülenme sırası)
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

    /**
     * 👤 Oluşturan kullanıcı
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    // ===============================
    // 📊 Dimension Category Enum
    // ===============================

    /**
     * Boyut Kategorileri
     */
    public enum DimensionCategory {
        TECHNICAL,          // Teknik Yetkinlikler
        BEHAVIORAL,         // Davranışsal Yetkinlikler
        LEADERSHIP,         // Liderlik Yetkinlikleri
        CORE_COMPETENCY,    // Temel Yetkinlikler
        FUNCTIONAL,         // Fonksiyonel Yetkinlikler
        SOFT_SKILLS,        // Yumuşak Beceriler
        COMMUNICATION,      // İletişim Becerileri
        PROBLEM_SOLVING,    // Problem Çözme
        TEAMWORK,           // Takım Çalışması
        CUSTOMER_FOCUS,     // Müşteri Odaklılık
        INNOVATION,         // İnovasyon
        ADAPTABILITY        // Uyum Yeteneği
    }

    // ===============================
    // 📏 Scale Type Enum
    // ===============================

    /**
     * Ölçek Türleri
     */
    public enum ScaleType {
        LIKERT_3,           // 3'lü Likert (1-3)
        LIKERT_5,           // 5'li Likert (1-5)
        LIKERT_7,           // 7'li Likert (1-7)
        LIKERT_10,          // 10'lu Likert (1-10)
        PERCENTAGE,         // Yüzde (0-100)
        NUMERIC,            // Sayısal (özel aralık)
        YES_NO,             // Evet/Hayır
        RATING_STARS,       // Yıldız Değerlendirme (1-5)
        CUSTOM              // Özel Ölçek
    }

    // ===============================
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 🔍 Ölçek aralığını kontrol et
     */
    public boolean isValueInRange(Double value) {
        if (value == null) return false;
        return value >= minValue && value <= maxValue;
    }

    /**
     * 📊 Değeri yüzdeye çevir
     */
    public Double valueToPercentage(Double value) {
        if (value == null || !isValueInRange(value)) return null;
        
        double range = maxValue - minValue;
        double normalizedValue = value - minValue;
        return (normalizedValue / range) * 100.0;
    }

    /**
     * 📊 Yüzdeyi değere çevir
     */
    public Double percentageToValue(Double percentage) {
        if (percentage == null || percentage < 0 || percentage > 100) return null;
        
        double range = maxValue - minValue;
        return minValue + (percentage / 100.0) * range;
    }

    /**
     * 🎯 Default ölçek tanımları oluştur
     */
    public void setDefaultScaleDescriptions() {
        switch (scaleType) {
            case LIKERT_5:
                this.scaleDescriptions = "{\"1\":\"Yetersiz\",\"2\":\"Gelişmeli\",\"3\":\"Yeterli\",\"4\":\"İyi\",\"5\":\"Mükemmel\"}";
                this.minValue = 1.0;
                this.maxValue = 5.0;
                break;
            case LIKERT_7:
                this.scaleDescriptions = "{\"1\":\"Çok Kötü\",\"2\":\"Kötü\",\"3\":\"Gelişmeli\",\"4\":\"Orta\",\"5\":\"İyi\",\"6\":\"Çok İyi\",\"7\":\"Mükemmel\"}";
                this.minValue = 1.0;
                this.maxValue = 7.0;
                break;
            case PERCENTAGE:
                this.scaleDescriptions = "{\"0\":\"0%\",\"25\":\"25%\",\"50\":\"50%\",\"75\":\"75%\",\"100\":\"100%\"}";
                this.minValue = 0.0;
                this.maxValue = 100.0;
                break;
            case YES_NO:
                this.scaleDescriptions = "{\"0\":\"Hayır\",\"1\":\"Evet\"}";
                this.minValue = 0.0;
                this.maxValue = 1.0;
                break;
            default:
                // Default 5'li Likert
                setDefaultScaleDescriptions();
                break;
        }
    }

    /**
     * 🏷️ Kategori görüntü adı
     */
    public String getCategoryDisplayName() {
        switch (category) {
            case TECHNICAL: return "Teknik Yetkinlikler";
            case BEHAVIORAL: return "Davranışsal Yetkinlikler";
            case LEADERSHIP: return "Liderlik Yetkinlikleri";
            case CORE_COMPETENCY: return "Temel Yetkinlikler";
            case FUNCTIONAL: return "Fonksiyonel Yetkinlikler";
            case SOFT_SKILLS: return "Yumuşak Beceriler";
            case COMMUNICATION: return "İletişim Becerileri";
            case PROBLEM_SOLVING: return "Problem Çözme";
            case TEAMWORK: return "Takım Çalışması";
            case CUSTOMER_FOCUS: return "Müşteri Odaklılık";
            case INNOVATION: return "İnovasyon";
            case ADAPTABILITY: return "Uyum Yeteneği";
            default: return category.name();
        }
    }
} 
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
 * 🎯 Success Profile Entity - Başarı Profili
 * 
 * Pozisyonlar için tanımlanan başarı kriterleri ve dimension'larla olan ilişkiler.
 * Her pozisyon için hangi dimension'ların ne kadar önemli olduğunu belirler.
 */
@Entity
@Table(name = "success_profiles", indexes = {
    @Index(name = "idx_success_profile_tenant", columnList = "tenant_id"),
    @Index(name = "idx_success_profile_position", columnList = "position_id"),
    @Index(name = "idx_success_profile_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessProfile {

    /**
     * 🆔 Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 🏷️ Başarı Profili Adı
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 📝 Açıklama
     */
    @Column(length = 1000)
    private String description;

    /**
     * 🏢 Tenant - Bu profil hangi şirkete ait
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /**
     * 💼 Pozisyon - Bu profil hangi pozisyon için (opsiyonel, genel profil olabilir)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    /**
     * 🏗️ Departman - Bu profil hangi departman için (opsiyonel)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * 📊 Profil Türü (POSITION_SPECIFIC, DEPARTMENT_WIDE, COMPANY_WIDE)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_type", nullable = false)
    private ProfileType profileType = ProfileType.POSITION_SPECIFIC;

    /**
     * 📋 Dimension'larla ilişkiler (SuccessProfileDimension ara tablosu)
     */
    @OneToMany(mappedBy = "successProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<SuccessProfileDimension> dimensions = new HashSet<>();

    /**
     * 🎯 Minimum Başarı Skoru (0-100)
     */
    @Column(name = "min_success_score")
    private Double minSuccessScore = 70.0;

    /**
     * 🎯 Hedef Başarı Skoru (0-100)
     */
    @Column(name = "target_success_score")
    private Double targetSuccessScore = 85.0;

    /**
     * ✅ Aktif mi?
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * 🔒 Sistem profili mi? (Silinebilir değil)
     */
    @Column(name = "is_system_profile", nullable = false)
    private Boolean isSystemProfile = false;

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
    // 📊 Profile Type Enum
    // ===============================

    /**
     * Profil Türleri
     */
    public enum ProfileType {
        POSITION_SPECIFIC,   // Pozisyona özel
        DEPARTMENT_WIDE,     // Departman geneli
        COMPANY_WIDE,        // Şirket geneli
        ROLE_BASED,          // Role dayalı
        LEVEL_BASED,         // Seviyeye dayalı
        CUSTOM               // Özel
    }

    // ===============================
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 📊 Dimension ekle
     */
    public void addDimension(Dimension dimension, Double weight, Double minScore, Double targetScore) {
        SuccessProfileDimension spd = new SuccessProfileDimension();
        spd.setSuccessProfile(this);
        spd.setDimension(dimension);
        spd.setWeight(weight);
        spd.setMinScore(minScore);
        spd.setTargetScore(targetScore);
        spd.setActive(true);
        
        this.dimensions.add(spd);
    }

    /**
     * 📊 Dimension kaldır
     */
    public void removeDimension(Dimension dimension) {
        this.dimensions.removeIf(spd -> spd.getDimension().equals(dimension));
    }

    /**
     * 📊 Toplam ağırlık
     */
    public Double getTotalWeight() {
        return dimensions.stream()
                .filter(SuccessProfileDimension::getActive)
                .mapToDouble(SuccessProfileDimension::getWeight)
                .sum();
    }

    /**
     * 📊 Aktif dimension sayısı
     */
    public int getActiveDimensionCount() {
        return (int) dimensions.stream()
                .filter(SuccessProfileDimension::getActive)
                .count();
    }

    /**
     * 🔍 Dimension var mı kontrol et
     */
    public boolean hasDimension(Dimension dimension) {
        return dimensions.stream()
                .anyMatch(spd -> spd.getDimension().equals(dimension) && spd.getActive());
    }

    /**
     * 📊 Dimension ağırlığı getir
     */
    public Double getDimensionWeight(Dimension dimension) {
        return dimensions.stream()
                .filter(spd -> spd.getDimension().equals(dimension) && spd.getActive())
                .findFirst()
                .map(SuccessProfileDimension::getWeight)
                .orElse(0.0);
    }

    /**
     * 🎯 Başarı skorunu hesapla
     * @param dimensionScores Dimension ID -> Skor mapping
     * @return Ağırlıklı ortalama skor
     */
    public Double calculateSuccessScore(java.util.Map<Long, Double> dimensionScores) {
        if (dimensions.isEmpty()) return 0.0;
        
        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;
        
        for (SuccessProfileDimension spd : dimensions) {
            if (!spd.getActive()) continue;
            
            Double score = dimensionScores.get(spd.getDimension().getId());
            if (score != null) {
                totalWeightedScore += score * spd.getWeight();
                totalWeight += spd.getWeight();
            }
        }
        
        return totalWeight > 0 ? totalWeightedScore / totalWeight : 0.0;
    }

    /**
     * ✅ Başarı kriterini karşılıyor mu?
     */
    public boolean meetsSuccessCriteria(java.util.Map<Long, Double> dimensionScores) {
        Double successScore = calculateSuccessScore(dimensionScores);
        return successScore >= minSuccessScore;
    }

    /**
     * 🎯 Hedef kriterini karşılıyor mu?
     */
    public boolean meetsTargetCriteria(java.util.Map<Long, Double> dimensionScores) {
        Double successScore = calculateSuccessScore(dimensionScores);
        return successScore >= targetSuccessScore;
    }

    /**
     * 🏷️ Profil türü görüntü adı
     */
    public String getProfileTypeDisplayName() {
        switch (profileType) {
            case POSITION_SPECIFIC: return "Pozisyona Özel";
            case DEPARTMENT_WIDE: return "Departman Geneli";
            case COMPANY_WIDE: return "Şirket Geneli";
            case ROLE_BASED: return "Role Dayalı";
            case LEVEL_BASED: return "Seviyeye Dayalı";
            case CUSTOM: return "Özel";
            default: return profileType.name();
        }
    }

    /**
     * 📝 Profil özeti
     */
    public String getProfileSummary() {
        return String.format("%s - %d Dimension, Min: %.1f%%, Hedef: %.1f%%", 
                name, getActiveDimensionCount(), minSuccessScore, targetSuccessScore);
    }
} 
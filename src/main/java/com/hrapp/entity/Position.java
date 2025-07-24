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
 * 💼 Position Entity - Pozisyon Tablosu
 * 
 * Excel'de belirtilen: "Pozisyon oluşturabilir, pozisyon-user atamalar"
 * Çalışanların iş pozisyonlarını tanımlar
 */
@Entity
@Table(name = "positions", indexes = {
    @Index(name = "idx_position_tenant", columnList = "tenant_id"),
    @Index(name = "idx_position_department", columnList = "department_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {

    /**
     * 🆔 Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 🏷️ Pozisyon Başlığı
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * 📝 Açıklama
     */
    @Column(length = 1000)
    private String description;

    /**
     * 🏢 Tenant - Bu pozisyon hangi şirkete ait
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /**
     * 🏗️ Departman - Bu pozisyon hangi departmana ait
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * 📊 Seviye (Junior, Mid, Senior, Lead, Manager vb.)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PositionLevel level = PositionLevel.JUNIOR;

    /**
     * 💰 Maaş Aralığı - Minimum
     */
    @Column(name = "salary_min")
    private Integer salaryMin;

    /**
     * 💰 Maaş Aralığı - Maksimum
     */
    @Column(name = "salary_max")
    private Integer salaryMax;

    /**
     * 💵 Para Birimi
     */
    @Column(length = 3)
    private String currency = "TRY";

    /**
     * 📋 Gereksinimler (JSON veya text)
     */
    @Column(length = 2000)
    private String requirements;

    /**
     * 🎯 Sorumluluklar
     */
    @Column(length = 2000)
    private String responsibilities;

    /**
     * 👥 Bu pozisyondaki kullanıcılar
     */
    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    /**
     * ✅ Aktif mi?
     */
    @Column(nullable = false)
    private Boolean active = true;

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
    // 📊 Position Level Enum
    // ===============================
    
    public enum PositionLevel {
        INTERN,      // Stajyer
        JUNIOR,      // Junior
        MID,         // Mid-level
        SENIOR,      // Senior
        LEAD,        // Lead
        MANAGER,     // Müdür
        DIRECTOR,    // Direktör
        VP,          // Başkan Yardımcısı
        CEO          // CEO
    }

    // ===============================
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 👥 Bu pozisyondaki aktif kullanıcı sayısı
     */
    public long getActiveUserCount() {
        return users.stream()
                .filter(User::getActive)
                .count();
    }

    /**
     * 💰 Maaş aralığı string
     */
    public String getSalaryRange() {
        if (salaryMin != null && salaryMax != null) {
            return salaryMin + " - " + salaryMax + " " + currency;
        } else if (salaryMin != null) {
            return salaryMin + "+ " + currency;
        }
        return "Belirtilmemiş";
    }
} 
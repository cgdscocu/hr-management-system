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
 * 🏢 Tenant Entity - Şirket/Organizasyon Tablosu
 * 
 * Multi-tenant yapı için. Her şirket kendi tenant'ında çalışır.
 * Excel'de belirtilen: "Süper admin tenant oluşturabilir"
 */
@Entity
@Table(name = "tenants", indexes = {
    @Index(name = "idx_tenant_domain", columnList = "domain"),
    @Index(name = "idx_tenant_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    /**
     * 🆔 Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 🏷️ Şirket Adı
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 📝 Açıklama
     */
    @Column(length = 500)
    private String description;

    /**
     * 🌐 Domain - Şirketin domain'i (example.com)
     */
    @Column(unique = true, length = 100)
    private String domain;

    /**
     * 📧 İletişim Email
     */
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    /**
     * 📱 Telefon
     */
    @Column(length = 20)
    private String phone;

    /**
     * 📍 Adres
     */
    @Column(length = 500)
    private String address;

    /**
     * 🌍 Şehir
     */
    @Column(length = 50)
    private String city;

    /**
     * 🏳️ Ülke
     */
    @Column(length = 50)
    private String country;

    /**
     * 📮 Posta Kodu
     */
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    /**
     * ✅ Aktif mi?
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * 📊 Abonelik Durumu
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false)
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.TRIAL;

    /**
     * 📅 Abonelik Başlangıç
     */
    @Column(name = "subscription_start")
    private LocalDateTime subscriptionStart;

    /**
     * 📅 Abonelik Bitiş
     */
    @Column(name = "subscription_end")
    private LocalDateTime subscriptionEnd;

    /**
     * 🔢 Maksimum Kullanıcı Sayısı
     */
    @Column(name = "max_users")
    private Integer maxUsers = 10;

    /**
     * 👥 Kullanıcılar - Bu tenant'a ait kullanıcılar
     */
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    /**
     * 🎭 Roller - Bu tenant'a özel roller
     */
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    /**
     * 🏗️ Departmanlar
     */
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Department> departments = new HashSet<>();

    /**
     * 💼 Pozisyonlar
     */
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Position> positions = new HashSet<>();

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
     * 👥 Aktif kullanıcı sayısı
     */
    public long getActiveUserCount() {
        return users.stream()
                .filter(User::getActive)
                .count();
    }

    /**
     * 🔍 Kullanıcı limiti aşıldı mı?
     */
    public boolean isUserLimitExceeded() {
        return getActiveUserCount() >= maxUsers;
    }

    /**
     * 📅 Abonelik aktif mi?
     */
    public boolean isSubscriptionActive() {
        return subscriptionStatus == SubscriptionStatus.ACTIVE &&
               subscriptionEnd != null &&
               subscriptionEnd.isAfter(LocalDateTime.now());
    }

    /**
     * 📊 Abonelik Durumu Enum
     */
    public enum SubscriptionStatus {
        TRIAL,      // Deneme
        ACTIVE,     // Aktif
        EXPIRED,    // Süresi dolmuş
        SUSPENDED,  // Askıya alınmış
        CANCELLED   // İptal edilmiş
    }
} 
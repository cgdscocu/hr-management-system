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
 * 👤 User Entity - Kullanıcı Tablosu
 * 
 * Excel'deki roller: Süper Admin, Admin, HR-Manager, HR-Specialist, User, Aday
 * 
 * JPA Annotations:
 * @Entity - Bu sınıfın bir veritabanı tablosu olduğunu belirtir
 * @Table - Tablo adını ve kısıtlamaları belirtir
 * @Id - Primary key
 * @GeneratedValue - Otomatik artan ID
 * @Column - Sütun özelliklerini belirtir
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_tenant", columnList = "tenant_id")
})
@Data // Lombok: getter, setter, toString, equals, hashCode oluşturur
@NoArgsConstructor // Lombok: parametresiz constructor
@AllArgsConstructor // Lombok: tüm parametreli constructor
public class User {

    /**
     * 🆔 Primary Key - Otomatik artan ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 📧 Email - Unique, login için kullanılır
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 🔒 Şifre - Hash'lenmiş şekilde saklanır
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 👤 Ad
     */
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /**
     * 👤 Soyad
     */
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * 📱 Telefon (opsiyonel)
     */
    @Column(length = 20)
    private String phone;

    /**
     * 🏢 Tenant - Kullanıcının ait olduğu şirket
     * 
     * @ManyToOne - Çoktan bire ilişki (Birçok user, bir tenant'a ait)
     * @JoinColumn - Foreign key sütun adı
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /**
     * 🎭 Roller - Kullanıcının sahip olduğu roller
     * 
     * @ManyToMany - Çoktan çoğa ilişki (Bir user birden fazla role sahip olabilir)
     * @JoinTable - Ara tablo tanımlaması
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * 💼 Pozisyon - Kullanıcının pozisyonu (opsiyonel)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    /**
     * 🏗️ Departman - Kullanıcının departmanı (opsiyonel)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * ✅ Aktif mi?
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * ✅ Email doğrulandı mı?
     */
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    /**
     * 📅 Oluşturulma tarihi - Otomatik
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 📅 Güncellenme tarihi - Otomatik
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 📅 Son giriş tarihi
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // ===============================
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 🔍 Kullanıcının belirli bir role sahip olup olmadığını kontrol eder
     */
    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }

    /**
     * 🔍 Kullanıcının Süper Admin olup olmadığını kontrol eder
     */
    public boolean isSuperAdmin() {
        return hasRole("SUPER_ADMIN");
    }

    /**
     * 🔍 Kullanıcının Admin olup olmadığını kontrol eder
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * 🔍 Kullanıcının HR Manager olup olmadığını kontrol eder
     */
    public boolean isHrManager() {
        return hasRole("HR_MANAGER");
    }

    /**
     * 🎭 Rol ekle
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * 🎭 Rol kaldır
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * 👤 Tam ad
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
} 
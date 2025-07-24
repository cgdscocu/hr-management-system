package com.hrapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 🎭 Role Entity - Rol Tablosu
 * 
 * Excel'den çıkardığımız roller:
 * - SUPER_ADMIN: Tam yetki, tenant oluşturma
 * - ADMIN: Tenant içinde tam yetki
 * - HR_MANAGER: İK süreçleri, sonuçlar görme
 * - HR_SPECIALIST: İK süreçleri, sonuçlar görme
 * - USER: Normal kullanıcı
 * - CANDIDATE: Aday kullanıcı
 */
@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_role_name", columnList = "name"),
    @Index(name = "idx_role_tenant", columnList = "tenant_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    /**
     * 🆔 Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 🏷️ Rol Adı (SUPER_ADMIN, ADMIN, HR_MANAGER vb.)
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 📝 Açıklama
     */
    @Column(length = 500)
    private String description;

    /**
     * 🏢 Tenant - Bu rol hangi tenant'a ait
     * NULL ise sistem geneli rol (SUPER_ADMIN gibi)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    /**
     * 🔑 İzinler - Bu rolün sahip olduğu izinler
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    /**
     * ✅ Aktif mi?
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * 🔒 Sistem rolü mü? (Silinebilir değil)
     */
    @Column(name = "is_system_role", nullable = false)
    private Boolean isSystemRole = false;

    /**
     * 📅 Oluşturulma tarihi
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ===============================
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 🔍 Bu rolün belirli bir izne sahip olup olmadığını kontrol eder
     */
    public boolean hasPermission(String permissionName) {
        return permissions.stream()
                .anyMatch(permission -> permission.getName().equalsIgnoreCase(permissionName));
    }

    /**
     * 🔑 İzin ekle
     */
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    /**
     * 🔑 İzin kaldır
     */
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    /**
     * 🔍 Sistem geneli rol mu?
     */
    public boolean isGlobalRole() {
        return tenant == null;
    }

    // ===============================
    // 📋 Rol Sabitleri (Excel'den)
    // ===============================
    
    public static final String SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ADMIN = "ADMIN";
    public static final String HR_MANAGER = "HR_MANAGER";
    public static final String HR_SPECIALIST = "HR_SPECIALIST";
    public static final String USER = "USER";
    public static final String CANDIDATE = "CANDIDATE";
} 
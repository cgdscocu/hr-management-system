package com.hrapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 🔑 Permission Entity - İzin Tablosu
 * 
 * Excel'den çıkardığımız yetkiler:
 * - TENANT_CREATE: Tenant oluşturma
 * - USER_MANAGE: Kullanıcı yönetimi
 * - ROLE_ASSIGN: Rol atama
 * - HR_PROCESS: İK süreçleri
 * - RESULTS_VIEW: Sonuçları görme
 * - PROJECT_CREATE: Proje oluş turma
 * vb.
 */
@Entity
@Table(name = "permissions", indexes = {
    @Index(name = "idx_permission_name", columnList = "name"),
    @Index(name = "idx_permission_resource", columnList = "resource")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    /**
     * 🆔 Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 🏷️ İzin Adı (USER_CREATE, ROLE_ASSIGN vb.)
     */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * 📝 Açıklama
     */
    @Column(length = 500)
    private String description;

    /**
     * 🎯 Kaynak - Hangi kaynağa ait (users, roles, tenants vb.)
     */
    @Column(nullable = false, length = 50)
    private String resource;

    /**
     * ⚡ Aksiyon - Ne yapılabilir (CREATE, READ, UPDATE, DELETE, ASSIGN vb.)
     */
    @Column(nullable = false, length = 50)
    private String action;

    /**
     * 📊 Kategori - İzin kategorisi (USER_MANAGEMENT, HR_OPERATIONS vb.)
     */
    @Column(length = 50)
    private String category;

    /**
     * ✅ Aktif mi?
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * 🔒 Sistem izni mi? (Silinebilir değil)
     */
    @Column(name = "is_system_permission", nullable = false)
    private Boolean isSystemPermission = false;

    /**
     * 📅 Oluşturulma tarihi
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ===============================
    // 📋 İzin Sabitleri (Excel'den çıkarılan)
    // ===============================
    
    // 🏢 Tenant İzinleri (Süper Admin)
    public static final String TENANT_CREATE = "TENANT_CREATE";
    public static final String TENANT_MANAGE = "TENANT_MANAGE";
    public static final String TENANT_DELETE = "TENANT_DELETE";
    
    // 👥 Kullanıcı İzinleri (Admin, HR Manager)
    public static final String USER_CREATE = "USER_CREATE";
    public static final String USER_READ = "USER_READ";
    public static final String USER_UPDATE = "USER_UPDATE";
    public static final String USER_DELETE = "USER_DELETE";
    public static final String USER_MANAGE = "USER_MANAGE";
    
    // 🎭 Rol İzinleri (Admin)
    public static final String ROLE_CREATE = "ROLE_CREATE";
    public static final String ROLE_ASSIGN = "ROLE_ASSIGN";
    public static final String ROLE_REMOVE = "ROLE_REMOVE";
    
    // 💼 İK İzinleri (HR Manager, HR Specialist)
    public static final String HR_PROCESS = "HR_PROCESS";
    public static final String RESULTS_VIEW = "RESULTS_VIEW";
    public static final String POSITION_MANAGE = "POSITION_MANAGE";
    public static final String DEPARTMENT_MANAGE = "DEPARTMENT_MANAGE";
    
    // 📊 Proje İzinleri (Admin, Proje Sahibi)
    public static final String PROJECT_CREATE = "PROJECT_CREATE";
    public static final String PROJECT_MANAGE = "PROJECT_MANAGE";
    public static final String PROJECT_ASSIGN = "PROJECT_ASSIGN";
    
    // 📝 Survey İzinleri (Admin)
    public static final String SURVEY_CREATE = "SURVEY_CREATE";
    public static final String SURVEY_ASSIGN = "SURVEY_ASSIGN";
    public static final String SURVEY_RESULTS = "SURVEY_RESULTS";
    
    // 📊 Dimension İzinleri (Admin, HR Manager)
    public static final String DIMENSION_CREATE = "DIMENSION_CREATE";
    public static final String DIMENSION_READ = "DIMENSION_READ";
    public static final String DIMENSION_UPDATE = "DIMENSION_UPDATE";
    public static final String DIMENSION_DELETE = "DIMENSION_DELETE";
    public static final String DIMENSION_MANAGE = "DIMENSION_MANAGE";
    
    // 🎯 Success Profile İzinleri (Admin, HR Manager)
    public static final String SUCCESS_PROFILE_CREATE = "SUCCESS_PROFILE_CREATE";
    public static final String SUCCESS_PROFILE_READ = "SUCCESS_PROFILE_READ";
    public static final String SUCCESS_PROFILE_UPDATE = "SUCCESS_PROFILE_UPDATE";
    public static final String SUCCESS_PROFILE_DELETE = "SUCCESS_PROFILE_DELETE";
    public static final String SUCCESS_PROFILE_MANAGE = "SUCCESS_PROFILE_MANAGE";
    
    // 📧 Mail İzinleri (Admin)
    public static final String MAIL_TEMPLATE_MANAGE = "MAIL_TEMPLATE_MANAGE";
    public static final String MAIL_SEND = "MAIL_SEND";
    
    // 🔍 Genel İzinler
    public static final String ALL_ACCESS = "ALL_ACCESS";
    public static final String PROFILE_VIEW = "PROFILE_VIEW";
    public static final String PROFILE_UPDATE = "PROFILE_UPDATE";
    
    // ===============================
    // 🛠️ Helper Methods
    // ===============================
    
    /**
     * 🔍 Tam izin kodu
     */
    public String getFullPermission() {
        return resource.toUpperCase() + "_" + action.toUpperCase();
    }
} 
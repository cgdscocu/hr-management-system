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
 * 🏗️ Department Entity - Departman Tablosu
 * 
 * Şirket içindeki departmanları tanımlar.
 * HR, IT, Sales, Marketing vb.
 */
@Entity
@Table(name = "departments", indexes = {
    @Index(name = "idx_department_tenant", columnList = "tenant_id"),
    @Index(name = "idx_department_manager", columnList = "manager_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    /**
     * 🆔 Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 🏷️ Departman Adı
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 📝 Açıklama
     */
    @Column(length = 500)
    private String description;

    /**
     * 🏢 Tenant - Bu departman hangi şirkete ait
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /**
     * 👨‍💼 Departman Müdürü
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    /**
     * 🏗️ Üst Departman (Hiyerarşi için)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_department_id")
    private Department parentDepartment;

    /**
     * 🌳 Alt Departmanlar
     */
    @OneToMany(mappedBy = "parentDepartment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Department> subDepartments = new HashSet<>();

    /**
     * 👥 Departmandaki kullanıcılar
     */
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    /**
     * 💼 Departmandaki pozisyonlar
     */
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Position> positions = new HashSet<>();

    /**
     * 📧 Departman Email
     */
    @Column(name = "department_email", length = 100)
    private String departmentEmail;

    /**
     * 📱 Telefon
     */
    @Column(length = 20)
    private String phone;

    /**
     * 📍 Lokasyon/Ofis
     */
    @Column(length = 200)
    private String location;

    /**
     * 💰 Bütçe
     */
    private Integer budget;

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
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 👥 Departmandaki toplam kullanıcı sayısı (alt departmanlar dahil)
     */
    public long getTotalUserCount() {
        long count = users.stream()
                .filter(User::getActive)
                .count();
        
        // Alt departmanları da say
        for (Department subDept : subDepartments) {
            count += subDept.getTotalUserCount();
        }
        
        return count;
    }

    /**
     * 💼 Departmandaki aktif pozisyon sayısı
     */
    public long getActivePositionCount() {
        return positions.stream()
                .filter(Position::getActive)
                .count();
    }

    /**
     * 🌳 Departman hiyerarşi seviyesi
     */
    public int getHierarchyLevel() {
        if (parentDepartment == null) {
            return 0;
        }
        return 1 + parentDepartment.getHierarchyLevel();
    }

    /**
     * 🏷️ Tam departman yolu (Parent > Child formatında)
     */
    public String getFullPath() {
        if (parentDepartment == null) {
            return name;
        }
        return parentDepartment.getFullPath() + " > " + name;
    }

    /**
     * 🔍 Alt departman ekle
     */
    public void addSubDepartment(Department subDepartment) {
        this.subDepartments.add(subDepartment);
        subDepartment.setParentDepartment(this);
    }

    /**
     * 🔍 Alt departman kaldır
     */
    public void removeSubDepartment(Department subDepartment) {
        this.subDepartments.remove(subDepartment);
        subDepartment.setParentDepartment(null);
    }
} 
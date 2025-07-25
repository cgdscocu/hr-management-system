package com.hrapp.repository;

import com.hrapp.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 🏗️ Department Repository - Departman Veritabanı İşlemleri
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * 🏷️ İsimle departman bul
     */
    Optional<Department> findByName(String name);

    /**
     * 🏢 Tenant'a göre departmanları getir
     */
    List<Department> findByTenantId(Long tenantId);

    /**
     * 🏢 Tenant'a göre aktif departmanları getir
     */
    List<Department> findByTenantIdAndActiveTrue(Long tenantId);

    /**
     * 👨‍💼 Manager'a göre departmanları getir
     */
    List<Department> findByManagerId(Long managerId);

    /**
     * 🌳 Üst departmana göre alt departmanları getir
     */
    List<Department> findByParentDepartmentId(Long parentDepartmentId);

    /**
     * 🌳 Ana departmanları getir (parent null olanlar)
     */
    List<Department> findByParentDepartmentIsNull();

    /**
     * 🏢 Tenant ve isimle departman bul
     */
    Optional<Department> findByNameAndTenantId(String name, Long tenantId);

    /**
     * 📊 Tenant'taki aktif departman sayısı
     */
    @Query("SELECT COUNT(d) FROM Department d WHERE d.tenant.id = :tenantId AND d.active = true")
    long countActiveDepartmentsByTenant(@Param("tenantId") Long tenantId);
} 
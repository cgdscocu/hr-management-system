package com.hrapp.repository;

import com.hrapp.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 💼 Position Repository - Pozisyon Veritabanı İşlemleri
 */
@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    /**
     * 🏷️ Başlıkla pozisyon bul
     */
    Optional<Position> findByTitle(String title);

    /**
     * 🏢 Tenant'a göre pozisyonları getir
     */
    List<Position> findByTenantId(Long tenantId);

    /**
     * 🏢 Tenant'a göre aktif pozisyonları getir
     */
    List<Position> findByTenantIdAndActiveTrue(Long tenantId);

    /**
     * 🏗️ Departmana göre pozisyonları getir
     */
    List<Position> findByDepartmentId(Long departmentId);

    /**
     * 📊 Seviyeye göre pozisyonları getir
     */
    List<Position> findByLevel(Position.PositionLevel level);

    /**
     * 💰 Maaş aralığına göre pozisyonları getir
     */
    @Query("SELECT p FROM Position p WHERE p.salaryMin >= :minSalary AND p.salaryMax <= :maxSalary")
    List<Position> findBySalaryRange(@Param("minSalary") Integer minSalary, @Param("maxSalary") Integer maxSalary);

    /**
     * 🏢 Tenant ve başlıkla pozisyon bul
     */
    Optional<Position> findByTitleAndTenantId(String title, Long tenantId);

    /**
     * 📊 Tenant'taki aktif pozisyon sayısı
     */
    @Query("SELECT COUNT(p) FROM Position p WHERE p.tenant.id = :tenantId AND p.active = true")
    long countActivePositionsByTenant(@Param("tenantId") Long tenantId);
} 
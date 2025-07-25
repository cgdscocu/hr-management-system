package com.hrapp.repository;

import com.hrapp.entity.SuccessProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 🎯 Success Profile Repository - Başarı Profili Veritabanı İşlemleri
 */
@Repository
public interface SuccessProfileRepository extends JpaRepository<SuccessProfile, Long> {

    /**
     * 🏷️ İsimle profil bul
     */
    Optional<SuccessProfile> findByName(String name);

    /**
     * 🏷️ İsmin var olup olmadığını kontrol et
     */
    boolean existsByName(String name);

    /**
     * 🏢 Tenant'a göre profilleri getir
     */
    List<SuccessProfile> findByTenantId(Long tenantId);

    /**
     * 🏢 Tenant'a göre aktif profilleri getir
     */
    List<SuccessProfile> findByTenantIdAndActiveTrue(Long tenantId);

    /**
     * 💼 Pozisyona göre profilleri getir
     */
    List<SuccessProfile> findByPositionId(Long positionId);

    /**
     * 💼 Pozisyona göre aktif profilleri getir
     */
    List<SuccessProfile> findByPositionIdAndActiveTrue(Long positionId);

    /**
     * 🏗️ Departmana göre profilleri getir
     */
    List<SuccessProfile> findByDepartmentId(Long departmentId);

    /**
     * 🏗️ Departmana göre aktif profilleri getir
     */
    List<SuccessProfile> findByDepartmentIdAndActiveTrue(Long departmentId);

    /**
     * 📊 Profil türüne göre profilleri getir
     */
    List<SuccessProfile> findByProfileType(SuccessProfile.ProfileType profileType);

    /**
     * 🏢 Tenant ve profil türüne göre profilleri getir
     */
    List<SuccessProfile> findByTenantIdAndProfileType(Long tenantId, SuccessProfile.ProfileType profileType);

    /**
     * ✅ Aktif profilleri getir
     */
    List<SuccessProfile> findByActiveTrue();

    /**
     * 🔒 Sistem profillerini getir
     */
    List<SuccessProfile> findByIsSystemProfileTrue();

    /**
     * 🔍 İsimle arama (LIKE)
     */
    @Query("SELECT sp FROM SuccessProfile sp WHERE sp.name LIKE %:name%")
    List<SuccessProfile> findByNameContaining(@Param("name") String name);

    /**
     * 🏢 Tenant ve isimle profil bul
     */
    Optional<SuccessProfile> findByNameAndTenantId(String name, Long tenantId);

    /**
     * 💼 Pozisyon ve isimle profil bul
     */
    Optional<SuccessProfile> findByNameAndPositionId(String name, Long positionId);

    /**
     * 👤 Oluşturan kullanıcıya göre profilleri getir
     */
    List<SuccessProfile> findByCreatedById(Long createdById);

    /**
     * 🎯 Belirli skor aralığındaki profilleri getir
     */
    @Query("SELECT sp FROM SuccessProfile sp WHERE sp.minSuccessScore >= :minScore AND sp.targetSuccessScore <= :maxScore")
    List<SuccessProfile> findByScoreRange(@Param("minScore") Double minScore, @Param("maxScore") Double maxScore);

    /**
     * 📊 Belirli dimension'a sahip profilleri getir
     */
    @Query("SELECT sp FROM SuccessProfile sp JOIN sp.dimensions spd WHERE spd.dimension.id = :dimensionId AND spd.active = true")
    List<SuccessProfile> findByDimensionId(@Param("dimensionId") Long dimensionId);

    /**
     * 📊 Belirli dimension'a sahip aktif profilleri getir
     */
    @Query("SELECT sp FROM SuccessProfile sp JOIN sp.dimensions spd WHERE spd.dimension.id = :dimensionId AND spd.active = true AND sp.active = true")
    List<SuccessProfile> findActiveBydimensionId(@Param("dimensionId") Long dimensionId);

    /**
     * 🏢 Tenant'taki profil sayısı
     */
    @Query("SELECT COUNT(sp) FROM SuccessProfile sp WHERE sp.tenant.id = :tenantId")
    long countByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 🏢 Tenant'taki aktif profil sayısı
     */
    @Query("SELECT COUNT(sp) FROM SuccessProfile sp WHERE sp.tenant.id = :tenantId AND sp.active = true")
    long countActiveProfilesByTenant(@Param("tenantId") Long tenantId);

    /**
     * 💼 Pozisyondaki profil sayısı
     */
    @Query("SELECT COUNT(sp) FROM SuccessProfile sp WHERE sp.position.id = :positionId AND sp.active = true")
    long countActiveProfilesByPosition(@Param("positionId") Long positionId);

    /**
     * 📊 Profil türündeki profil sayısı
     */
    @Query("SELECT COUNT(sp) FROM SuccessProfile sp WHERE sp.tenant.id = :tenantId AND sp.profileType = :profileType AND sp.active = true")
    long countByTenantIdAndProfileType(@Param("tenantId") Long tenantId, @Param("profileType") SuccessProfile.ProfileType profileType);
} 
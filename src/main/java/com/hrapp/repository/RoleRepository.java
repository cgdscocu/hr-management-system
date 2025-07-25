package com.hrapp.repository;

import com.hrapp.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 🎭 Role Repository - Rol Veritabanı İşlemleri
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 🏷️ İsimle rol bul
     */
    Optional<Role> findByName(String name);

    /**
     * 🏷️ İsmin var olup olmadığını kontrol et
     */
    boolean existsByName(String name);

    /**
     * 🏢 Tenant'a göre rolleri getir (tenant-specific roller)
     */
    List<Role> findByTenantId(Long tenantId);

    /**
     * 🌐 Global rolleri getir (tenant null olanlar)
     */
    List<Role> findByTenantIsNull();

    /**
     * ✅ Aktif rolleri getir
     */
    List<Role> findByActiveTrue();

    /**
     * 🏢 Tenant'a göre aktif rolleri getir
     */
    List<Role> findByTenantIdAndActiveTrue(Long tenantId);

    /**
     * 🔍 İsimle arama (LIKE)
     */
    @Query("SELECT r FROM Role r WHERE r.name LIKE %:name%")
    List<Role> findByNameContaining(@Param("name") String name);

    /**
     * 🔑 Belirli bir izne sahip rolleri getir
     */
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.name = :permissionName")
    List<Role> findByPermissionName(@Param("permissionName") String permissionName);

    /**
     * 🏢 Tenant ve isimle rol bul
     */
    Optional<Role> findByNameAndTenantId(String name, Long tenantId);

    /**
     * 🌐 Global rolü isimle bul (tenant null)
     */
    Optional<Role> findByNameAndTenantIsNull(String name);
} 
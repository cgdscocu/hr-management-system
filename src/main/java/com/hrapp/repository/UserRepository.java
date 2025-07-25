package com.hrapp.repository;

import com.hrapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 👤 User Repository - Kullanıcı Veritabanı İşlemleri
 * 
 * JpaRepository Spring Data JPA'nın sunduğu hazır methodları içerir:
 * - save() - Kaydet
 * - findById() - ID ile bul
 * - findAll() - Hepsini getir
 * - deleteById() - ID ile sil
 * - count() - Sayısını getir
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 📧 Email ile kullanıcı bul (Login için)
     */
    Optional<User> findByEmail(String email);

    /**
     * 📧 Email'in var olup olmadığını kontrol et
     */
    boolean existsByEmail(String email);

    /**
     * 🏢 Tenant'a göre kullanıcıları getir
     */
    List<User> findByTenantId(Long tenantId);

    /**
     * 🏢 Tenant'a göre aktif kullanıcıları getir
     */
    List<User> findByTenantIdAndActiveTrue(Long tenantId);

    /**
     * 🎭 Role göre kullanıcıları getir
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    /**
     * 🏗️ Departmana göre kullanıcıları getir
     */
    List<User> findByDepartmentId(Long departmentId);

    /**
     * 💼 Pozisyona göre kullanıcıları getir
     */
    List<User> findByPositionId(Long positionId);

    /**
     * 🔍 İsim ve soyisimle arama (LIKE)
     */
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    List<User> findByNameContaining(@Param("name") String name);

    /**
     * 📊 Tenant'taki aktif kullanıcı sayısı
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.tenant.id = :tenantId AND u.active = true")
    long countActiveUsersByTenant(@Param("tenantId") Long tenantId);
} 
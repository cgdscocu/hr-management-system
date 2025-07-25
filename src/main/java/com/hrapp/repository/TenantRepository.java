package com.hrapp.repository;

import com.hrapp.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 🏢 Tenant Repository - Şirket/Organizasyon Veritabanı İşlemleri
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    /**
     * 🌐 Domain ile tenant bul
     */
    Optional<Tenant> findByDomain(String domain);

    /**
     * 🏷️ İsimle tenant bul
     */
    Optional<Tenant> findByName(String name);

    /**
     * 🌐 Domain'in var olup olmadığını kontrol et
     */
    boolean existsByDomain(String domain);

    /**
     * 🏷️ İsmin var olup olmadığını kontrol et
     */
    boolean existsByName(String name);

    /**
     * ✅ Aktif tenant'ları getir
     */
    List<Tenant> findByActiveTrue();

    /**
     * 🔍 İsimle arama (LIKE)
     */
    @Query("SELECT t FROM Tenant t WHERE t.name LIKE %:name%")
    List<Tenant> findByNameContaining(@Param("name") String name);

    /**
     * 🌍 Şehire göre tenant'ları getir
     */
    List<Tenant> findByCity(String city);

    /**
     * 🏳️ Ülkeye göre tenant'ları getir
     */
    List<Tenant> findByCountry(String country);

    /**
     * 📊 Aktif tenant sayısı
     */
    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.active = true")
    long countActiveTenants();
} 
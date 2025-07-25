package com.hrapp.repository;

import com.hrapp.entity.Dimension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 📊 Dimension Repository - Boyut Veritabanı İşlemleri
 */
@Repository
public interface DimensionRepository extends JpaRepository<Dimension, Long> {

    /**
     * 🏷️ İsimle boyut bul
     */
    Optional<Dimension> findByName(String name);

    /**
     * 🏷️ İsmin var olup olmadığını kontrol et
     */
    boolean existsByName(String name);

    /**
     * 🏢 Tenant'a göre boyutları getir
     */
    List<Dimension> findByTenantId(Long tenantId);

    /**
     * 🏢 Tenant'a göre aktif boyutları getir
     */
    List<Dimension> findByTenantIdAndActiveTrue(Long tenantId);

    /**
     * 📊 Kategoriye göre boyutları getir
     */
    List<Dimension> findByCategory(Dimension.DimensionCategory category);

    /**
     * 🏢 Tenant ve kategoriye göre boyutları getir
     */
    List<Dimension> findByTenantIdAndCategory(Long tenantId, Dimension.DimensionCategory category);

    /**
     * 🏢 Tenant ve kategoriye göre aktif boyutları getir
     */
    List<Dimension> findByTenantIdAndCategoryAndActiveTrue(Long tenantId, Dimension.DimensionCategory category);

    /**
     * ✅ Aktif boyutları getir
     */
    List<Dimension> findByActiveTrue();

    /**
     * 🔒 Sistem boyutlarını getir
     */
    List<Dimension> findByIsSystemDimensionTrue();

    /**
     * 🔍 İsimle arama (LIKE)
     */
    @Query("SELECT d FROM Dimension d WHERE d.name LIKE %:name%")
    List<Dimension> findByNameContaining(@Param("name") String name);

    /**
     * 🏢 Tenant ve isimle boyut bul
     */
    Optional<Dimension> findByNameAndTenantId(String name, Long tenantId);

    /**
     * 📊 Display order'a göre sıralı getir
     */
    @Query("SELECT d FROM Dimension d WHERE d.tenant.id = :tenantId AND d.active = true ORDER BY d.displayOrder ASC, d.name ASC")
    List<Dimension> findByTenantIdOrderByDisplayOrder(@Param("tenantId") Long tenantId);

    /**
     * 📊 Kategoriye göre sıralı getir
     */
    @Query("SELECT d FROM Dimension d WHERE d.tenant.id = :tenantId AND d.category = :category AND d.active = true ORDER BY d.displayOrder ASC, d.name ASC")
    List<Dimension> findByTenantIdAndCategoryOrderByDisplayOrder(@Param("tenantId") Long tenantId, @Param("category") Dimension.DimensionCategory category);

    /**
     * 👤 Oluşturan kullanıcıya göre boyutları getir
     */
    List<Dimension> findByCreatedById(Long createdById);

    /**
     * 📏 Ölçek türüne göre boyutları getir
     */
    List<Dimension> findByScaleType(Dimension.ScaleType scaleType);

    /**
     * ⚖️ Ağırlık aralığına göre boyutları getir
     */
    @Query("SELECT d FROM Dimension d WHERE d.weight >= :minWeight AND d.weight <= :maxWeight")
    List<Dimension> findByWeightRange(@Param("minWeight") Double minWeight, @Param("maxWeight") Double maxWeight);

    /**
     * 📊 Tenant'taki boyut sayısı
     */
    @Query("SELECT COUNT(d) FROM Dimension d WHERE d.tenant.id = :tenantId")
    long countByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 📊 Tenant'taki aktif boyut sayısı
     */
    @Query("SELECT COUNT(d) FROM Dimension d WHERE d.tenant.id = :tenantId AND d.active = true")
    long countActiveDimensionsByTenant(@Param("tenantId") Long tenantId);

    /**
     * 📊 Kategorideki boyut sayısı
     */
    @Query("SELECT COUNT(d) FROM Dimension d WHERE d.tenant.id = :tenantId AND d.category = :category AND d.active = true")
    long countByTenantIdAndCategory(@Param("tenantId") Long tenantId, @Param("category") Dimension.DimensionCategory category);
} 
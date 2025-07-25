package com.hrapp.repository;

import com.hrapp.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 🔑 Permission Repository - İzin Veritabanı İşlemleri
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * 🏷️ İsimle izin bul
     */
    Optional<Permission> findByName(String name);

    /**
     * 🏷️ İsmin var olup olmadığını kontrol et
     */
    boolean existsByName(String name);

    /**
     * 🎯 Kaynağa göre izinleri getir
     */
    List<Permission> findByResource(String resource);

    /**
     * ⚡ Aksiyona göre izinleri getir
     */
    List<Permission> findByAction(String action);

    /**
     * 📊 Kategoriye göre izinleri getir
     */
    List<Permission> findByCategory(String category);

    /**
     * 🎯 Kaynak ve aksiyona göre izin bul
     */
    Optional<Permission> findByResourceAndAction(String resource, String action);

    /**
     * ✅ Aktif izinleri getir
     */
    List<Permission> findByActiveTrue();

    /**
     * 🔒 Sistem izinlerini getir
     */
    List<Permission> findByIsSystemPermissionTrue();

    /**
     * 🔍 İsimle arama (LIKE)
     */
    @Query("SELECT p FROM Permission p WHERE p.name LIKE %:name%")
    List<Permission> findByNameContaining(@Param("name") String name);
} 
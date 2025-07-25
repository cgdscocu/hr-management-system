package com.hrapp.repository;

import com.hrapp.entity.SuccessProfileDimension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 🔗 Success Profile Dimension Repository - Başarı Profili-Dimension İlişki Veritabanı İşlemleri
 */
@Repository
public interface SuccessProfileDimensionRepository extends JpaRepository<SuccessProfileDimension, Long> {

    /**
     * 🎯 Başarı profiline göre dimension'ları getir
     */
    List<SuccessProfileDimension> findBySuccessProfileId(Long successProfileId);

    /**
     * 🎯 Başarı profiline göre aktif dimension'ları getir
     */
    List<SuccessProfileDimension> findBySuccessProfileIdAndActiveTrue(Long successProfileId);

    /**
     * 📊 Dimension'a göre profilleri getir
     */
    List<SuccessProfileDimension> findByDimensionId(Long dimensionId);

    /**
     * 📊 Dimension'a göre aktif profilleri getir
     */
    List<SuccessProfileDimension> findByDimensionIdAndActiveTrue(Long dimensionId);

    /**
     * 🔍 Profil ve dimension kombinasyonu bul
     */
    Optional<SuccessProfileDimension> findBySuccessProfileIdAndDimensionId(Long successProfileId, Long dimensionId);

    /**
     * 🔍 Aktif profil ve dimension kombinasyonu bul
     */
    Optional<SuccessProfileDimension> findBySuccessProfileIdAndDimensionIdAndActiveTrue(Long successProfileId, Long dimensionId);

    /**
     * 🔥 Kritik dimension'ları getir
     */
    List<SuccessProfileDimension> findByIsCriticalTrue();

    /**
     * 🎯 Profildeki kritik dimension'ları getir
     */
    List<SuccessProfileDimension> findBySuccessProfileIdAndIsCriticalTrue(Long successProfileId);

    /**
     * 📊 Display order'a göre sıralı getir
     */
    @Query("SELECT spd FROM SuccessProfileDimension spd WHERE spd.successProfile.id = :successProfileId AND spd.active = true ORDER BY spd.displayOrder ASC, spd.dimension.name ASC")
    List<SuccessProfileDimension> findBySuccessProfileIdOrderByDisplayOrder(@Param("successProfileId") Long successProfileId);

    /**
     * ⚖️ Ağırlık aralığına göre dimension'ları getir
     */
    @Query("SELECT spd FROM SuccessProfileDimension spd WHERE spd.weight >= :minWeight AND spd.weight <= :maxWeight")
    List<SuccessProfileDimension> findByWeightRange(@Param("minWeight") Double minWeight, @Param("maxWeight") Double maxWeight);

    /**
     * 🎯 Minimum skor aralığına göre dimension'ları getir
     */
    @Query("SELECT spd FROM SuccessProfileDimension spd WHERE spd.minScore >= :minScore AND spd.minScore <= :maxScore")
    List<SuccessProfileDimension> findByMinScoreRange(@Param("minScore") Double minScore, @Param("maxScore") Double maxScore);

    /**
     * 🎯 Hedef skor aralığına göre dimension'ları getir
     */
    @Query("SELECT spd FROM SuccessProfileDimension spd WHERE spd.targetScore >= :minScore AND spd.targetScore <= :maxScore")
    List<SuccessProfileDimension> findByTargetScoreRange(@Param("minScore") Double minScore, @Param("maxScore") Double maxScore);

    /**
     * 📊 Profildeki dimension sayısı
     */
    @Query("SELECT COUNT(spd) FROM SuccessProfileDimension spd WHERE spd.successProfile.id = :successProfileId")
    long countBySuccessProfileId(@Param("successProfileId") Long successProfileId);

    /**
     * 📊 Profildeki aktif dimension sayısı
     */
    @Query("SELECT COUNT(spd) FROM SuccessProfileDimension spd WHERE spd.successProfile.id = :successProfileId AND spd.active = true")
    long countActiveBySuccessProfileId(@Param("successProfileId") Long successProfileId);

    /**
     * 🔥 Profildeki kritik dimension sayısı
     */
    @Query("SELECT COUNT(spd) FROM SuccessProfileDimension spd WHERE spd.successProfile.id = :successProfileId AND spd.isCritical = true AND spd.active = true")
    long countCriticalBySuccessProfileId(@Param("successProfileId") Long successProfileId);

    /**
     * ⚖️ Toplam ağırlık hesapla
     */
    @Query("SELECT SUM(spd.weight) FROM SuccessProfileDimension spd WHERE spd.successProfile.id = :successProfileId AND spd.active = true")
    Double sumWeightBySuccessProfileId(@Param("successProfileId") Long successProfileId);

    /**
     * 🗑️ Profildeki tüm dimension'ları sil
     */
    void deleteBySuccessProfileId(Long successProfileId);

    /**
     * 🗑️ Dimension'ın tüm profil ilişkilerini sil
     */
    void deleteByDimensionId(Long dimensionId);
} 
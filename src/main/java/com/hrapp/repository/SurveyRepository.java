package com.hrapp.repository;

import com.hrapp.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 📋 Survey Repository - Anket Veritabanı İşlemleri
 */
@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    /**
     * 🏷️ Başlıkla anket bul
     */
    Optional<Survey> findByTitle(String title);

    /**
     * 🏷️ Başlığın var olup olmadığını kontrol et
     */
    boolean existsByTitle(String title);

    /**
     * 🏢 Tenant'a göre anketleri getir
     */
    List<Survey> findByTenantId(Long tenantId);

    /**
     * 🏢 Tenant'a göre aktif anketleri getir
     */
    List<Survey> findByTenantIdAndActiveTrue(Long tenantId);

    /**
     * 📊 Anket türüne göre anketleri getir
     */
    List<Survey> findBySurveyType(Survey.SurveyType surveyType);

    /**
     * 📊 Anket durumuna göre anketleri getir
     */
    List<Survey> findByStatus(Survey.SurveyStatus status);

    /**
     * 🏢 Tenant ve duruma göre anketleri getir
     */
    List<Survey> findByTenantIdAndStatus(Long tenantId, Survey.SurveyStatus status);

    /**
     * 🎯 Hedef gruba göre anketleri getir
     */
    List<Survey> findByTargetGroup(Survey.TargetGroup targetGroup);

    /**
     * 🏗️ Hedef departmana göre anketleri getir
     */
    List<Survey> findByTargetDepartmentId(Long departmentId);

    /**
     * 💼 Hedef pozisyona göre anketleri getir
     */
    List<Survey> findByTargetPositionId(Long positionId);

    /**
     * ✅ Aktif anketleri getir
     */
    List<Survey> findByActiveTrue();

    /**
     * 🔒 Sistem anketlerini getir
     */
    List<Survey> findByIsSystemSurveyTrue();

    /**
     * 🔍 Başlıkla arama (LIKE)
     */
    @Query("SELECT s FROM Survey s WHERE s.title LIKE %:title%")
    List<Survey> findByTitleContaining(@Param("title") String title);

    /**
     * 🏢 Tenant ve başlıkla anket bul
     */
    Optional<Survey> findByTitleAndTenantId(String title, Long tenantId);

    /**
     * 👤 Oluşturan kullanıcıya göre anketleri getir
     */
    List<Survey> findByCreatedById(Long createdById);

    /**
     * 📅 Tarih aralığındaki anketleri getir
     */
    @Query("SELECT s FROM Survey s WHERE s.startDate >= :startDate AND s.endDate <= :endDate")
    List<Survey> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * ⏰ Şu anda aktif olan anketleri getir
     */
    @Query("SELECT s FROM Survey s WHERE s.active = true AND s.status = 'ACTIVE' AND " +
           "(s.startDate IS NULL OR s.startDate <= :now) AND " +
           "(s.endDate IS NULL OR s.endDate >= :now)")
    List<Survey> findCurrentlyActivesurveys(@Param("now") LocalDateTime now);

    /**
     * ⏰ Süresi yaklaşan anketleri getir (belirtilen gün içinde)
     */
    @Query("SELECT s FROM Survey s WHERE s.active = true AND s.status = 'ACTIVE' AND " +
           "s.endDate IS NOT NULL AND s.endDate <= :deadline")
    List<Survey> findSurveysDueSoon(@Param("deadline") LocalDateTime deadline);

    /**
     * 🔄 Tekrarlanabilir anketleri getir
     */
    List<Survey> findByIsRepeatableTrue();

    /**
     * 🔒 Anonim anketleri getir
     */
    List<Survey> findByIsAnonymousTrue();

    /**
     * ⏱️ Belirli süre aralığındaki anketleri getir
     */
    @Query("SELECT s FROM Survey s WHERE s.estimatedDuration >= :minDuration AND s.estimatedDuration <= :maxDuration")
    List<Survey> findByDurationRange(@Param("minDuration") Integer minDuration, @Param("maxDuration") Integer maxDuration);

    /**
     * 📊 Tenant'taki anket sayısı
     */
    @Query("SELECT COUNT(s) FROM Survey s WHERE s.tenant.id = :tenantId")
    long countByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 📊 Tenant'taki aktif anket sayısı
     */
    @Query("SELECT COUNT(s) FROM Survey s WHERE s.tenant.id = :tenantId AND s.active = true")
    long countActiveSurveysByTenant(@Param("tenantId") Long tenantId);

    /**
     * 📊 Durumdaki anket sayısı
     */
    @Query("SELECT COUNT(s) FROM Survey s WHERE s.tenant.id = :tenantId AND s.status = :status")
    long countByTenantIdAndStatus(@Param("tenantId") Long tenantId, @Param("status") Survey.SurveyStatus status);

    /**
     * 📊 Anket türündeki anket sayısı
     */
    @Query("SELECT COUNT(s) FROM Survey s WHERE s.tenant.id = :tenantId AND s.surveyType = :surveyType")
    long countByTenantIdAndSurveyType(@Param("tenantId") Long tenantId, @Param("surveyType") Survey.SurveyType surveyType);

    /**
     * 📧 Email bildirimi gönderilecek anketleri getir
     */
    List<Survey> findBySendEmailNotificationTrue();

    /**
     * 📊 Maksimum yanıt sayısı olan anketleri getir
     */
    @Query("SELECT s FROM Survey s WHERE s.maxResponses IS NOT NULL")
    List<Survey> findSurveysWithMaxResponses();

    /**
     * 📊 Yanıt sayısı belirli aralıkta olan anketleri getir
     */
    @Query("SELECT s FROM Survey s WHERE " +
           "(SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey = s AND sr.responseStatus = 'SUBMITTED') " +
           "BETWEEN :minResponses AND :maxResponses")
    List<Survey> findByResponseCountRange(@Param("minResponses") long minResponses, @Param("maxResponses") long maxResponses);

    /**
     * 🎯 Kullanıcının hedef grubunda olduğu anketleri getir
     */
    @Query("SELECT s FROM Survey s WHERE s.tenant.id = :tenantId AND s.active = true AND s.status = 'ACTIVE' AND " +
           "(s.targetGroup = 'ALL_EMPLOYEES' OR " +
           "(s.targetGroup = 'DEPARTMENT' AND s.targetDepartment.id = :departmentId) OR " +
           "(s.targetGroup = 'POSITION' AND s.targetPosition.id = :positionId))")
    List<Survey> findAvailableSurveysForUser(@Param("tenantId") Long tenantId, 
                                           @Param("departmentId") Long departmentId, 
                                           @Param("positionId") Long positionId);
} 
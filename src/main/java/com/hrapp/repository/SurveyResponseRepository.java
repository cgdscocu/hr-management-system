package com.hrapp.repository;

import com.hrapp.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 📝 Survey Response Repository - Anket Yanıtları Veritabanı İşlemleri
 */
@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    /**
     * 📋 Anket ID'sine göre yanıtları getir
     */
    List<SurveyResponse> findBySurveyId(Long surveyId);

    /**
     * 👤 Kullanıcı ID'sine göre yanıtları getir
     */
    List<SurveyResponse> findByRespondentId(Long respondentId);

    /**
     * 📋 Anket ve kullanıcı kombinasyonu
     */
    Optional<SurveyResponse> findBySurveyIdAndRespondentId(Long surveyId, Long respondentId);

    /**
     * 📊 Yanıt durumuna göre yanıtları getir
     */
    List<SurveyResponse> findByResponseStatus(SurveyResponse.ResponseStatus responseStatus);

    /**
     * 📋 Anket ve durum kombinasyonu
     */
    List<SurveyResponse> findBySurveyIdAndResponseStatus(Long surveyId, SurveyResponse.ResponseStatus responseStatus);

    /**
     * 📤 Teslim edilmiş yanıtları getir
     */
    List<SurveyResponse> findByResponseStatusOrderBySubmittedAtDesc(SurveyResponse.ResponseStatus responseStatus);

    /**
     * 📋 Anketin teslim edilmiş yanıtları
     */
    List<SurveyResponse> findBySurveyIdAndResponseStatusOrderBySubmittedAtDesc(Long surveyId, SurveyResponse.ResponseStatus responseStatus);

    /**
     * 💻 Platform'a göre yanıtları getir
     */
    List<SurveyResponse> findByPlatform(SurveyResponse.Platform platform);

    /**
     * 🔒 Anonim yanıtları getir
     */
    List<SurveyResponse> findByIsAnonymousTrue();

    /**
     * 📋 Anketin anonim yanıtları
     */
    List<SurveyResponse> findBySurveyIdAndIsAnonymousTrue(Long surveyId);

    /**
     * 📅 Tarih aralığındaki yanıtları getir
     */
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.submittedAt >= :startDate AND sr.submittedAt <= :endDate")
    List<SurveyResponse> findBySubmittedDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 📊 Tamamlanma yüzdesi aralığına göre yanıtları getir
     */
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.completionPercentage >= :minPercentage AND sr.completionPercentage <= :maxPercentage")
    List<SurveyResponse> findByCompletionPercentageRange(@Param("minPercentage") Double minPercentage, @Param("maxPercentage") Double maxPercentage);

    /**
     * ⏱️ Tamamlanma süresi aralığına göre yanıtları getir
     */
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.completionTime >= :minTime AND sr.completionTime <= :maxTime")
    List<SurveyResponse> findByCompletionTimeRange(@Param("minTime") Integer minTime, @Param("maxTime") Integer maxTime);

    /**
     * 🔢 Skor aralığına göre yanıtları getir
     */
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.totalScore >= :minScore AND sr.totalScore <= :maxScore")
    List<SurveyResponse> findByScoreRange(@Param("minScore") Double minScore, @Param("maxScore") Double maxScore);

    /**
     * 📊 Kalite puanına göre yanıtları getir
     */
    List<SurveyResponse> findByQualityScore(Integer qualityScore);

    /**
     * 🌐 IP adresine göre yanıtları getir
     */
    List<SurveyResponse> findByIpAddress(String ipAddress);

    /**
     * 📋 Anketdeki toplam yanıt sayısı
     */
    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    long countBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * 📋 Anketdeki teslim edilmiş yanıt sayısı
     */
    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.responseStatus = 'SUBMITTED'")
    long countSubmittedBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * 📋 Anketdeki tamamlanan yanıt sayısı
     */
    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.responseStatus IN ('COMPLETED', 'SUBMITTED')")
    long countCompletedBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * 👤 Kullanıcının toplam yanıt sayısı
     */
    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.respondent.id = :respondentId")
    long countByRespondentId(@Param("respondentId") Long respondentId);

    /**
     * 👤 Kullanıcının teslim edilmiş yanıt sayısı
     */
    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.respondent.id = :respondentId AND sr.responseStatus = 'SUBMITTED'")
    long countSubmittedByRespondentId(@Param("respondentId") Long respondentId);

    /**
     * 📊 Durumdaki yanıt sayısı
     */
    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.responseStatus = :status")
    long countByResponseStatus(@Param("status") SurveyResponse.ResponseStatus status);

    /**
     * 📊 Platform'daki yanıt sayısı
     */
    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.platform = :platform")
    long countByPlatform(@Param("platform") SurveyResponse.Platform platform);

    /**
     * 📊 Anonim yanıt sayısı
     */
    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.isAnonymous = true")
    long countAnonymousResponses();

    /**
     * 📊 Anketin ortalama tamamlanma süresi
     */
    @Query("SELECT AVG(sr.completionTime) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.responseStatus = 'SUBMITTED'")
    Double getAverageCompletionTimeBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * 📊 Anketin ortalama skoru
     */
    @Query("SELECT AVG(sr.totalScore) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.responseStatus = 'SUBMITTED'")
    Double getAverageScoreBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * 📊 Anketin ortalama tamamlanma yüzdesi
     */
    @Query("SELECT AVG(sr.completionPercentage) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    Double getAverageCompletionPercentageBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * 📊 Kullanıcının belirli anketi yanıtladı mı?
     */
    @Query("SELECT COUNT(sr) > 0 FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.respondent.id = :respondentId AND sr.responseStatus = 'SUBMITTED'")
    boolean hasUserSubmittedSurvey(@Param("surveyId") Long surveyId, @Param("respondentId") Long respondentId);

    /**
     * 📊 Kullanıcının ankete başladı mı?
     */
    @Query("SELECT COUNT(sr) > 0 FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.respondent.id = :respondentId")
    boolean hasUserStartedSurvey(@Param("surveyId") Long surveyId, @Param("respondentId") Long respondentId);

    /**
     * 🏆 En yüksek skorlu yanıtları getir
     */
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.responseStatus = 'SUBMITTED' ORDER BY sr.totalScore DESC")
    List<SurveyResponse> findTopScorersBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * ⏱️ En hızlı tamamlanan yanıtları getir
     */
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.responseStatus = 'SUBMITTED' ORDER BY sr.completionTime ASC")
    List<SurveyResponse> findFastestResponsesBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * 📅 Son teslim edilen yanıtları getir
     */
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.responseStatus = 'SUBMITTED' ORDER BY sr.submittedAt DESC")
    List<SurveyResponse> findRecentlySubmittedResponses();

    /**
     * 🗑️ Anketin tüm yanıtlarını sil
     */
    void deleteBySurveyId(Long surveyId);

    /**
     * 🗑️ Kullanıcının tüm yanıtlarını sil
     */
    void deleteByRespondentId(Long respondentId);
} 
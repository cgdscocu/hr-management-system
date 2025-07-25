package com.hrapp.repository;

import com.hrapp.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ❓ Question Repository - Anket Soruları Veritabanı İşlemleri
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * 📋 Anket ID'sine göre soruları getir
     */
    List<Question> findBySurveyId(Long surveyId);

    /**
     * 📋 Anket ID'sine göre aktif soruları getir
     */
    List<Question> findBySurveyIdAndActiveTrue(Long surveyId);

    /**
     * 📊 Soru türüne göre soruları getir
     */
    List<Question> findByQuestionType(Question.QuestionType questionType);

    /**
     * 📋 Anket ve soru türüne göre soruları getir
     */
    List<Question> findBySurveyIdAndQuestionType(Long surveyId, Question.QuestionType questionType);

    /**
     * ✅ Zorunlu soruları getir
     */
    List<Question> findByRequiredTrue();

    /**
     * 📋 Anket ID'sine göre zorunlu soruları getir
     */
    List<Question> findBySurveyIdAndRequiredTrue(Long surveyId);

    /**
     * ✅ Aktif soruları getir
     */
    List<Question> findByActiveTrue();

    /**
     * 📊 Display order'a göre sıralı getir
     */
    @Query("SELECT q FROM Question q WHERE q.survey.id = :surveyId AND q.active = true ORDER BY q.displayOrder ASC")
    List<Question> findBySurveyIdOrderByDisplayOrder(@Param("surveyId") Long surveyId);

    /**
     * 📊 Dimension ile ilişkili soruları getir
     */
    List<Question> findByDimensionId(Long dimensionId);

    /**
     * 📊 Dimension ile ilişkili aktif soruları getir
     */
    List<Question> findByDimensionIdAndActiveTrue(Long dimensionId);

    /**
     * 🔀 Koşullu soruları getir
     */
    @Query("SELECT q FROM Question q WHERE q.conditionalQuestionId IS NOT NULL")
    List<Question> findConditionalQuestions();

    /**
     * 🔀 Belirli soruya bağlı koşullu soruları getir
     */
    List<Question> findByConditionalQuestionId(Long conditionalQuestionId);

    /**
     * 📱 Sadece mobil soruları getir
     */
    List<Question> findByMobileOnlyTrue();

    /**
     * 🖥️ Sadece web soruları getir
     */
    List<Question> findByWebOnlyTrue();

    /**
     * ⚖️ Ağırlık aralığına göre soruları getir
     */
    @Query("SELECT q FROM Question q WHERE q.weight >= :minWeight AND q.weight <= :maxWeight")
    List<Question> findByWeightRange(@Param("minWeight") Double minWeight, @Param("maxWeight") Double maxWeight);

    /**
     * 📏 Değer aralığına göre soruları getir
     */
    @Query("SELECT q FROM Question q WHERE q.minValue >= :minValue AND q.maxValue <= :maxValue")
    List<Question> findByValueRange(@Param("minValue") Integer minValue, @Param("maxValue") Integer maxValue);

    /**
     * 🔍 Soru metni ile arama (LIKE)
     */
    @Query("SELECT q FROM Question q WHERE q.questionText LIKE %:text%")
    List<Question> findByQuestionTextContaining(@Param("text") String text);

    /**
     * 📊 Anketdeki soru sayısı
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.survey.id = :surveyId")
    long countBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * 📊 Anketdeki aktif soru sayısı
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.survey.id = :surveyId AND q.active = true")
    long countActiveBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * 📊 Anketdeki zorunlu soru sayısı
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.survey.id = :surveyId AND q.required = true AND q.active = true")
    long countRequiredBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * 📊 Soru türündeki soru sayısı
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.survey.id = :surveyId AND q.questionType = :questionType AND q.active = true")
    long countByQuestionType(@Param("surveyId") Long surveyId, @Param("questionType") Question.QuestionType questionType);

    /**
     * 📊 Dimension ile ilişkili soru sayısı
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.dimension.id = :dimensionId AND q.active = true")
    long countByDimensionId(@Param("dimensionId") Long dimensionId);

    /**
     * 📊 Maksimum display order'ı getir
     */
    @Query("SELECT MAX(q.displayOrder) FROM Question q WHERE q.survey.id = :surveyId")
    Integer findMaxDisplayOrderBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * 📊 Sonraki display order'ı hesapla
     */
    default Integer getNextDisplayOrder(Long surveyId) {
        Integer maxOrder = findMaxDisplayOrderBySurveyId(surveyId);
        return maxOrder != null ? maxOrder + 1 : 1;
    }

    /**
     * 🔀 Koşullu soru ve değer kombinasyonu kontrol et
     */
    @Query("SELECT q FROM Question q WHERE q.conditionalQuestionId = :conditionalQuestionId AND q.conditionalValue = :conditionalValue")
    List<Question> findByConditionalQuestionIdAndValue(@Param("conditionalQuestionId") Long conditionalQuestionId, 
                                                      @Param("conditionalValue") String conditionalValue);

    /**
     * 📊 Seçenekli soruları getir
     */
    @Query("SELECT q FROM Question q WHERE q.options IS NOT NULL AND q.options != ''")
    List<Question> findQuestionsWithOptions();

    /**
     * 📋 Anketdeki seçenekli soru sayısı
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.survey.id = :surveyId AND q.options IS NOT NULL AND q.options != '' AND q.active = true")
    long countQuestionsWithOptionsBySurveyId(@Param("surveyId") Long surveyId);
} 
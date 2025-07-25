package com.hrapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * ❓ Question Entity - Anket Soruları
 * 
 * Survey'lerde kullanılan sorular:
 * - Çoktan Seçmeli
 * - Likert Ölçeği
 * - Açık Uçlu
 * - Evet/Hayır
 * - Sıralama
 * vb.
 */
@Entity
@Table(name = "questions", indexes = {
    @Index(name = "idx_question_survey", columnList = "survey_id"),
    @Index(name = "idx_question_type", columnList = "question_type"),
    @Index(name = "idx_question_order", columnList = "display_order")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    /**
     * 🆔 Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 📋 Anket
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    /**
     * ❓ Soru Metni
     */
    @Column(nullable = false, length = 1000)
    private String questionText;

    /**
     * 📝 Açıklama / Yardım Metni
     */
    @Column(length = 500)
    private String helpText;

    /**
     * 📊 Soru Türü (MULTIPLE_CHOICE, LIKERT, TEXT, YES_NO, RATING vb.)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType = QuestionType.MULTIPLE_CHOICE;

    /**
     * 📊 Sıralama (Görüntülenme sırası)
     */
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    /**
     * ✅ Zorunlu mu?
     */
    @Column(nullable = false)
    private Boolean required = true;

    /**
     * ✅ Aktif mi?
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * 🎛️ Seçenekler (JSON format - çoktan seçmeli, likert vb. için)
     * Örnek: ["Kesinlikle Katılmıyorum", "Katılmıyorum", "Kararsızım", "Katılıyorum", "Kesinlikle Katılıyorum"]
     */
    @Column(columnDefinition = "TEXT")
    private String options;

    /**
     * 📏 Minimum Değer (Rating, Slider vb. için)
     */
    @Column(name = "min_value")
    private Integer minValue;

    /**
     * 📏 Maksimum Değer (Rating, Slider vb. için)
     */
    @Column(name = "max_value")
    private Integer maxValue;

    /**
     * 📏 Adım Değeri (Slider için)
     */
    @Column(name = "step_value")
    private Integer stepValue = 1;

    /**
     * 📊 Dimension ile ilişkilendirilmiş mi? (Performans değerlendirme için)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dimension_id")
    private Dimension dimension;

    /**
     * ⚖️ Ağırlık (Puanlama için)
     */
    @Column
    private Double weight = 1.0;

    /**
     * 🔀 Koşullu Görünürlük (Hangi sorunun hangi cevabına bağlı olarak görünür)
     */
    @Column(name = "conditional_question_id")
    private Long conditionalQuestionId;

    /**
     * 🔀 Koşullu Değer (Hangi cevap verildiğinde bu soru görünür)
     */
    @Column(name = "conditional_value", length = 200)
    private String conditionalValue;

    /**
     * 📱 Sadece mobilde mi görünsün?
     */
    @Column(name = "mobile_only", nullable = false)
    private Boolean mobileOnly = false;

    /**
     * 🖥️ Sadece web'de mi görünsün?
     */
    @Column(name = "web_only", nullable = false)
    private Boolean webOnly = false;

    /**
     * 📅 Oluşturulma tarihi
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 📅 Güncellenme tarihi
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===============================
    // 📊 Question Type Enum
    // ===============================

    /**
     * Soru Türleri
     */
    public enum QuestionType {
        MULTIPLE_CHOICE,        // Çoktan Seçmeli (Tek seçim)
        MULTIPLE_SELECT,        // Çoktan Seçmeli (Çoklu seçim)
        LIKERT_5,              // 5'li Likert Ölçeği
        LIKERT_7,              // 7'li Likert Ölçeği
        LIKERT_10,             // 10'lu Likert Ölçeği
        TEXT_SHORT,            // Kısa Metin (Tek satır)
        TEXT_LONG,             // Uzun Metin (Çok satır)
        YES_NO,                // Evet/Hayır
        RATING_STARS,          // Yıldız Değerlendirme (1-5)
        RATING_NUMERIC,        // Sayısal Değerlendirme
        SLIDER,                // Kaydırıcı
        DROPDOWN,              // Açılır Liste
        RANKING,               // Sıralama
        MATRIX,                // Matris (Çok boyutlu)
        DATE,                  // Tarih Seçimi
        TIME,                  // Saat Seçimi
        EMAIL,                 // E-posta
        PHONE,                 // Telefon
        NUMBER,                // Sayı
        FILE_UPLOAD,           // Dosya Yükleme
        IMAGE_CHOICE,          // Resimli Seçim
        NET_PROMOTER_SCORE,    // NPS (0-10)
        CUSTOM                 // Özel
    }

    // ===============================
    // 🛠️ Helper Methods
    // ===============================

    /**
     * 🎛️ Seçenekleri liste olarak getir
     */
    public String[] getOptionsArray() {
        if (options == null || options.trim().isEmpty()) {
            return new String[0];
        }
        
        // JSON array formatını parse et
        String cleanOptions = options.trim();
        if (cleanOptions.startsWith("[") && cleanOptions.endsWith("]")) {
            cleanOptions = cleanOptions.substring(1, cleanOptions.length() - 1);
        }
        
        return cleanOptions.split(",");
    }

    /**
     * 🎛️ Seçenekleri ayarla
     */
    public void setOptionsArray(String[] optionsArray) {
        if (optionsArray == null || optionsArray.length == 0) {
            this.options = null;
            return;
        }
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < optionsArray.length; i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(optionsArray[i].trim()).append("\"");
        }
        sb.append("]");
        
        this.options = sb.toString();
    }

    /**
     * 📊 Likert ölçeği için default seçenekler
     */
    public void setDefaultLikertOptions() {
        switch (questionType) {
            case LIKERT_5:
                setOptionsArray(new String[]{
                    "Kesinlikle Katılmıyorum", 
                    "Katılmıyorum", 
                    "Kararsızım", 
                    "Katılıyorum", 
                    "Kesinlikle Katılıyorum"
                });
                setMinValue(1);
                setMaxValue(5);
                break;
            case LIKERT_7:
                setOptionsArray(new String[]{
                    "Kesinlikle Katılmıyorum", 
                    "Katılmıyorum", 
                    "Kısmen Katılmıyorum",
                    "Kararsızım", 
                    "Kısmen Katılıyorum",
                    "Katılıyorum", 
                    "Kesinlikle Katılıyorum"
                });
                setMinValue(1);
                setMaxValue(7);
                break;
            case LIKERT_10:
                setOptionsArray(new String[]{
                    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
                });
                setMinValue(1);
                setMaxValue(10);
                break;
            case YES_NO:
                setOptionsArray(new String[]{"Hayır", "Evet"});
                setMinValue(0);
                setMaxValue(1);
                break;
            case RATING_STARS:
                setOptionsArray(new String[]{"⭐", "⭐⭐", "⭐⭐⭐", "⭐⭐⭐⭐", "⭐⭐⭐⭐⭐"});
                setMinValue(1);
                setMaxValue(5);
                break;
            case NET_PROMOTER_SCORE:
                setOptionsArray(new String[]{
                    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
                });
                setMinValue(0);
                setMaxValue(10);
                break;
        }
    }

    /**
     * 🔍 Seçenekli soru mu?
     */
    public boolean hasOptions() {
        return questionType == QuestionType.MULTIPLE_CHOICE ||
               questionType == QuestionType.MULTIPLE_SELECT ||
               questionType == QuestionType.DROPDOWN ||
               questionType == QuestionType.RANKING ||
               questionType.name().startsWith("LIKERT") ||
               questionType == QuestionType.YES_NO;
    }

    /**
     * 📊 Sayısal değer gerektiren soru mu?
     */
    public boolean requiresNumericValue() {
        return questionType == QuestionType.RATING_NUMERIC ||
               questionType == QuestionType.SLIDER ||
               questionType == QuestionType.NUMBER ||
               questionType.name().startsWith("LIKERT") ||
               questionType == QuestionType.RATING_STARS ||
               questionType == QuestionType.NET_PROMOTER_SCORE;
    }

    /**
     * 📝 Metin cevabı gerektiren soru mu?
     */
    public boolean requiresTextValue() {
        return questionType == QuestionType.TEXT_SHORT ||
               questionType == QuestionType.TEXT_LONG ||
               questionType == QuestionType.EMAIL ||
               questionType == QuestionType.PHONE;
    }

    /**
     * 🔀 Koşullu soru mu?
     */
    public boolean isConditional() {
        return conditionalQuestionId != null && conditionalValue != null;
    }

    /**
     * 📊 Soru türü görüntü adı
     */
    public String getQuestionTypeDisplayName() {
        switch (questionType) {
            case MULTIPLE_CHOICE: return "Çoktan Seçmeli (Tek)";
            case MULTIPLE_SELECT: return "Çoktan Seçmeli (Çoklu)";
            case LIKERT_5: return "5'li Likert Ölçeği";
            case LIKERT_7: return "7'li Likert Ölçeği";
            case LIKERT_10: return "10'lu Likert Ölçeği";
            case TEXT_SHORT: return "Kısa Metin";
            case TEXT_LONG: return "Uzun Metin";
            case YES_NO: return "Evet/Hayır";
            case RATING_STARS: return "Yıldız Değerlendirme";
            case RATING_NUMERIC: return "Sayısal Değerlendirme";
            case SLIDER: return "Kaydırıcı";
            case DROPDOWN: return "Açılır Liste";
            case RANKING: return "Sıralama";
            case MATRIX: return "Matris";
            case DATE: return "Tarih";
            case TIME: return "Saat";
            case EMAIL: return "E-posta";
            case PHONE: return "Telefon";
            case NUMBER: return "Sayı";
            case FILE_UPLOAD: return "Dosya Yükleme";
            case IMAGE_CHOICE: return "Resimli Seçim";
            case NET_PROMOTER_SCORE: return "NPS (0-10)";
            case CUSTOM: return "Özel";
            default: return questionType.name();
        }
    }

    /**
     * 📝 Soru özeti
     */
    public String getQuestionSummary() {
        String summary = String.format("S%d: %s (%s)", 
                displayOrder, 
                questionText.length() > 50 ? questionText.substring(0, 50) + "..." : questionText,
                getQuestionTypeDisplayName());
        
        if (required) summary += " [Zorunlu]";
        if (dimension != null) summary += " [" + dimension.getName() + "]";
        
        return summary;
    }
} 
package com.hrapp.service;

import com.hrapp.entity.*;
import com.hrapp.repository.SuccessProfileRepository;
import com.hrapp.repository.SuccessProfileDimensionRepository;
import com.hrapp.repository.DimensionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 🎯 Success Profile Service - Başarı Profili İş Mantığı
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SuccessProfileService {

    private final SuccessProfileRepository successProfileRepository;
    private final SuccessProfileDimensionRepository successProfileDimensionRepository;
    private final DimensionRepository dimensionRepository;

    /**
     * 📋 Tüm profilleri getir
     */
    public List<SuccessProfile> findAll() {
        log.debug("Tüm success profiller getiriliyor");
        return successProfileRepository.findAll();
    }

    /**
     * ✅ Aktif profilleri getir
     */
    public List<SuccessProfile> findActiveProfiles() {
        log.debug("Aktif success profiller getiriliyor");
        return successProfileRepository.findByActiveTrue();
    }

    /**
     * 🏢 Tenant'a göre profilleri getir
     */
    public List<SuccessProfile> findByTenant(Long tenantId) {
        log.debug("Tenant success profilleri getiriliyor - Tenant ID: {}", tenantId);
        return successProfileRepository.findByTenantIdAndActiveTrue(tenantId);
    }

    /**
     * 💼 Pozisyona göre profilleri getir
     */
    public List<SuccessProfile> findByPosition(Long positionId) {
        log.debug("Pozisyon success profilleri getiriliyor - Position ID: {}", positionId);
        return successProfileRepository.findByPositionIdAndActiveTrue(positionId);
    }

    /**
     * 🏗️ Departmana göre profilleri getir
     */
    public List<SuccessProfile> findByDepartment(Long departmentId) {
        log.debug("Departman success profilleri getiriliyor - Department ID: {}", departmentId);
        return successProfileRepository.findByDepartmentIdAndActiveTrue(departmentId);
    }

    /**
     * 📊 Profil türüne göre profilleri getir
     */
    public List<SuccessProfile> findByProfileType(SuccessProfile.ProfileType profileType) {
        log.debug("Profil türü success profilleri getiriliyor - Tür: {}", profileType);
        return successProfileRepository.findByProfileType(profileType);
    }

    /**
     * 🆔 ID ile profil bul
     */
    public Optional<SuccessProfile> findById(Long id) {
        log.debug("Success profil aranıyor - ID: {}", id);
        return successProfileRepository.findById(id);
    }

    /**
     * 🏷️ İsimle profil bul
     */
    public Optional<SuccessProfile> findByName(String name) {
        log.debug("Success profil aranıyor - İsim: {}", name);
        return successProfileRepository.findByName(name);
    }

    /**
     * 🏢 Tenant ve isimle profil bul
     */
    public Optional<SuccessProfile> findByNameAndTenant(String name, Long tenantId) {
        log.debug("Success profil aranıyor - İsim: {}, Tenant ID: {}", name, tenantId);
        return successProfileRepository.findByNameAndTenantId(name, tenantId);
    }

    /**
     * 📝 Yeni profil oluştur
     */
    @Transactional
    public SuccessProfile createProfile(SuccessProfile profile) {
        log.info("Yeni success profil oluşturuluyor - İsim: {}", profile.getName());
        
        // İsim kontrolü (tenant bazında)
        if (successProfileRepository.findByNameAndTenantId(profile.getName(), profile.getTenant().getId()).isPresent()) {
            throw new RuntimeException("Bu profil adı zaten kullanılıyor: " + profile.getName());
        }
        
        // Default değerler
        profile.setActive(true);
        profile.setIsSystemProfile(false);
        
        SuccessProfile savedProfile = successProfileRepository.save(profile);
        log.info("Success profil başarıyla oluşturuldu - ID: {}, İsim: {}", savedProfile.getId(), savedProfile.getName());
        
        return savedProfile;
    }

    /**
     * 🔄 Profil güncelle
     */
    @Transactional
    public SuccessProfile updateProfile(Long id, SuccessProfile profileDetails) {
        log.info("Success profil güncelleniyor - ID: {}", id);
        
        SuccessProfile profile = successProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profil bulunamadı - ID: " + id));
        
        // Sistem profili kontrolü
        if (profile.getIsSystemProfile()) {
            throw new RuntimeException("Sistem profilleri güncellenemez - ID: " + id);
        }
        
        // İsim kontrolü (eğer değiştiriliyorsa)
        if (!profile.getName().equals(profileDetails.getName())) {
            if (successProfileRepository.findByNameAndTenantId(profileDetails.getName(), profile.getTenant().getId()).isPresent()) {
                throw new RuntimeException("Bu profil adı zaten kullanılıyor: " + profileDetails.getName());
            }
        }
        
        // Güncellenebilir alanlar
        profile.setName(profileDetails.getName());
        profile.setDescription(profileDetails.getDescription());
        profile.setProfileType(profileDetails.getProfileType());
        profile.setPosition(profileDetails.getPosition());
        profile.setDepartment(profileDetails.getDepartment());
        profile.setMinSuccessScore(profileDetails.getMinSuccessScore());
        profile.setTargetSuccessScore(profileDetails.getTargetSuccessScore());
        
        return successProfileRepository.save(profile);
    }

    /**
     * 📊 Profile dimension ekle
     */
    @Transactional
    public SuccessProfile addDimensionToProfile(Long profileId, Long dimensionId, 
                                               Double weight, Double minScore, Double targetScore, Boolean isCritical) {
        log.info("Profile dimension ekleniyor - Profile ID: {}, Dimension ID: {}", profileId, dimensionId);
        
        SuccessProfile profile = successProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profil bulunamadı - ID: " + profileId));
        
        Dimension dimension = dimensionRepository.findById(dimensionId)
                .orElseThrow(() -> new RuntimeException("Dimension bulunamadı - ID: " + dimensionId));
        
        // Zaten var mı kontrol et
        if (successProfileDimensionRepository.findBySuccessProfileIdAndDimensionId(profileId, dimensionId).isPresent()) {
            throw new RuntimeException("Bu dimension zaten profile eklenmiş");
        }
        
        // Yeni ilişki oluştur
        SuccessProfileDimension spd = new SuccessProfileDimension();
        spd.setSuccessProfile(profile);
        spd.setDimension(dimension);
        spd.setWeight(weight != null ? weight : 10.0);
        spd.setMinScore(minScore != null ? minScore : dimension.getMinValue());
        spd.setTargetScore(targetScore != null ? targetScore : dimension.getMaxValue() * 0.8);
        spd.setIsCritical(isCritical != null ? isCritical : false);
        spd.setActive(true);
        
        successProfileDimensionRepository.save(spd);
        
        return successProfileRepository.findById(profileId).orElse(profile);
    }

    /**
     * 📊 Profile'dan dimension kaldır
     */
    @Transactional
    public SuccessProfile removeDimensionFromProfile(Long profileId, Long dimensionId) {
        log.info("Profile'dan dimension kaldırılıyor - Profile ID: {}, Dimension ID: {}", profileId, dimensionId);
        
        SuccessProfile profile = successProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profil bulunamadı - ID: " + profileId));
        
        SuccessProfileDimension spd = successProfileDimensionRepository
                .findBySuccessProfileIdAndDimensionId(profileId, dimensionId)
                .orElseThrow(() -> new RuntimeException("Profile dimension ilişkisi bulunamadı"));
        
        successProfileDimensionRepository.delete(spd);
        
        return successProfileRepository.findById(profileId).orElse(profile);
    }

    /**
     * 📊 Profile dimension güncelle
     */
    @Transactional
    public SuccessProfileDimension updateProfileDimension(Long profileId, Long dimensionId,
                                                         Double weight, Double minScore, Double targetScore, Boolean isCritical) {
        log.info("Profile dimension güncelleniyor - Profile ID: {}, Dimension ID: {}", profileId, dimensionId);
        
        SuccessProfileDimension spd = successProfileDimensionRepository
                .findBySuccessProfileIdAndDimensionId(profileId, dimensionId)
                .orElseThrow(() -> new RuntimeException("Profile dimension ilişkisi bulunamadı"));
        
        if (weight != null) spd.setWeight(weight);
        if (minScore != null) spd.setMinScore(minScore);
        if (targetScore != null) spd.setTargetScore(targetScore);
        if (isCritical != null) spd.setIsCritical(isCritical);
        
        return successProfileDimensionRepository.save(spd);
    }

    /**
     * 📊 Profil dimension'larını getir
     */
    public List<SuccessProfileDimension> getProfileDimensions(Long profileId) {
        log.debug("Profil dimension'ları getiriliyor - Profile ID: {}", profileId);
        return successProfileDimensionRepository.findBySuccessProfileIdOrderByDisplayOrder(profileId);
    }

    /**
     * 🎯 Başarı skorunu hesapla
     */
    public Double calculateSuccessScore(Long profileId, Map<Long, Double> dimensionScores) {
        log.debug("Başarı skoru hesaplanıyor - Profile ID: {}", profileId);
        
        SuccessProfile profile = successProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profil bulunamadı - ID: " + profileId));
        
        return profile.calculateSuccessScore(dimensionScores);
    }

    /**
     * ✅ Başarı kriterini karşılıyor mu kontrol et
     */
    public boolean checkSuccessCriteria(Long profileId, Map<Long, Double> dimensionScores) {
        log.debug("Başarı kriteri kontrol ediliyor - Profile ID: {}", profileId);
        
        SuccessProfile profile = successProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profil bulunamadı - ID: " + profileId));
        
        return profile.meetsSuccessCriteria(dimensionScores);
    }

    /**
     * ✅ Profili aktif/pasif yap
     */
    @Transactional
    public SuccessProfile toggleProfileStatus(Long id) {
        log.info("Profil durumu değiştiriliyor - ID: {}", id);
        
        SuccessProfile profile = successProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profil bulunamadı - ID: " + id));
        
        // Sistem profili kontrolü
        if (profile.getIsSystemProfile()) {
            throw new RuntimeException("Sistem profilleri deaktive edilemez - ID: " + id);
        }
        
        profile.setActive(!profile.getActive());
        return successProfileRepository.save(profile);
    }

    /**
     * 🗑️ Profil sil
     */
    @Transactional
    public void deleteProfile(Long id) {
        log.info("Profil siliniyor - ID: {}", id);
        
        SuccessProfile profile = successProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profil bulunamadı - ID: " + id));
        
        // Sistem profili kontrolü
        if (profile.getIsSystemProfile()) {
            throw new RuntimeException("Sistem profilleri silinemez - ID: " + id);
        }
        
        // İlişkili dimension'ları da sil
        successProfileDimensionRepository.deleteBySuccessProfileId(id);
        
        successProfileRepository.delete(profile);
    }

    /**
     * 📊 Tenant'taki profil sayısı
     */
    public long countByTenant(Long tenantId) {
        return successProfileRepository.countActiveProfilesByTenant(tenantId);
    }

    /**
     * 💼 Pozisyondaki profil sayısı
     */
    public long countByPosition(Long positionId) {
        return successProfileRepository.countActiveProfilesByPosition(positionId);
    }

    /**
     * 🔍 İsimle arama
     */
    public List<SuccessProfile> searchByName(String name) {
        log.debug("Profil arama - İsim: {}", name);
        return successProfileRepository.findByNameContaining(name);
    }

    /**
     * 📊 Dimension kullanım analizi
     */
    public Map<String, Object> getDimensionUsageAnalysis(Long dimensionId) {
        log.debug("Dimension kullanım analizi - Dimension ID: {}", dimensionId);
        
        List<SuccessProfile> profilesUsingDimension = successProfileRepository.findActiveBydimensionId(dimensionId);
        List<SuccessProfileDimension> dimensionUsages = successProfileDimensionRepository.findByDimensionIdAndActiveTrue(dimensionId);
        
        double avgWeight = dimensionUsages.stream()
                .mapToDouble(SuccessProfileDimension::getWeight)
                .average()
                .orElse(0.0);
        
        double avgMinScore = dimensionUsages.stream()
                .mapToDouble(SuccessProfileDimension::getMinScore)
                .average()
                .orElse(0.0);
        
        Map<String, Object> analysis = new java.util.HashMap<>();
        analysis.put("profileCount", profilesUsingDimension.size());
        analysis.put("usageCount", dimensionUsages.size());
        analysis.put("averageWeight", avgWeight);
        analysis.put("averageMinScore", avgMinScore);
        analysis.put("profiles", profilesUsingDimension);
        
        return analysis;
    }
} 
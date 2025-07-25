package com.hrapp.service;

import com.hrapp.entity.Dimension;
import com.hrapp.entity.User;
import com.hrapp.repository.DimensionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 📊 Dimension Service - Boyut İş Mantığı
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DimensionService {

    private final DimensionRepository dimensionRepository;

    /**
     * 📋 Tüm boyutları getir
     */
    public List<Dimension> findAll() {
        log.debug("Tüm boyutlar getiriliyor");
        return dimensionRepository.findAll();
    }

    /**
     * ✅ Aktif boyutları getir
     */
    public List<Dimension> findActiveDimensions() {
        log.debug("Aktif boyutlar getiriliyor");
        return dimensionRepository.findByActiveTrue();
    }

    /**
     * 🏢 Tenant'a göre boyutları getir
     */
    public List<Dimension> findByTenant(Long tenantId) {
        log.debug("Tenant boyutları getiriliyor - Tenant ID: {}", tenantId);
        return dimensionRepository.findByTenantIdAndActiveTrue(tenantId);
    }

    /**
     * 📊 Kategoriye göre boyutları getir
     */
    public List<Dimension> findByCategory(Dimension.DimensionCategory category) {
        log.debug("Kategori boyutları getiriliyor - Kategori: {}", category);
        return dimensionRepository.findByCategory(category);
    }

    /**
     * 🏢 Tenant ve kategoriye göre boyutları getir
     */
    public List<Dimension> findByTenantAndCategory(Long tenantId, Dimension.DimensionCategory category) {
        log.debug("Tenant ve kategori boyutları getiriliyor - Tenant ID: {}, Kategori: {}", tenantId, category);
        return dimensionRepository.findByTenantIdAndCategoryAndActiveTrue(tenantId, category);
    }

    /**
     * 📊 Sıralı boyutları getir
     */
    public List<Dimension> findByTenantOrderByDisplayOrder(Long tenantId) {
        log.debug("Sıralı boyutlar getiriliyor - Tenant ID: {}", tenantId);
        return dimensionRepository.findByTenantIdOrderByDisplayOrder(tenantId);
    }

    /**
     * 🆔 ID ile boyut bul
     */
    public Optional<Dimension> findById(Long id) {
        log.debug("Boyut aranıyor - ID: {}", id);
        return dimensionRepository.findById(id);
    }

    /**
     * 🏷️ İsimle boyut bul
     */
    public Optional<Dimension> findByName(String name) {
        log.debug("Boyut aranıyor - İsim: {}", name);
        return dimensionRepository.findByName(name);
    }

    /**
     * 🏢 Tenant ve isimle boyut bul
     */
    public Optional<Dimension> findByNameAndTenant(String name, Long tenantId) {
        log.debug("Boyut aranıyor - İsim: {}, Tenant ID: {}", name, tenantId);
        return dimensionRepository.findByNameAndTenantId(name, tenantId);
    }

    /**
     * 📝 Yeni boyut oluştur
     */
    @Transactional
    public Dimension createDimension(Dimension dimension) {
        log.info("Yeni boyut oluşturuluyor - İsim: {}", dimension.getName());
        
        // İsim kontrolü (tenant bazında)
        if (dimensionRepository.findByNameAndTenantId(dimension.getName(), dimension.getTenant().getId()).isPresent()) {
            throw new RuntimeException("Bu boyut adı zaten kullanılıyor: " + dimension.getName());
        }
        
        // Default değerler
        dimension.setActive(true);
        dimension.setIsSystemDimension(false);
        
        // Default ölçek tanımları ayarla
        if (dimension.getScaleDescriptions() == null || dimension.getScaleDescriptions().isEmpty()) {
            dimension.setDefaultScaleDescriptions();
        }
        
        // Display order ayarla (eğer belirtilmemişse en sona ekle)
        if (dimension.getDisplayOrder() == null || dimension.getDisplayOrder() == 0) {
            long maxOrder = dimensionRepository.countByTenantId(dimension.getTenant().getId());
            dimension.setDisplayOrder((int) (maxOrder + 1));
        }
        
        Dimension savedDimension = dimensionRepository.save(dimension);
        log.info("Boyut başarıyla oluşturuldu - ID: {}, İsim: {}", savedDimension.getId(), savedDimension.getName());
        
        return savedDimension;
    }

    /**
     * 🔄 Boyut güncelle
     */
    @Transactional
    public Dimension updateDimension(Long id, Dimension dimensionDetails) {
        log.info("Boyut güncelleniyor - ID: {}", id);
        
        Dimension dimension = dimensionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boyut bulunamadı - ID: " + id));
        
        // Sistem boyutu kontrolü (sistem boyutları güncellenemez)
        if (dimension.getIsSystemDimension()) {
            throw new RuntimeException("Sistem boyutları güncellenemez - ID: " + id);
        }
        
        // İsim kontrolü (eğer değiştiriliyorsa)
        if (!dimension.getName().equals(dimensionDetails.getName())) {
            if (dimensionRepository.findByNameAndTenantId(dimensionDetails.getName(), dimension.getTenant().getId()).isPresent()) {
                throw new RuntimeException("Bu boyut adı zaten kullanılıyor: " + dimensionDetails.getName());
            }
        }
        
        // Güncellenebilir alanlar
        dimension.setName(dimensionDetails.getName());
        dimension.setDescription(dimensionDetails.getDescription());
        dimension.setCategory(dimensionDetails.getCategory());
        dimension.setScaleType(dimensionDetails.getScaleType());
        dimension.setWeight(dimensionDetails.getWeight());
        dimension.setDisplayOrder(dimensionDetails.getDisplayOrder());
        
        // Ölçek değerleri güncellemesi
        if (dimensionDetails.getMinValue() != null) {
            dimension.setMinValue(dimensionDetails.getMinValue());
        }
        if (dimensionDetails.getMaxValue() != null) {
            dimension.setMaxValue(dimensionDetails.getMaxValue());
        }
        if (dimensionDetails.getScaleDescriptions() != null) {
            dimension.setScaleDescriptions(dimensionDetails.getScaleDescriptions());
        }
        
        return dimensionRepository.save(dimension);
    }

    /**
     * 📊 Dimension'a özel ölçek tanımları oluştur
     */
    @Transactional
    public Dimension updateScaleDescriptions(Long id, String scaleDescriptions) {
        log.info("Boyut ölçek tanımları güncelleniyor - ID: {}", id);
        
        Dimension dimension = dimensionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boyut bulunamadı - ID: " + id));
        
        dimension.setScaleDescriptions(scaleDescriptions);
        return dimensionRepository.save(dimension);
    }

    /**
     * 📊 Display order güncelle
     */
    @Transactional
    public Dimension updateDisplayOrder(Long id, Integer newOrder) {
        log.info("Boyut sıralaması güncelleniyor - ID: {}, Yeni Sıra: {}", id, newOrder);
        
        Dimension dimension = dimensionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boyut bulunamadı - ID: " + id));
        
        dimension.setDisplayOrder(newOrder);
        return dimensionRepository.save(dimension);
    }

    /**
     * ✅ Boyutu aktif/pasif yap
     */
    @Transactional
    public Dimension toggleDimensionStatus(Long id) {
        log.info("Boyut durumu değiştiriliyor - ID: {}", id);
        
        Dimension dimension = dimensionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boyut bulunamadı - ID: " + id));
        
        // Sistem boyutu kontrolü
        if (dimension.getIsSystemDimension()) {
            throw new RuntimeException("Sistem boyutları deaktive edilemez - ID: " + id);
        }
        
        dimension.setActive(!dimension.getActive());
        return dimensionRepository.save(dimension);
    }

    /**
     * 🗑️ Boyut sil
     */
    @Transactional
    public void deleteDimension(Long id) {
        log.info("Boyut siliniyor - ID: {}", id);
        
        Dimension dimension = dimensionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boyut bulunamadı - ID: " + id));
        
        // Sistem boyutu kontrolü
        if (dimension.getIsSystemDimension()) {
            throw new RuntimeException("Sistem boyutları silinemez - ID: " + id);
        }
        
        // TODO: İlerde Success Profile'larda kullanılıp kullanılmadığını kontrol et
        
        dimensionRepository.delete(dimension);
    }

    /**
     * 📊 Tenant'taki boyut sayısı
     */
    public long countByTenant(Long tenantId) {
        return dimensionRepository.countActiveDimensionsByTenant(tenantId);
    }

    /**
     * 📊 Kategorideki boyut sayısı
     */
    public long countByTenantAndCategory(Long tenantId, Dimension.DimensionCategory category) {
        return dimensionRepository.countByTenantIdAndCategory(tenantId, category);
    }

    /**
     * 🔍 İsimle arama
     */
    public List<Dimension> searchByName(String name) {
        log.debug("Boyut arama - İsim: {}", name);
        return dimensionRepository.findByNameContaining(name);
    }

    /**
     * 🎯 Default sistem boyutları oluştur
     */
    @Transactional
    public void createDefaultDimensions(Long tenantId, User createdBy) {
        log.info("Default boyutlar oluşturuluyor - Tenant ID: {}", tenantId);
        
        String[][] defaultDimensions = {
            {"Teknik Yetkinlik", "Teknik beceriler ve uzmanlık", "TECHNICAL"},
            {"İletişim", "Sözlü ve yazılı iletişim becerileri", "COMMUNICATION"},
            {"Takım Çalışması", "Takım içi işbirliği ve uyum", "TEAMWORK"},
            {"Problem Çözme", "Analitik düşünme ve çözüm üretme", "PROBLEM_SOLVING"},
            {"Liderlik", "Yönetim ve liderlik becerileri", "LEADERSHIP"},
            {"Müşteri Odaklılık", "Müşteri memnuniyeti ve hizmet kalitesi", "CUSTOMER_FOCUS"},
            {"İnovasyon", "Yaratıcılık ve yenilikçi düşünce", "INNOVATION"},
            {"Uyum Yeteneği", "Değişime adaptasyon ve esneklik", "ADAPTABILITY"}
        };
        
        for (int i = 0; i < defaultDimensions.length; i++) {
            String[] dim = defaultDimensions[i];
            
            // Zaten var mı kontrol et
            if (dimensionRepository.findByNameAndTenantId(dim[0], tenantId).isEmpty()) {
                Dimension dimension = new Dimension();
                dimension.setName(dim[0]);
                dimension.setDescription(dim[1]);
                dimension.setCategory(Dimension.DimensionCategory.valueOf(dim[2]));
                dimension.setScaleType(Dimension.ScaleType.LIKERT_5);
                dimension.setWeight(10.0);
                dimension.setDisplayOrder(i + 1);
                dimension.setActive(true);
                dimension.setIsSystemDimension(true);
                dimension.setCreatedBy(createdBy);
                dimension.setDefaultScaleDescriptions();
                
                // Tenant set edilecek (service katmanında)
                
                dimensionRepository.save(dimension);
                log.debug("Default boyut oluşturuldu: {}", dim[0]);
            }
        }
    }
} 
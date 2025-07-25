package com.hrapp.service;

import com.hrapp.entity.User;
import com.hrapp.entity.Role;
import com.hrapp.repository.UserRepository;
import com.hrapp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 👤 User Service - Kullanıcı İş Mantığı
 * 
 * @Service - Spring'in service component'i olarak işaretler
 * @RequiredArgsConstructor - Lombok: final field'lar için constructor oluşturur
 * @Slf4j - Lombok: Logger oluşturur
 * @Transactional - Database transaction yönetimi
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 📧 Email ile kullanıcı bul
     */
    public Optional<User> findByEmail(String email) {
        log.debug("Kullanıcı aranıyor - Email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * 🆔 ID ile kullanıcı bul
     */
    public Optional<User> findById(Long id) {
        log.debug("Kullanıcı aranıyor - ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * 📋 Tüm kullanıcıları getir
     */
    public List<User> findAll() {
        log.debug("Tüm kullanıcılar getiriliyor");
        return userRepository.findAll();
    }

    /**
     * 🏢 Tenant'a göre kullanıcıları getir
     */
    public List<User> findByTenant(Long tenantId) {
        log.debug("Tenant kullanıcıları getiriliyor - Tenant ID: {}", tenantId);
        return userRepository.findByTenantIdAndActiveTrue(tenantId);
    }

    /**
     * 🎭 Role göre kullanıcıları getir
     */
    public List<User> findByRole(String roleName) {
        log.debug("Rol kullanıcıları getiriliyor - Rol: {}", roleName);
        return userRepository.findByRoleName(roleName);
    }

    /**
     * 💾 Kullanıcı kaydet/güncelle
     */
    @Transactional
    public User save(User user) {
        log.info("Kullanıcı kaydediliyor - Email: {}", user.getEmail());
        
        // Şifre hash'leme (eğer yeni şifre varsa)
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(user);
    }

    /**
     * 📝 Yeni kullanıcı oluştur
     */
    @Transactional
    public User createUser(User user) {
        log.info("Yeni kullanıcı oluşturuluyor - Email: {}", user.getEmail());
        
        // Email kontrolü
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Bu email adresi zaten kullanılıyor: " + user.getEmail());
        }
        
        // Şifre hash'leme
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Default değerler
        user.setActive(true);
        user.setEmailVerified(false);
        
        return userRepository.save(user);
    }

    /**
     * 🔄 Kullanıcı güncelle
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        log.info("Kullanıcı güncelleniyor - ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı - ID: " + id));
        
        // Güncellenebilir alanlar
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhone(userDetails.getPhone());
        user.setDepartment(userDetails.getDepartment());
        user.setPosition(userDetails.getPosition());
        
        // Şifre güncelleme (eğer yeni şifre varsa)
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        return userRepository.save(user);
    }

    /**
     * 🎭 Kullanıcıya rol ata
     */
    @Transactional
    public User assignRole(Long userId, String roleName) {
        log.info("Rol atanıyor - User ID: {}, Rol: {}", userId, roleName);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı - ID: " + userId));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı: " + roleName));
        
        user.addRole(role);
        return userRepository.save(user);
    }

    /**
     * 🎭 Kullanıcıdan rol kaldır
     */
    @Transactional
    public User removeRole(Long userId, String roleName) {
        log.info("Rol kaldırılıyor - User ID: {}, Rol: {}", userId, roleName);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı - ID: " + userId));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı: " + roleName));
        
        user.removeRole(role);
        return userRepository.save(user);
    }

    /**
     * ✅ Kullanıcıyı aktif/pasif yap
     */
    @Transactional
    public User toggleUserStatus(Long id) {
        log.info("Kullanıcı durumu değiştiriliyor - ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı - ID: " + id));
        
        user.setActive(!user.getActive());
        return userRepository.save(user);
    }

    /**
     * 🕒 Son giriş zamanını güncelle
     */
    @Transactional
    public void updateLastLogin(String email) {
        log.debug("Son giriş zamanı güncelleniyor - Email: {}", email);
        
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    /**
     * 🗑️ Kullanıcıyı sil
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("Kullanıcı siliniyor - ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Kullanıcı bulunamadı - ID: " + id);
        }
        
        userRepository.deleteById(id);
    }

    /**
     * 📊 Tenant'taki aktif kullanıcı sayısı
     */
    public long countActiveUsersByTenant(Long tenantId) {
        return userRepository.countActiveUsersByTenant(tenantId);
    }
} 
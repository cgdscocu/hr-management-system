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

import java.util.Optional;

/**
 * 🔐 Auth Service - Kimlik Doğrulama İş Mantığı
 * 
 * Login, Register, Token işlemleri burada yapılır
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 🔓 Kullanıcı girişi (Login)
     */
    public Optional<User> login(String email, String password) {
        log.info("Giriş denemesi - Email: {}", email);
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            log.warn("Kullanıcı bulunamadı - Email: {}", email);
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        // Kullanıcı aktif mi kontrol et
        if (!user.getActive()) {
            log.warn("Pasif kullanıcı giriş denemesi - Email: {}", email);
            return Optional.empty();
        }
        
        // Şifre kontrolü
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Hatalı şifre - Email: {}", email);
            return Optional.empty();
        }
        
        log.info("Başarılı giriş - Email: {}", email);
        return Optional.of(user);
    }

    /**
     * 📝 Kullanıcı kaydı (Register)
     */
    @Transactional
    public User register(User user, String defaultRoleName) {
        log.info("Yeni kullanıcı kaydı - Email: {}", user.getEmail());
        
        // Email kontrolü
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Bu email adresi zaten kullanılıyor: " + user.getEmail());
        }
        
        // Şifre hash'leme
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Default değerler
        user.setActive(true);
        user.setEmailVerified(false);
        
        // Default rol ata (örn: "USER")
        Role defaultRole = roleRepository.findByName(defaultRoleName)
                .orElseThrow(() -> new RuntimeException("Default rol bulunamadı: " + defaultRoleName));
        
        user.addRole(defaultRole);
        
        User savedUser = userRepository.save(user);
        log.info("Kullanıcı başarıyla kaydedildi - ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());
        
        return savedUser;
    }

    /**
     * 🔑 Admin kullanıcı oluştur
     */
    @Transactional
    public User createAdminUser(User user) {
        log.info("Admin kullanıcı oluşturuluyor - Email: {}", user.getEmail());
        
        return register(user, Role.ADMIN);
    }

    /**
     * 🔑 HR Manager kullanıcı oluştur
     */
    @Transactional
    public User createHrManagerUser(User user) {
        log.info("HR Manager kullanıcı oluşturuluyor - Email: {}", user.getEmail());
        
        return register(user, Role.HR_MANAGER);
    }

    /**
     * 🔒 Şifre değiştir
     */
    @Transactional
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        log.info("Şifre değiştirme denemesi - Email: {}", email);
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            log.warn("Kullanıcı bulunamadı - Email: {}", email);
            return false;
        }
        
        User user = userOpt.get();
        
        // Eski şifre kontrolü
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Eski şifre hatalı - Email: {}", email);
            return false;
        }
        
        // Yeni şifreyi hash'le ve kaydet
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Şifre başarıyla değiştirildi - Email: {}", email);
        return true;
    }

    /**
     * 🔄 Şifre sıfırla (Admin tarafından)
     */
    @Transactional
    public String resetPassword(Long userId) {
        log.info("Şifre sıfırlama - User ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı - ID: " + userId));
        
        // Geçici şifre oluştur
        String tempPassword = generateTempPassword();
        
        // Hash'le ve kaydet
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);
        
        log.info("Şifre sıfırlandı - User ID: {}", userId);
        return tempPassword;
    }

    /**
     * ✅ Email doğrula
     */
    @Transactional
    public boolean verifyEmail(String email) {
        log.info("Email doğrulama - Email: {}", email);
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        user.setEmailVerified(true);
        userRepository.save(user);
        
        log.info("Email doğrulandı - Email: {}", email);
        return true;
    }

    /**
     * 🔢 Geçici şifre oluştur
     */
    private String generateTempPassword() {
        // Basit geçici şifre oluşturma (production'da daha güvenli olmalı)
        return "TempPass" + System.currentTimeMillis();
    }
} 
package com.hrapp.service;

import com.hrapp.entity.Role;
import com.hrapp.entity.Permission;
import com.hrapp.repository.RoleRepository;
import com.hrapp.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 🎭 Role Service - Rol Yönetimi İş Mantığı
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    /**
     * 📋 Tüm rolleri getir
     */
    public List<Role> findAll() {
        log.debug("Tüm roller getiriliyor");
        return roleRepository.findAll();
    }

    /**
     * ✅ Aktif rolleri getir
     */
    public List<Role> findActiveRoles() {
        log.debug("Aktif roller getiriliyor");
        return roleRepository.findByActiveTrue();
    }

    /**
     * 🏢 Tenant'a göre rolleri getir
     */
    public List<Role> findByTenant(Long tenantId) {
        log.debug("Tenant rolleri getiriliyor - Tenant ID: {}", tenantId);
        return roleRepository.findByTenantIdAndActiveTrue(tenantId);
    }

    /**
     * 🌐 Global rolleri getir
     */
    public List<Role> findGlobalRoles() {
        log.debug("Global roller getiriliyor");
        return roleRepository.findByTenantIsNull();
    }

    /**
     * 🆔 ID ile rol bul
     */
    public Optional<Role> findById(Long id) {
        log.debug("Rol aranıyor - ID: {}", id);
        return roleRepository.findById(id);
    }

    /**
     * 🏷️ İsimle rol bul
     */
    public Optional<Role> findByName(String name) {
        log.debug("Rol aranıyor - İsim: {}", name);
        return roleRepository.findByName(name);
    }

    /**
     * 📝 Yeni rol oluştur
     */
    @Transactional
    public Role createRole(Role role) {
        log.info("Yeni rol oluşturuluyor - İsim: {}", role.getName());
        
        // İsim kontrolü
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Bu rol adı zaten kullanılıyor: " + role.getName());
        }
        
        // Default değerler
        role.setActive(true);
        role.setIsSystemRole(false);
        
        Role savedRole = roleRepository.save(role);
        log.info("Rol başarıyla oluşturuldu - ID: {}, İsim: {}", savedRole.getId(), savedRole.getName());
        
        return savedRole;
    }

    /**
     * 🔄 Rol güncelle
     */
    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
        log.info("Rol güncelleniyor - ID: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı - ID: " + id));
        
        // Sistem rolü kontrolü (sistem rolleri güncellenemez)
        if (role.getIsSystemRole()) {
            throw new RuntimeException("Sistem rolleri güncellenemez - ID: " + id);
        }
        
        // Güncellenebilir alanlar
        role.setDescription(roleDetails.getDescription());
        
        return roleRepository.save(role);
    }

    /**
     * 🔑 Role izin ekle
     */
    @Transactional
    public Role addPermissionToRole(Long roleId, String permissionName) {
        log.info("Role izin ekleniyor - Role ID: {}, İzin: {}", roleId, permissionName);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı - ID: " + roleId));
        
        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new RuntimeException("İzin bulunamadı: " + permissionName));
        
        role.addPermission(permission);
        return roleRepository.save(role);
    }

    /**
     * 🔑 Rolden izin kaldır
     */
    @Transactional
    public Role removePermissionFromRole(Long roleId, String permissionName) {
        log.info("Rolden izin kaldırılıyor - Role ID: {}, İzin: {}", roleId, permissionName);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı - ID: " + roleId));
        
        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new RuntimeException("İzin bulunamadı: " + permissionName));
        
        role.removePermission(permission);
        return roleRepository.save(role);
    }

    /**
     * ✅ Rolü aktif/pasif yap
     */
    @Transactional
    public Role toggleRoleStatus(Long id) {
        log.info("Rol durumu değiştiriliyor - ID: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı - ID: " + id));
        
        // Sistem rolü kontrolü
        if (role.getIsSystemRole()) {
            throw new RuntimeException("Sistem rolleri deaktive edilemez - ID: " + id);
        }
        
        role.setActive(!role.getActive());
        return roleRepository.save(role);
    }

    /**
     * 🗑️ Rol sil
     */
    @Transactional
    public void deleteRole(Long id) {
        log.info("Rol siliniyor - ID: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı - ID: " + id));
        
        // Sistem rolü kontrolü
        if (role.getIsSystemRole()) {
            throw new RuntimeException("Sistem rolleri silinemez - ID: " + id);
        }
        
        roleRepository.delete(role);
    }
} 
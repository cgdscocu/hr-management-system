package com.hrapp.config;

import com.hrapp.entity.*;
import com.hrapp.repository.*;
import com.hrapp.service.DimensionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 🌱 Data Initializer - Test Verileri Oluşturur
 * 
 * Uygulama başlatıldığında otomatik olarak test verileri oluşturur
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final TenantRepository tenantRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final DimensionRepository dimensionRepository;
    private final PasswordEncoder passwordEncoder;
    private final DimensionService dimensionService;

    @Override
    public void run(String... args) throws Exception {
        log.info("🌱 Test verileri oluşturuluyor...");
        
        // 1. İzinleri oluştur
        createPermissions();
        
        // 2. Tenant oluştur
        Tenant defaultTenant = createDefaultTenant();
        
        // 3. Rolleri oluştur
        createRoles(defaultTenant);
        
        // 4. Test kullanıcıları oluştur
        createTestUsers(defaultTenant);
        
        // 5. Departman ve pozisyonları oluştur
        createDepartmentsAndPositions(defaultTenant);
        
        // 6. Default dimension'ları oluştur
        createDefaultDimensions(defaultTenant);
        
        log.info("✅ Test verileri başarıyla oluşturuldu!");
        logLoginCredentials();
    }

    private void createPermissions() {
        log.debug("🔑 İzinler oluşturuluyor...");
        
        String[][] permissions = {
            // User Management
            {"USER_CREATE", "Kullanıcı oluşturma", "users", "CREATE", "USER_MANAGEMENT"},
            {"USER_READ", "Kullanıcı görüntüleme", "users", "READ", "USER_MANAGEMENT"},
            {"USER_UPDATE", "Kullanıcı güncelleme", "users", "UPDATE", "USER_MANAGEMENT"},
            {"USER_DELETE", "Kullanıcı silme", "users", "DELETE", "USER_MANAGEMENT"},
            {"USER_MANAGE", "Kullanıcı yönetimi", "users", "MANAGE", "USER_MANAGEMENT"},
            
            // Role Management
            {"ROLE_CREATE", "Rol oluşturma", "roles", "CREATE", "ROLE_MANAGEMENT"},
            {"ROLE_ASSIGN", "Rol atama", "roles", "ASSIGN", "ROLE_MANAGEMENT"},
            {"ROLE_REMOVE", "Rol kaldırma", "roles", "REMOVE", "ROLE_MANAGEMENT"},
            
            // HR Operations
            {"HR_PROCESS", "İK süreçleri", "hr", "PROCESS", "HR_OPERATIONS"},
            {"RESULTS_VIEW", "Sonuçları görme", "results", "VIEW", "HR_OPERATIONS"},
            {"POSITION_MANAGE", "Pozisyon yönetimi", "positions", "MANAGE", "HR_OPERATIONS"},
            {"DEPARTMENT_MANAGE", "Departman yönetimi", "departments", "MANAGE", "HR_OPERATIONS"},
            
            // Survey Operations
            {"SURVEY_CREATE", "Survey oluşturma", "surveys", "CREATE", "SURVEY_OPERATIONS"},
            {"SURVEY_ASSIGN", "Survey atama", "surveys", "ASSIGN", "SURVEY_OPERATIONS"},
            {"SURVEY_RESULTS", "Survey sonuçları", "surveys", "RESULTS", "SURVEY_OPERATIONS"},
            
            // Dimension Operations
            {"DIMENSION_CREATE", "Dimension oluşturma", "dimensions", "CREATE", "DIMENSION_OPERATIONS"},
            {"DIMENSION_READ", "Dimension görüntüleme", "dimensions", "READ", "DIMENSION_OPERATIONS"},
            {"DIMENSION_UPDATE", "Dimension güncelleme", "dimensions", "UPDATE", "DIMENSION_OPERATIONS"},
            {"DIMENSION_DELETE", "Dimension silme", "dimensions", "DELETE", "DIMENSION_OPERATIONS"},
            {"DIMENSION_MANAGE", "Dimension yönetimi", "dimensions", "MANAGE", "DIMENSION_OPERATIONS"},
            
            // Success Profile Operations
            {"SUCCESS_PROFILE_CREATE", "Success Profile oluşturma", "success-profiles", "CREATE", "SUCCESS_PROFILE_OPERATIONS"},
            {"SUCCESS_PROFILE_READ", "Success Profile görüntüleme", "success-profiles", "READ", "SUCCESS_PROFILE_OPERATIONS"},
            {"SUCCESS_PROFILE_UPDATE", "Success Profile güncelleme", "success-profiles", "UPDATE", "SUCCESS_PROFILE_OPERATIONS"},
            {"SUCCESS_PROFILE_DELETE", "Success Profile silme", "success-profiles", "DELETE", "SUCCESS_PROFILE_OPERATIONS"},
            {"SUCCESS_PROFILE_MANAGE", "Success Profile yönetimi", "success-profiles", "MANAGE", "SUCCESS_PROFILE_OPERATIONS"},
            
            // Tenant Management
            {"TENANT_CREATE", "Tenant oluşturma", "tenants", "CREATE", "TENANT_MANAGEMENT"},
            {"TENANT_MANAGE", "Tenant yönetimi", "tenants", "MANAGE", "TENANT_MANAGEMENT"},
            
            // General
            {"ALL_ACCESS", "Tam erişim", "all", "ACCESS", "GENERAL"},
            {"PROFILE_VIEW", "Profil görüntüleme", "profile", "VIEW", "GENERAL"},
            {"PROFILE_UPDATE", "Profil güncelleme", "profile", "UPDATE", "GENERAL"}
        };
        
        for (String[] perm : permissions) {
            if (!permissionRepository.existsByName(perm[0])) {
                Permission permission = new Permission();
                permission.setName(perm[0]);
                permission.setDescription(perm[1]);
                permission.setResource(perm[2]);
                permission.setAction(perm[3]);
                permission.setCategory(perm[4]);
                permission.setActive(true);
                permission.setIsSystemPermission(true);
                
                permissionRepository.save(permission);
                log.debug("İzin oluşturuldu: {}", perm[0]);
            }
        }
    }

    private Tenant createDefaultTenant() {
        log.debug("🏢 Default tenant oluşturuluyor...");
        
        if (tenantRepository.findByName("Varsayılan Şirket").isEmpty()) {
            Tenant tenant = new Tenant();
            tenant.setName("Varsayılan Şirket");
            tenant.setDescription("Test için varsayılan şirket");
            tenant.setDomain("default.com");
            tenant.setContactEmail("admin@default.com");
            tenant.setPhone("+90 212 555 0000");
            tenant.setAddress("İstanbul, Türkiye");
            tenant.setCity("İstanbul");
            tenant.setCountry("Türkiye");
            tenant.setPostalCode("34000");
            tenant.setActive(true);
            tenant.setMaxUsers(100);
            tenant.setSubscriptionStatus(Tenant.SubscriptionStatus.ACTIVE);
            
            return tenantRepository.save(tenant);
        }
        
        return tenantRepository.findByName("Varsayılan Şirket").orElseThrow();
    }

    private void createRoles(Tenant tenant) {
        log.debug("🎭 Roller oluşturuluyor...");
        
        // SUPER_ADMIN (Global)
        createRoleIfNotExists("SUPER_ADMIN", "Süper Yönetici", null, true, 
                "ALL_ACCESS", "TENANT_CREATE", "TENANT_MANAGE", "USER_MANAGE", "ROLE_ASSIGN");
        
        // ADMIN (Tenant-specific)
        createRoleIfNotExists("ADMIN", "Yönetici", tenant, true,
                "USER_MANAGE", "ROLE_ASSIGN", "ROLE_REMOVE", "HR_PROCESS", "RESULTS_VIEW", 
                "SURVEY_CREATE", "SURVEY_ASSIGN", "SURVEY_RESULTS", "DEPARTMENT_MANAGE", "POSITION_MANAGE",
                "DIMENSION_CREATE", "DIMENSION_READ", "DIMENSION_UPDATE", "DIMENSION_DELETE", "DIMENSION_MANAGE",
                "SUCCESS_PROFILE_CREATE", "SUCCESS_PROFILE_READ", "SUCCESS_PROFILE_UPDATE", "SUCCESS_PROFILE_DELETE", "SUCCESS_PROFILE_MANAGE");
        
        // HR_MANAGER
        createRoleIfNotExists("HR_MANAGER", "İK Müdürü", tenant, true,
                "HR_PROCESS", "RESULTS_VIEW", "USER_READ", "POSITION_MANAGE", "DEPARTMENT_MANAGE",
                "DIMENSION_READ", "DIMENSION_CREATE", "DIMENSION_UPDATE",
                "SUCCESS_PROFILE_READ", "SUCCESS_PROFILE_CREATE", "SUCCESS_PROFILE_UPDATE");
        
        // HR_SPECIALIST
        createRoleIfNotExists("HR_SPECIALIST", "İK Uzmanı", tenant, true,
                "HR_PROCESS", "RESULTS_VIEW", "USER_READ", "DIMENSION_READ", "SUCCESS_PROFILE_READ");
        
        // USER
        createRoleIfNotExists("USER", "Kullanıcı", tenant, true,
                "PROFILE_VIEW", "PROFILE_UPDATE");
        
        // CANDIDATE
        createRoleIfNotExists("CANDIDATE", "Aday", tenant, true,
                "PROFILE_VIEW");
    }

    private void createRoleIfNotExists(String name, String description, Tenant tenant, 
                                     boolean isSystem, String... permissionNames) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            role.setTenant(tenant);
            role.setActive(true);
            role.setIsSystemRole(isSystem);
            
            Role savedRole = roleRepository.save(role);
            
            // İzinleri ekle
            for (String permName : permissionNames) {
                permissionRepository.findByName(permName).ifPresent(permission -> {
                    savedRole.addPermission(permission);
                });
            }
            
            roleRepository.save(savedRole);
            log.debug("Rol oluşturuldu: {}", name);
        }
    }

    private void createTestUsers(Tenant tenant) {
        log.debug("👤 Test kullanıcıları oluşturuluyor...");
        
        // Super Admin
        createUserIfNotExists("superadmin@test.com", "SuperAdmin123", "Süper", "Admin", 
                tenant, "SUPER_ADMIN");
        
        // Admin
        createUserIfNotExists("admin@test.com", "Admin123", "Ali", "Yönetici", 
                tenant, "ADMIN");
        
        // HR Manager
        createUserIfNotExists("hr-manager@test.com", "Hr123", "Ayşe", "İK Müdürü", 
                tenant, "HR_MANAGER");
        
        // HR Specialist
        createUserIfNotExists("hr-specialist@test.com", "Hr123", "Mehmet", "İK Uzmanı", 
                tenant, "HR_SPECIALIST");
        
        // Normal User
        createUserIfNotExists("user@test.com", "User123", "Fatma", "Kullanıcı", 
                tenant, "USER");
    }

    private void createUserIfNotExists(String email, String password, String firstName, 
                                     String lastName, Tenant tenant, String roleName) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setTenant(tenant);
            user.setActive(true);
            user.setEmailVerified(true);
            
            User savedUser = userRepository.save(user);
            
            // Rol ata
            roleRepository.findByName(roleName).ifPresent(role -> {
                savedUser.addRole(role);
                userRepository.save(savedUser);
            });
            
            log.debug("Kullanıcı oluşturuldu: {} ({})", email, roleName);
        }
    }

    private void createDepartmentsAndPositions(Tenant tenant) {
        log.debug("🏗️ Departman ve pozisyonlar oluşturuluyor...");
        
        // IT Departmanı
        Department itDept = createDepartmentIfNotExists("Bilgi İşlem", "IT Department", tenant);
        createPositionIfNotExists("Software Developer", "Yazılım Geliştirici", tenant, itDept, 
                Position.PositionLevel.MID, 15000, 25000);
        createPositionIfNotExists("IT Manager", "IT Müdürü", tenant, itDept, 
                Position.PositionLevel.MANAGER, 25000, 35000);
        
        // HR Departmanı
        Department hrDept = createDepartmentIfNotExists("İnsan Kaynakları", "HR Department", tenant);
        createPositionIfNotExists("HR Specialist", "İK Uzmanı", tenant, hrDept, 
                Position.PositionLevel.MID, 12000, 18000);
        createPositionIfNotExists("HR Manager", "İK Müdürü", tenant, hrDept, 
                Position.PositionLevel.MANAGER, 20000, 30000);
        
        // Sales Departmanı
        Department salesDept = createDepartmentIfNotExists("Satış", "Sales Department", tenant);
        createPositionIfNotExists("Sales Representative", "Satış Temsilcisi", tenant, salesDept, 
                Position.PositionLevel.JUNIOR, 10000, 15000);
    }

    private Department createDepartmentIfNotExists(String name, String description, Tenant tenant) {
        return departmentRepository.findByNameAndTenantId(name, tenant.getId())
                .orElseGet(() -> {
                    Department dept = new Department();
                    dept.setName(name);
                    dept.setDescription(description);
                    dept.setTenant(tenant);
                    dept.setActive(true);
                    return departmentRepository.save(dept);
                });
    }

    private Position createPositionIfNotExists(String title, String description, Tenant tenant, 
                                             Department department, Position.PositionLevel level,
                                             int minSalary, int maxSalary) {
        return positionRepository.findByTitleAndTenantId(title, tenant.getId())
                .orElseGet(() -> {
                    Position position = new Position();
                    position.setTitle(title);
                    position.setDescription(description);
                    position.setTenant(tenant);
                    position.setDepartment(department);
                    position.setLevel(level);
                    position.setSalaryMin(minSalary);
                    position.setSalaryMax(maxSalary);
                    position.setCurrency("TRY");
                    position.setActive(true);
                    return positionRepository.save(position);
                });
    }

    private void logLoginCredentials() {
        log.info("");
        log.info("🔐 TEST KULLANICI BİLGİLERİ:");
        log.info("═══════════════════════════════════════");
        log.info("🔸 Super Admin: superadmin@test.com / SuperAdmin123");
        log.info("🔸 Admin: admin@test.com / Admin123");
        log.info("🔸 HR Manager: hr-manager@test.com / Hr123");
        log.info("🔸 HR Specialist: hr-specialist@test.com / Hr123");
        log.info("🔸 User: user@test.com / User123");
        log.info("═══════════════════════════════════════");
        log.info("🌐 API Base URL: http://localhost:8080/api");
        log.info("📚 Swagger UI: http://localhost:8080/swagger-ui.html");
        log.info("🗄️ H2 Console: http://localhost:8080/h2-console");
        log.info("");
    }

    private void createDefaultDimensions(Tenant tenant) {
        log.debug("📊 Default dimension'lar oluşturuluyor...");
        
        // Admin kullanıcısını bul (dimension'ları oluşturan)
        User adminUser = userRepository.findByEmail("admin@test.com").orElse(null);
        
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
            if (dimensionRepository.findByNameAndTenantId(dim[0], tenant.getId()).isEmpty()) {
                Dimension dimension = new Dimension();
                dimension.setName(dim[0]);
                dimension.setDescription(dim[1]);
                dimension.setCategory(Dimension.DimensionCategory.valueOf(dim[2]));
                dimension.setTenant(tenant);
                dimension.setScaleType(Dimension.ScaleType.LIKERT_5);
                dimension.setWeight(10.0);
                dimension.setDisplayOrder(i + 1);
                dimension.setActive(true);
                dimension.setIsSystemDimension(true);
                dimension.setCreatedBy(adminUser);
                dimension.setDefaultScaleDescriptions();
                
                dimensionRepository.save(dimension);
                log.debug("Default dimension oluşturuldu: {}", dim[0]);
            }
        }
    }
} 
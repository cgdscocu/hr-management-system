# 🚀 HR APP SPRING BOOT ROADMAP

Excel dosyasındaki rol ve yetki yapısına dayalı olarak kapsamlı HR App geliştirme planı.

## 📋 ROL VE YETKİ ÖZETİ (Excel Analizi)

### Roller:
- **Süper Admin**: Tam yetki, tenant oluşturma, tüm sayfalara erişim
- **Admin**: Tenant içinde tam yetki, kullanıcı ve rol yönetimi
- **HR-Manager**: İK süreçleri, sonuçlar görüntüleme, rol atama YOK
- **HR-Specialist**: İK süreçleri, sonuçlar görüntüleme, rol atama YOK
- **User**: Normal kullanıcı, sınırlı erişim
- **Aday**: Adaylık sürecindeki kullanıcı

---

## 🎯 FAZE 1: PROJE KURULUMU VE TEMEL YAPI

### 1.1 Spring Boot Projesi Oluşturma
```xml
<!-- Ana Dependencies -->
- Spring Boot Web
- Spring Data JPA
- Spring Security
- MySQL/PostgreSQL Driver
- Validation
- Lombok
- MapStruct
- Swagger/OpenAPI
```

### 1.2 Proje Yapısı
```
src/main/java/com/hrapp/
├── config/           # Configuration sınıfları
├── controller/       # REST Controllers
├── service/          # Business Logic
├── repository/       # JPA Repositories
├── entity/           # Database Entities
├── dto/              # Data Transfer Objects
├── security/         # Security Configuration
├── exception/        # Global Exception Handling
└── util/             # Utility Classes
```

---

## 🗄️ FAZE 2: VERİTABANI TASARIMI

### 2.1 Ana Entities
```java
// Tenant (Şirket/Organizasyon)
@Entity Tenant {
    Long id, String name, String domain, LocalDateTime createdAt, Boolean active
}

// User (Kullanıcı)
@Entity User {
    Long id, String email, String password, String firstName, String lastName,
    Tenant tenant, Set<Role> roles, Boolean active, LocalDateTime createdAt
}

// Role (Rol)
@Entity Role {
    Long id, String name, String description, Set<Permission> permissions
}

// Permission (İzin)
@Entity Permission {
    Long id, String name, String resource, String action
}

// Position (Pozisyon)
@Entity Position {
    Long id, String title, String description, Tenant tenant, Department department
}

// Department (Departman)
@Entity Department {
    Long id, String name, Tenant tenant, User manager
}
```

### 2.2 İlişkiler
- User ↔ Tenant (ManyToOne)
- User ↔ Role (ManyToMany)
- Role ↔ Permission (ManyToMany)
- User ↔ Position (ManyToOne)
- Position ↔ Department (ManyToOne)

---

## 🔐 FAZE 3: GÜVENLİK VE AUTHENTICATION

### 3.1 Spring Security Configuration
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // JWT Token işlemleri
    // Role-based access control
    // Multi-tenant security
}
```

### 3.2 JWT Token System
```java
// JwtService: Token generate/validate
// JwtAuthenticationFilter: Request filter
// UserDetailsService: User authentication
```

### 3.3 Role-Permission Mapping
```java
// Excel'deki rol yapısına göre:
SUPER_ADMIN: ["TENANT_CREATE", "ALL_ACCESS", "USER_MANAGE"]
ADMIN: ["USER_MANAGE", "ROLE_ASSIGN", "PROJECT_CREATE"]
HR_MANAGER: ["HR_PROCESS", "RESULTS_VIEW"]
HR_SPECIALIST: ["HR_PROCESS", "RESULTS_VIEW"]
USER: ["PROFILE_VIEW", "TASK_VIEW"]
CANDIDATE: ["APPLICATION_SUBMIT", "STATUS_VIEW"]
```

---

## 👥 FAZE 4: KULLANICI YÖNETİMİ

### 4.1 User Management APIs
```java
@RestController("/api/users")
- POST /register (Kullanıcı kayıt)
- POST /login (Giriş)
- GET /profile (Profil görüntüleme)
- PUT /profile (Profil güncelleme)
- GET /users (Kullanıcı listesi - Admin)
- POST /users/{id}/roles (Rol atama - Admin)
- DELETE /users/{id}/roles/{roleId} (Rol kaldırma)
```

### 4.2 Role Management
```java
@RestController("/api/roles")
- GET /roles (Rol listesi)
- POST /roles (Yeni rol oluşturma)
- PUT /roles/{id} (Rol güncelleme)
- DELETE /roles/{id} (Rol silme)
```

---

## 🏢 FAZE 5: TENANT YÖNETİMİ

### 5.1 Multi-Tenant Architecture
```java
// Tenant isolation için filter
@Component TenantFilter implements Filter
// Tenant context holder
@Component TenantContext
// Tenant-aware JPA repositories
```

### 5.2 Tenant Management APIs
```java
@RestController("/api/tenants")
- POST /tenants (Tenant oluşturma - SuperAdmin)
- GET /tenants (Tenant listesi - SuperAdmin)
- PUT /tenants/{id} (Tenant güncelleme)
- POST /tenants/{id}/users (Tenant'a user ekleme)
```

---

## 💼 FAZE 6: HR MODÜLLERİ

### 6.1 Position Management
```java
@RestController("/api/positions")
- CRUD operations for positions
- Position hierarchy management
- Position-user assignments
```

### 6.2 Leadership Level Management
```java
@Entity LeadershipLevel {
    Long id, String name, Integer level, String description
}
```

### 6.3 Success Profile Management
```java
@Entity SuccessProfile {
    Long id, String name, Position position, List<Criteria> criteria
}
```

---

## 📊 FAZE 7: PROJE YÖNETİMİ

### 7.1 Project Entity & APIs
```java
@Entity Project {
    Long id, String name, String description, User owner,
    Set<User> members, Tenant tenant, ProjectStatus status
}

@RestController("/api/projects")
- CRUD operations
- Project member management
- Project reporting
```

---

## 📝 FAZE 8: SURVEY/ANKET SİSTEMİ

### 8.1 Survey System
```java
@Entity Survey {
    Long id, String title, String description, List<Question> questions,
    Tenant tenant, Boolean active, LocalDateTime deadline
}

@Entity Question {
    Long id, String text, QuestionType type, List<Option> options
}

@Entity SurveyResponse {
    Long id, Survey survey, User respondent, List<Answer> answers
}
```

### 8.2 Survey APIs
```java
@RestController("/api/surveys")
- Survey creation and management
- Survey assignment to users
- Response collection and analysis
```

---

## 📧 FAZE 9: MAİL ŞABLONU SİSTEMİ

### 9.1 Email Template System
```java
@Entity EmailTemplate {
    Long id, String name, String subject, String content,
    TemplateType type, Tenant tenant
}

@Service EmailService {
    // Template-based email sending
    // Variable substitution
    // Bulk email operations
}
```

---

## 📈 FAZE 10: RAPORLAMA VE ANALYTİCS

### 10.1 Reports & Dashboard
```java
@RestController("/api/reports")
- User activity reports
- Survey result analytics
- Project progress reports
- Excel export functionality
```

### 10.2 Results Screen (Excel'de belirtilen)
```java
// HR rollerinin görebileceği sonuçlar ekranı
// Filtering by department, position, etc.
// Data visualization components
```

---

## 🎨 FAZE 11: FRONTEND ENTEGRASYONU

### 11.1 API Documentation
```java
// Swagger/OpenAPI integration
// API documentation for frontend team
```

### 11.2 CORS & Frontend Setup
```java
// CORS configuration
// Frontend build integration
```

---

## 🧪 FAZE 12: TEST VE DAĞITIM

### 12.1 Testing
```java
// Unit tests with JUnit 5
// Integration tests with TestContainers
// Security tests
```

### 12.2 Deployment
```java
// Docker containerization
// Environment configurations
// Production deployment
```

---

## 🛠️ TEKNİK DETAYLAR

### Database Schema Priority:
1. **Users & Authentication** (En kritik)
2. **Roles & Permissions** (Güvenlik)
3. **Tenants** (Multi-tenancy)
4. **HR Modules** (İş mantığı)
5. **Projects & Surveys** (Özellikler)

### API Security Levels:
```java
// Public endpoints: /login, /register
// Authenticated: /profile, /dashboard
// Admin only: /users/*, /roles/*
// Super Admin only: /tenants/*
```

---

## 🎯 SONRAKI ADIM

**FAZE 1'den başlayacağız:**
1. Spring Boot projesi kurulumu
2. Temel dependency'lerin eklenmesi
3. Proje yapısının oluşturulması
4. Temel configuration'ların hazırlanması

Hazırsan FAZE 1'i başlatalım! 🚀 
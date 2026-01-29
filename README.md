# 📚 StudyPort

> **Spring Boot 기반 스터디 그룹 관리 플랫폼**  
> OAuth2 소셜 로그인, 3단계 권한 시스템, 정기 모임 관리 구현

[![Java](https://img.shields.io/badge/Java_17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5.3-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![OAuth2](https://img.shields.io/badge/OAuth2-EB5424?style=flat-square&logo=auth0&logoColor=white)](https://oauth.net/2/)
[![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariadb&logoColor=white)](https://mariadb.org/)

🔗 **[배포 링크](http://52.78.152.205:8081/)** | 📧 **hyochan.lee91@gmail.com**

---

## 📌 프로젝트 개요

| 항목 | 내용 |
|------|------|
| **개발 기간** | 2024.12 ~ 2025.01 (2개월) |
| **개발 인원** | 1명 (개인 프로젝트) |
| **프로젝트 목적** | OAuth2, 권한 관리 등 인증/인가 중심 학습 |

### 🎯 **핵심 목표**
단순 CRUD를 넘어 **OAuth2 소셜 로그인**, **세밀한 권한 시스템**, **상태 관리 패턴**을 구현한 스터디 관리 플랫폼

---

## 🛠️ 기술 스택

### **Backend**
- **Language**: Java 17
- **Framework**: Spring Boot 3.5.3, Spring Data JPA, Spring Security
- **Authentication**: OAuth2 Client (Google, Kakao, Naver)
- **ORM**: Hibernate, JPQL
- **Library**: 
  - Lombok (코드 간소화)
  - ModelMapper (Entity ↔ DTO 변환)
- **Build Tool**: Gradle

### **Frontend**
- **Template Engine**: Thymeleaf
- **JavaScript**: ES6, jQuery (AJAX 비동기 통신)
- **CSS**: Bootstrap

### **Database**
- MariaDB

---

## ✨ 주요 기능

### 👤 **회원 & 인증**
- OAuth2 소셜 로그인 (Google, Kakao, Naver), 일반 회원가입/로그인

### 📖 **스터디 관리**
- 스터디 CRUD (카테고리, 난이도, 온/오프라인, 공개/비공개), 참여 신청 → 승인/거절

### 👥 **멤버 관리**
- 역할 변경 (일반 멤버 ↔ 운영진), 멤버 강퇴, 승인 대기 목록

### 📅 **모임 관리**
- 모임 CRUD, 참석 신청/취소, 정원 관리, 상태 자동 변경 (모집중/마감/완료)

### 🔒 **권한 시스템**
- 3단계 권한 (STUDY_LEADER, STUDY_OPERATOR, USER), 스터디장만 멤버 관리, 모임 생성자만 수정/삭제

---

## 🔥 핵심 기술 구현

### **1. OAuth2 소셜 로그인 (Google, Kakao, Naver)**

```java
// CustomOAuth2UserService - 소셜 로그인 처리
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        // Provider 구분 (google, kakao, naver)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        // Provider별 사용자 정보 추출
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, oAuth2User.getAttributes());
        
        // 회원 조회 or 신규 생성
        Members member = saveOrUpdate(attributes);
        
        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
            attributes.getAttributes(),
            attributes.getNameAttributeKey()
        );
    }
    
    private Members saveOrUpdate(OAuthAttributes attributes) {
        Members member = memberRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());
        
        return memberRepository.save(member);
    }
}
```

```java
// OAuthAttributes - Provider별 사용자 정보 추출
@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;
    
    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {
        // Google
        if ("google".equals(registrationId)) {
            return ofGoogle(attributes);
        }
        // Kakao
        else if ("kakao".equals(registrationId)) {
            return ofKakao(attributes);
        }
        // Naver
        else if ("naver".equals(registrationId)) {
            return ofNaver(attributes);
        }
        throw new IllegalArgumentException("지원하지 않는 OAuth Provider입니다: " + registrationId);
    }
    
    private static OAuthAttributes ofGoogle(Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey("sub")
                .build();
    }
    
    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        
        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) profile.get("profile_image_url"))
                .attributes(attributes)
                .nameAttributeKey("id")
                .build();
    }
    
    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        
        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(attributes)
                .nameAttributeKey("id")
                .build();
    }
}
```

**구현 포인트**:
- **3개 Provider 통합 처리**: Google, Kakao, Naver의 서로 다른 응답 형식을 `OAuthAttributes`로 통일
- **회원 자동 생성/업데이트**: 최초 로그인 시 자동 회원가입, 기존 회원은 정보 업데이트
- **Provider별 nameAttributeKey 관리**: Google(sub), Kakao/Naver(id)

**사용 기술**: `DefaultOAuth2UserService`, `OAuth2UserRequest`, `OAuth2User`, Provider별 JSON 파싱

---

### **2. 3단계 권한 시스템**

```java
// Role Enum - 스터디 내 권한 정의
public enum Role {
    STUDY_LEADER,    // 스터디장: 모든 권한 (멤버 관리, 역할 변경, 강퇴)
    STUDY_OPERATOR,  // 운영진: 모임 생성 및 관리
    USER             // 일반 멤버: 모임 참석만 가능
}
```

```java
// StudyParticipant Entity - 스터디 참여자 엔티티
@Entity
@Table(name = "study_participant")
public class StudyParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Members member;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;  // 기본값: 일반 멤버
    
    @Enumerated(EnumType.STRING)
    private ParticipantStatus status = ParticipantStatus.PENDING;  // 승인 대기
    
    public enum ParticipantStatus {
        PENDING,   // 승인 대기
        APPROVED,  // 승인됨
        REJECTED   // 거절됨
    }
}
```

```java
// Controller - 권한 검증 로직
@PostMapping("/members/{studyId}/change-role")
public String changeMemberRole(@PathVariable Long studyId,
                               @RequestParam Long participantId,
                               @RequestParam String newRole,
                               @LoginUser Members currentUser) {
    
    // 1. 스터디 조회
    Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new EntityNotFoundException("스터디를 찾을 수 없습니다"));
    
    // 2. 스터디장 권한 검증 (스터디 생성자만 역할 변경 가능)
    if (!study.getMembers().getId().equals(currentUser.getId())) {
        throw new AccessDeniedException("권한이 없습니다. 스터디장만 역할을 변경할 수 있습니다.");
    }
    
    // 3. 역할 변경 대상 조회
    StudyParticipant participant = participantRepository.findById(participantId)
            .orElseThrow(() -> new EntityNotFoundException("참여자를 찾을 수 없습니다"));
    
    // 4. 역할 변경
    participant.setRole(Role.valueOf(newRole));
    participantRepository.save(participant);
    
    return "redirect:/study/members/" + studyId;
}
```

```java
// Service - 모임 생성 권한 검증
@Transactional
public MeetingDTO createMeeting(Long studyId, MeetingDTO meetingDTO, Members currentUser) {
    
    // 1. 스터디 참여자 조회
    StudyParticipant participant = participantRepository
            .findByStudyIdAndMemberId(studyId, currentUser.getId())
            .orElseThrow(() -> new AccessDeniedException("스터디 멤버가 아닙니다"));
    
    // 2. 권한 검증 (운영진 이상만 모임 생성 가능)
    if (participant.getRole() != Role.STUDY_LEADER && 
        participant.getRole() != Role.STUDY_OPERATOR) {
        throw new AccessDeniedException("모임 생성 권한이 없습니다. 운영진 이상만 가능합니다.");
    }
    
    // 3. 모임 생성 로직...
    Meeting meeting = Meeting.builder()
            .study(study)
            .title(meetingDTO.getTitle())
            .createdBy(currentUser)
            .status(Meeting.MeetingStatus.RECRUITING)
            .enabled(true)
            .build();
    
    return modelMapper.map(meetingRepository.save(meeting), MeetingDTO.class);
}
```

**구현 포인트**:
- **계층적 권한 구조**: STUDY_LEADER > STUDY_OPERATOR > USER
- **엔티티 레벨 권한 관리**: `StudyParticipant.role` 필드로 스터디별 권한 부여
- **다중 검증**: Controller(접근 제어) + Service(비즈니스 로직 검증) 2단계 검증
- **명확한 예외 처리**: `AccessDeniedException`으로 권한 오류 구분

**사용 기술**: `@Enumerated(EnumType.STRING)`, `Role` Enum, 2단계 권한 검증

---

### **3. 모임 참석 관리 (정원 검증 + AJAX)**

```java
// Meeting Entity - 모임 엔티티
@Entity
@Table(name = "meeting")
public class Meeting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Column(nullable = false)
    private Integer capacity;  // 정원
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingStatus status;  // 상태 (RECRUITING, CLOSED, DONE)
    
    @OneToMany(mappedBy = "meeting", cascade = CascadeType.REMOVE)
    private List<MeetingVoter> voters = new ArrayList<>();
    
    public enum MeetingStatus {
        RECRUITING,  // 모집중
        CLOSED,      // 마감
        DONE         // 완료
    }
}
```

```java
// Controller - 모임 참석 신청 (AJAX)
@PostMapping("/{studyId}/meeting/{meetingId}/attend")
@ResponseBody
public Map<String, Object> attendMeeting(@PathVariable Long studyId,
                                         @PathVariable Long meetingId,
                                         @LoginUser Members currentUser) {
    
    Map<String, Object> response = new HashMap<>();
    
    try {
        // 1. 모임 조회
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("모임을 찾을 수 없습니다"));
        
        // 2. 현재 참석자 수 조회
        Long currentAttendees = meetingVoterRepository.countByMeetingId(meetingId);
        
        // 3. 정원 초과 검증
        if (currentAttendees >= meeting.getCapacity()) {
            response.put("success", false);
            response.put("message", "정원이 초과되었습니다.");
            return response;
        }
        
        // 4. 중복 참석 검증
        boolean alreadyAttended = meetingVoterRepository
                .existsByMeetingIdAndMemberId(meetingId, currentUser.getId());
        
        if (alreadyAttended) {
            response.put("success", false);
            response.put("message", "이미 참석 신청한 모임입니다.");
            return response;
        }
        
        // 5. 자기 자신이 만든 모임인지 검증
        if (meeting.getCreatedBy().getId().equals(currentUser.getId())) {
            response.put("success", false);
            response.put("message", "본인이 생성한 모임에는 참석할 수 없습니다.");
            return response;
        }
        
        // 6. 참석 기록 저장
        MeetingVoter voter = new MeetingVoter();
        voter.setMeeting(meeting);
        voter.setMember(currentUser);
        meetingVoterRepository.save(voter);
        
        // 7. 정원 도달 시 상태 변경
        if (currentAttendees + 1 >= meeting.getCapacity()) {
            meeting.setStatus(Meeting.MeetingStatus.CLOSED);
            meetingRepository.save(meeting);
        }
        
        response.put("success", true);
        response.put("message", "모임에 참석했습니다!");
        
    } catch (Exception e) {
        response.put("success", false);
        response.put("message", "참석 처리 중 오류가 발생했습니다: " + e.getMessage());
    }
    
    return response;
}
```

```javascript
// JavaScript - AJAX 비동기 참석 신청
function attendMeeting(meetingId, studyId) {
    $.ajax({
        url: `/study/${studyId}/meeting/${meetingId}/attend`,
        type: 'POST',
        success: function(response) {
            if (response.success) {
                alert(response.message);
                location.reload();  // 참석자 수 실시간 업데이트
            } else {
                alert(response.message);  // 정원 초과, 중복 참석 등 에러
            }
        },
        error: function(xhr) {
            alert('참석 신청 중 오류가 발생했습니다.');
        }
    });
}
```

**구현 포인트**:
- **다단계 검증**: 정원 초과 → 중복 참석 → 본인 생성 모임 순서로 검증
- **AJAX 비동기 처리**: 페이지 새로고침 없이 실시간 참석 처리
- **자동 상태 변경**: 정원 도달 시 `RECRUITING` → `CLOSED` 자동 전환
- **원자적 처리**: `@Transactional`로 조회-검증-저장을 하나의 트랜잭션으로

**사용 기술**: `@ResponseBody` (JSON 응답), jQuery AJAX, `Map<String, Object>` 응답 구조

---

### **4. Soft Delete 패턴**

```java
// Meeting Entity - enabled 필드로 논리적 삭제
@Entity
public class Meeting {
    
    @Column(nullable = false)
    private Boolean enabled;  // true: 활성, false: 삭제됨
    
    @PrePersist
    protected void onCreate() {
        if (this.enabled == null) {
            this.enabled = true;  // 기본값: 활성
        }
    }
}
```

```java
// Study Entity - enabled 필드
@Entity
public class Study {
    
    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean enabled = true;
    
    // Soft Delete: 삭제 시 enabled = false로 변경
    public void delete() {
        this.enabled = false;
    }
}
```

```java
// Repository - 활성화된 데이터만 조회
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    
    // 활성화된 모임만 조회 (enabled = true)
    @Query("SELECT m FROM Meeting m WHERE m.study.id = :studyId AND m.enabled = true")
    List<Meeting> findByStudyIdAndEnabledTrue(@Param("studyId") Long studyId);
    
    // 모든 모임 조회 (삭제된 것 포함)
    @Query("SELECT m FROM Meeting m WHERE m.study.id = :studyId")
    List<Meeting> findAllByStudyId(@Param("studyId") Long studyId);
}
```

```java
// Controller - 삭제 처리 (물리적 삭제 대신 enabled = false)
@DeleteMapping("/{studyId}/meeting/{meetingId}")
@ResponseBody
public Map<String, Object> deleteMeeting(@PathVariable Long studyId,
                                         @PathVariable Long meetingId,
                                         @LoginUser Members currentUser) {
    
    Map<String, Object> response = new HashMap<>();
    
    // 1. 모임 조회
    Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new EntityNotFoundException("모임을 찾을 수 없습니다"));
    
    // 2. 권한 검증 (생성자만 삭제 가능)
    if (!meeting.getCreatedBy().getId().equals(currentUser.getId())) {
        response.put("success", false);
        response.put("message", "삭제 권한이 없습니다.");
        return response;
    }
    
    // 3. Soft Delete 처리 (물리적 삭제 X)
    meeting.setEnabled(false);
    meetingRepository.save(meeting);
    
    response.put("success", true);
    response.put("message", "모임이 삭제되었습니다.");
    
    return response;
}
```

**구현 포인트**:
- **데이터 보존**: 물리적 삭제 대신 `enabled = false`로 논리적 삭제
- **복구 가능성**: 삭제된 데이터도 DB에 남아있어 필요 시 복구 가능
- **이력 추적**: 삭제된 모임도 이력으로 남아 통계나 감사에 활용
- **조회 쿼리 분리**: 일반 조회(`enabled = true`), 전체 조회(관리자용)

**사용 기술**: `enabled` Boolean 필드, `@PrePersist`, JPQL `WHERE enabled = true`

---

### **5. 상태 관리 시스템**

```java
// Meeting.MeetingStatus - 모임 상태 관리
public enum MeetingStatus {
    RECRUITING,  // 모집중 - 정원 미달 시
    CLOSED,      // 마감 - 정원 도달 시
    DONE         // 완료 - 모임 종료 후
}
```

```java
// StudyParticipant.ParticipantStatus - 참여자 상태 관리
public enum ParticipantStatus {
    PENDING,   // 승인 대기 - 참여 신청 후 대기
    APPROVED,  // 승인됨 - 스터디장이 승인
    REJECTED   // 거절됨 - 스터디장이 거절
}
```

```java
// Service - 모임 상태 자동 변경 로직
@Transactional
public void updateMeetingStatus(Long meetingId) {
    Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new EntityNotFoundException("모임을 찾을 수 없습니다"));
    
    // 현재 참석자 수 조회
    Long currentAttendees = meetingVoterRepository.countByMeetingId(meetingId);
    
    // 1. 정원 도달 → CLOSED
    if (currentAttendees >= meeting.getCapacity()) {
        meeting.setStatus(MeetingStatus.CLOSED);
    }
    // 2. 모임 날짜 지남 → DONE
    else if (meeting.getDate().isBefore(LocalDateTime.now())) {
        meeting.setStatus(MeetingStatus.DONE);
    }
    // 3. 그 외 → RECRUITING
    else {
        meeting.setStatus(MeetingStatus.RECRUITING);
    }
    
    meetingRepository.save(meeting);
}
```

```java
// Controller - 참여 신청 승인 처리
@PostMapping("/members/{studyId}/approve")
public String approveParticipant(@PathVariable Long studyId,
                                 @RequestParam Long participantId,
                                 @LoginUser Members currentUser) {
    
    // 1. 스터디장 권한 검증
    Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new EntityNotFoundException("스터디를 찾을 수 없습니다"));
    
    if (!study.getMembers().getId().equals(currentUser.getId())) {
        throw new AccessDeniedException("승인 권한이 없습니다.");
    }
    
    // 2. 참여자 조회
    StudyParticipant participant = participantRepository.findById(participantId)
            .orElseThrow(() -> new EntityNotFoundException("참여자를 찾을 수 없습니다"));
    
    // 3. 상태 변경: PENDING → APPROVED
    participant.setStatus(ParticipantStatus.APPROVED);
    participantRepository.save(participant);
    
    return "redirect:/study/members/" + studyId + "/pending";
}
```

**구현 포인트**:
- **명확한 상태 정의**: Enum으로 가능한 상태 제한 (무결성 보장)
- **자동 상태 전환**: 정원 도달, 날짜 경과 등 조건에 따라 자동 변경
- **상태 기반 UI**: 프론트엔드에서 상태에 따라 버튼 활성화/비활성화
- **워크플로우 관리**: PENDING → APPROVED/REJECTED, RECRUITING → CLOSED → DONE

**사용 기술**: `@Enumerated(EnumType.STRING)`, Enum, 조건부 상태 전환 로직

---

## 🏗️ 아키텍처

```
┌─────────────┐
│   Client    │
│ (Thymeleaf) │
└──────┬──────┘
       │
┌──────▼──────────────┐
│   Controller        │
│  (권한 검증)         │
└──────┬──────────────┘
       │
┌──────▼──────────────┐
│   Service           │
│ (비즈니스 로직)      │
└──────┬──────────────┘
       │
┌──────▼──────────────┐
│   Repository        │
│  (Spring Data JPA)  │
└──────┬──────────────┘
       │
┌──────▼──────────────┐
│   MariaDB           │
└─────────────────────┘
```

**설계 원칙**:
- Controller: 권한 검증 + 요청/응답 처리
- Service: 비즈니스 로직 + 트랜잭션 관리
- Repository: 데이터 접근

---

## 📊 ERD (주요 테이블)

```
Members (회원)
  ├─ 1:N → Study (스터디 생성)
  ├─ 1:N → StudyParticipant (스터디 참여)
  └─ 1:N → MeetingVoter (모임 참석)

Study (스터디)
  ├─ N:1 → Members (생성자)
  ├─ 1:N → StudyParticipant (참여자)
  └─ 1:N → Meeting (모임)

StudyParticipant (스터디 참여자)
  ├─ N:1 → Study
  ├─ N:1 → Members
  └─ role (LEADER, OPERATOR, USER)

Meeting (모임)
  ├─ N:1 → Study
  ├─ N:1 → Members (생성자)
  └─ 1:N → MeetingVoter (참석자)
```

---

## 🚀 트러블슈팅

### **1. Meeting 생성 시 status 필드 null 오류**
**문제**: `@Column(nullable = false)` 설정했지만 초기값 미설정으로 DB 제약조건 위반  
**해결**: `meeting.setStatus(MeetingStatus.RECRUITING)` 명시적 기본값 설정

### **2. OAuth2 Provider별 응답 형식 차이**
**문제**: Google, Kakao, Naver의 JSON 구조가 달라 파싱 실패  
**해결**: `OAuthAttributes` 클래스로 Provider별 분기 처리 (`ofGoogle()`, `ofKakao()`, `ofNaver()`)

### **3. 모임 참석 동시성 문제 (정원 초과)**
**문제**: 동시 요청 시 정원 검증 통과 후 저장되어 정원 초과 발생  
**해결**: `@Transactional` + `synchronized` 메서드로 동시성 제어

---

## 💡 개발 과정에서 배운 점

### **기술적 성장**
- OAuth2의 복잡한 인증 플로우 이해 및 구현
- Provider별 응답 형식 차이를 추상화하는 설계 경험
- Enum 기반 상태 관리 패턴 학습
- Soft Delete를 통한 데이터 보존 전략

### **설계적 성장**
- 엔티티 레벨에서의 권한 관리 (StudyParticipant.role)
- Controller-Service 2단계 권한 검증 패턴
- 상태 기반 워크플로우 설계 (PENDING → APPROVED → ...)

---

## 📂 프로젝트 구조

```
src/main/java/com/example/studyport/
├── config/          # Security, OAuth2 설정
├── constant/        # Enum (Role)
├── controller/      # MVC 컨트롤러
├── dto/             # DTO (OAuthAttributes 포함)
├── entity/          # JPA 엔티티
├── repository/      # Spring Data JPA
└── service/         # 비즈니스 로직
```

---

## 🔗 Links

- **배포 URL**: http://52.78.152.205:8081/
- **GitHub**: https://github.com/HyochanCodeRepo/studyPort
- **개발자**: 이효찬 (hyochan.lee91@gmail.com)

---

## 📝 License

이 프로젝트는 포트폴리오 목적으로 제작되었습니다.

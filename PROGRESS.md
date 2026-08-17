# 진행 기록

포트폴리오 프로젝트(영화관 좌석 예약 시스템) 진행 상황을 정리한 기록입니다.
나중에 README나 "트러블슈팅 경험" 섹션을 쓸 때 참고용으로 남겨둡니다.

## 1. 프로젝트 개요

- **도메인**: 영화관 좌석형 예약 시스템
- **핵심 차별화 포인트**: 좌석 임시 선점(hold)의 동시성 제어와 정합성
- **동기**: 실제 CGV 앱에서 겪은 "예약 안 했는데 좌석이 잠겨있던" 경험을 계기로,
  좌석 임시 선점 → TTL 자동 해제 구조를 직접 설계/구현해보고자 시작함
- **스택**: Java 21, Spring Boot 4.1.0, Gradle(Kotlin DSL), MySQL

## 2. 지금까지 완료한 것

### 엔티티

- `Movie` — 영화 (id, title, runningTime)
- `Screen` — 상영관 (id, name)
- `Screening` — 상영 회차 (id, movie, screen, startTime) — `Movie`, `Screen`과 `@ManyToOne` 관계
- `Seat` — 좌석 (id, screen, seatRow, seatColumn) — `Screen`과 `@ManyToOne` 관계

### API 엔드포인트

- `GET /api/movies` — 영화 목록 조회
- `GET /api/movies/{movieId}/screenings` — 특정 영화의 상영 회차 목록 조회

두 엔드포인트 모두 Controller → Service → Repository 계층 구조로 구현하고,
Entity를 직접 응답하지 않고 DTO(`MovieResponse`, `ScreeningResponse`)로 변환해서 내려줌.

### 인프라

- MySQL에 `movie_reservation` DB와 전용 계정(`movie_app`) 생성 (root 계정을 앱에서 직접 쓰지 않음)
- DB 접속 정보(계정/비밀번호)를 `application.properties`에서 빼서 환경변수(`DB_USERNAME`, `DB_PASSWORD`)로 분리
- Git 저장소 초기화 및 GitHub 원격 저장소 연결, 첫 커밋 완료

## 3. 배운 개념

- **레이어드 아키텍처**: Controller(요청 전달) → Service(비즈니스 판단) → Repository(DB 접근), 응답은 Entity 대신 DTO로 변환
- **JPA 엔티티 & 관계 매핑**: `@Entity`, `@Id`, `@ManyToOne`, `@JoinColumn`으로 테이블 간 관계 표현
- **Derived Query**: `findByMovieId`처럼 메서드 이름 규칙만으로 Spring Data JPA가 쿼리를 자동 생성
- **예외 처리 전략**: Service가 예외를 던지면 `@RestControllerAdvice`가 전역에서 잡아 일관된 에러 응답을 만드는 구조 (개념만 학습, 아직 코드로 구현 전)
- **환경변수로 민감정보 분리**: `${DB_PASSWORD}` 형태로 설정 파일에 값을 직접 적지 않고 실행 시점에 주입받는 방식

## 4. 트러블슈팅 경험

| 문제 | 원인 | 해결 |
| --- | --- | --- |
| `Cannot find a Java installation ... languageVersion=17/21` | 로컬엔 JDK 25만 설치되어 있어서 Gradle이 필요한 버전을 못 찾음 | `settings.gradle.kts`에 `foojay-resolver-convention` 플러그인 추가해 Gradle이 필요한 JDK를 자동 다운로드하도록 설정 |
| `Settings plugins must be applied in the settings script` | 플러그인을 `build.gradle.kts`에 잘못 추가함 | `settings.gradle.kts`로 옮겨서 해결 |
| `cannot find symbol: MovieRepository` (컴파일 에러) | 파일을 `mv`로 옮긴 뒤 Gradle 증분 컴파일 캐시가 꼬임 | `./gradlew clean build`로 캐시를 지우고 재빌드 |
| 파일이 패키지 경로와 다른 폴더에 위치 (`com/Seat.java`, `java/MovieRepository.java` 등) | 패키지 선언(`package com.example.demo`)과 실제 폴더 위치가 안 맞아도 컴파일 자체는 되다 보니 여러 번 반복됨 | 패키지 경로 = 폴더 경로 규칙을 지켜 파일 위치를 맞춤 |
| 한글 데이터가 `1ê´€`처럼 깨져서 저장됨 | 터미널 `mysql` 클라이언트로 데이터를 넣을 때 UTF-8 인코딩을 명시하지 않아 문자셋이 잘못 해석됨 | `--default-character-set=utf8mb4`로 재삽입, JDBC URL에도 `useUnicode=true&characterEncoding=UTF-8` 추가 |
| `DataSourceBeanCreationException` (테스트 실패) | DB 자체가 아직 없거나 연결 정보가 없는 상태에서 Spring이 DataSource를 자동 구성하려다 실패 | DB/계정 생성 + `application.properties`에 연결 정보 추가 |

## 5. 전체 진행 체크리스트

### 1단계: 도메인 선정

- [x] 포트폴리오 도메인 선정 (영화관 좌석 예약 시스템)

### 2단계: 요구사항 정의 + API 명세

- [x] 핵심 유저 시나리오 정리
- [x] API 엔드포인트 초안 작성
- [x] ERD 핵심 설계 포인트 정리 (좌석 상태는 `(Screening, Seat)` 조합에 귀속)

### 3단계: 아키텍처 결정

- [x] 레이어드 아키텍처 구조 결정 (Controller/Service/Repository/DTO)
- [x] 예외 처리 전략 개념 학습
- [x] DB 선택 (MySQL)

### 4단계: 구현

**조회 API (인증 불필요)**

- [x] `Movie` 엔티티 + `GET /api/movies`
- [x] `Screen` 엔티티
- [x] `Screening` 엔티티 + `GET /api/movies/{movieId}/screenings`
- [x] `Seat` 엔티티
- [ ] 좌석 상태를 `Seat`가 아니라 `(Screening, Seat)` 조합으로 설계
- [ ] `GET /api/screenings/{screeningId}/seats` (좌석 배치+상태 조회)

**인증**

- [ ] 회원가입 API
- [ ] 로그인 API

**좌석 임시 선점 (핵심 기능)**

- [ ] `SeatHold` 엔티티 설계
- [ ] `POST /api/screenings/{screeningId}/seats/{seatId}/hold`
- [ ] TTL 자동 해제 (스케줄러 또는 조회 시점 lazy 체크)
- [ ] 동시성 제어 (비관적 락)

**예약/결제**

- [ ] `POST /api/reservations` (결제 → 확정)
- [ ] `GET /api/reservations/me`
- [ ] `DELETE /api/reservations/{id}` (취소)

**예외 처리**

- [ ] `ErrorCode` / `BusinessException` / `GlobalExceptionHandler` 실제 구현
- [ ] 도메인 전용 에러 코드 추가 (`SEAT_ALREADY_HELD` 등)

**테스트**

- [ ] 단위 테스트
- [ ] 동시성 테스트 (좌석 중복 선점 방지 검증)

### 5단계: 문서화

- [ ] Swagger/OpenAPI 적용
- [ ] README 작성
- [ ] 아키텍처 다이어그램

### 6단계: 포트폴리오 정리

- [ ] 트러블슈팅 경험 정리 (이 문서 4번 섹션에 계속 추가)
- [ ] 기술적 의사결정 근거 서술

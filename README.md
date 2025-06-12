# 🔐 jwt-auth 인증/인가 시스템

> Spring Boot 기반의 JWT 인증/인가 시스템입니다.  
> 사용자 회원가입, 로그인, 토큰 기반 인증, 그리고 역할 기반 접근 제어를 제공합니다.

| 주요 링크             | 상세                                                                                                 |
|-------------------|----------------------------------------------------------------------------------------------------|
| GitHub Repository | [https://github.com/Soobinnni/jwt-auth](https://github.com/Soobinnni/jwt-auth)                     |
| Swagger UI        | [http://52.207.211.95:8080/swagger-ui/index.html](http://52.207.211.95:8080/swagger-ui/index.html) |
| API Endpoint      | [http://52.207.211.95:8080/](http://52.207.211.95:8080/)                                           |

<br/>

## 📑 목차

- [📦 기술 스택](#-기술-스택)
- [🚀 주요 기능](#-주요-기능)
- [👤 기본 계정 생성](#-기본-계정-생성)
- [📘 주요 특징 상세](#-주요-특징-상세)
- [🔁 CI/CD 파이프라인 및 배포](#-cicd-파이프라인-및-배포)
- [📂 API 명세](#-api-명세)
- [📌 PR 리스트](#-pr-리스트)
- [🧪 테스트](#-테스트)

<br/>

## 📦 기술 스택

| 항목         | 사용 기술                       |
|------------|-----------------------------|
| Framework  | Spring Boot 3.4.3           |
| Java       | 17                          |
| Security   | Spring Security             |
| JWT        | JJWT 0.12.3                 |
| API 문서화    | SpringDoc OpenAPI (Swagger) |
| Test       | JUnit 5, Spring Boot Test   |
| Build Tool | Gradle                      |
| 패스워드 암호화   | BCrypt                      |

<br/>

## 🚀 주요 기능

| 기능           | 설명                                     |
|--------------|----------------------------------------|
| 사용자 인증 시스템   | 회원가입 및 로그인 기능 제공                       |
| JWT 토큰 기반 인증 | Access Token과 Refresh Token을 활용한 보안 강화 |
| 역할 기반 접근 제어  | 일반 사용자(USER)와 관리자(ADMIN) 권한 구분 접근 제어   |
| 관리자 권한 부여    | 관리자가 다른 사용자에게 관리자 권한을 부여할 수 있음         |
| 토큰 갱신        | Refresh Token으로 Access Token 자동 갱신     |
| 메모리 내 데이터 저장 | 실제 DB 없이 메모리에서 사용자 데이터 처리              |

<br/>

## 👤 기본 계정 생성

테스트 편의를 위해 애플리케이션 시작 시 자동 생성되는 기본 계정:

| 사용자명  | 비밀번호     | 권한    | 설명        |
|-------|----------|-------|-----------|
| admin | admin123 | ADMIN | 관리자 계정    |
| user  | user1234 | USER  | 일반 사용자 계정 |

<br/>

## 📘 주요 특징 상세

| 구분            | 핵심 내용 요약                                         | 상세 내용 링크                                                   |
|---------------|--------------------------------------------------|------------------------------------------------------------|
| **코드**        | SOLID 원칙 준수                                      |                                                            |
| **아키텍처 설계**   | 4계층 기반의 레이어드 아키텍처 + DDD 적용 → 기술 독립성, 테스트 용이, 확장성 | [아키텍처 상세](https://github.com/Soobinnni/jwt-auth/wiki/1)    |
| **보안 아키텍처**   | 다층 보안 필터 체인, 이중 토큰 전략(Access 5분 / Refresh 30일)   | [보안 아키텍처 상세](https://github.com/Soobinnni/jwt-auth/wiki/2) |
| **예외 처리 시스템** | 계층별 예외 변환, 전역 예외 처리, 타입 안전한 예외 체계                | [예외 처리 상세](https://github.com/Soobinnni/jwt-auth/wiki/3)   |
| **필터 기반 인증**  | Spring Security 통합, 경로별 맞춤 필터링                   | [필터 인증 상세](https://github.com/Soobinnni/jwt-auth/wiki/4)   |
| **테스트 아키텍처**  | 계층별 단위/통합/Mock 테스트, End-to-End 토큰 플로우 검증         | [테스트 상세](https://github.com/Soobinnni/jwt-auth/wiki/5)     |
| **API 문서화**   | OpenAPI 3.0 적용, Swagger UI, 보안 스키마, 개발자 경험 최적화   |                                                            |

<br/>

## 🔁 CI/CD 파이프라인 및 배포

> [.github/workflows/deploy.yml](https://github.com/Soobinnni/jwt-auth/blob/develop/.github/workflows/deploy.yml) 참고

```text
1. develop 또는 main 브랜치에 push/pull request 발생 시 → 테스트 수행  
2. main 브랜치에 push된 경우:  
   - 테스트 후 빌드 수행  
   - 빌드 완료 시 Docker 이미지 생성 및 EC2 서버에 자동 배포
```

<br/>

| 구분          | 내용                                                 |
|-------------|----------------------------------------------------|
| **컨테이너화**   | `openjdk:17-jdk-slim` 기반 경량 컨테이너, 환경 변수 `.env` 관리  |
| **포트 매핑**   | 내부 Spring Boot 8888 포트 → 호스트 8888 포트 매핑            |
| **리버스 프록시** | Nginx가 8080 포트에서 외부 요청 수신 → 8888 포트로 프록시           |
| **CORS 정책** | 허용 도메인 지정(`http://52.207.211.95:8080`)으로 API 접근 제한 |
| **보안 그룹**   | AWS 보안 그룹에서 8080 포트만 외부 허용, 8888 포트는 비허용           |
| **운영 안정성**  | Docker 헬스체크 및 자동 재시작 정책 적용                         |
| **배포 자동화**  | Shell 스크립트로 이미지 빌드, 컨테이너 실행, 로그 관리 등 일괄 처리         |

<br/>

## 📂 API 명세

> [http://52.207.211.95:8080/swagger-ui/index.html](http://52.207.211.95:8080/swagger-ui/index.html) 에서도 확인 가능합니다.  
> wiki에 API 문서도 정리되어 있습니다. → [🔗 링크](https://github.com/Soobinnni/jwt-auth/wiki/api)

<br/>

## 📌 PR 리스트

| 제목                                                                                    |
|---------------------------------------------------------------------------------------|
| [feat: 인증 서비스 도메인 모델 구축](https://github.com/Soobinnni/jwt-auth/pull/2)                |
| [feat: 공통 예외 처리 기반 클래스 및 글로벌 예외 핸들러 구현](https://github.com/Soobinnni/jwt-auth/pull/5) |
| [feat: 회원 가입 기능 구현](https://github.com/Soobinnni/jwt-auth/pull/6)                     |
| [feat: 사용자 도메인 예외 처리 시스템 구현](https://github.com/Soobinnni/jwt-auth/pull/8)            |
| [test: 회원가입 기능 테스트 코드 작성](https://github.com/Soobinnni/jwt-auth/pull/10)              |
| [feat: JWT 기반 인증/인가 시스템 통합 구현](https://github.com/Soobinnni/jwt-auth/pull/12)         |
| [feat: 관리자 권한 변경 기능 추가 및 권한 시스템 리팩토링](https://github.com/Soobinnni/jwt-auth/pull/13)  |
| [test: 통합 테스트 코드 작성](https://github.com/Soobinnni/jwt-auth/pull/14)                   |

<br/>

## 🧪 테스트

### 테스트 커버리지

| 항목           | 설명                     |
|--------------|------------------------|
| 인증/인가 필터 테스트 | 토큰 검증, 권한 확인           |
| 로그인 API 테스트  | 성공/실패 케이스              |
| 토큰 갱신 테스트    | 리프레시 토큰 검증             |
| 회원가입 테스트     | 입력 검증, 중복 확인           |
| 관리자 권한 테스트   | 권한 부여 로직               |
| 도메인 객체 테스트   | VO(Value Object) 검증 로직 |

### 관련 문서

> 테스트 관련 PR 문서 → [🔗 링크](https://github.com/Soobinnni/jwt-auth/pull/14)

<br/>

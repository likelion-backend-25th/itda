# ITDA

**Interest + Together** — 관심사로 사람과 사람을 잇는 취미 SNS 백엔드

Like Lion Backend BootCamp 25기 · 1팀 응용프로젝트

공개 피드와 **구독자 전용 콘텐츠**, **테마 개인화**, **PortOne 결제**를 Spring Boot REST API로 제공합니다.

---

## Links

| | |
|--|--|
| Frontend | [https://itda.likelion.shop](https://itda.likelion.shop) |
| API | [https://api.eony.site](https://api.eony.site) |
| Swagger | [https://api.eony.site/swagger-ui/index.html](https://api.eony.site/swagger-ui/index.html) |
| Repository | [likelion-backend-25th/itda](https://github.com/likelion-backend-25th/itda) |

---

## Features

- **회원·인증** — 이메일 가입/로그인, Google·Kakao OAuth2, JWT Access + Refresh Cookie **RTR**
- **피드** — 게시글 CRUD, 카테고리·검색, 구독자 전용 글, S3 이미지
- **인터랙션** — 좋아요·스크랩 토글, 댓글, 팔로우
- **구독·결제** — PortOne prepare/complete/webhook, 구독·테마 결제·환불
- **테마** — 목록/구매/보유/적용, CSS 테마 주입
- **관리자** — 회원(활동 정지), 결제·환불·구독, 게시글/댓글, 테마 관리

---

## Tech Stack

| Layer | Stack |
|-------|--------|
| Language | Java 25 |
| Framework | Spring Boot 4, Spring Security, OAuth2 Client |
| Persistence | MyBatis, MySQL |
| Auth | JWT (JJWT), Refresh Token Rotation, HttpOnly Cookie |
| Storage / Pay | AWS S3, PortOne |
| Docs | springdoc OpenAPI (Swagger) |
| Deploy | Docker Compose, Nginx, Certbot, AWS EC2 · RDS · S3 |

Frontend (별도): React, TypeScript, Vite · Netlify

---

## Architecture

```text
Browser → Netlify (FE)
       → api.eony.site
            → Nginx (TLS / Certbot)
            → Spring Boot
                 ├─ AWS RDS (MySQL, Private)
                 └─ AWS S3 (images)
```

- Access Token: **1시간** (Bearer)
- Refresh Token: **7일** (HttpOnly Cookie, `Path=/api/v1/auth`, RTR)

자세한 구성: [docs/02_design/01_architecture.md](docs/02_design/01_architecture.md)

---

## Team

| 이름 | 역할 | 담당 |
|------|------|------|
| 이지원 | PM | 게시글·인터랙션·마이페이지 |
| 이승언 | PL | 회원·보안(JWT/OAuth)·테마·배포 |
| 박병찬 | 팀원 | 결제·환불 |
| 최승혁 | 팀원 | 구독·관리자 |
| 소지현 | 팀원 | 팔로우 |

---

## Getting Started

### Requirements

- JDK 25+
- MySQL 8+/9+
- (선택) Docker

### 1. Environment

```bash
cp .env.example .env
```

필수 값 예시: `DB_HOST`, `JWT_SECRET`, OAuth / PortOne / AWS 키 (`.env.example` 참고)

### 2. Database

스키마·시드:

- [src/main/resources/schema.sql](src/main/resources/schema.sql)
- [src/main/resources/data.sql](src/main/resources/data.sql)

로컬 테스트 계정 비밀번호(시드): `1234`

### 3. Run (local)

```bash
./gradlew bootRun
```

기본 API prefix: `/api/v1`  
Swagger(로컬): `http://localhost:8080/swagger-ui/index.html`

### 4. Run (Docker Compose · 서버)

```bash
docker compose up -d
```

- App: Spring Boot 컨테이너
- Nginx: 80/443 + Let’s Encrypt (Certbot renew)

---

## API Overview

| Domain | Base |
|--------|------|
| Auth | `/api/v1/auth` — login, refresh, logout, OAuth |
| Members | `/api/v1/members` — me, profile, interests, follow |
| Posts / Replies | `/api/v1/posts` |
| Themes | `/api/v1/themes` |
| Payments / Subscriptions | `/api/v1/payments`, `/api/v1/subscriptions` |
| Admin | `/api/v1/admin/...` |

명세: [docs/02_design/04_api_specification.md](docs/02_design/04_api_specification.md)

---

## Documentation

| 문서 | 경로 |
|------|------|
| 기획서 | [docs/01_planning/01_proposal.md](docs/01_planning/01_proposal.md) |
| PRD | [docs/01_planning/02_prd.md](docs/01_planning/02_prd.md) |
| 아키텍처 | [docs/02_design/01_architecture.md](docs/02_design/01_architecture.md) |
| ERD | [docs/02_design/02_erd.md](docs/02_design/02_erd.md) |
| 와이어프레임 | [docs/02_design/03_ui_wireframe.md](docs/02_design/03_ui_wireframe.md) |
| API 명세 | [docs/02_design/04_api_specification.md](docs/02_design/04_api_specification.md) |
| 트러블슈팅 | [docs/03_reports/troubleshooting.md](docs/03_reports/troubleshooting.md) |
| 산출물 작성 가이드 | [docs/00_deliverables_guide.md](docs/00_deliverables_guide.md) |

---

## License

Bootcamp team project — educational use.

# 1. REST API 명세서 및 더미 데이터 규격서 (ITDA SNS)

> 기준: 운영 API [`https://api.eony.site`](https://api.eony.site) · Swagger UI [`/swagger-ui/index.html`](https://api.eony.site/swagger-ui/index.html#/) · OpenAPI `/v3/api-docs`  
> Base Path: `/api/v1`  
> 샘플 회원/테마/게시글은 `data.sql` 시드 기준입니다.

## 목차
- [1.1 공통 응답 규격](#11-공통-응답-규격)
- [1.2 인증 API](#12-인증-api)
- [1.3 회원 · 팔로우 API](#13-회원--팔로우-api)
- [1.4 피드 · 게시글 API](#14-피드--게시글-api)
- [1.5 댓글 · 좋아요 · 스크랩 API](#15-댓글--좋아요--스크랩-api)
- [1.6 결제 · 구독 API](#16-결제--구독-api)
- [1.7 테마 API](#17-테마-api)
- [1.8 마이페이지 · 내 결제 API](#18-마이페이지--내-결제-api)
- [1.9 관리자 API (요약)](#19-관리자-api-요약)

---

## 1.1 공통 응답 규격

### 1.1.1 성공 응답 포맷 (HTTP 200 / 201 / 204)
- 공통 래퍼 없이 Controller가 DTO 단건, List, Page 객체를 Body로 직접 반환합니다.
- `204 No Content`는 Body 없이 성공을 의미합니다. (로그아웃, 일부 삭제 등)

단건 샘플 (`PostResponse`):
```json
{
  "id": 1,
  "memberId": 1,
  "nickname": "책읽는사람",
  "profileImage": "https://presigned.example/profile/user1_profile.png",
  "categoryId": 1,
  "categoryName": "독서",
  "content": "오늘 읽은 책 구절을 공유합니다.",
  "imageUrl": "https://presigned.example/posts/1.png",
  "replyCount": 3,
  "likeCount": 5,
  "viewCount": 12,
  "subscriberOnly": false,
  "liked": true,
  "scrapped": false,
  "createdAt": "2025-01-10T10:00:00",
  "updatedAt": "2025-01-10T10:00:00"
}
```

페이지 샘플 (`PageResponse`):
```json
{
  "content": [],
  "page": 1,
  "size": 6,
  "totalElements": 8,
  "totalPages": 2
}
```

### 1.1.2 인증 헤더 · 쿠키
| 구분 | 전달 방식 |
|------|-----------|
| Access Token | `Authorization: Bearer <accessToken>` |
| Refresh Token | HttpOnly Cookie `refreshToken` (path=`/api/v1/auth`, SameSite=None, Secure) |
| OAuth2 | `/oauth2/authorization/google`, `/oauth2/authorization/kakao` (Spring Security 기본 엔드포인트) |

### 1.1.3 실패 응답 포맷 (`ApiErrorResponse`)
- HTTP: 400, 401, 403, 404, 500
- 주요 `ErrorCode`: `INVALID_INPUT_VALUE`, `BUSINESS_RULE_VIOLATION`, `UNAUTHORIZED_ACCESS`, `FORBIDDEN_OPERATION`, `RESOURCE_NOT_FOUND`, `INTERNAL_SERVER_ERROR`

```json
{
  "code": "UNAUTHORIZED_ACCESS",
  "message": "인증이 필요하거나 유효하지 않은 자격 증명입니다.",
  "status": 401,
  "timestamp": "2026-10-02T10:00:00",
  "errors": []
}
```

Bean Validation 실패 예시:
```json
{
  "code": "INVALID_INPUT_VALUE",
  "message": "입력값 검증에 실패했습니다.",
  "status": 400,
  "timestamp": "2026-10-02T10:00:00",
  "errors": [
    {
      "field": "email",
      "rejectedValue": "",
      "reason": "이메일은 필수입니다."
    }
  ]
}
```

---

## 1.2 인증 API

### 1.2.1 로그인
- Method: `POST`
- URI: `/api/v1/auth/login`
- 인증: 불필요
- Request (`LoginRequest`):
```json
{
  "email": "user1@itda.com",
  "password": "password123!"
}
```
- Response (HTTP 200, `TokenResponse`) + `Set-Cookie: refreshToken=...`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```
> Refresh Token은 Body가 아니라 **HttpOnly Cookie**로만 전달됩니다. (사자그램 예시와 다름)

### 1.2.2 Access Token 재발급 (RTR)
- Method: `POST`
- URI: `/api/v1/auth/refresh`
- 인증: Cookie `refreshToken` **필수** (Request Body **없음**)
- Cookie 속성: `HttpOnly`, `Secure`, `SameSite=None`, `Path=/api/v1/auth`
- 처리: JWT·DB 유효성 검증 → 기존 Refresh **revoke** → Access/Refresh **재발급**(Rotation) → 새 Cookie 설정
- Response (HTTP 200, `TokenResponse`) + `Set-Cookie: refreshToken=<새토큰>`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```
- 실패 예시 (Cookie 없음 / 만료 / 이미 revoke): HTTP **401**
```json
{
  "code": "UNAUTHORIZED_ACCESS",
  "message": "유효하지 않거나 만료된 Refresh Token입니다.",
  "status": 401,
  "timestamp": "2026-10-02T11:00:00",
  "errors": []
}
```

### 1.2.3 로그아웃
- Method: `POST`
- URI: `/api/v1/auth/logout`
- 인증: Cookie `refreshToken` **선택** (Request Body **없음**)
- Cookie 속성: 로그인·refresh와 동일 (`HttpOnly`, `Secure`, `SameSite=None`, `Path=/api/v1/auth`)
- 처리:
  - Cookie가 있고 유효하면 DB에서 해당 Refresh **revoke**
  - Cookie가 없어도 성공 처리
  - 클라이언트 Cookie 삭제용 `Set-Cookie` 반환 (`maxAge=0`, value 빈 문자열)
- Response: HTTP **204 No Content** (Body 없음)

---

## 1.3 회원 · 팔로우 API

### 1.3.1 회원가입
- Method: `POST`
- URI: `/api/v1/members`
- 인증: 불필요 (Public)
- Content-Type: `multipart/form-data`
- Request Parts:

  | Part | 타입 | 필수 | 설명 |
  |------|------|------|------|
  | `request` | JSON (`SignupRequest`) | O | 회원 기본 정보·관심사 |
  | `profileImage` | file | X | 프로필 이미지. 있으면 S3 `profile/` 업로드 |

- `request` Body (`SignupRequest`):
```json
{
  "email": "newuser@itda.com",
  "password": "password123!",
  "nickname": "잇다새회원",
  "interestCategoryIds": [1, 2]
}
```
> `SignupRequest.profileImage` 문자열 필드는 사용하지 않습니다. 이미지는 **파일 part** `profileImage`로만 전달합니다.

- Response: HTTP **201 Created** (Body 없음)
- 서버 처리:
  1. 이메일 중복 검사
  2. 관심사 1개 이상 · 활성 카테고리(`common_code`) 검증
  3. 비밀번호 인코딩 후 `member` 저장 (`ROLE_USER`, `authmethod=LOCAL`)
  4. 기본 테마 id 조회 → `member.theme_id` 설정 + `theme_purchase` 등록
  5. `member_interest` 저장
  6. (선택) 프로필 이미지 S3 업로드 후 key 저장
- 실패 예시:
  - 중복 이메일 → `400` `"이미 가입된 이메일입니다."`
  - 관심사 없음/무효 → `400` `"관심사를 하나 이상 선택해 주세요."` / `"유효하지 않은 관심사입니다."`

### 1.3.2 내 프로필 조회
- Method: `GET`
- URI: `/api/v1/members/me`
- 인증: 필수
- Response (`MemberProfileResponse`, data.sql 1번 회원 기준):
```json
{
  "id": 1,
  "email": "user1@itda.com",
  "nickname": "책읽는사람",
  "profileImage": "https://presigned.example/profile/user1_profile.png",
  "role": "ROLE_USER",
  "introduction": "책과 독서를 좋아합니다.",
  "themeId": 3,
  "createdAt": "2025-01-01T10:00:00",
  "followerCount": 2,
  "followingCount": 3,
  "postCount": 5,
  "status": "ACTIVE",
  "interestCategoryIds": [1, 2]
}
```

### 1.3.3 내 프로필 수정
- Method: `PUT`
- URI: `/api/v1/members/me`
- 인증: 필수 (`Authorization: Bearer <accessToken>`)
- Content-Type: `multipart/form-data`
- Request Parts:

  | Part | 타입 | 필수 | 설명 |
  |------|------|------|------|
  | `request` | JSON (`MemberUpdateRequest`) | O | 닉네임·소개·이미지 초기화 플래그 |
  | `profileImage` | file | X | 새 프로필 이미지. 있으면 교체 우선 (`removeProfileImage` 무시) |

- `request` Body (`MemberUpdateRequest`):
```json
{
  "nickname": "책읽는사람",
  "introduction": "책과 독서를 좋아합니다.",
  "removeProfileImage": false
}
```
- Response: HTTP **204 No Content** (Body 없음)
- 규칙:
  - `profileImage` 파일이 있으면 S3 업로드 후 교체, 기존 키 삭제
  - 파일 없이 `removeProfileImage: true`면 프로필 이미지를 NULL로 초기화
  - 둘 다 없으면 닉네임·소개만 수정하고 기존 이미지 유지

### 1.3.4 내 관심사 수정
- Method: `PUT`
- URI: `/api/v1/members/me/interests`
```json
{
  "interestCategoryIds": [1, 3, 5]
}
```

### 1.3.5 타 회원 프로필 조회
- Method: `GET`
- URI: `/api/v1/members/{memberId}`
- Response: `MemberProfileResponse`

### 1.3.6 팔로우 / 언팔로우
- `POST /api/v1/members/{id}/follow`
- `DELETE /api/v1/members/{id}/follow`
- 인증: 필수

### 1.3.7 팔로워 · 팔로잉 목록
- `GET /api/v1/members/{id}/followers` → `List<FollowerResponse>`
- `GET /api/v1/members/{id}/followings` → `List<FollowingResponse>`
```json
[
  {
    "id": 2,
    "nickname": "요리하는사람",
    "profileImage": "https://presigned.example/profile/user2_profile.png"
  }
]
```

---

## 1.4 피드 · 게시글 API

### 1.4.1 피드 조회 (공개 + 구독 혼합)
- Method: `GET`
- URI: `/api/v1/posts`
- Query:
  - `publicCursor` (선택)
  - `subscribedCursor` (선택)
  - `categoryId` (선택)
  - `size` (선택)
- Response (`PostFeedResponse`):
```json
{
  "posts": [
    {
      "id": 1,
      "memberId": 1,
      "nickname": "책읽는사람",
      "profileImage": "https://presigned.example/profile/user1_profile.png",
      "categoryId": 1,
      "categoryName": "독서",
      "content": "오늘 읽은 책 구절을 공유합니다.",
      "imageUrl": "https://presigned.example/posts/1.png",
      "replyCount": 3,
      "likeCount": 5,
      "viewCount": 12,
      "subscriberOnly": false,
      "liked": false,
      "scrapped": false,
      "createdAt": "2025-01-10T10:00:00",
      "updatedAt": "2025-01-10T10:00:00"
    }
  ],
  "nextPublicCursor": "10",
  "nextSubscribedCursor": "8",
  "hasNext": true
}
```

### 1.4.2 게시글 검색 (커서 무한스크롤)
- Method: `GET`
- URI: `/api/v1/posts/search`
- Query: `keyword`, `targetMemberIds`(다중), `cursor`, `size`
- Response: `List<PostResponse>` (또는 검색용 페이지/커서 구조 — Swagger `PostResponse` 배열)

### 1.4.3 게시글 등록
- Method: `POST`
- URI: `/api/v1/posts`
- 인증: 필수 (`Authorization: Bearer <accessToken>`)
- Content-Type: `multipart/form-data`
- Request Parts:

  | Part | 타입 | 필수 | 설명 |
  |------|------|------|------|
  | `request` | JSON (`PostCreateRequest`) | O | 카테고리·본문·구독자 전용 여부 |
  | `imageUrl` | file | X | 게시글 이미지. 있으면 S3 업로드 후 key 저장 |

- `request` Body (`PostCreateRequest`):
```json
{
  "categoryId": 1,
  "content": "구독자 전용 취미 팁입니다.",
  "subscriberOnly": true
}
```
> JSON의 `imageUrl` 문자열 필드는 비워 두거나 생략합니다. 이미지는 **파일 part** 이름 `imageUrl`로 전달합니다.

- Response: HTTP **201 Created**
  - Header: `Location: /api/v1/posts/{id}`
  - Body: `PostResponse`

### 1.4.4 게시글 단건 조회
- Method: `GET`
- URI: `/api/v1/posts/{id}`
- Cookie `viewedPosts`(선택): 조회수 중복 방지용
- 구독자 전용 글은 작성자 또는 활성 구독자만 조회 가능
- Response: `PostResponse`

### 1.4.5 게시글 수정
- Method: `PUT`
- URI: `/api/v1/posts/{id}`
- 인증: 필수 (작성자만)
- Content-Type: `multipart/form-data`
- Request Parts: 등록과 동일 (`request` + 선택 `imageUrl` 파일)
- `request` Body (`PostUpdateRequest`):
```json
{
  "categoryId": 1,
  "content": "내용을 수정했습니다.",
  "subscriberOnly": false
}
```
- Response: HTTP 200, `PostResponse`

### 1.4.6 게시글 삭제
- Method: `DELETE`
- URI: `/api/v1/posts/{id}`
- 인증: 필수 (작성자 또는 관리자)
- Response: HTTP **204 No Content**

---

## 1.5 댓글 · 좋아요 · 스크랩 API

### 1.5.1 댓글 목록
- Method: `GET`
- URI: `/api/v1/posts/{postId}/replies`
- Response: `List<ReplyResponse>`
```json
[
  {
    "id": 1,
    "memberId": 2,
    "nickname": "요리하는사람",
    "profileImage": "https://presigned.example/profile/user2_profile.png",
    "postId": 1,
    "content": "좋은 글 감사합니다!",
    "createdAt": "2025-01-10T11:00:00",
    "updatedAt": "2025-01-10T11:00:00"
  }
]
```

### 1.5.2 댓글 등록 / 수정 / 삭제
- `POST /api/v1/posts/{postId}/replies`  
  Body: `{ "content": "좋은 글 잘 읽었습니다!" }`
- `PUT /api/v1/posts/{postId}/replies/{replyId}`
- `DELETE /api/v1/posts/{postId}/replies/{replyId}`

### 1.5.3 좋아요 토글
- Method: `POST`
- URI: `/api/v1/posts/{id}/like`
- Response (`PostLikeResponse`):
```json
{
  "liked": true,
  "likesCount": 6
}
```

### 1.5.4 스크랩 토글
- Method: `POST`
- URI: `/api/v1/posts/{id}/scrap`
- Response (`PostScrapResponse`):
```json
{
  "scrapped": true
}
```

---

## 1.6 결제 · 구독 API

> PG: **PortOne**. 결제 유형(`paymentType`)은 `THEME` / `SUBSCRIPTION`.

### 1.6.1 결제 준비
- Method: `POST`
- URI: `/api/v1/payments/prepare`
- 인증: 필수
- Request (`PaymentPrepareRequest`):
```json
{
  "paymentType": "THEME",
  "targetId": 3,
  "payMethod": "TOSSPAY"
}
```
- Response (`PaymentPrepareResponse`):
```json
{
  "paymentId": "payment-uuid-...",
  "amount": 3000,
  "storeId": "store-xxx",
  "channelKey": "channel-key-tosspay"
}
```
- 서버: 결제 대기(PS01) 저장 → PortOne 사전등록

### 1.6.2 결제 완료 검증
- Method: `POST`
- URI: `/api/v1/payments/complete`
- Request (`PaymentCompleteRequest`):
```json
{
  "paymentId": "payment-uuid-..."
}
```
- Response (`PaymentCompleteResponse`):
```json
{
  "paymentId": "payment-uuid-...",
  "status": "PAID",
  "amount": 3000,
  "transactionId": "tx_xxx"
}
```
- 서버: PortOne `PAID`·금액·본인 검증 후 PS02, 테마 구매 또는 구독(SS01) 생성 (동일 트랜잭션)

### 1.6.3 결제 환불 요청
- Method: `POST`
- URI: `/api/v1/payments/refund/{paymentId}`
- Request: `{ "reason": "단순 변심" }`
- Response (`PaymentRefundResponse`): `paymentId`, `cancellationId`, `refundAmount`, `deductionAmount`
- 규칙 요약:
  - 테마: 적용(`is_used`) 이력이 있으면 환불 불가
  - 구독: 사용 일수 차감 + 남은 금액 10% 위약금

### 1.6.4 구독 생성
- Method: `POST`
- URI: `/api/v1/subscriptions`
- Request (`SubscriptionRequest`):
```json
{
  "targetId": 3,
  "priceId": 1
}
```
> 실제 결제 확정은 `payments/prepare` → `complete` 플로우와 연동됩니다.

### 1.6.5 구독 상태 · 해지 · 삭제
- `GET /api/v1/subscriptions/{targetId}` → `{ "subscribed": true }`
- `PATCH /api/v1/subscriptions/{targetId}/cancel`
- `DELETE /api/v1/subscriptions/{targetId}`

### 1.6.6 내 구독 목록
- `GET /api/v1/subscriptions/me?page=1&size=10`
- Response: `PageResponse<MySubscriptionResponse>`

### 1.6.7 구독자 수 · 월 수익
- `GET /api/v1/subscriptions/{targetId}/count` → `{ "subscriberCount": 12 }`
- `GET /api/v1/subscriptions/{targetId}/income` → `{ "monthlyIncome": 50000 }`

---

## 1.7 테마 API

### 1.7.1 테마 목록
- Method: `GET`
- URI: `/api/v1/themes?page=1&size=6&keyword=`
- Response: `PageResponse<ThemeResponse>` (ON_SALE만)
```json
{
  "content": [
    {
      "id": 3,
      "themeName": "오션 블루",
      "price": 3000,
      "thumbnailUrl": "https://presigned.example/themes/ocean.png",
      "themeCode": "OCEAN",
      "isOwned": true,
      "isApplied": true
    }
  ],
  "page": 1,
  "size": 6,
  "totalElements": 8,
  "totalPages": 2
}
```

### 1.7.2 테마 상세
- `GET /api/v1/themes/{themeId}` → `ThemeDetailResponse`

### 1.7.3 0원 테마 무료 수령
- `POST /api/v1/themes/{themeId}/claim`
- 유료(price>0)이거나 이미 보유 시 400

### 1.7.4 테마 적용
- `POST /api/v1/themes/{themeId}/apply`
- 보유(또는 기본 테마)만 가능 → `member.theme_id` 갱신, 필요 시 `is_used=true`

### 1.7.5 테마 스타일 (CSS)
- Method: `GET`
- URI: `/api/v1/themes/{themeId}/styles`
- 인증: 필수
- 권한: **기본 테마 또는 구매(보유) 회원만**
- Response (`ThemeStylesResponse`):
```json
{
  "themeId": 3,
  "themeCode": "OCEAN",
  "cssText": ":root[data-theme='ocean']{--bg:#d9f0f8;...}"
}
```
- FE는 `themeCode`를 `data-theme`에 맞추고 `cssText`를 주입해 동적 테마를 적용합니다.

### 1.7.6 내 보유 테마
- `GET /api/v1/themes/owned?page=1&size=6`

---

## 1.8 마이페이지 · 내 결제 API

### 1.8.1 내 글 / 좋아요 / 스크랩
- `GET /api/v1/mypage/posts?cursor=&size=`
- `GET /api/v1/mypage/likes?cursor=&size=`
- `GET /api/v1/mypage/scraps?cursor=&size=`
- Response (`MyPagePostResponse`):
```json
{
  "posts": [],
  "nextCursor": "15",
  "hadNext": true
}
```

### 1.8.2 내 결제 내역
- `GET /api/v1/customer/payments`
- Response: `List<MyPaymentResponse>` (`refundAvailable` 포함)

---

## 1.9 관리자 API (요약)

> 모두 `ROLE_ADMIN` 필요 (`/api/v1/admin/**`)

| Method | URI | 설명 |
|--------|-----|------|
| GET | `/admin/members` | 회원 목록 |
| PATCH | `/admin/members/{memberId}/status?status=SUSPENDED` | 회원 정지/활성화 |
| GET | `/admin/payments` | 결제 목록 |
| GET | `/admin/payments/refunds` | 환불 목록 |
| PATCH | `/admin/payments/{paymentId}/refund` | 환불 승인 |
| PATCH | `/admin/payments/{paymentId}/refund/reject` | 환불 거절 |
| GET | `/admin/subscriptions` | 구독 목록 |
| GET | `/admin/posts` | 게시글 목록 |
| DELETE | `/admin/posts/{postId}` | 게시글 삭제 |
| GET | `/admin/replies` | 댓글 목록 |
| DELETE | `/admin/replies/{replyId}` | 댓글 삭제 |
| GET | `/admin/themes` | 테마 목록 |
| POST | `/admin/themes` | 테마 등록 (multipart: 메타 + `cssText` + 썸네일) |
| PUT | `/admin/themes/{themeId}` | 테마 수정 |
| PATCH | `/admin/themes/{themeId}/status?status=ON_SALE` | 판매 상태 |
| PATCH | `/admin/themes/{themeId}/default` | 기본 테마 지정 |

관리자 테마 등록 시 `cssText`를 DB에 저장하므로, **FE 재배포 없이** 테마 스타일을 운영할 수 있습니다.

---

## 부록 A. 시드 계정 (data.sql)

| id | email | nickname | 비고 |
|----|-------|----------|------|
| 1 | user1@itda.com | 책읽는사람 | 오션 테마 적용 |
| 2 | user2@itda.com | 요리하는사람 | |
| 3 | creator@itda.com | 오늘의취미 | 크리에이터 |
| 7 | admin@itda.com | (admin) | `ROLE_ADMIN` |

비밀번호는 시드 bcrypt 해시 기준(팀 내부 공유 값)을 사용합니다.

## 부록 B. 실시간 명세
최신 스키마·Try it out은 Swagger UI를 참고하세요.  
→ [https://api.eony.site/swagger-ui/index.html#/](https://api.eony.site/swagger-ui/index.html#/)

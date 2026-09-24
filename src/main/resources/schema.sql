-- 0. 기존 테이블 삭제 (외래 키 참조 역순으로 삭제)
DROP TABLE IF EXISTS theme_purchase;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS subscription;
DROP TABLE IF EXISTS post_reaction;
DROP TABLE IF EXISTS reply;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS member_interest;
DROP TABLE IF EXISTS follow;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS theme;
DROP TABLE IF EXISTS common_code;



-- 1. 공통 코드 테이블
-- 코드 종류
-- 예:
-- 1 = 결제 상태
-- 2 = 결제 수단
-- 3 = 게시글 카테고리
-- 4 = 구독 상태
CREATE TABLE common_code (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        type INT NOT NULL,
                        -- CA10, PS01 등의 4자리 코드
                        code VARCHAR(4) NOT NULL,
                        -- 사용자에게 표시할 이름
                        name VARCHAR(30) NOT NULL,
                        -- 관리자에게 코드에 대한 설명
                        description VARCHAR(255),
                        -- 정렬 순서
                        sort INT,
                        -- 활성화 여부
                        is_active BOOLEAN NOT NULL DEFAULT TRUE,
                        UNIQUE (type, code)
);

-- 2. 사이트 등록 테마 테이블
CREATE TABLE theme (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        theme_name VARCHAR(100) NOT NULL,
                        description VARCHAR(255),
                        price INT NOT NULL DEFAULT 0,
                        thumbnail_url VARCHAR(255),
                        theme_code VARCHAR(255) NOT NULL UNIQUE,
                        status VARCHAR(255) NOT NULL DEFAULT 'ON_SALE',  -- ON_SALE: 상점에서 판매중
                        is_default BOOLEAN NOT NULL DEFAULT FALSE,
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,   -- 현재 시간으로 저장하고 해당 행이 수정될 때마다 자동으로 수정 시작을 현재 시간으로 갱신
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- 3. 회원 테이블 (기본 개체)
CREATE TABLE member (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        nickname VARCHAR(50) NOT NULL DEFAULT '철수',
                        role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',  -- ROLE_USER 회원, ROLE_ADMIN 관리자
                        -- default 기본 이미지 주소 추가
                        profile_image VARCHAR(255),
                        theme_id BIGINT NOT NULL DEFAULT 1,
                        introduction VARCHAR(255),
                        authmethod VARCHAR(20) NOT NULL,    -- 로그인 인증 방식
                        month_income BIGINT,
                        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (theme_id) REFERENCES theme(id)
);

-- 4. 회원 관심 테이블
CREATE TABLE member_interest (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        member_id BIGINT NOT NULL,
                        category_id BIGINT NOT NULL,
                        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                        FOREIGN KEY (category_id) REFERENCES common_code(id) ON DELETE CASCADE
);

-- 5. 회원 팔로우 테이블
CREATE TABLE follow (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        from_id BIGINT NOT NULL,
                        to_id BIGINT NOT NULL,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (from_id) REFERENCES member(id) ON DELETE CASCADE,
                        FOREIGN KEY (to_id) REFERENCES member(id) ON DELETE CASCADE,
                        UNIQUE (from_id, to_id),        -- 같은 사용자 여러번 팔로우하지 못하도록
                        CHECK ( from_id <> to_id )      -- 자기 자신 팔로우 방지
);

-- 6. 게시글 테이블
CREATE TABLE post (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        member_id BIGINT NOT NULL,
                        category_id BIGINT NOT NULL,
                        content TEXT NOT NULL,
                        image_url VARCHAR(255),
                        like_count INT NOT NULL DEFAULT 0,
                        view_count INT NOT NULL DEFAULT 0,
                        subscriber_only BOOLEAN NOT NULL DEFAULT FALSE,    -- 구독자 전용 여부
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                        FOREIGN KEY (category_id) REFERENCES common_code(id) ON DELETE CASCADE
);

-- 7. 댓글 테이블
CREATE TABLE reply (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       member_id BIGINT NOT NULL,
                       post_id BIGINT NOT NULL,
                       content TEXT NOT NULL,
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                       FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE
);

-- 8. 게시글 좋아요/스크랩 테이블
CREATE TABLE post_reaction (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        member_id BIGINT NOT NULL,
                        post_id BIGINT NOT NULL,
                        type INT NOT NULL,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                        FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                        UNIQUE (member_id, post_id, type)   -- 같은 게시글에 같은 reaction 중복방지
);
-- 9. 결제 테이블
CREATE TABLE payment (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        member_id BIGINT NOT NULL,
                        payment_type VARCHAR(20) NOT NULL,
                        target_id BIGINT NOT NULL,
                        payment_id VARCHAR(100),
                        transaction_id VARCHAR(100) NOT NULL UNIQUE, -- 서비스 내부 주문번호
                        amount BIGINT NOT NULL,
                        -- 결제 상태: WAITING / PAID / FAILED / CANCELLED 등
                        status_id BIGINT NOT NULL,
                        -- 결제 수단: 카드, 카카오페이 등
                        -- 결제 레코드를 먼저 만들고 결제 수단은 나중에 확정될 수 있기 때문에 NULL허용
                        pay_method_id BIGINT,
                        -- CURRENT_TIME_STAMP인지?
                        paid_at DATETIME DEFAULT NULL,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                        FOREIGN KEY (status_id) REFERENCES common_code(id),
                        FOREIGN KEY (pay_method_id) REFERENCES common_code(id)
);

-- 10. 사용자 정기 구독 테이블
CREATE TABLE subscription (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        member_id BIGINT NOT NULL,
                          -- 구독 대상 회원
                        target_id BIGINT NOT NULL,
                        customer_uid VARCHAR(100) NOT NULL,
                        price_id BIGINT NOT NULL,
                          -- ACTIVE / CANCELLED / EXPIRED 등
                          -- DEFAULT를 둘지 말지 고민
                        status_id BIGINT NOT NULL,
                        next_billing_at DATETIME NOT NULL,
                          -- 실제 구독 종료일
                        ended_at DATETIME,
                        started_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                        FOREIGN KEY (target_id) REFERENCES member(id) ON DELETE CASCADE,
                        FOREIGN KEY (price_id) REFERENCES common_code(id),
                        FOREIGN KEY (status_id) REFERENCES common_code(id),
                          -- 자기 자신 구독 방지
                        CHECK ( member_id <> target_id)
);

-- 11. 구매한 테마 테이블
CREATE TABLE theme_purchase (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        member_id BIGINT NOT NULL,
                        theme_id BIGINT NOT NULL,
                        payment_id BIGINT,
                        apply_theme BOOLEAN,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                        FOREIGN KEY (theme_id) REFERENCES theme(id),
                        FOREIGN KEY (payment_id) REFERENCES payment(id),
                        -- 같은 회원이 같은 테마를 중복 구매하지 못하도록 설정
                        UNIQUE (member_id,theme_id)

);
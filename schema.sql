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
                        theme_id BIGINT NOT NULL,
                        introduction VARCHAR(255),
                        authmethod VARCHAR(20) NOT NULL,    -- 로그인 인증 방식
                        update_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (theme_id) REFERENCES theme(id) ON DELETE CASCADE
);

-- 4. 회원 관심 테이블
CREATE TABLE member_interest (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        member_id BIGINT NOT NULL,
                        category_id BIGINT NOT NULL,
                        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                        FOREIGN KEY (category_id) REFERENCES common_code(code) ON DELETE CASCADE
);

-- 3. 게시글 테이블
CREATE TABLE post (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      member_id BIGINT NOT NULL,
                      category_id BIGINT NOT NULL,
                      content TEXT NOT NULL,
                      image_url VARCHAR(255),
                      like_count INT DEFAULT 0,
                      view_count INT DEFAULT 0,
                      subscriber_only BOOLEAN DEFAULT FALSE,    -- 구독자 전용 여부
                      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                      FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                      FOREIGN KEY (category_id) REFERENCES common_code(code) ON DELETE CASCADE
);

-- 4. 댓글 테이블
CREATE TABLE reply (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       post_id BIGINT NOT NULL,
                       member_id BIGINT NOT NULL,
                       content TEXT NOT NULL,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                       FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE
);

-- 5. 게시글 좋아요/스크랩 테이블
CREATE TABLE post_reaction (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               member_id BIGINT NOT NULL,
                               post_id BIGINT NOT NULL,
                               type INT NOT NULL,
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                               FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE
);

-- 6. 결제 테이블
CREATE TABLE payment (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         member_id BIGINT NOT NULL,
                         post_id BIGINT NOT NULL,
                         payment_type VARCHAR(30) NOT NULL,
    -- subscription target_id 하고 컬럼 명이 겹치는데 이대로 진행할건가요?
                         target_id BIGINT NOT NULL,
                         imp_uid BIGINT NOT NULL,
                         merchan_uid BIGINT NOT NULL,
                         amount BIGINT NOT NULL,
    -- 공통 코드의 타입 1의 무엇을 default값으로 저장하는가?
                         status_id BIGINT NOT NULL DEFAULT 1,
    -- 공통 코드의 타입 2, 결제 수단 등록? null을 허용해야하는가?
                         pay_method_id BIGINT NOT NULL,
    -- CURRENT_TIME_STAMP인지?
                         paid_at DATETIME,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                         FOREIGN KEY (status_id) REFERENCES common_code(code) ON DELETE CASCADE,
                         FOREIGN KEY (pay_method_id) REFERENCES common_code(code) ON DELETE CASCADE
);

-- 7. 사용자 정기 구독 테이블
CREATE TABLE subscription (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              member_id BIGINT NOT NULL,
                              target_id BIGINT NOT NULL,
                              customer_id VARCHAR(100) NOT NULL,
                              price_id BIGINT NOT NULL,
                              status_id VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                              next_billing_at DATETIME NOT NULL,
                              started_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                              ended_at DATETIME,
                              FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                              FOREIGN KEY (target_id) REFERENCES member(id) ON DELETE CASCADE,
                              FOREIGN KEY (price_id) REFERENCES common_code(code) ON DELETE CASCADE,
                              FOREIGN KEY (status_id) REFERENCES common_code(code) ON DELETE CASCADE
);


 -- 8. 게시글 좋아요/ 스크랩
CREATE TABLE post_reaction(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    type INT NOT NULL  -- 1 LIKE, 2 SCRAP
);

-- 10. 구매한 테마 테이블
CREATE TABLE theme_purchase (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                member_id BIGINT NOT NULL,
                                theme_id BIGINT NOT NULL,
                                payment_id BIGINT NOT NULL,
                                apply_theme BOOLEAN,
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 11. 회원 팔로우 테이블
CREATE TABLE follow (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        from_id INT NOT NULL,
                        to_id INT NOT NULL,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP

);
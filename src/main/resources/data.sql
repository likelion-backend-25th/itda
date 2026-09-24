-- =========================================================
-- ITDA TEST DATA
-- =========================================================

-- 1. 공통 코드
-- type: 1 결제 상태 / 2 결제 수단 / 3 게시글 카테고리
--       4 구독 상태 / 5 구독 가격 / 6 게시글 반응
INSERT INTO common_code (id, type, code, name, description, sort, is_active)
VALUES
-- 결제 상태
(1, 1, 'PS01', '결제 대기', '결제 대기 상태', 1, TRUE),
(2, 1, 'PS02', '결제 완료', '결제가 정상적으로 완료된 상태', 2, TRUE),
(3, 1, 'PS03', '결제 취소', '결제가 취소된 상태', 3, TRUE),
(4, 1, 'PS04', '환불 완료', '결제가 환불된 상태', 4, TRUE),

-- 결제 수단
(5, 2, 'PM01', '카드', '신용카드 및 체크카드', 1, TRUE),
(6, 2, 'PM02', '카카오페이', '카카오페이 결제', 2, TRUE),
(7, 2, 'PM03', '토스페이', '토스페이 결제', 3, TRUE),

-- 게시글 카테고리
(8, 3, 'CA01', '독서', '독서 및 책 관련 취미', 1, TRUE),
(9, 3, 'CA02', '음악', '음악 관련 취미', 2, TRUE),
(10, 3, 'CA03', '요리', '요리 및 베이킹 관련 취미', 3, TRUE),
(11, 3, 'CA04', '공예', '공예 및 만들기 관련 취미', 4, TRUE),
(12, 3, 'CA05', '쥬얼리', '쥬얼리 및 액세서리 관련 취미', 5, TRUE),
(13, 3, 'CA06', '그림', '그림 및 미술 관련 취미', 6, TRUE),
(14, 3, 'CA07', '기타', '기타 취미', 7, TRUE),

-- 구독 상태
(15, 4, 'SS01', 'ACTIVE', '현재 구독 중인 상태', 1, TRUE),
(16, 4, 'SS02', 'CANCELLED', '구독이 취소된 상태', 2, TRUE),
(17, 4, 'SS03', 'EXPIRED', '구독 기간이 종료된 상태', 3, TRUE),
(18, 4, 'SS04', 'PAUSED', '구독이 일시 정지된 상태', 4, TRUE),

-- 구독 가격
(19, 5, 'PR01', '월 3,000원', '월 구독 3,000원', 1, TRUE),
(20, 5, 'PR02', '월 5,000원', '월 구독 5,000원', 2, TRUE),
(21, 5, 'PR03', '월 10,000원', '월 구독 10,000원', 3, TRUE),

-- 게시글 반응
(22, 6, 'RT01', 'LIKE', '게시글 좋아요', 1, TRUE),
(23, 6, 'RT02', 'SCRAP', '게시글 스크랩', 2, TRUE);


-- 2. 테마
INSERT INTO theme (id, theme_name, description, price, thumbnail_url, theme_code, status, is_default)
VALUES
    (1, '기본 테마', 'ITDA 기본 테마', 0, '/images/themes/default.png', 'DEFAULT', 'ON_SALE', TRUE),
    (2, '오션 블루', '시원한 바다 느낌의 테마', 3000, '/images/themes/ocean.png', 'OCEAN', 'ON_SALE', FALSE),
    (3, '포레스트 그린', '차분한 숲 느낌의 테마', 3000, '/images/themes/forest.png', 'FOREST', 'ON_SALE', FALSE);


-- 3. 회원
-- 테스트 비밀번호: 1234
-- password는 프로젝트에서 사용하는 BCrypt 해시로 교체 가능
INSERT INTO member (
    id, email, password, nickname, role, introduction,
    profile_image, theme_id, authmethod, month_income
)
VALUES
    (1, 'user1@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '책읽는사람', 'ROLE_USER', '책과 독서를 좋아합니다.', '/images/profile/user1.png', 1, 'LOCAL', 0),

    (2, 'user2@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '요리하는사람', 'ROLE_USER', '맛있는 요리를 만드는 것을 좋아합니다.', '/images/profile/user2.png', 1, 'LOCAL', 0),

    (3, 'creator@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '오늘의취미', 'ROLE_USER', '취미를 함께 나누고 있습니다.', '/images/profile/creator.png', 2, 'LOCAL', 50000),

    (4, 'artist@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '그림그리는사람', 'ROLE_USER', '그림과 드로잉을 공유합니다.', '/images/profile/artist.png', 1, 'LOCAL', 30000);


-- 4. 회원 관심 카테고리
INSERT INTO member_interest (member_id, category_id)
VALUES
    (1, 8), (1, 9),
    (2, 10), (2, 11),
    (3, 8), (3, 9), (3, 10),
    (4, 13), (4, 11);


-- 5. 게시글
INSERT INTO post (
    id, member_id, category_id, content, image_url,
    like_count, view_count, subscriber_only
)
VALUES
    (1, 1, 8, '오늘 읽은 책이 정말 재미있었어요. 오랜만에 시간 가는 줄 모르고 읽었습니다.',
     '/images/posts/book1.png', 3, 25, FALSE),

    (2, 1, 8, '요즘 읽고 있는 책입니다. 비슷한 책을 좋아한다면 추천해요.',
     '/images/posts/book2.png', 5, 41, FALSE),

    (3, 2, 10, '주말에 직접 만든 파스타입니다. 생각보다 만들기 어렵지 않았어요.',
     '/images/posts/pasta.png', 7, 56, FALSE),

    (4, 3, 9, '이번 주에 들었던 음악들을 정리해봤어요. 구독자분들과 공유합니다.',
     '/images/posts/music.png', 10, 83, TRUE),

    (5, 3, 10, '제가 자주 사용하는 홈카페 레시피를 정리했습니다.',
     '/images/posts/cafe.png', 8, 72, TRUE),

    (6, 4, 13, '최근에 그린 그림입니다. 새로운 재료를 사용해봤어요.',
     '/images/posts/drawing.png', 4, 31, FALSE),

    (7, 4, 13, '그림을 그릴 때 사용하는 도구들을 소개해볼게요.',
     '/images/posts/tools.png', 6, 48, TRUE);


-- 6. 댓글
INSERT INTO reply (id, post_id, member_id, content)
VALUES
    (1, 1, 2, '저도 이 책 읽어보고 싶네요!'),
    (2, 1, 3, '저도 재미있게 읽었던 책이에요.'),
    (3, 3, 1, '파스타 정말 맛있어 보여요!'),
    (4, 4, 2, '좋은 음악 추천 감사합니다.'),
    (5, 6, 1, '그림 분위기가 정말 좋네요.');


-- 7. 게시글 좋아요 / 스크랩
INSERT INTO post_reaction (id, member_id, post_id, type)
VALUES
-- 좋아요
(1, 2, 1, 22),
(2, 3, 1, 22),
(3, 4, 1, 22),
(4, 1, 3, 22),
(5, 3, 3, 22),
(6, 4, 3, 22),
(7, 1, 6, 22),

-- 스크랩
(8, 2, 1, 23),
(9, 3, 2, 23),
(10, 1, 3, 23),
(11, 4, 4, 23);


-- 8. 팔로우
INSERT INTO follow (id, from_id, to_id)
VALUES
    (1, 1, 3),
    (2, 2, 3),
    (3, 1, 4),
    (4, 3, 4),
    (5, 4, 3);


-- 9. 테마 결제
INSERT INTO payment (
    id, member_id, payment_type, target_id,
    payment_id, transaction_id, amount, status_id, pay_method_id, paid_at
)
VALUES
    (1, 1, 'THEME', 2, 'imp_test_theme_001', 'merchant_theme_001', 3000, 2, 5, CURRENT_TIMESTAMP),
    (2, 2, 'THEME', 3, 'imp_test_theme_002', 'merchant_theme_002', 3000, 2, 6, CURRENT_TIMESTAMP);


-- 10. 테마 구매
INSERT INTO theme_purchase (id, member_id, theme_id, payment_id, apply_theme)
VALUES
    (1, 1, 2, 1, TRUE),
    (2, 2, 3, 2, TRUE);


-- 11. 회원 테마 적용
UPDATE member SET theme_id = 2 WHERE id = 1;
UPDATE member SET theme_id = 3 WHERE id = 2;


-- 12. 구독 결제
INSERT INTO payment (
    id, member_id, payment_type, target_id,
    payment_id, transaction_id, amount, status_id, pay_method_id, paid_at
)
VALUES
    (3, 1, 'SUBSCRIPTION', 3, 'imp_test_subscription_001', 'merchant_subscription_001',
     5000, 2, 5, CURRENT_TIMESTAMP),

    (4, 2, 'SUBSCRIPTION', 3, 'imp_test_subscription_002', 'merchant_subscription_002',
     5000, 2, 6, CURRENT_TIMESTAMP);


-- 13. 정기 구독
INSERT INTO subscription (
    id, member_id, target_id, customer_uid,
    price_id, status_id, next_billing_at, started_at, ended_at
)
VALUES
    (1, 1, 3, 'customer_test_001', 20, 15,
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 MONTH), CURRENT_TIMESTAMP, NULL),

    (2, 2, 3, 'customer_test_002', 20, 15,
     DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 MONTH), CURRENT_TIMESTAMP, NULL);
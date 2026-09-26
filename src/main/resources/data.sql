-- =========================================================
-- ITDA TEST DATA (확장)
-- 비밀번호(로컬): 1234
-- =========================================================

-- 1. 공통 코드
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


-- 2. 테마 (ON_SALE 8개 → size=6이면 2페이지 / HIDDEN 1개)
INSERT INTO theme (id, theme_name, description, price, thumbnail_url, theme_code, status, is_default)
VALUES
    (1, '기본 테마',     'ITDA 기본 테마 (무료, is_default)', 0,    '/images/themes/default.png',  'DEFAULT',  'ON_SALE', TRUE),
    (2, '오션 블루',     '시원한 바다 느낌의 테마',           3000, '/images/themes/ocean.png',    'OCEAN',    'ON_SALE', FALSE),
    (3, '포레스트 그린', '차분한 숲 느낌의 테마',             3000, '/images/themes/forest.png',   'FOREST',   'ON_SALE', FALSE),
    (4, '선셋 코랄',     '따뜻한 노을 느낌의 테마',           5000, '/images/themes/sunset.png',   'SUNSET',   'ON_SALE', FALSE),
    (5, '라벤더 나이트', '보랏빛 밤 느낌의 테마',             5000, '/images/themes/lavender.png', 'LAVENDER', 'ON_SALE', FALSE),
    (6, '미드나잇 다크', '눈 편한 다크 테마',                 4000, '/images/themes/dark.png',     'DARK',     'ON_SALE', FALSE),
    (7, '코튼 크림',     '부드러운 크림톤 테마',             2000, '/images/themes/cream.png',    'CREAM',    'ON_SALE', FALSE),
    (8, '스카이 라이트', '맑은 하늘 느낌의 테마',             2000, '/images/themes/sky.png',      'SKY',      'ON_SALE', FALSE),
    (9, '숨김 테마',     '관리자 비공개 테마 (목록 제외)',     9999, '/images/themes/hidden.png',   'HIDDEN',   'HIDDEN',  FALSE);


-- 3. 회원
INSERT INTO member (
    id, email, password, nickname, role, introduction,
    profile_image, theme_id, authmethod, month_income
)
VALUES
    (1, 'user1@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '책읽는사람', 'ROLE_USER', '책과 독서를 좋아합니다.', '/images/profile/user1.png', 2, 'LOCAL', 0),
    (2, 'user2@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '요리하는사람', 'ROLE_USER', '맛있는 요리를 만드는 것을 좋아합니다.', '/images/profile/user2.png', 3, 'LOCAL', 0),
    (3, 'creator@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '오늘의취미', 'ROLE_USER', '취미를 함께 나누고 있습니다.', '/images/profile/creator.png', 1, 'LOCAL', 50000),
    (4, 'artist@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '그림그리는사람', 'ROLE_USER', '그림과 드로잉을 공유합니다.', '/images/profile/artist.png', 1, 'LOCAL', 30000),
    (5, 'music@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '멜로디유저', 'ROLE_USER', '플레이리스트를 모으는 중입니다.', '/images/profile/music.png', 1, 'LOCAL', 0),
    (6, 'craft@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '손뜨개러', 'ROLE_USER', '뜨개질과 공예를 좋아합니다.', '/images/profile/craft.png', 6, 'LOCAL', 10000),
    (7, 'admin@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '관리자', 'ROLE_ADMIN', 'ITDA 관리자 계정입니다.', '/images/profile/admin.png', 1, 'LOCAL', 0);


-- 4. 회원 관심 카테고리
INSERT INTO member_interest (member_id, category_id)
VALUES
    (1, 8), (1, 9),
    (2, 10), (2, 11),
    (3, 8), (3, 9), (3, 10),
    (4, 13), (4, 11),
    (5, 9), (5, 14),
    (6, 11), (6, 12), (6, 10),
    (7, 8), (7, 14);


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
     '/images/posts/tools.png', 6, 48, TRUE),
    (8, 5, 9, '비 오는 날 듣기 좋은 플레이리스트를 모아봤습니다.',
     '/images/posts/playlist.png', 9, 60, FALSE),
    (9, 5, 9, '구독자 전용: 이번 달 추천 앨범 리스트입니다.',
     '/images/posts/album.png', 12, 90, TRUE),
    (10, 6, 11, '첫 코바늘 작품이에요. 실패도 많았지만 완성하니 뿌듯합니다.',
     '/images/posts/knit.png', 5, 33, FALSE),
    (11, 6, 12, '비즈로 만든 간단한 팔찌 튜토리얼을 남겨둡니다.',
     '/images/posts/bead.png', 7, 44, FALSE),
    (12, 2, 10, '초간단 김치볶음밥 레시피. 자취생도 바로 따라할 수 있어요.',
     '/images/posts/friedrice.png', 11, 70, FALSE),
    (13, 1, 8, '서점 나들이 후기. 신간 코너에서 발견한 책들입니다.',
     '/images/posts/bookstore.png', 2, 18, FALSE),
    (14, 3, 14, '취미를 꾸준히 하는 루틴을 공유합니다. (구독자 전용)',
     '/images/posts/routine.png', 15, 120, TRUE),
    (15, 4, 13, '스케치북 한 권을 다 채웠습니다. 과정 사진 모음.',
     '/images/posts/sketch.png', 8, 55, FALSE);


-- 6. 댓글
INSERT INTO reply (id, post_id, member_id, content)
VALUES
    (1, 1, 2, '저도 이 책 읽어보고 싶네요!'),
    (2, 1, 3, '저도 재미있게 읽었던 책이에요.'),
    (3, 3, 1, '파스타 정말 맛있어 보여요!'),
    (4, 4, 2, '좋은 음악 추천 감사합니다.'),
    (5, 6, 1, '그림 분위기가 정말 좋네요.'),
    (6, 8, 1, '비 오는 날 듣기 딱이겠어요.'),
    (7, 8, 3, '플레이리스트 공유 고마워요!'),
    (8, 10, 4, '코바늘 도안 있으신가요?'),
    (9, 10, 2, '색감이 예뻐요.'),
    (10, 12, 5, '오늘 저녁에 만들어볼게요.'),
    (11, 12, 6, '자취 필수 레시피네요.'),
    (12, 15, 3, '스케치 과정이 궁금했어요.'),
    (13, 15, 1, '한 권 다 채우시다니 대단해요.'),
    (14, 5, 2, '홈카페 레시피 저장했습니다.'),
    (15, 11, 5, '비즈 팔찌 난이도 어떤가요?');


-- 7. 게시글 좋아요 / 스크랩
INSERT INTO post_reaction (id, member_id, post_id, type)
VALUES
-- LIKE (22)
(1, 2, 1, 22), (2, 3, 1, 22), (3, 4, 1, 22),
(4, 1, 3, 22), (5, 3, 3, 22), (6, 4, 3, 22),
(7, 1, 6, 22), (8, 2, 6, 22),
(9, 1, 8, 22), (10, 3, 8, 22), (11, 6, 8, 22),
(12, 1, 10, 22), (13, 4, 10, 22), (14, 5, 10, 22),
(15, 1, 12, 22), (16, 3, 12, 22), (17, 5, 12, 22), (18, 6, 12, 22),
(19, 2, 15, 22), (20, 5, 15, 22),
(21, 1, 4, 22), (22, 2, 5, 22), (23, 4, 9, 22),
-- SCRAP (23)
(24, 2, 1, 23), (25, 3, 2, 23), (26, 1, 3, 23), (27, 4, 4, 23),
(28, 1, 8, 23), (29, 6, 8, 23),
(30, 2, 10, 23), (31, 5, 11, 23),
(32, 3, 12, 23), (33, 1, 14, 23), (34, 6, 15, 23);


-- 8. 팔로우
INSERT INTO follow (id, from_id, to_id)
VALUES
    (1, 1, 3), (2, 2, 3), (3, 1, 4), (4, 3, 4), (5, 4, 3),
    (6, 5, 3), (7, 5, 4), (8, 6, 3), (9, 6, 4),
    (10, 1, 5), (11, 2, 5), (12, 4, 5),
    (13, 1, 6), (14, 2, 6), (15, 5, 6),
    (16, 3, 1), (17, 4, 1), (18, 5, 1);


-- 9. 테마/구독 결제
INSERT INTO payment (
    id, member_id, payment_type, target_id,
    payment_id, transaction_id, amount, status_id, pay_method_id, paid_at
)
VALUES
    (1, 1, 'THEME', 2, 'imp_test_theme_001', 'merchant_theme_001', 3000, 2, 5, CURRENT_TIMESTAMP),
    (2, 1, 'THEME', 4, 'imp_test_theme_002', 'merchant_theme_002', 5000, 2, 6, CURRENT_TIMESTAMP),
    (3, 2, 'THEME', 3, 'imp_test_theme_003', 'merchant_theme_003', 3000, 2, 5, CURRENT_TIMESTAMP),
    (4, 4, 'THEME', 5, 'imp_test_theme_004', 'merchant_theme_004', 5000, 2, 7, CURRENT_TIMESTAMP),
    (5, 6, 'THEME', 6, 'imp_test_theme_005', 'merchant_theme_005', 4000, 2, 5, CURRENT_TIMESTAMP),
    (6, 1, 'SUBSCRIPTION', 3, 'imp_test_subscription_001', 'merchant_subscription_001', 5000, 2, 5, CURRENT_TIMESTAMP),
    (7, 2, 'SUBSCRIPTION', 3, 'imp_test_subscription_002', 'merchant_subscription_002', 5000, 2, 6, CURRENT_TIMESTAMP),
    (8, 4, 'SUBSCRIPTION', 3, 'imp_test_subscription_003', 'merchant_subscription_003', 3000, 2, 7, CURRENT_TIMESTAMP),
    (9, 5, 'SUBSCRIPTION', 4, 'imp_test_subscription_004', 'merchant_subscription_004', 5000, 2, 5, CURRENT_TIMESTAMP);

-- 10. 테마 구매
INSERT INTO theme_purchase (id, member_id, theme_id, payment_id, is_used)
VALUES
    (1, 1, 2, 1, TRUE),   -- user1 오션 적용
    (2, 1, 4, 2, FALSE),  -- user1 선셋 보유만
    (3, 2, 3, 3, TRUE),   -- user2 포레스트 적용
    (4, 4, 5, 4, FALSE),  -- artist 라벤더 보유만
    (5, 6, 6, 5, TRUE);   -- craft 다크 적용

-- 11. 정기 구독
INSERT INTO subscription (
    id, member_id, target_id, customer_uid,
    price_id, status_id, next_billing_at, started_at, ended_at
) VALUES
      (1, 1, 3, 'customer_test_001', 20, 15,
       DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 MONTH), CURRENT_TIMESTAMP, NULL),
      (2, 2, 3, 'customer_test_002', 20, 15,
       DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 MONTH), CURRENT_TIMESTAMP, NULL),
      (3, 4, 3, 'customer_test_003', 19, 15,
       DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 MONTH), CURRENT_TIMESTAMP, NULL),
      (4, 5, 4, 'customer_test_004', 20, 16,
       DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY), DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 MONTH),DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY));

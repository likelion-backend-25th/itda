-- =========================================================
-- ITDA TEST DATA (확장)
-- 비밀번호(로컬): 1234
-- =========================================================

-- 1. 공통 코드
INSERT INTO common_code (id, type, code, name, description, numeric_value, sort, is_active)
VALUES
-- 결제 상태
(1, 1, 'PS01', '결제 대기', '결제 대기 상태',null, 1, TRUE),
(2, 1, 'PS02', '결제 완료', '결제가 정상적으로 완료된 상태',null,2, TRUE),
(3, 1, 'PS03', '결제 취소', '결제가 취소된 상태',null, 3, TRUE),
(4, 1, 'PS04', '환불 완료', '결제가 환불된 상태', null, 4, TRUE),
-- 결제 수단
(5, 2, 'PM01', '카드', '신용카드 및 체크카드', null, 1, TRUE),
(6, 2, 'PM02', '카카오페이', '카카오페이 결제', null, 2, TRUE),
(7, 2, 'PM03', '토스페이', '토스페이 결제', null, 3, TRUE),
-- 게시글 카테고리
(8, 3, 'CA01', '맛집', '맛있는 음식 관련 취미', null, 1, TRUE),
(9, 3, 'CA02', '여행', '여행 관련 취미', null, 1, TRUE),
(10, 3, 'CA03', '운동', '운동 관련 취미', null, 1, TRUE),
(11, 3, 'CA04', '독서', '독서 및 책 관련 취미', null, 1, TRUE),
(12, 3, 'CA05', '음악', '음악 관련 취미', null, 2, TRUE),
(13, 3, 'CA06', '요리', '요리 및 베이킹 관련 취미', null, 3, TRUE),
(14, 3, 'CA07', '공예', '공예 및 만들기 관련 취미', null, 4, TRUE),
(15, 3, 'CA08', '그림', '그림 및 미술 관련 취미', null, 6, TRUE),
(16, 3, 'CA09', '게임', '게임 관련 취미', null, 6, TRUE),
(17, 3, 'CA10', '기타', '기타 취미', null, 7, TRUE),
-- 구독 상태
(18, 4, 'SS01', 'ACTIVE', '현재 구독 중인 상태', null, 1, TRUE),
(19, 4, 'SS02', 'CANCELLED', '구독이 취소된 상태', null, 2, TRUE),
(20, 4, 'SS03', 'EXPIRED', '구독 기간이 종료된 상태', null, 3, TRUE),
(21, 4, 'SS04', 'PAUSED', '구독이 일시 정지된 상태', null, 4, TRUE),
-- 구독 가격
(22, 5, 'PR01', '월 3,000원', '월 구독 3,000원',3000, 1, TRUE),
(23, 5, 'PR02', '월 5,000원', '월 구독 5,000원',5000, 2, TRUE),
(24, 5, 'PR03', '월 10,000원', '월 구독 10,000원',10000, 3, TRUE),
-- 게시글 반응
(25, 6, 'RT01', 'LIKE', '게시글 좋아요', null, 1, TRUE),
(26, 6, 'RT02', 'SCRAP', '게시글 스크랩', null, 2, TRUE),
-- 환불 신청 대기 상태
(27, 1, 'PS05', '환불 대기','환불 신청 후 환불 처리를 기다리는 상태', NULL, 5, TRUE);

-- 2. 테마 (ON_SALE 8개 → size=6이면 2페이지 / HIDDEN 1개)
INSERT INTO theme (id, theme_name, description, price, thumbnail_url, theme_code, css_text, status, is_default)
VALUES
    (1, '기본 테마',     'ITDA 기본 테마', 0,    'themes/default.png',  'DEFAULT', NULL, 'ON_SALE', TRUE),
    (2, '미드나잇 다크',     '눈 편한 다크 테마',0, 'themes/dark.png',     'DARK',':root[data-theme=''dark'']{--bg:#0c121c;--surface:#161e2b;--text:#d8e2ef;--text-soft:#b9c7d8;--muted:#96a6b9;--line:#2a384a;--blue:#6b9ae8;--blue-deep:#4a7fd0;--blue-soft:#1a2838;--shadow:0 14px 34px rgba(0,0,0,.38);--search-bg:#101826;--on-accent:#0b1220;--radius:20px;}:root[data-theme=''dark''] body,:root[data-theme=''dark''] .page{background-color:var(--bg);background-image:radial-gradient(ellipse 55% 30% at 18% 0%,rgba(45,85,135,.12),transparent 58%),radial-gradient(ellipse 45% 28% at 92% 6%,rgba(30,65,110,.08),transparent 52%),linear-gradient(180deg,#0a1018 0%,#0e1520 50%,#0c121c 100%);}:root[data-theme=''dark''] .topbar,:root[data-theme=''dark''] .sidebar,:root[data-theme=''dark''] .post,:root[data-theme=''dark''] .empty{background:linear-gradient(180deg,#171f2c 0%,#131b27 100%);border-color:rgba(107,154,232,.16);box-shadow:0 12px 28px rgba(0,0,0,.34);}:root[data-theme=''dark''] .search{border:1px solid var(--line);}:root[data-theme=''dark''] .write-btn{color:var(--on-accent);box-shadow:0 6px 16px rgba(74,127,208,.22);}:root[data-theme=''dark''] .logo,:root[data-theme=''dark''] .stat{color:var(--blue);}','ON_SALE', FALSE),
    (3, '오션 블루',     '시원한 바다 느낌의 테마',           3000, 'themes/ocean.png',    'OCEAN', ':root[data-theme=''ocean'']{--bg:#d9f0f8;--surface:#f4fbff;--text:#0c2f3f;--text-soft:#3a6b80;--muted:#6a93a6;--line:#bfe0ee;--blue:#0a8dbf;--blue-deep:#06729e;--blue-soft:#cfeff9;--shadow:0 14px 36px rgba(8,110,150,.14);--search-bg:#e3f5fb;--on-accent:#fff;--radius:22px;}:root[data-theme=''ocean''] body,:root[data-theme=''ocean''] .page{background-color:var(--bg);background-image:radial-gradient(ellipse 90% 55% at 15% 0%,rgba(120,210,240,.55),transparent 58%),radial-gradient(ellipse 70% 50% at 90% 10%,rgba(70,180,220,.35),transparent 55%),linear-gradient(180deg,#c8ebf6 0%,#e4f4fb 42%,#dff2f9 100%);}:root[data-theme=''ocean''] .topbar,:root[data-theme=''ocean''] .sidebar,:root[data-theme=''ocean''] .post,:root[data-theme=''ocean''] .empty{border-color:rgba(10,141,191,.18);box-shadow:0 12px 32px rgba(8,110,150,.12);backdrop-filter:saturate(1.05);}:root[data-theme=''ocean''] .sidebar{background:linear-gradient(180deg,#f7fdff 0%,#eef8fc 100%);}:root[data-theme=''ocean''] .post{background:linear-gradient(165deg,#ffffff 0%,#f3fbfe 100%);}:root[data-theme=''ocean''] .logo,:root[data-theme=''ocean''] .stat{color:var(--blue);}','ON_SALE', FALSE),
    (4, '포레스트 그린', '차분한 숲 느낌의 테마',             3000, 'themes/forest.png',   'FOREST',':root[data-theme=''forest'']{--bg:#eef3e6;--surface:#f8faf4;--text:#24331c;--text-soft:#556848;--muted:#7f9170;--line:#d5e0c8;--blue:#738a4d;--blue-deep:#5c703c;--blue-soft:#e6efd8;--shadow:0 14px 34px rgba(70,95,40,.12);--search-bg:#e9f0df;--on-accent:#fff;--radius:20px;}:root[data-theme=''forest''] body,:root[data-theme=''forest''] .page{background-color:var(--bg);background-image:radial-gradient(ellipse 80% 45% at 10% 0%,rgba(170,200,120,.4),transparent 60%),radial-gradient(ellipse 60% 40% at 95% 5%,rgba(120,150,80,.25),transparent 55%),linear-gradient(185deg,#e8efdc 0%,#f3f6ec 50%,#eef3e6 100%);}:root[data-theme=''forest''] .topbar,:root[data-theme=''forest''] .sidebar,:root[data-theme=''forest''] .post,:root[data-theme=''forest''] .empty{border-color:rgba(115,138,77,.28);box-shadow:0 10px 28px rgba(70,95,40,.1);}:root[data-theme=''forest''] .sidebar{background:linear-gradient(180deg,#fbfcf7 0%,#f1f5e8 100%);border-width:1.5px;}:root[data-theme=''forest''] .post{border-color:rgba(115,138,77,.22);background:#fffcf7;}:root[data-theme=''forest''] .write-btn{border-radius:999px;}', 'ON_SALE', FALSE),
    (5, '선셋 코랄',     '따뜻한 노을 느낌의 테마',           5000, 'themes/sunset.png',   'SUNSET',':root[data-theme=''sunset'']{--bg:#ffe8dc;--surface:#fff8f3;--text:#3d241c;--text-soft:#8a5a4a;--muted:#b08474;--line:#f0d5c8;--blue:#e56a45;--blue-deep:#c84e34;--blue-soft:#ffe0d4;--shadow:0 14px 36px rgba(200,90,50,.14);--search-bg:#ffefe6;--on-accent:#fff;--radius:22px;}:root[data-theme=''sunset''] body,:root[data-theme=''sunset''] .page{background-color:var(--bg);background-image:radial-gradient(ellipse 85% 55% at 20% -5%,rgba(255,180,120,.65),transparent 55%),radial-gradient(ellipse 70% 45% at 100% 0%,rgba(255,140,100,.45),transparent 50%),linear-gradient(180deg,#ffd4c0 0%,#ffe9df 40%,#fff3ec 100%);}:root[data-theme=''sunset''] .topbar,:root[data-theme=''sunset''] .sidebar,:root[data-theme=''sunset''] .post,:root[data-theme=''sunset''] .empty{border-color:rgba(229,106,69,.2);box-shadow:0 12px 30px rgba(200,90,50,.12);}:root[data-theme=''sunset''] .sidebar{background:linear-gradient(180deg,#fffaf6 0%,#fff0e8 100%);}:root[data-theme=''sunset''] .post{background:linear-gradient(160deg,#ffffff 0%,#fff6f1 100%);}:root[data-theme=''sunset''] .nav-item.active,:root[data-theme=''sunset''] .write-btn{box-shadow:0 8px 18px rgba(229,106,69,.28);}', 'ON_SALE', FALSE),
    (6, '라벤더 나이트', '보랏빛 밤 느낌의 테마',             5000, 'themes/lavender.png', 'LAVENDER', ':root[data-theme=''lavender'']{--bg:#efe8fb;--surface:#fbf8ff;--text:#2c2344;--text-soft:#5c5278;--muted:#8d84a8;--line:#ddd2f0;--blue:#7c5cbf;--blue-deep:#62469e;--blue-soft:#ece3ff;--shadow:0 14px 36px rgba(90,60,150,.14);--search-bg:#f0e9ff;--on-accent:#fff;--radius:22px;}:root[data-theme=''lavender''] body,:root[data-theme=''lavender''] .page{background-color:var(--bg);background-image:radial-gradient(ellipse 80% 50% at 12% 0%,rgba(190,160,240,.5),transparent 58%),radial-gradient(ellipse 65% 45% at 95% 8%,rgba(160,130,220,.35),transparent 55%),linear-gradient(180deg,#e8dff8 0%,#f3eefc 45%,#efeaf8 100%);}:root[data-theme=''lavender''] .topbar,:root[data-theme=''lavender''] .sidebar,:root[data-theme=''lavender''] .post,:root[data-theme=''lavender''] .empty{border-color:rgba(124,92,191,.2);box-shadow:0 12px 32px rgba(90,60,150,.12);}:root[data-theme=''lavender''] .sidebar{background:linear-gradient(180deg,#fffcff 0%,#f5f0fc 100%);}:root[data-theme=''lavender''] .post{background:linear-gradient(165deg,#ffffff 0%,#f8f4ff 100%);}:root[data-theme=''lavender''] .logo{letter-spacing:-0.05em;}', 'ON_SALE', FALSE),
    (7, '코튼 크림',     '부드러운 크림톤 테마',             2000, 'themes/cream.png',    'CREAM',':root[data-theme=''cream'']{--bg:#f5efe6;--surface:#fffdf9;--text:#3d342c;--text-soft:#7a6c5e;--muted:#a89786;--line:#e5dbcf;--blue:#c4a574;--blue-deep:#a88655;--blue-soft:#f3ebdf;--shadow:0 14px 34px rgba(120,90,50,.12);--search-bg:#efe7db;--on-accent:#fff;--radius:20px;}:root[data-theme=''cream''] body,:root[data-theme=''cream''] .page{background-color:var(--bg);background-image:radial-gradient(ellipse 75% 45% at 15% 0%,rgba(230,210,180,.55),transparent 58%),radial-gradient(ellipse 60% 40% at 100% 0%,rgba(210,180,140,.3),transparent 55%),linear-gradient(180deg,#efe6d8 0%,#f7f1e8 48%,#f5efe6 100%);}:root[data-theme=''cream''] .topbar,:root[data-theme=''cream''] .sidebar,:root[data-theme=''cream''] .post,:root[data-theme=''cream''] .empty{border-color:rgba(196,165,116,.28);box-shadow:0 12px 28px rgba(120,90,50,.1);}:root[data-theme=''cream''] .sidebar{background:linear-gradient(180deg,#fffefb 0%,#f8f2e9 100%);}:root[data-theme=''cream''] .post{background:#fffefb;}:root[data-theme=''cream''] .write-btn{border-radius:14px;}','ON_SALE', FALSE),
    (8, '스카이 라이트', '맑은 하늘 느낌의 테마',             2000, 'themes/sky.png',      'SKY',':root[data-theme=''sky'']{--bg:#e6f1ff;--surface:#f7fbff;--text:#1a3550;--text-soft:#4a6d8c;--muted:#7f9cb8;--line:#cfe2f6;--blue:#4aa3ff;--blue-deep:#2f86e0;--blue-soft:#dcebff;--shadow:0 14px 34px rgba(50,120,200,.12);--search-bg:#e2efff;--on-accent:#fff;--radius:22px;}:root[data-theme=''sky''] body,:root[data-theme=''sky''] .page{background-color:var(--bg);background-image:radial-gradient(ellipse 80% 50% at 25% -5%,rgba(160,205,255,.55),transparent 55%),radial-gradient(ellipse 65% 40% at 95% 0%,rgba(120,180,255,.35),transparent 50%),linear-gradient(180deg,#d8e9ff 0%,#eef6ff 45%,#e6f1ff 100%);}:root[data-theme=''sky''] .topbar,:root[data-theme=''sky''] .sidebar,:root[data-theme=''sky''] .post,:root[data-theme=''sky''] .empty{border-color:rgba(74,163,255,.22);box-shadow:0 12px 30px rgba(50,120,200,.11);}:root[data-theme=''sky''] .sidebar{background:linear-gradient(180deg,#ffffff 0%,#f0f7ff 100%);}:root[data-theme=''sky''] .post{background:linear-gradient(165deg,#ffffff 0%,#f5faff 100%);}','ON_SALE', FALSE),
    (9, '숨김 테마',     '관리자 비공개 테마 (목록 제외)',     9999, 'themes/hidden.png',   'HIDDEN',':root[data-theme=''hidden'']{--bg:#e8e6ec;--surface:#f6f5f7;--text:#3a3840;--text-soft:#6b6774;--muted:#9a96a3;--line:#d5d1db;--blue:#7a7488;--blue-deep:#5f596c;--blue-soft:#ebe8ef;--shadow:0 12px 30px rgba(60,50,80,.1);--search-bg:#e4e2e8;--on-accent:#fff;--radius:18px;}:root[data-theme=''hidden''] body,:root[data-theme=''hidden''] .page{background-color:var(--bg);background-image:radial-gradient(ellipse 70% 45% at 20% 0%,rgba(170,165,185,.35),transparent 58%),linear-gradient(180deg,#e2e0e7 0%,#eceaef 50%,#e8e6ec 100%);}:root[data-theme=''hidden''] .topbar,:root[data-theme=''hidden''] .sidebar,:root[data-theme=''hidden''] .post,:root[data-theme=''hidden''] .empty{border-color:rgba(122,116,136,.22);box-shadow:0 10px 26px rgba(60,50,80,.09);}:root[data-theme=''hidden''] .sidebar{background:linear-gradient(180deg,#faf9fb 0%,#f0eef3 100%);}:root[data-theme=''hidden''] .post{background:#faf9fb;}', 'HIDDEN', FALSE);


SELECT theme_code, LEFT(css_text, 40) AS css_head FROM theme;

-- 3. 회원
INSERT INTO member (
    id, email, password, nickname, role, introduction,
    profile_image, theme_id, authmethod, month_income
)
VALUES
    (1, 'user1@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '책읽는사람', 'ROLE_USER', '책과 독서를 좋아합니다.', 'profile/user1_profile.png', 3, 'LOCAL', 0),
    (2, 'user2@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '요리하는사람', 'ROLE_USER', '맛있는 요리를 만드는 것을 좋아합니다.', 'profile/user2_profile.png', 4, 'LOCAL', 0),
    (3, 'creator@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '오늘의취미', 'ROLE_USER', '취미를 함께 나누고 있습니다.', 'profile/creator_profile.png', 1, 'LOCAL', 50000),
    (4, 'artist@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '그림그리는사람', 'ROLE_USER', '그림과 드로잉을 공유합니다.', 'profile/artist_profile.png', 1, 'LOCAL', 30000),
    (5, 'music@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '멜로디유저', 'ROLE_USER', '플레이리스트를 모으는 중입니다.', 'profile/music_profile.png', 1, 'LOCAL', 0),
    (6, 'craft@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '손뜨개러', 'ROLE_USER', '뜨개질과 공예를 좋아합니다.', 'profile/craft_profile.png', 2, 'LOCAL', 10000),
    (7, 'admin@itda.com', '$2a$10$e4dlYcuksXo57vvps8oPaeyDcD0keCuSwPIHBuYDn5oIGZD1PpHrq',
     '관리자', 'ROLE_ADMIN', 'ITDA 관리자 계정입니다.', 'profile/admin_profile.png', 1, 'LOCAL', 0);


-- 4. 회원 관심 카테고리
INSERT INTO member_interest (member_id, category_id)
VALUES
    (1, 11), (1, 12),
    (2, 13), (2, 14),
    (3, 11), (3, 12), (3, 13),
    (4, 15), (4, 14),
    (5, 12), (5, 14),
    (6, 14), (6, 17), (6, 13),
    (7, 11), (7, 14);


-- 5. 게시글
INSERT INTO post (
    id, member_id, category_id, content, image_url, reply_count,
    like_count, view_count, subscriber_only
)
VALUES
    (1, 1, 11, '오늘 읽은 책이 정말 재미있었어요. 오랜만에 시간 가는 줄 모르고 읽었습니다.',
     'posts/book1.png', 2, 3, 25, FALSE),
    (2, 1, 11, '요즘 읽고 있는 책입니다. 비슷한 책을 좋아한다면 추천해요.',
     'posts/book2.png', 0, 5, 41, FALSE),
    (3, 2, 13, '주말에 직접 만든 파스타입니다. 생각보다 만들기 어렵지 않았어요.',
     'posts/pasta.png', 1, 7, 56, FALSE),
    (4, 3, 12, '이번 주에 들었던 음악들을 정리해봤어요. 구독자분들과 공유합니다.',
     'posts/music.png', 1, 10, 83, TRUE),
    (5, 3, 13, '제가 자주 사용하는 홈카페 레시피를 정리했습니다.',
     'posts/cafe.png', 1, 8, 72, TRUE),
    (6, 4, 15, '최근에 그린 그림입니다. 새로운 재료를 사용해봤어요.',
     'posts/drawing.png', 1, 4, 31, FALSE),
    (7, 4, 15, '그림을 그릴 때 사용하는 도구들을 소개해볼게요.',
     'posts/tools.png', 0, 6, 48, TRUE),
    (8, 5, 12, '비 오는 날 듣기 좋은 플레이리스트를 모아봤습니다.',
     'posts/playlist.png', 2, 9, 60, FALSE),
    (9, 5, 12, '구독자 전용: 이번 달 추천 앨범 리스트입니다.',
     'posts/album.png', 0, 12, 90, TRUE),
    (10, 6, 14, '첫 코바늘 작품이에요. 실패도 많았지만 완성하니 뿌듯합니다.',
     'posts/knit.png', 2, 5, 33, FALSE),
    (11, 6, 14, '비즈로 만든 간단한 팔찌 튜토리얼을 남겨둡니다.',
     'posts/bead.png', 1, 7, 44, FALSE),
    (12, 2, 13, '초간단 김치볶음밥 레시피. 자취생도 바로 따라할 수 있어요.',
     'posts/friedrice.png', 2, 11, 70, FALSE),
    (13, 1, 11, '서점 나들이 후기. 신간 코너에서 발견한 책들입니다.',
     'posts/bookstore.png', 0, 2, 18, FALSE),
    (14, 3, 17, '취미를 꾸준히 하는 루틴을 공유합니다. (구독자 전용)',
     'posts/routine.png', 0, 15, 120, TRUE),
    (15, 4, 15, '스케치북 한 권을 다 채웠습니다. 과정 사진 모음.',
     'posts/sketch.png', 2, 8, 55, FALSE);


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
-- LIKE(25)
(1, 2, 1, 25), (2, 3, 1, 25), (3, 4, 1, 25),
(4, 1, 3, 25), (5, 3, 3, 25), (6, 4, 3, 25),
(7, 1, 6, 25), (8, 2, 6, 25),
(9, 1, 8, 25), (10, 3, 8, 25), (11, 6, 8, 25),
(12, 1, 10, 25), (13, 4, 10, 25), (14, 5, 10, 25),
(15, 1, 12, 25), (16, 3, 12, 25), (17, 5, 12, 25), (18, 6, 12, 25),
(19, 2, 15, 25), (20, 5, 15, 25),
(21, 1, 4, 25), (22, 2, 5, 25), (23, 4, 9, 25),
-- SCRAP(26)
(24, 2, 1, 26), (25, 3, 2, 26), (26, 1, 3, 26), (27, 4, 4, 26),
(28, 1, 8, 26), (29, 6, 8, 26),
(30, 2, 10, 26), (31, 5, 11, 26),
(32, 3, 12, 26), (33, 1, 14, 26), (34, 6, 15, 26);


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
    (1, 1, 'THEME', 3, 'imp_test_theme_001', 'merchant_theme_001', 3000, 2, 5, CURRENT_TIMESTAMP),
    (2, 1, 'THEME', 5, 'imp_test_theme_002', 'merchant_theme_002', 5000, 2, 6, CURRENT_TIMESTAMP),
    (3, 2, 'THEME', 4, 'imp_test_theme_003', 'merchant_theme_003', 3000, 2, 5, CURRENT_TIMESTAMP),
    (4, 4, 'THEME', 6, 'imp_test_theme_004', 'merchant_theme_004', 5000, 2, 7, CURRENT_TIMESTAMP),
    (5, 6, 'THEME', 2, 'imp_test_theme_005', 'merchant_theme_005', 0, 2, 5, CURRENT_TIMESTAMP),
    (6, 1, 'SUBSCRIPTION', 3, 'imp_test_subscription_001', 'merchant_subscription_001', 5000, 2, 5, CURRENT_TIMESTAMP),
    (7, 2, 'SUBSCRIPTION', 3, 'imp_test_subscription_002', 'merchant_subscription_002', 5000, 2, 6, CURRENT_TIMESTAMP),
    (8, 4, 'SUBSCRIPTION', 3, 'imp_test_subscription_003', 'merchant_subscription_003', 3000, 2, 7, CURRENT_TIMESTAMP),
    (9, 5, 'SUBSCRIPTION', 4, 'imp_test_subscription_004', 'merchant_subscription_004', 5000, 2, 5, CURRENT_TIMESTAMP);

-- 10. 테마 구매
INSERT INTO theme_purchase (id, member_id, theme_id, payment_id, is_used)
VALUES
    -- 기본 테마 (전 회원) — 회원가입과 동일하게 무료 지급
    (1, 1, 1, NULL, FALSE),  -- user1: 오션 적용 중
    (2, 2, 1, NULL, FALSE),  -- user2: 포레스트 적용 중
    (3, 3, 1, NULL, TRUE),   -- creator: 기본 적용
    (4, 4, 1, NULL, TRUE),   -- artist: 기본 적용
    (5, 5, 1, NULL, TRUE),   -- music: 기본 적용
    (6, 6, 1, NULL, FALSE),  -- craft: 미드나잇 다크 적용 중
    (7, 7, 1, NULL, TRUE),   -- admin: 기본 적용
    -- 유료 구매 (payment 1~5 와 연결)
    (8,  1, 3, 1, TRUE),     -- user1 오션 블루 적용
    (9,  1, 5, 2, FALSE),    -- user1 선셋 코랄 보유만
    (10, 2, 4, 3, TRUE),     -- user2 포레스트 그린 적용
    (11, 4, 6, 4, FALSE),    -- artist 라벤더 나이트 보유만
    (12, 6, 2, 5, TRUE),     -- craft 미드나잇 다크 적용
    -- 추가 보유(시드용 무료 지급) — styles/보유 목록 테스트용
    (13, 1, 7, NULL, FALSE), -- user1 코튼 크림 보유
    (14, 2, 8, NULL, FALSE), -- user2 스카이 라이트 보유
    (15, 3, 7, NULL, FALSE), -- creator 코튼 크림 보유
    (16, 5, 8, NULL, FALSE), -- music 스카이 라이트 보유
    (17, 7, 3, NULL, FALSE); -- admin 오션 블루 보유

-- 11. 정기 구독
INSERT INTO subscription (
    id, member_id, target_id, billing_key,
    price_id, status_id, next_billing_at, started_at, ended_at, payment_id
) VALUES
      (1, 1, 3, 'customer_test_001', 23, 18,
       DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 MONTH), CURRENT_TIMESTAMP, NULL,6),
      (2, 2, 3, 'customer_test_002', 23, 18,
       DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 MONTH), CURRENT_TIMESTAMP, NULL,7),
      (3, 4, 3, 'customer_test_003', 22, 18,
       DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 MONTH), CURRENT_TIMESTAMP, NULL,8),
      (4, 5, 4, 'customer_test_004', 23, 19,
       DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY), DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 MONTH),DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY),9);

-- 12. 게시글 추가
INSERT INTO post
(member_id, category_id, content, image_url, subscriber_only)
VALUES

-- =========================
-- 맛집 8
-- =========================
(1, 8, '주말에 다녀온 서울 맛집 후기입니다. 파스타가 특히 맛있었어요.', NULL, FALSE),
(2, 8, '여행 중 우연히 발견한 작은 맛집을 추천합니다.', NULL, FALSE),
(3, 8, '구독자분들에게만 알려드리는 숨은 맛집 추천입니다.', NULL, TRUE),
(5, 8, '혼자 방문하기 좋은 맛집을 찾아서 다녀왔습니다.', NULL, FALSE),

-- =========================
-- 여행 9
-- =========================
(1, 9, '부산 여행을 다녀왔습니다. 바다 산책 코스를 추천합니다.', NULL, FALSE),
(2, 9, '제주도 여행 후기입니다. 조용하게 걷기 좋은 곳이 많았어요.', NULL, FALSE),
(3, 9, '당일치기로 다녀오기 좋은 서울 근교 여행지를 소개합니다.', NULL, FALSE),
(3, 9, '구독자 전용으로 공개하는 가을 여행 코스입니다.', NULL, TRUE),
(4, 9, '사진을 찍으러 다녀온 여행 후기입니다. 풍경이 정말 좋았습니다.', NULL, FALSE),

-- =========================
-- 운동 10
-- =========================
(2, 10, '운동 초보자를 위한 가벼운 홈트레이닝 루틴입니다.', NULL, FALSE),
(3, 10, '아침 운동을 한 달 동안 꾸준히 해본 후기입니다.', NULL, FALSE),
(5, 10, '퇴근 후 할 수 있는 간단한 운동 루틴을 정리했습니다.', NULL, FALSE),
(6, 10, '집에서 스트레칭과 운동을 같이 해봤습니다.', NULL, FALSE),

-- =========================
-- 독서 11
-- =========================
(1, 11, '최근 읽은 소설 작품 중 가장 기억에 남은 책을 소개합니다.', NULL, FALSE),
(1, 11, '주말 독서 기록입니다. 오랜만에 책 한 권을 끝까지 읽었습니다.', NULL, FALSE),
(3, 11, '여행에 관한 에세이를 읽고 기억에 남는 부분을 정리했습니다.', NULL, FALSE),
(3, 11, '구독자분들에게만 추천하는 이번 달 독서 목록입니다.', NULL, TRUE),
(5, 11, '자기 전에 읽기 좋은 짧은 책을 추천합니다.', NULL, FALSE),

-- =========================
-- 음악 12
-- =========================
(2, 12, '집중할 때 듣기 좋은 음악 플레이리스트를 추천합니다.', NULL, FALSE),
(5, 12, '비 오는 날 듣기 좋은 음악을 모아봤습니다.', NULL, FALSE),
(5, 12, '최근 가장 자주 듣는 음악과 플레이리스트 후기입니다.', NULL, FALSE),
(6, 12, '공예 작업을 하면서 듣기 좋은 잔잔한 음악 추천입니다.', NULL, FALSE),
(3, 12, '구독자 전용으로 공유하는 새벽 음악 플레이리스트입니다.', NULL, TRUE),

-- =========================
-- 요리 13
-- =========================
(2, 13, '초보자도 쉽게 만들 수 있는 파스타 요리 레시피입니다.', NULL, FALSE),
(2, 13, '냉장고 재료만 사용해서 간단한 요리를 만들어봤습니다.', NULL, FALSE),
(3, 13, '여행에서 먹었던 음식을 생각하며 만든 요리입니다.', NULL, FALSE),
(3, 13, '구독자에게만 공개하는 특별한 볶음 요리 레시피입니다.', NULL, TRUE),
(6, 13, '주말에 만들어본 간단한 베이킹 후기입니다.', NULL, FALSE),

-- =========================
-- 공예 14
-- =========================
(6, 14, '초보자를 위한 코바늘 공예 작품을 만들어봤습니다.', NULL, FALSE),
(6, 14, '이번 주말에 완성한 뜨개질 작품입니다.', NULL, FALSE),
(4, 14, '작은 소품을 직접 만드는 공예 작업 과정을 기록했습니다.', NULL, FALSE),
(3, 14, '구독자분들과 공유하는 공예 작품 제작 과정입니다.', NULL, TRUE),
(1, 14, '책갈피를 직접 만들어본 간단한 공예 후기입니다.', NULL, FALSE),

-- =========================
-- 그림 15
-- =========================
(4, 15, '오랜만에 완성한 그림 작품입니다. 풍경을 주제로 그렸습니다.', NULL, FALSE),
(4, 15, '여행에서 찍은 사진을 참고해서 그림을 그려봤습니다.', NULL, FALSE),
(1, 15, '책을 읽다가 떠오른 장면을 그림으로 표현해봤습니다.', NULL, FALSE),
(3, 15, '연습 삼아 시작했는데 마음에 드는 그림 작품이 완성됐습니다.', NULL, TRUE),
(6, 15, '공예 작품을 참고해서 간단한 그림을 그려봤습니다.', NULL, FALSE),

-- =========================
-- 게임 16
-- =========================
(5, 16, '주말에 친구들과 같이 플레이한 게임 후기입니다.', NULL, FALSE),
(1, 16, '최근 시작한 게임을 초보자 입장에서 리뷰해봤습니다.', NULL, FALSE),
(2, 16, '가볍게 즐기기 좋은 게임을 몇 가지 추천합니다.', NULL, FALSE),
(3, 16, '구독자들과 함께 즐기고 싶은 게임 추천 목록입니다.', NULL, TRUE),

-- =========================
-- 기타 17
-- =========================
(1, 17, '요즘 새롭게 시작한 취미에 대한 기록입니다.', NULL, FALSE),
(2, 17, '주말에 집에서 할 수 있는 간단한 취미를 추천합니다.', NULL, FALSE),
(4, 17, '사진 정리를 하면서 예전 여행 기록을 다시 살펴봤습니다.', NULL, FALSE),
(5, 17, '최근 배우기 시작한 새로운 취미 후기입니다.', NULL, FALSE),
(3, 17, '구독자분들과만 공유하는 이번 달 취미 계획입니다.', NULL, TRUE);
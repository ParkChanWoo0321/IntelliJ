📘 Hackerthon Project API 명세서 (Spring Boot, JWT, Refresh, 로그인 유지)
Spring Boot 기반의 JWT 인증/인가 프로젝트 전체 API 명세

로그인 유지(RefreshToken 재발급)

회원가입, 로그인, 로그아웃, 토큰 블랙리스트

게시글/댓글/대댓글/좋아요/스크랩

비밀번호(찾기/변경/재설정), 프로필/닉네임 관리

JWT 인증(Access/Refresh 토큰)

공통
Base URL:
/api (Auth는 /api/auth, 나머지 리소스는 /posts, /likes, /scraps 등)

인증 필요 API:
모든 API 요청 헤더에 아래 포함

http
복사
편집
Authorization: Bearer {accessToken}
Content-Type:
application/json (파일 업로드만 multipart/form-data)

✅ Auth API (/api/auth)
회원가입
POST /api/auth/signup

설명: 회원가입

Request:

json
복사
편집
{
  "username": "user01",
  "password": "Pass1234!",
  "email": "user01@email.com",
  "nickname": "닉네임"
}
성공 Response (200 OK):

복사
편집
회원가입 성공
실패 Response (예시):

복사
편집
이미 사용중인 아이디입니다.
로그인
POST /api/auth/login

설명: JWT access/refresh 토큰 발급

Request:

json
복사
편집
{
  "username": "user01",
  "password": "Pass1234!"
}
성공 Response:

json
복사
편집
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci...",
  "username": "user01",
  "role": "USER"
}
실패 Response (예시):

복사
편집
존재하지 않는 아이디입니다.
로그아웃
POST /api/auth/logout

설명: accessToken을 블랙리스트 처리(로그아웃)

Headers:

css
복사
편집
Authorization: Bearer {accessToken}
성공 Response:

복사
편집
로그아웃 성공
실패 Response (예시):

복사
편집
토큰 없음
토큰 재발급(로그인 유지)
POST /api/auth/reissue

설명: refreshToken으로 accessToken/refreshToken 재발급
(accessToken 만료 시 사용, refreshToken 1회용)

Request:

json
복사
편집
{
  "refreshToken": "eyJhbGci..."
}
성공 Response:

json
복사
편집
{
  "accessToken": "newAccessToken...",
  "refreshToken": "newRefreshToken..."
}
실패 Response (예시):

복사
편집
재로그인 필요
내 정보 조회
GET /api/auth/me

설명: 내 정보 + 활동 내역

Headers:

css
복사
편집
Authorization: Bearer {accessToken}
성공 Response:

json
복사
편집
{
  "username": "user01",
  "email": "user01@email.com",
  "nickname": "닉네임",
  "role": "USER",
  "profileImageUrl": "/profile_uploads/default_profile.png",
  "myPosts": [
    {
      "id": 1,
      "title": "첫번째 글",
      "content": "내용",
      "authorUsername": "user01",
      "authorNickname": "닉네임",
      "createdAt": "2025-07-27T10:00:00",
      "updatedAt": "2025-07-27T10:30:00",
      "mediaList": [ { "url": "/uploads/a.jpg", "type": "IMAGE" } ],
      "likeCount": 2,
      "scrapCount": 1,
      "commentCount": 1
    }
  ],
  "myLikes": [ ... ],
  "myScraps": [ ... ],
  "myCommentedPosts": [ ... ]
}
닉네임 변경
POST /api/auth/update-nickname

설명: 닉네임 변경

Request:

json
복사
편집
{ "nickname": "새닉네임" }
성공 Response:

복사
편집
닉네임이 변경되었습니다.
닉네임 중복확인
GET /api/auth/nickname-check?nickname=새닉네임

성공 Response:
true 또는 false

프로필 이미지 업로드
POST /api/auth/profile-image

Content-Type: multipart/form-data

Request:

file: (이미지 파일)

성공 Response:

복사
편집
프로필 사진이 변경되었습니다.
프로필 이미지 삭제
DELETE /api/auth/profile-image

성공 Response:

복사
편집
프로필 사진이 기본 이미지로 변경되었습니다.
비밀번호 재설정 (이메일 인증 기반 3단계)
인증번호 요청

POST /api/auth/password-reset/request

Request:

json
복사
편집
{ "email": "user01@email.com" }
성공 Response:

복사
편집
비밀번호 재설정 인증번호를 이메일로 발송했습니다.
인증번호 검증

POST /api/auth/password-reset/verify

Request:

json
복사
편집
{ "email": "user01@email.com", "code": "123456" }
성공 Response:

복사
편집
인증번호가 유효합니다.
비밀번호 변경

POST /api/auth/password-reset/change

Request:

json
복사
편집
{ "email": "user01@email.com", "newPassword": "NewPass123!" }
성공 Response:

복사
편집
비밀번호가 변경되었습니다.
비밀번호 변경 (로그인 상태)
POST /api/auth/change-password

Request:

json
복사
편집
{
  "currentPassword": "OldPass123!",
  "newPassword": "NewPass456!"
}
성공 Response:

복사
편집
비밀번호가 변경되었습니다.
회원 탈퇴
DELETE /api/auth/withdraw

성공 Response:

복사
편집
회원 탈퇴가 완료되었습니다.
내가 댓글 단 게시글 목록 조회
GET /api/auth/my-comments/posts

성공 Response:

json
복사
편집
[
  {
    "id": 1,
    "title": "첫번째 글",
    "content": "내용",
    "authorUsername": "user01",
    "authorNickname": "닉네임",
    "createdAt": "2025-07-27T10:00:00",
    "updatedAt": "2025-07-27T10:30:00",
    "mediaList": [],
    "likeCount": 2,
    "scrapCount": 1,
    "commentCount": 1
  }
]
📝 게시글 API (/posts)
게시글 등록
POST /posts

Content-Type: multipart/form-data 또는 application/json

Request (multipart):

postRequest: { "title": "제목", "content": "내용" }

files: [file1.jpg, file2.mp4]

Request (json):

json
복사
편집
{
  "title": "제목",
  "content": "내용"
}
성공 Response:

복사
편집
작성 완료
게시글 수정
PUT /posts/{id}

Content-Type: multipart/form-data 또는 application/json

Request (json):

json
복사
편집
{
  "title": "수정된 제목",
  "content": "수정된 내용"
}
성공 Response:

복사
편집
수정 완료
게시글 삭제
DELETE /posts/{id}

성공 Response:

복사
편집
삭제 완료
게시글 단일/전체/내글/검색 조회
GET /posts → 전체

GET /posts/{id} → 단일

GET /posts/me → 내글만

GET /posts/search?keyword=키워드 → 검색

성공 Response(단일, 목록):

json
복사
편집
{
  "id": 1,
  "title": "제목",
  "content": "내용",
  "authorUsername": "user01",
  "authorNickname": "닉네임",
  "createdAt": "2025-07-27T10:00:00",
  "updatedAt": "2025-07-27T10:30:00",
  "mediaList": [ { "url": "/uploads/a.jpg", "type": "IMAGE" } ],
  "likeCount": 2,
  "scrapCount": 1,
  "commentCount": 1
}
💬 댓글/대댓글 API (/posts/{postId}/comments)
댓글/대댓글 등록
POST /posts/{postId}/comments

Request:

json
복사
편집
{
  "content": "댓글/대댓글입니다",
  "parentId": null // 대댓글은 부모 댓글 ID
}
성공 Response:

json
복사
편집
{
  "id": 1,
  "content": "댓글/대댓글입니다",
  "author": "user01",
  "createdAt": "2025-07-27T10:00:00",
  "updatedAt": null,
  "parentId": null,
  "replies": []
}
댓글/대댓글 수정
PUT /posts/{postId}/comments/{commentId}

Request:

json
복사
편집
{ "content": "수정된 내용" }
성공 Response:

json
복사
편집
{
  "id": 1,
  "content": "수정된 내용",
  "author": "user01",
  "createdAt": "2025-07-27T10:00:00",
  "updatedAt": "2025-07-27T11:00:00",
  "parentId": null,
  "replies": []
}
댓글/대댓글 삭제
DELETE /posts/{postId}/comments/{commentId}

성공 Response:

복사
편집
200 OK
댓글/대댓글 전체 조회
GET /posts/{postId}/comments

성공 Response (계층 포함):

json
복사
편집
[
  {
    "id": 1,
    "content": "댓글입니다",
    "author": "user01",
    "createdAt": "2025-07-27T10:00:00",
    "updatedAt": null,
    "parentId": null,
    "replies": [
      {
        "id": 2,
        "content": "대댓글입니다",
        "author": "user02",
        "createdAt": "2025-07-27T10:20:00",
        "updatedAt": null,
        "parentId": 1,
        "replies": []
      }
    ]
  }
]
❤️ 좋아요 API (/likes)
좋아요 토글
POST /likes/{postId}/toggle

성공 Response:

json
복사
편집
{
  "postId": 1,
  "likeCount": 7,
  "liked": true
}
내가 좋아요한 글 목록
GET /likes/mine

성공 Response: (게시글 리스트 구조 위와 동일)

📌 스크랩 API (/scraps)
스크랩 토글
POST /scraps/{postId}/toggle

성공 Response:

복사
편집
스크랩 완료
또는

복사
편집
스크랩 취소
내가 스크랩한 글 목록
GET /scraps/mine

성공 Response: (게시글 리스트 구조 위와 동일)

❗️ 예외/에러 응답
상황	상태코드	메시지
JWT/AccessToken 만료	401	JWT 토큰 없음/만료
RefreshToken 만료/블랙리스트	401/400	재로그인 필요
회원가입 중복	400	이미 사용중인 이메일입니다. 등
권한 없음	403	본인만 수정 가능
잘못된 입력	400	비밀번호는 8자 이상...
댓글/게시글 없는 경우	400/404	댓글 없음, 게시글 없음 등

🔒 JWT/토큰 정책 요약
accessToken: 1시간 유효, 요청 헤더에 필수

refreshToken: 7일 유효, /api/auth/reissue로만 사용(1회성, 블랙리스트)

로그인 유지:

accessToken 만료시 refreshToken으로 자동 재발급

refreshToken까지 만료시 재로그인 필요

로그아웃/탈퇴 시 토큰 블랙리스트 처리


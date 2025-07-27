# 📘 Hackerthon Spring Boot Backend API

**JWT 기반 인증/인가, Refresh 토큰(로그인 유지), 게시글/댓글/좋아요/스크랩/회원 등 전 기능**

---

## 🔑 인증 & 사용자 (Auth API)

### 회원가입  
`POST /api/auth/signup`  
새 유저 등록

**Request**
```json
{
  "username": "user01",
  "password": "Pass1234!",
  "email": "user@example.com",
  "nickname": "닉네임"
}
Response

복사
편집
회원가입 성공
예외

아이디 중복: 이미 사용중인 아이디입니다.

이메일 중복: 이미 사용중인 이메일입니다.

닉네임 중복: 이미 사용중인 닉네임입니다.

비밀번호 정책: 비밀번호는 8자 이상, 영문/숫자/특수문자를 각각 1개 이상 포함해야 합니다.

로그인 (JWT + Refresh)
POST /api/auth/login
로그인 및 토큰 2종 발급(로그인 유지 지원)

Request

json
복사
편집
{
  "username": "user01",
  "password": "Pass1234!"
}
Response

json
복사
편집
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "user01",
  "role": "USER"
}
비고

accessToken: 1시간, refreshToken: 7일

토큰은 header로 사용
Authorization: Bearer {accessToken}

AccessToken 재발급 (로그인 유지)
POST /api/auth/refresh-token
Request

json
복사
편집
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
Response

json
복사
편집
{
  "accessToken": "새로운 액세스토큰 문자열",
  "refreshToken": "기존(혹은 새) 리프레시토큰"
}
예외

만료/잘못된 리프레시 토큰: 401, 403 에러

로그아웃
POST /api/auth/logout
헤더
Authorization: Bearer {accessToken}

Response

복사
편집
로그아웃 성공
내 정보 조회
GET /api/auth/me

Response

json
복사
편집
{
  "username": "user01",
  "email": "user@example.com",
  "nickname": "닉네임",
  "role": "USER",
  "profileImageUrl": "/profile_uploads/default_profile.png",
  "myPosts": [ { ...PostResponse } ],
  "myLikes": [ { ...PostResponse } ],
  "myScraps": [ { ...PostResponse } ],
  "myCommentedPosts": [ { ...PostResponse } ]
}
닉네임 변경
POST /api/auth/update-nickname

json
복사
편집
{ "nickname": "새닉네임" }
Response

복사
편집
닉네임이 변경되었습니다.
닉네임 중복 확인
GET /api/auth/nickname-check?nickname=테스트
Response: true(중복) / false(가능)

프로필 이미지 업로드/삭제
업로드: POST /api/auth/profile-image (FormData: file)

삭제: DELETE /api/auth/profile-image

비밀번호 관리 & 찾기 (이메일 인증 3단계)
인증번호 요청
POST /api/auth/password-reset/request

json
복사
편집
{ "email": "user@example.com" }
인증번호 검증
POST /api/auth/password-reset/verify

json
복사
편집
{ "email": "user@example.com", "code": "123456" }
새 비밀번호 설정
POST /api/auth/password-reset/change

json
복사
편집
{ "email": "user@example.com", "newPassword": "새비번1!" }
직접 비번 변경(로그인 상태)
POST /api/auth/change-password

json
복사
편집
{ "currentPassword": "OldPass!", "newPassword": "NewPass1!" }
회원 탈퇴
DELETE /api/auth/withdraw
Response

복사
편집
회원 탈퇴가 완료되었습니다.
내가 댓글 쓴 게시글
GET /api/auth/my-comments/posts
Response:
[ { ...PostResponse } ]

📝 게시글 API (/posts)
게시글 등록
JSON: POST /posts (Content-Type: application/json)

Multipart(파일): POST /posts (Content-Type: multipart/form-data)

Request(JSON)

json
복사
편집
{
  "title": "제목",
  "content": "본문"
}
Request(Multipart)

css
복사
편집
postRequest: { "title": "제목", "content": "본문" }
files: [file1.jpg, ...]
Response

복사
편집
작성 완료
게시글 수정
JSON: PUT /posts/{id} (application/json)

Multipart: PUT /posts/{id} (multipart/form-data)

Request

json
복사
편집
{
  "title": "수정 제목",
  "content": "수정 본문"
}
Response

복사
편집
수정 완료
게시글 삭제
DELETE /posts/{id}
Response

복사
편집
삭제 완료
단일 게시글 조회
GET /posts/{id}
Response

json
복사
편집
{
  "id": 1,
  "title": "테스트 제목",
  "content": "본문",
  "authorUsername": "user01",
  "authorNickname": "닉네임",
  "createdAt": "2025-07-27T10:00:00",
  "updatedAt": "2025-07-27T10:15:00",
  "mediaList": [
    { "url": "/uploads/img.jpg", "type": "IMAGE" }
  ],
  "likeCount": 10,
  "scrapCount": 2,
  "commentCount": 5
}
전체 게시글 목록
GET /posts

내 게시글 목록
GET /posts/me

게시글 검색
GET /posts/search?keyword=...

💬 댓글/대댓글 API (/posts/{postId}/comments)
댓글/대댓글 등록
POST /posts/{postId}/comments

json
복사
편집
{
  "content": "댓글 본문",
  "parentId": null     // 대댓글이면 부모 댓글 ID
}
Response

json
복사
편집
{
  "id": 1,
  "content": "댓글 본문",
  "author": "닉네임",
  "createdAt": "2025-07-27T10:00:00",
  "updatedAt": null,
  "parentId": null,
  "replies": []
}
댓글/대댓글 수정
PUT /posts/{postId}/comments/{commentId}

json
복사
편집
{ "content": "수정된 내용" }
Response

json
복사
편집
{
  "id": 1,
  "content": "수정된 내용",
  "author": "닉네임",
  "createdAt": "2025-07-27T10:00:00",
  "updatedAt": "2025-07-27T11:00:00",
  "parentId": null,
  "replies": []
}
댓글/대댓글 삭제
DELETE /posts/{postId}/comments/{commentId}
Response: 200 OK

댓글/대댓글 트리 전체 조회
GET /posts/{postId}/comments

Response

json
복사
편집
[
  {
    "id": 1,
    "content": "댓글입니다",
    "author": "user01",
    "createdAt": "2025-07-27T10:00:00",
    "updatedAt": "2025-07-27T10:30:00",
    "parentId": null,
    "replies": [
      {
        "id": 2,
        "content": "대댓글입니다",
        "author": "user02",
        "createdAt": "2025-07-27T10:35:00",
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

Response

json
복사
편집
{
  "postId": 1,
  "likeCount": 13,
  "liked": true
}
내가 좋아요 누른 글
GET /likes/mine
Response: [ { ...PostResponse } ]

📌 스크랩 API (/scraps)
스크랩 토글
POST /scraps/{postId}/toggle
Response

복사
편집
스크랩 완료
또는

복사
편집
스크랩 취소
내가 스크랩한 글
GET /scraps/mine
Response: [ { ...PostResponse } ]

❗️ 공통 에러/권한 응답
상황	상태코드	메시지
로그인 실패	400	비밀번호가 틀렸습니다.
중복 데이터	400	이미 사용중인 이메일입니다. 등
권한 없음	403	본인만 수정 가능
인증 없음	401	JWT 토큰 없음

🔒 JWT/Refresh 정책
AccessToken: 1시간, 헤더로 인증, 권한 필요 API에 사용

RefreshToken: 7일, 로그인 유지(재발급 전용, 일반적으로 HttpOnly 쿠키 또는 클라이언트 저장)

로그인 유지:

RefreshToken으로 accessToken 갱신

블랙리스트에 등록된 토큰은 무효

로그아웃:

AccessToken 블랙리스트에 등록(재사용 불가)

💡 기타
모든 요청은 Authorization: Bearer {accessToken} 헤더 필요 (로그인/회원가입/인증번호 요청 등 제외)

미디어 파일은 /uploads/, 프로필 이미지는 /profile_uploads/ 디렉토리에 저장됨

각 기능/로직별 알림/권한/데이터 정책은 코드와 동일하게 반영됨


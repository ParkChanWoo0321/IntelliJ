# 🌐 백엔드 API 명세서

Spring Boot 기반 JWT 인증, 이메일 인증, 소셜 로그인(Kakao, Naver, Google), 댓글/대댓글, 좋아요/스크랩, 로그인 유지 기능이 포함된 백엔드 시스템입니다.

---

## 🔐 인증 및 회원 기능

### ✅ 회원가입 `POST /api/auth/signup`

**Request**

```json
{
  "username": "testuser",
  "password": "Test1234!",
  "email": "test@example.com",
  "nickname": "다음달"
}
```

**Response**

```json
{
  "message": "회원가입 성공"
}
```

---

### ✅ 로그인 `POST /api/auth/login`

**Request**

```json
{
  "username": "testuser",
  "password": "Test1234!"
}
```

**Response**

```json
{
  "accessToken": "xxx.yyy.zzz",
  "refreshToken": "aaa.bbb.ccc",
  "username": "testuser",
  "role": "USER"
}
```

---

### 🔄 로그인 유지 (Access Token 재발급) `POST /api/auth/reissue`

**Request**

```json
{
  "refreshToken": "aaa.bbb.ccc"
}
```

**Response**

```json
{
  "accessToken": "new-access-token",
  "refreshToken": "new-refresh-token"
}
```

---

### 🔓 로그아웃 `POST /api/auth/logout`

* `Authorization: Bearer <accessToken>`

**Response**

```json
{
  "message": "로그아웃 성공"
}
```

---

### 🗑 회원 탈퇴 `DELETE /api/auth/withdraw`

* 헤더: `Authorization: Bearer accessToken`

**Response**

```json
{
  "message": "회원 탈퇴가 완료되었습니다."
}
```

---

## 👤 사용자 정보

### 🙋 내 정보 조회 `GET /api/auth/me`

**Response**

```json
{
  "username": "testuser",
  "email": "test@example.com",
  "nickname": "다음달",
  "role": "USER",
  "profileImageUrl": "/profile_uploads/profile.png",
  "myPosts": [...],
  "myScraps": [...],
  "myLikes": [...],
  "myCommentedPosts": [...]
}
```

---

## 🔐 비밀번호 재설정 (이메일 인증 기능)

### 1⃣️ 인증 코드 요청 `POST /api/auth/password-reset/request`

```json
{
  "email": "test@example.com"
}
```

**Response**

```json
{
  "message": "비밀번호 재설정 인증번호를 이메일로 발송했습니다."
}
```

---

### 2⃣️ 인증 코드 확인 `POST /api/auth/password-reset/verify`

```json
{
  "email": "test@example.com",
  "code": "123456"
}
```

**Response**

```json
{
  "message": "인증번호가 유효합니다."
}
```

---

### 3⃣️ 새 비밀번호 설정 `POST /api/auth/password-reset/change`

```json
{
  "email": "test@example.com",
  "newPassword": "NewPass123!"
}
```

**Response**

```json
{
  "message": "비밀번호가 변경되었습니다."
}
```

---

### 🔐 내 정보에서 비밀번호 변경 `POST /api/auth/change-password`

```json
{
  "currentPassword": "OldPass123!",
  "newPassword": "NewPass123!"
}
```

**Response**

```json
{
  "message": "비밀번호가 변경되었습니다."
}
```

---

## 🖼 프로필 & 닉네임

### 🖼 프로필 이미지 업로드 `POST /api/auth/profile-image`

* `multipart/form-data`
* 필드명: `file`

**Response**

```json
{
  "message": "프로필 사진이 변경되었습니다."
}
```

---

### ❌ 프로필 이미지 삭제 `DELETE /api/auth/profile-image`

**Response**

```json
{
  "message": "프로필 사진이 기본 이미지로 변경되었습니다."
}
```

---

### 🧑 닉네임 변경 `POST /api/auth/update-nickname`

```json
{
  "nickname": "새닉네임"
}
```

**Response**

```json
{
  "message": "닉네임이 변경되었습니다."
}
```

---

### 🧚 닉네임 중복 확인 `GET /api/auth/nickname-check?nickname=새닉네임`

**Response**

```json
true
```

또는

```json
false
```

---

## 📝 게시글 / 댓글 / 좋아요 / 스크랩

### 📌 게시글 등록 `POST /api/posts`

* 이미지/동영상 포함 시: `multipart/form-data`

  * 필드: `title`, `content`, `files`

**Request**

* `title`: 게시글 제목
* `content`: 게시글 내용
* `files`: 이미지/동영상 파일들

**Response**

```json
{
  "id": 1,
  "title": "게시글 제목",
  "content": "내용",
  "author": "nickname",
  "createdAt": "2024-07-01T00:00:00",
  "mediaList": [
    {
      "url": "/media/abc.jpg",
      "type": "IMAGE"
    }
  ]
}
```

---

### ✏️ 게시글 수정 `PUT /api/posts/{postId}`

```json
{
  "title": "수정된 제목",
  "content": "수정된 내용"
}
```

**Response**

```json
{
  "message": "게시글이 수정되었습니다."
}
```

---

### ❌ 게시글 삭제 `DELETE /api/posts/{postId}`

**Response**

```json
{
  "message": "게시글이 삭제되었습니다."
}
```

---

### 🔍 게시글 검색 `GET /api/posts?keyword=keyword&page=0`

**Response**

```json
[PostResponse...]
```

---

### 📌 게시글 조회 `GET /api/posts/{postId}`

**Response**

```json
{
  "id": 1,
  "title": "게시글 제목",
  "content": "내용",
  "author": "nickname",
  "createdAt": "2024-07-01T00:00:00",
  "likeCount": 10,
  "scrapCount": 3,
  "commentCount": 5,
  "mediaList": [ { "url": "...", "type": "IMAGE" } ]
}
```

---

### 📌 좋아요한 게시글 조회 `GET /api/posts/liked`

**Response**

```json
[PostResponse...]
```

---

### 📌 스크랩한 게시글 조회 `GET /api/posts/scrapped`

**Response**

```json
[PostResponse...]
```

---

### 👍 좋아요 `POST /api/posts/{postId}/like`

**Response**

```json
{
  "message": "게시글에 좋아요를 눌렀습니다."
}
```

### 🔁 좋아요 취소 `DELETE /api/posts/{postId}/like`

**Response**

```json
{
  "message": "게시글 좋아요가 취소되었습니다."
}
```

---

### ⭐ 스크랩 `POST /api/posts/{postId}/scrap`

**Response**

```json
{
  "message": "게시글을 스크랩했습니다."
}
```

### 🔁 스크랩 취소 `DELETE /api/posts/{postId}/scrap`

**Response**

```json
{
  "message": "스크랩이 취소되었습니다."
}
```

---

## 💬 댓글 / 대댓글

### 💬 댓글 등록 `POST /api/comments`

```json
{
  "postId": 1,
  "content": "댓글 내용",
  "parentId": null
}
```

### 💬 대댓글 등록 `POST /api/comments`

```json
{
  "postId": 1,
  "content": "대댓글 내용",
  "parentId": 5
}
```

### ✏️ 댓글/대댓글 수정 `PUT /api/comments/{commentId}`

```json
{
  "content": "수정된 내용"
}
```

**Response**

```json
{
  "message": "댓글이 수정되었습니다."
}
```

---

### ❌ 댓글/대댓글 삭제 `DELETE /api/comments/{commentId}`

**Response**

```json
{
  "message": "댓글이 삭제되었습니다."
}
```

---

### 📌 댓글 조회 `GET /api/posts/{postId}/comments`

**Response**

```json
[CommentResponseDto...]
```

---

## 🔗 소셜 로그인

### 🔑 카카오 로그인 `GET /oauth2/authorization/kakao`

### 🔑 네이버 로그인 `GET /oauth2/authorization/naver`

### 🔑 구글 로그인 `GET /oauth2/authorization/google`

* 로그인 후 Redirect URI에 따라 JWT 발급
* AccessToken, RefreshToken 발급 포함

---

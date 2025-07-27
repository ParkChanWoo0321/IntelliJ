# 🌐 패시팅 커널 게시판 백엔드 API 명세서

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

```
200 OK
"회원가입 성공"
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

```
200 OK
"로그아웃 성공"
```

---

### 🗑 회원 탈퇴 `DELETE /api/auth/withdraw`

* 헤더: `Authorization: Bearer accessToken`

**Response**

```
200 OK
"회원 탈퇴가 완료되었습니다."
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

* `myPosts`, `myScraps`, `myLikes`, `myCommentedPosts`: `PostResponse` 구조

```json
{
  "postId": 1,
  "title": "제목",
  "author": "닉네임",
  "likeCount": 5,
  "scrapCount": 2,
  "commentCount": 4,
  "mediaList": [
    {
      "url": "/uploads/image1.jpg",
      "type": "IMAGE"
    }
  ]
}
```

---

## 🔐 비밀번호 재설정 (이메일 인증 기반)

### 1️⃣ 인증 코드 요청 `POST /api/auth/password-reset/request`

```json
{
  "email": "test@example.com"
}
```

**Response**

```
200 OK
"비밀번호 재설정 인증번호를 이메일로 발송했습니다."
```

---

### 2️⃣ 인증 코드 확인 `POST /api/auth/password-reset/verify`

```json
{
  "email": "test@example.com",
  "code": "123456"
}
```

**Response**

```
200 OK
"인증번호가 유효합니다."
```

---

### 3️⃣ 새 비밀번호 설정 `POST /api/auth/password-reset/change`

```json
{
  "email": "test@example.com",
  "newPassword": "NewPass123!"
}
```

**Response**

```
200 OK
"비밀번호가 변경되었습니다."
```

---

### 🔒 내 정보에서 비밀번호 변경 `POST /api/auth/change-password`

```json
{
  "currentPassword": "OldPass123!",
  "newPassword": "NewPass123!"
}
```

**Response**

```
200 OK
"비밀번호가 변경되었습니다."
```

---

## 🖼 프로필 & 닉네임

### 🖼 프로필 이미지 업로드 `POST /api/auth/profile-image`

* `multipart/form-data`
* 필드명: `file`

**Response**

```
200 OK
"프로필 사진이 변경되었습니다."
```

---

### ❌ 프로필 이미지 삭제 `DELETE /api/auth/profile-image`

**Response**

```
200 OK
"프로필 사진이 기본 이미지로 변경되었습니다."
```

---

### 🧑‍💼 닉네임 변경 `POST /api/auth/update-nickname`

```json
{
  "nickname": "새닉네임"
}
```

**Response**

```
200 OK
"닉네임이 변경되었습니다."
```

---

### 🧪 닉네임 중복 확인 `GET /api/auth/nickname-check?nickname=새닉네임`

**Response**

```json
true 또는 false
```

---

## 📝 게시글 / 댓글 / 좋아요 / 스크랩

### 💬 내가 댓글 단 게시글 조회 `GET /api/auth/my-comments/posts`

**Response**

* `List<PostResponse>`

---

## 💬 대댓글 기능

### ✏️ 대댓글 수정 `PUT /api/comments/replies/{replyId}`

```json
{
  "content": "수정된 대댓글입니다"
}
```

### ❌ 대댓글 삭제 `DELETE /api/comments/replies/{replyId}`

---

## 🌐 소셜 로그인

### 지원 플랫폼

* Google
* Kakao
* Naver

### OAuth2 엔드포인트

```http
GET /oauth2/authorization/google
GET /oauth2/authorization/kakao
GET /oauth2/authorization/naver
```

**Response 예시**

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "username": "testuser",
  "role": "USER"
}
```

---

## ⚙️ 환경 변수 설정

```properties
jwt.secret=your-secret-key
spring.mail.username=youremail@gmail.com
spring.mail.password=application-specific-password
```

---

## ✅ 보안 및 특이사항 요약

* AccessToken + RefreshToken 조합 기반 인증
* RefreshToken 기반 자동 로그인
* 로그아웃 시 AccessToken 블랙리스트 처리
* 프로필 이미지 업로드 및 삭제
* 닉네임 중복 확인/변경
* 댓글 단 게시글 조회
* 내가 좋아요, 스크랩한 게시글 포함
* 대댓글 수정/삭제 기능 포함
* 소셜 로그인 연동 (Kakao, Naver, Google)
* 비밀번호 재설정 이메일 인증 및 내 정보에서 변경 지원

---

🎉 **모든 기능 완벽 구현 완료!**

이 문서는 GitHub `README.md`에 바로 복사하여 사용 가능합니다.

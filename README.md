
# 🛡️ Hackerthon Spring Boot API Server

Spring Boot 기반의 커뮤니티 서비스 백엔드 API 서버입니다.  
JWT 기반 인증 및 로그인 유지 기능, 댓글/대댓글, 게시글 좋아요/스크랩, 알림, 비밀번호 찾기 등 다양한 기능을 제공합니다.

---

## 🔐 인증(Authentication)

### 1. 회원가입

- **URL:** `/api/auth/signup`
- **Method:** `POST`
- **RequestBody:**

```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "nickname": "string"
}
```

- **Response:**
  - 200 OK : `"회원가입 성공"`
  - 400 BAD REQUEST : `"에러 메시지"`

---

### 2. 로그인

- **URL:** `/api/auth/login`
- **Method:** `POST`
- **RequestBody:**

```json
{
  "username": "string",
  "password": "string"
}
```

- **Response:**

```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "username": "string",
  "role": "USER"
}
```

---

### 3. 로그아웃

- **URL:** `/api/auth/logout`
- **Method:** `POST`
- **Header:** `Authorization: Bearer {accessToken}`

- **Response:** `"로그아웃 성공"`

---

### 4. 로그인 유지 (Refresh Token 재발급)

- **URL:** `/api/auth/refresh`
- **Method:** `POST`
- **Header:** `Authorization: Bearer {refreshToken}`

- **Response:**

```json
{
  "accessToken": "string"
}
```

---

### 5. 내 정보 조회

- **URL:** `/api/auth/me`
- **Method:** `GET`
- **Header:** `Authorization: Bearer {accessToken}`

- **Response:** 유저 정보 + 내가 쓴 글/스크랩/좋아요/댓글단 게시글 목록

---

### 6. 프로필 이미지 변경

- **URL:** `/api/auth/profile-image`
- **Method:** `POST`
- **Header:** `Authorization: Bearer {accessToken}`
- **Body:** `multipart/form-data (file)`

- **Response:** `"프로필 사진이 변경되었습니다."`

---

### 7. 프로필 이미지 삭제

- **URL:** `/api/auth/profile-image`
- **Method:** `DELETE`

- **Response:** `"프로필 사진이 기본 이미지로 변경되었습니다."`

---

### 8. 비밀번호 변경

- **URL:** `/api/auth/change-password`
- **Method:** `POST`

```json
{
  "currentPassword": "string",
  "newPassword": "string"
}
```

---

### 9. 비밀번호 재설정 (이메일 인증 방식)

#### 9-1. 인증번호 요청

- **URL:** `/api/auth/password-reset/request`
- **Method:** `POST`

```json
{
  "email": "string"
}
```

#### 9-2. 인증번호 검증

- **URL:** `/api/auth/password-reset/verify`
- **Method:** `POST`

```json
{
  "email": "string",
  "code": "123456"
}
```

#### 9-3. 비밀번호 재설정

```json
{
  "email": "string",
  "newPassword": "string"
}
```

---

### 10. 닉네임 변경

- **URL:** `/api/auth/update-nickname`
- **Method:** `POST`

```json
{
  "nickname": "newNickname"
}
```

---

### 11. 닉네임 중복 체크

- **URL:** `/api/auth/nickname-check?nickname=newNickname`
- **Method:** `GET`

---

### 12. 내가 댓글 단 게시글 목록

- **URL:** `/api/auth/my-comments/posts`
- **Method:** `GET`

---

### 13. 회원 탈퇴

- **URL:** `/api/auth/withdraw`
- **Method:** `DELETE`

---

## 💬 댓글 기능

### 1. 댓글 작성

- **URL:** `/api/comments/{postId}`
- **Method:** `POST`

```json
{
  "content": "string"
}
```

### 2. 대댓글 작성

- **URL:** `/api/comments/{postId}/{parentId}`
- **Method:** `POST`

```json
{
  "content": "string"
}
```

### 3. 댓글 수정

- **URL:** `/api/comments/{commentId}`
- **Method:** `PUT`

```json
{
  "content": "updated content"
}
```

### 4. 댓글 삭제

- **URL:** `/api/comments/{commentId}`
- **Method:** `DELETE`

---

## 🛠️ 기술 스택

- Spring Boot 3
- Spring Security + JWT
- JPA + H2/MySQL
- Lombok / Validation / Multipart
- Mail API (비밀번호 찾기)

---

## 🔐 로그인 유지 전략

- AccessToken: 1시간 유효
- RefreshToken: 7일 유효
- Refresh 요청 시 새 AccessToken 발급
- Logout 및 회원탈퇴 시 토큰 블랙리스트에 등록하여 무효화

---

## ✅ 기타 기능

- 비밀번호 규칙 유효성 검사
- 중복 아이디/이메일/닉네임 검사
- 기본 프로필 이미지 제공
- 내가 댓글 단 게시글 목록 조회
- 커뮤니티 기능(게시글, 댓글, 좋아요, 스크랩)

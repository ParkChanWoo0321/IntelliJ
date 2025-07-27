# 🏃 Runny Buddy Backend API Documentation

Spring Boot 기반의 러닝 커뮤니티 백엔드 프로젝트입니다. 회원가입부터 게시글, 댓글, 좋아요, 스크랩, 알림, 뱃지까지 다양한 기능을 포함하고 있으며, JWT 기반 인증과 OAuth2 소셜 로그인(Kakao, Google, Naver)을 지원합니다.

---

## 📌 목차

* [기술 스택](#기술-스택)
* [인증 및 보안](#인증-및-보안)
* [기능 요약](#기능-요약)
* [엔티티 관계](#엔티티-관계)
* [API 명세](#api-명세)

  * [회원 인증](#1-회원-인증)
  * [게시글](#2-게시글)
  * [댓글](#3-댓글)
  * [좋아요](#4-좋아요)
  * [스크랩](#5-스크랩)
  * [알림](#6-알림)
  * [뱃지](#7-뱃지)

---

## 🧰 기술 스택

* Java 17
* Spring Boot 3.x
* Spring Security + JWT
* JPA (Hibernate)
* MySQL
* SSE (Server-Sent Events)
* MultipartFile (이미지 업로드)
* Gradle

---

## 🔐 인증 및 보안

* JWT 기반 인증 (AccessToken + RefreshToken)
* 소셜 로그인 성공 시 JWT 자동 발급
* 인증 불필요 경로: `/auth/**`, `/oauth2/**`, `/error`
* 모든 인증 요청에 `Authorization: Bearer {accessToken}` 필요

---

## 📋 기능 요약

| 분류           | 기능                                                              |
| ------------ | --------------------------------------------------------------- |
| Auth         | 회원가입, 로그인, 로그아웃, 소셜 로그인, 닉네임 중복확인, 프로필 변경, 내 정보 조회/수정, 비밀번호 재설정 |
| Post         | 게시글 생성/수정/삭제/조회, 이미지 첨부, 태그 검색, 내가 쓴 글 조회                       |
| Comment      | 댓글 작성/수정/삭제, 대댓글 작성/수정/삭제                                       |
| Like         | 게시글 좋아요 토글, 좋아요 수 조회, 내가 받은 총 좋아요 수, 내가 쓴 글 별 좋아요 수 조회, 좋아요 취소  |
| Scrap        | 스크랩 토글, 스크랩한 게시글 조회, 스크랩 취소                                     |
| Notification | SSE 알림 구독, 전체 알림 조회                                             |
| Badge        | 사용자 뱃지 조회 (좋아요 수 기준 자동 지급)                                      |

---

## 🔗 엔티티 관계 (요약)

* `User` ⟷ `Post` (1\:N)
* `User` ⟷ `Comment` (1\:N)
* `Post` ⟷ `Comment` (1\:N)
* `User` ⟷ `Like`, `Scrap` (1\:N)
* `Post` ⟷ `Like`, `Scrap` (1\:N)
* `Post` ⟷ `PostImage`, `Tag` (1\:N)
* `User` ⟷ `Notification`, `Badge` (1\:N)

---

## 📑 API 명세

### 1. 🧾 회원 인증

#### ✅ 회원가입

* `POST /api/auth/signup`
* Request Body:

```json
{
  "username": "string",
  "password": "string",
  "nickname": "string"
}
```

* Response:

```json
{
  "userId": 1,
  "username": "string",
  "nickname": "string"
}
```

#### ✅ 로그인

* `POST /api/auth/login`
* Request Body:

```json
{
  "username": "string",
  "password": "string"
}
```

* Response:

```json
{
  "accessToken": "jwt-token",
  "refreshToken": "jwt-refresh-token"
}
```

#### ✅ 로그아웃

* `POST /api/auth/logout`
* Header: `Authorization: Bearer {token}`

#### ✅ 내 정보 조회

* `GET /api/auth/me`
* Header: `Authorization`

#### ✅ 닉네임 중복 확인

* `GET /api/auth/check-nickname?nickname=abc`

### 2. 📝 게시글

#### ✅ 게시글 작성

* `POST /posts`
* Header: `Authorization`
* Content-Type: `multipart/form-data`
* Request: `@ModelAttribute PostRequestDto + MultipartFile[] images`

```json
{
  "title": "제목",
  "content": "내용",
  "tags": ["운동", "다이어트"],
  "userId": 1
}
```

* Response:

```json
{
  "postId": 1,
  "title": "제목",
  "content": "내용",
  "imageUrls": ["url1", "url2"],
  "tags": ["운동", "다이어트"],
  "createdAt": "2025-07-28T12:00:00"
}
```

#### ✅ 게시글 수정

* `PUT /posts/{postId}`
* Header: `Authorization`
* Request Body: `PostUpdateRequestDto`

```json
{
  "title": "수정된 제목",
  "content": "수정된 내용"
}
```

#### ✅ 게시글 삭제

* `DELETE /posts/{postId}`
* Header: `Authorization`

#### ✅ 태그 검색

* `GET /posts/search-by-tag?tagName=운동`
* Response: `List<PostResponseDto>`

#### ✅ 내가 쓴 글 조회

* `GET /posts/my-posts?userId=1`
* Response: `List<PostResponseDto>`

---

### 3. 💬 댓글

#### ✅ 댓글 작성

* `POST /api/comments/{postId}?userId=1`
* Header: `Authorization`
* Body:

```json
{
  "content": "댓글 내용"
}
```

* Response:

```json
{
  "commentId": 1,
  "postId": 123,
  "content": "댓글 내용"
}
```

#### ✅ 댓글 수정

* `PUT /api/comments/{commentId}`
* Header: `Authorization`
* Body:

```json
{
  "content": "수정된 댓글 내용"
}
```

#### ✅ 댓글 삭제

* `DELETE /api/comments/{commentId}`
* Header: `Authorization`

### 4. 👍 좋아요

#### ✅ 좋아요 토글

* `POST /posts/like?userId=1`
* Header: `Authorization`
* Body:

```json
{
  "postId": 123
}
```

* Response:

```json
{
  "liked": true,
  "likeCount": 12
}
```

#### ✅ 내가 받은 총 좋아요 수

* `GET /posts/likes/total?userId=1`
* Header: `Authorization`
* Response:

```json
{
  "totalLikeCount": 55
}
```

#### ✅ 내가 쓴 게시글별 좋아요 수 목록

* `GET /posts/likes/posts?userId=1`
* Header: `Authorization`
* Response:

```json
[
  {
    "postId": 1,
    "likeCount": 12
  },
  {
    "postId": 2,
    "likeCount": 5
  }
]
```

### 5. 📌 스크랩

#### ✅ 스크랩 토글

* `POST /api/scraps`
* Header: `Authorization`
* Body:

```json
{
  "userId": 1,
  "postId": 123
}
```

* Response:

```json
{
  "scrapped": true,
  "scrapCount": 5
}
```

#### ✅ 스크랩한 게시글 목록

* `GET /api/scraps/posts?userId=1`
* Header: `Authorization`
* Response:

```json
[
  {
    "postId": 123,
    "title": "스크랩한 글 제목"
  }
]
```

### 6. 🔔 알림 (Notification)

#### ✅ SSE 실시간 알림 구독

* `GET /notifications/subscribe?userId=1`
* Header: `Authorization`
* Response: `SseEmitter` (스트리밍 방식으로 알림 push)

#### ✅ 전체 알림 조회

* `GET /notifications?userId=1`
* Header: `Authorization`
* Response:

```json
[
  {
    "content": "누가 당신의 글에 좋아요를 눌렀습니다.",
    "postId": 123,
    "createdAt": "2025-07-28T13:00:00"
  }
]
```

### 7. 🏅 뱃지

#### ✅ 유저 뱃지 조회

* `GET /api/badges/user/{userId}`
* Header: `Authorization`
* Response:

```json
[
  {
    "name": "열정러너",
    "requiredLikes": 10
  },
  {
    "name": "레전드러너",
    "requiredLikes": 100
  }
]
```

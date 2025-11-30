# Hướng dẫn Sử dụng API Mạng Xã hội

## 1. Xác thực (Authentication)

Hệ thống sử dụng JWT. Bạn cần lấy token qua API đăng nhập và đính kèm vào header `Authorization: Bearer <token>` cho các request tiếp theo.

### Đăng nhập

- **URL**: `POST /api/authenticate`
- **Body**:
  ```json
  {
    "username": "user",
    "password": "user",
    "rememberMe": true
  }
  ```
- **Response**: Trả về `id_token`.

### Đăng ký

- **URL**: `POST /api/register`
- **Body**:
  ```json
  {
    "login": "newuser",
    "email": "newuser@example.com",
    "password": "password",
    "firstName": "Nguyen",
    "lastName": "Van A",
    "langKey": "en"
  }
  ```

## 2. Quản lý Tài khoản (Account)

### Lấy thông tin cá nhân

- **URL**: `GET /api/account`
- **Method**: `GET`

### Cập nhật thông tin cá nhân

- **URL**: `POST /api/account`
- **Method**: `POST`
- **Body**:
  ```json
  {
    "firstName": "Nguyen",
    "lastName": "Van B",
    "email": "user@example.com",
    "langKey": "en",
    "avatarUrl": "http://...",
    "bio": "Hello world",
    "phoneNumber": "0909..."
  }
  ```

## 3. Bài viết (Posts)

### Đăng bài viết mới

- **URL**: `POST /api/posts`
- **Body**:
  ```json
  {
    "content": "Hôm nay trời đẹp quá!",
    "mediaUrls": ["http://image1.jpg"],
    "privacyLevel": "PUBLIC"
  }
  ```

### Lấy Bảng tin (News Feed)

- **URL**: `GET /api/news-feed?userId={your_user_id}`
- **Note**: Trong thực tế `userId` sẽ được lấy từ Token, nhưng hiện tại API hỗ trợ truyền param để test dễ hơn.

### Lấy Timeline của người dùng

- **URL**: `GET /api/users/{userId}/timeline`

### Chi tiết bài viết

- **URL**: `GET /api/posts/{postId}`

### Xóa bài viết

- **URL**: `DELETE /api/posts/{postId}`

## 4. Bạn bè (Friends)

### Gửi lời mời kết bạn

- **URL**: `POST /api/friends/request?senderId={id}&receiverId={id}`

### Chấp nhận kết bạn

- **URL**: `POST /api/friends/accept?senderId={id_nguoi_gui}&receiverId={id_nguoi_nhan}`
- **Note**: `receiverId` là người đang thực hiện hành động chấp nhận.

### Từ chối kết bạn

- **URL**: `POST /api/friends/reject?senderId={id}&receiverId={id}`

### Danh sách lời mời đã nhận

- **URL**: `GET /api/friends/requests?userId={id}`

### Danh sách bạn bè

- **URL**: `GET /api/friends?userId={id}`

### Hủy kết bạn

- **URL**: `DELETE /api/friends?userId={id}&friendId={id}`

## 5. Bình luận (Comments)

### Thêm bình luận

- **URL**: `POST /api/comments`
- **Body**:
  ```json
  {
    "postId": "uuid...",
    "userId": "uuid...",
    "content": "Bài viết hay quá!"
  }
  ```

### Lấy danh sách bình luận

- **URL**: `GET /api/posts/{postId}/comments`

### Xóa bình luận

- **URL**: `DELETE /api/posts/{postId}/comments/{commentId}`

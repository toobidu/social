- **Search Engine**: Elasticsearch (để tìm kiếm bài viết, người dùng)
- **Build Tool**: Maven

## 3. Kiến trúc Phân lớp (Layered Architecture)

Hệ thống tuân thủ nghiêm ngặt kiến trúc phân lớp để đảm bảo tính tách biệt (Separation of Concerns) và dễ bảo trì:

1.  **Web Layer (`app.web.rest`)**:

    - Chứa các **REST Controllers** (Resources).
    - Chỉ chịu trách nhiệm nhận request HTTP, validate dữ liệu đầu vào, và trả về response HTTP.
    - **Không chứa logic nghiệp vụ**.
    - Ví dụ: `PostResource`, `FriendResource`, `CommentResource`.

2.  **Service Layer (`app.service`)**:

    - Chứa toàn bộ **logic nghiệp vụ** (Business Logic).
    - Được chia thành Interface (`app.service.interfaces`) và Implementation (`app.service.impl`).
    - Xử lý các giao dịch, logic phức tạp như Fan-out, gửi thông báo, v.v.
    - Ví dụ: `PostServiceImpl`, `FriendServiceImpl`.

3.  **Repository Layer (`app.repository`)**:

    - Giao tiếp trực tiếp với cơ sở dữ liệu Cassandra và Elasticsearch.
    - Sử dụng **Spring Data Cassandra Reactive**.
    - Ví dụ: `PostRepository`, `NewsFeedRepository`.

4.  **Domain Layer (`app.domain`)**:
    - Định nghĩa các thực thể (Entities) ánh xạ với bảng trong Cassandra.
    - Ví dụ: `Post`, `User`, `NewsFeed`.

## 4. Chiến lược sử dụng Elasticsearch vs Cassandra

Hệ thống sử dụng **Cassandra** làm kho dữ liệu chính và **Elasticsearch** làm công cụ tìm kiếm bổ trợ. Dưới đây là lý do tại sao chỉ `User` và `Post` được đánh index trong Elasticsearch:

### 4.1. Tại sao dùng Elasticsearch cho User và Post?

- **Nhu cầu Tìm kiếm Phức tạp (Fuzzy Search)**:
  - **User**: Người dùng thường tìm kiếm bạn bè bằng tên gần đúng (ví dụ: gõ "Tuan" ra "Tuan Nguyen", "Minh Tuan"). Cassandra không hỗ trợ tốt việc tìm kiếm chuỗi ký tự gần đúng (LIKE %...%).
  - **Post**: Người dùng muốn tìm bài viết theo từ khóa (keywords), hashtag, hoặc kết hợp nhiều điều kiện lọc (trạng thái + loại media + nội dung). Đây là thế mạnh tuyệt đối của Elasticsearch (Full-text search).

### 4.2. Tại sao KHÔNG dùng cho các thực thể khác?

- **Truy vấn theo Khóa chính (Primary Key Access)**:
  - **Comment**: Luôn được truy xuất theo `postId`. Câu lệnh `SELECT * FROM comments WHERE post_id = ?` trong Cassandra cực nhanh và hiệu quả. Không ai tìm kiếm comment trên toàn hệ thống.
  - **Friend**: Luôn truy xuất theo `userId`.
  - **Notification**: Luôn truy xuất theo `userId`.
  - **Message**: Luôn truy xuất theo `conversationId`.
- **Hiệu năng**: Việc đồng bộ dữ liệu sang Elasticsearch có độ trễ (latency) và tốn tài nguyên. Chỉ nên áp dụng cho những dữ liệu thực sự cần khả năng tìm kiếm nâng cao mà Cassandra không đáp ứng được.

## 5. Mô hình Dữ liệu Cassandra & Tối ưu hóa

### 4.1. Chiến lược ID (TimeUUID)

- Sử dụng **TimeUUID** cho `postId` và `commentId`.
- **Lợi ích**: TimeUUID chứa thông tin về thời gian tạo, giúp sắp xếp dữ liệu theo thời gian một cách tự nhiên mà không cần cột `createdDate` riêng biệt để sort (tránh tốn kém chi phí sort khi query).
- **Áp dụng**: `Post`, `NewsFeed`, `UserPost`, `Comment`.

### 4.2. Mô hình Fan-out on Write (News Feed)

Để tối ưu hóa việc lấy Bảng tin (News Feed) - thao tác đọc phổ biến nhất:

- **Khi tạo bài viết (Write)**:
  1.  Lưu bài viết vào bảng `posts`.
  2.  Lưu bài viết vào bảng `user_posts` (Timeline của người đăng).
  3.  **Fan-out**: Hệ thống tìm danh sách bạn bè của người đăng và chèn bản sao (hoặc tham chiếu) của bài viết vào bảng `news_feed` của từng người bạn.
- **Khi xem Bảng tin (Read)**:
  - Chỉ cần `SELECT * FROM news_feed WHERE user_id = ?`.
  - Truy vấn cực nhanh vì dữ liệu đã được chuẩn bị sẵn cho từng user.
  - Không cần JOIN phức tạp hay query nặng nề.

### 4.3. Denormalization (Phi chuẩn hóa)

- Dữ liệu người dùng (Tên, Avatar) được lưu kèm trong `NewsFeed`, `Comment`, `Friend` để tránh phải query lại bảng `User` khi hiển thị danh sách.
- **Trade-off**: Tăng dung lượng lưu trữ nhưng giảm thiểu số lượng query (Read amplification), tăng tốc độ phản hồi API.

## 5. Luồng dữ liệu chính

### 5.1. Đăng bài viết

1.  Client gọi `POST /api/posts`.
2.  `PostResource` nhận request, gọi `PostService.createPost`.
3.  `PostServiceImpl`:
    - Tạo `Post` với `TimeUUID`.
    - Lưu vào `PostRepository`.
    - Lưu vào `UserPostRepository` (Timeline).
    - Lấy danh sách bạn bè từ `FriendRepository`.
    - Lặp qua danh sách bạn bè, lưu `NewsFeed` item vào `NewsFeedRepository`.
4.  Trả về kết quả cho Client.

### 5.2. Kết bạn

1.  Client A gửi lời mời cho B: `POST /api/friends/request`.
2.  `FriendService` tạo `FriendRequest` trong DB.
3.  Client B chấp nhận: `POST /api/friends/accept`.
4.  `FriendService`:
    - Tạo record `Friend` cho A (friend là B).
    - Tạo record `Friend` cho B (friend là A).
    - Xóa `FriendRequest`.

## 6. Bảo mật

- Sử dụng **Spring Security** với JWT (JSON Web Token).
- Mật khẩu được mã hóa (BCrypt).
- API được bảo vệ theo quyền hạn (ROLE_USER, ROLE_ADMIN).

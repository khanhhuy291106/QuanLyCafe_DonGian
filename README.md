# Quản lý quán cafe (Java Swing + SQL Server) — bản bài tập

Chương trình bán hàng cho quán cafe take away, làm ở mức bài tập.

## Chức năng
- Đăng nhập, phân quyền 2 vai trò: **Quản lý** (tất cả chức năng) và **Nhân viên** (Bán hàng, Hóa đơn).
- **Bán hàng**: chọn món (hiện ảnh món đang chọn), số lượng, thêm vào giỏ, chọn khuyến mãi, thanh toán -> lưu hóa đơn.
- **Hóa đơn**: xem theo khoảng ngày, tìm mã hóa đơn/tên nhân viên kết hợp trạng thái, xem chi tiết, hủy đơn, **xuất hóa đơn PDF A4** (nhiều trang nếu nhiều món).
- **Thực đơn** (Quản lý): quản lý Món (có chọn ảnh cho món) và Loại món; tìm mã/tên/loại và lọc trạng thái.
- **Nhân viên** (Quản lý): thêm, sửa, xóa nhân viên kèm tên đăng nhập, mật khẩu, chức vụ; tìm kiếm mã/tên/SĐT/tên đăng nhập kết hợp trạng thái. Mật khẩu được che khi nhập và không hiện lại khi chọn nhân viên; để trống khi sửa nghĩa là giữ mật khẩu hiện có.
- **Khuyến mãi** (Quản lý): giảm theo %, trong khoảng ngày, bật/tắt; tìm mã/tên kết hợp trạng thái.
- **Thống kê** (Quản lý): doanh thu theo ngày, xuất CSV.

## Cấu trúc
- `src/connectDB`: kết nối SQL Server (sửa tài khoản/mật khẩu SQL Server ở đây).
- `src/entity`: 6 lớp thực thể (NhanVien, LoaiMon, Mon, KhuyenMai, HoaDon, ChiTietHoaDon).
- `src/dao`: truy vấn CSDL cho từng thực thể.
- `src/gui`: giao diện, chạy từ `gui.Main`.
- `sql/tao_bang.sql`, `sql/du_lieu_mau.sql`: tạo database `QuanLyCafe_DonGian` và dữ liệu mẫu.
- `lib`: driver JDBC SQL Server.
- `image/mon`: ảnh món. CSDL chỉ lưu tên file, ảnh được chép vào thư mục này khi bấm "Chọn ảnh".

## Cách chạy trên Eclipse
1. **Nếu đã có database với dữ liệu thật, KHÔNG chạy lại `sql/tao_bang.sql`!** Script này DROP DATABASE `QuanLyCafe_DonGian` và làm mất toàn bộ dữ liệu trong database đó. Bản nâng cấp không thay đổi schema nên chỉ cần dùng database đang có.
   Nếu cài mới lần đầu, mở SSMS, chạy `sql/tao_bang.sql` rồi `sql/du_lieu_mau.sql`.
   (Tên database: `QuanLyCafe_DonGian`.)
2. Sửa `username` / `pwd` trong `src/connectDB/ConnectDB.java` cho đúng SQL Server của bạn.
3. Eclipse: File -> Import -> Existing Projects into Workspace -> chọn thư mục này.
4. Chạy `gui.Main` (chạy từ Eclipse thì tự nhận đúng thư mục `image/mon` của project).

## Tài khoản mẫu (mật khẩu 123456)
- `quan.le` — Quản lý
- `van.a`, `bich.tran` — Nhân viên

## Ghi chú
- Mật khẩu lưu dạng văn bản thường cho đơn giản; thực tế cần mã hóa (băm) mật khẩu.
- Món/nhân viên/khuyến mãi đã dùng trong hóa đơn thì không xóa được, hãy chuyển sang "Ngừng bán" / "Nghỉ việc" / "Tắt".
- Ảnh món: bấm "Chọn ảnh" ở màn hình Thực đơn > Món, rồi bấm Thêm hoặc Sửa để lưu. Dữ liệu mẫu mới có ảnh cho món đầu tiên (`cf_den.png`).

## Chức năng mới và kiểm tra
- Vào **Hóa đơn** → đặt khoảng ngày → chọn một dòng → bấm **Xuất hóa đơn PDF** → chọn nơi lưu. PDF là ảnh trang A4; in xem được chữ tiếng Việt, nhưng không chọn/copy chữ trong PDF được.
- Tại Món / Nhân viên / Khuyến mãi, nhập ô **Tìm mã / tên** và chọn **Trạng thái** để lọc nhiều tiêu chí đồng thời. Loại món có tìm mã/tên. Hóa đơn lọc ngày + từ khóa + trạng thái.
- Nếu món/nhân viên/khuyến mãi đã nằm trong lịch sử hóa đơn, chương trình không xóa vật lý mà hiện thông báo gợi ý dừng bán/nghỉ việc/tắt. Loại món đang có món cũng không thể xóa cho tới khi chuyển các món sang loại khác.
- `Ctrl+F`: chuyển đến ô tìm kiếm của tab; `Alt+1` ... `Alt+6`: chọn nhanh các tab chính theo thứ tự trên màn hình.
- Không cần thêm JAR để xuất PDF. Dùng Java 11+ và JDBC SQL Server hiện có.

## Phạm vi kiểm thử
- Đã thử biên dịch tất cả Java bằng JDK trong môi trường kiểm tra, không báo lỗi.
- Đã chạy thử hàm xuất PDF với hóa đơn giả lập nhiều dòng, PDF A4 hợp lệ.
- **Chưa kết nối SQL Server thật trên máy bạn**: hãy thực hiện kiểm thử đăng nhập, thêm/sửa/xóa, thanh toán, tìm kiếm và xuất từ hóa đơn thực tế.
- Chương trình vẫn dùng mật khẩu plaintext trong database và `double` cho tiền như bản gốc. Khi đưa vào vận hành thực tế cần cải thiện bảo mật và độ chính xác tiền tệ; bản này ưu tiên giữ nguyên CSDL và dữ liệu cũ.

## Xem trước hóa đơn trên màn hình

Trong tab **Hóa đơn**, chọn một dòng hóa đơn rồi nhấn **Xem / Xuất hóa đơn**.
Cửa sổ phiếu thanh toán hiển thị trực tiếp trên màn hình và cho phép **Lưu hóa đơn (PNG)** hoặc **Đóng**. Không có tính năng xuất PDF và in.
Không cần lưu file trước khi xem. Chức năng sử dụng Java Swing/Java2D có sẵn, không cần thêm JAR.

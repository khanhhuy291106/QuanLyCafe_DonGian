/* =====================================================================
   QUẢN LÝ QUÁN CAFE (bản đơn giản) - SQL Server
   Script 1/2: tạo database, bảng, ràng buộc.
   Chạy bằng SSMS: mở file -> Execute (F5).
   Database tên QuanLyCafe_DonGian (KHÁC database cũ nên không ảnh hưởng dữ liệu cũ).
   Nếu chạy lại, database này sẽ bị xóa và tạo mới.
   ===================================================================== */
USE master;
GO
IF DB_ID(N'QuanLyCafe_DonGian') IS NOT NULL
BEGIN
    ALTER DATABASE QuanLyCafe_DonGian SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLyCafe_DonGian;
END
GO
CREATE DATABASE QuanLyCafe_DonGian;
GO
USE QuanLyCafe_DonGian;
GO

-- 1. Nhân viên (có luôn tên đăng nhập, mật khẩu, chức vụ)
CREATE TABLE NhanVien (
    maNV        VARCHAR(10)   NOT NULL PRIMARY KEY,
    hoTen       NVARCHAR(100) NOT NULL,
    gioiTinh    BIT           NOT NULL DEFAULT 1,       -- 1 = Nam, 0 = Nữ
    sdt         VARCHAR(10)   NOT NULL,
    tenDangNhap VARCHAR(30)   NOT NULL UNIQUE,
    matKhau     VARCHAR(30)   NOT NULL,                 -- lưu thẳng, cho đơn giản
    chucVu      VARCHAR(20)   NOT NULL CHECK (chucVu IN ('QUAN_LY', 'NHAN_VIEN')),
    trangThai   BIT           NOT NULL DEFAULT 1        -- 1 = đang làm, 0 = nghỉ việc
);

-- 2. Loại món
CREATE TABLE LoaiMon (
    maLoai  VARCHAR(10)  NOT NULL PRIMARY KEY,
    tenLoai NVARCHAR(50) NOT NULL UNIQUE
);

-- 3. Món (mỗi món một giá)
CREATE TABLE Mon (
    maMon     VARCHAR(10)   NOT NULL PRIMARY KEY,
    tenMon    NVARCHAR(100) NOT NULL,
    gia       DECIMAL(12,0) NOT NULL CHECK (gia >= 0),
    maLoai    VARCHAR(10)   NOT NULL REFERENCES LoaiMon(maLoai),
    hinhAnh   NVARCHAR(260) NULL,                         -- tên file ảnh nằm trong thư mục image/mon (có thể để trống)
    trangThai BIT           NOT NULL DEFAULT 1            -- 1 = đang bán, 0 = ngừng bán
);

-- 4. Khuyến mãi (giảm theo phần trăm, trong khoảng ngày)
CREATE TABLE KhuyenMai (
    maKM      VARCHAR(10)  NOT NULL PRIMARY KEY,
    tenKM     NVARCHAR(100) NOT NULL,
    phanTram  INT          NOT NULL CHECK (phanTram BETWEEN 1 AND 100),
    ngayBD    DATE         NOT NULL,
    ngayKT    DATE         NOT NULL,
    trangThai BIT          NOT NULL DEFAULT 1,            -- 1 = bật, 0 = tắt
    CHECK (ngayKT >= ngayBD)
);

-- 5. Hóa đơn
CREATE TABLE HoaDon (
    maHD      VARCHAR(10)   NOT NULL PRIMARY KEY,
    ngayLap   DATETIME      NOT NULL DEFAULT GETDATE(),
    maNV      VARCHAR(10)   NOT NULL REFERENCES NhanVien(maNV),
    maKM      VARCHAR(10)   NULL REFERENCES KhuyenMai(maKM),
    giamGia   DECIMAL(12,0) NOT NULL DEFAULT 0,           -- số tiền được giảm
    tongTien  DECIMAL(12,0) NOT NULL,                     -- số tiền khách phải trả (đã trừ giảm giá)
    tienNhan  DECIMAL(12,0) NULL,                         -- tiền khách đưa; NULL cho hóa đơn cũ
    trangThai VARCHAR(15)   NOT NULL DEFAULT 'HOAN_THANH'
              CHECK (trangThai IN ('HOAN_THANH', 'DA_HUY'))
);

-- 6. Chi tiết hóa đơn
CREATE TABLE ChiTietHoaDon (
    maCT    INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    maHD    VARCHAR(10)   NOT NULL REFERENCES HoaDon(maHD) ON DELETE CASCADE,
    maMon   VARCHAR(10)   NOT NULL REFERENCES Mon(maMon),
    soLuong INT           NOT NULL CHECK (soLuong > 0),
    donGia  DECIMAL(12,0) NOT NULL                        -- giá món tại thời điểm bán
);
GO
PRINT N'Đã tạo xong database QuanLyCafe_DonGian.';

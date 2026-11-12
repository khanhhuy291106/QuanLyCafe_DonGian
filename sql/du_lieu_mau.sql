/* Script 2/2: dữ liệu mẫu. Chạy SAU khi đã chạy tao_bang.sql.
   Tài khoản mẫu (mật khẩu chung: 123456):
     quan.le  (Quản lý)
     van.a, bich.tran (Nhân viên) */
USE QuanLyCafe_DonGian;
GO
SET NOCOUNT ON;
DECLARE @h DATETIME = CAST(CAST(GETDATE() AS DATE) AS DATETIME);   -- 00:00 hôm nay

INSERT INTO NhanVien(maNV, hoTen, gioiTinh, sdt, tenDangNhap, matKhau, chucVu, trangThai) VALUES
('NV001', N'Lê Minh Quân',   1, '0901000001', 'quan.le',   '123456', 'QUAN_LY',  1),
('NV002', N'Nguyễn Văn A',   1, '0901000002', 'van.a',     '123456', 'NHAN_VIEN', 1),
('NV003', N'Trần Thị Bích',  0, '0901000003', 'bich.tran', '123456', 'NHAN_VIEN', 1);

INSERT INTO LoaiMon(maLoai, tenLoai) VALUES
('L01', N'Cà phê'), ('L02', N'Trà'), ('L03', N'Sinh tố'), ('L04', N'Bánh');

INSERT INTO Mon(maMon, tenMon, gia, maLoai, hinhAnh, trangThai) VALUES
('MON001', N'Cà phê đen đá',    25000, 'L01', N'cf_den.png', 1),
('MON002', N'Cà phê sữa đá',    29000, 'L01', N'cf_sua.png', 1),
('MON003', N'Bạc xỉu',          32000, 'L01', N'bac_xiu.png', 1),
('MON004', N'Americano',        35000, 'L01', N'americano.png', 1),
('MON005', N'Cappuccino',       49000, 'L01', N'cappuccino.png', 1),
('MON006', N'Trà đào cam sả',   39000, 'L02', N'tra_dao.png', 1),
('MON007', N'Trà vải',          38000, 'L02', N'tra_vai.png', 1),
('MON008', N'Trà sữa trân châu',42000, 'L02', N'tra_sua.png', 1),
('MON009', N'Matcha latte',     45000, 'L02', N'matcha.png', 1),
('MON010', N'Sinh tố bơ',       45000, 'L03', N'sinh_to_bo.png', 1),
('MON011', N'Sinh tố xoài',     42000, 'L03', N'sinh_to_xoai.png', 1),
('MON012', N'Bánh croissant',   30000, 'L04', N'banh_croissant.png', 1),
('MON013', N'Sinh tố dâu',      44000, 'L03', N'tra_dau.png', 0);

-- Khuyến mãi: KM001 đang áp dụng, KM002 đã hết hạn
INSERT INTO KhuyenMai(maKM, tenKM, phanTram, ngayBD, ngayKT, trangThai) VALUES
('KM001', N'Giảm 10% toàn menu', 10, DATEADD(DAY, -30, @h), DATEADD(DAY, 30, @h), 1),
('KM002', N'Khai trương giảm 20%', 20, DATEADD(DAY, -60, @h), DATEADD(DAY, -30, @h), 1);

-- Hóa đơn mẫu (trải trong vài ngày gần đây để có số liệu thống kê)
INSERT INTO HoaDon(maHD, ngayLap, maNV, maKM, giamGia, tongTien, trangThai) VALUES
('HD0001', DATEADD(MINUTE, 555, DATEADD(DAY, -2, @h)), 'NV002', NULL,    0,     89000, 'HOAN_THANH'),
('HD0002', DATEADD(MINUTE, 870, DATEADD(DAY, -2, @h)), 'NV003', 'KM001', 10900, 98100, 'HOAN_THANH'),
('HD0003', DATEADD(MINUTE, 520, DATEADD(DAY, -1, @h)), 'NV002', NULL,    0,    129000, 'HOAN_THANH'),
('HD0004', DATEADD(MINUTE, 960, DATEADD(DAY, -1, @h)), 'NV003', 'KM001', 9000,  81000, 'HOAN_THANH'),
('HD0005', DATEADD(MINUTE, 300, @h),                   'NV002', NULL,    0,     70000, 'HOAN_THANH'),
('HD0006', DATEADD(MINUTE, 320, @h),                   'NV003', NULL,    0,     35000, 'DA_HUY');

INSERT INTO ChiTietHoaDon(maHD, maMon, soLuong, donGia) VALUES
('HD0001', 'MON001', 2, 25000), ('HD0001', 'MON006', 1, 39000),
('HD0002', 'MON005', 1, 49000), ('HD0002', 'MON012', 2, 30000),
('HD0003', 'MON002', 3, 29000), ('HD0003', 'MON008', 1, 42000),
('HD0004', 'MON010', 2, 45000),
('HD0005', 'MON003', 1, 32000), ('HD0005', 'MON007', 1, 38000),
('HD0006', 'MON004', 1, 35000);
GO
PRINT N'Đã thêm dữ liệu mẫu.';

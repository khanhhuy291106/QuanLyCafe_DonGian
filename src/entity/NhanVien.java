package entity;

public class NhanVien {
    public static final String QUAN_LY = "QUAN_LY";
    public static final String NHAN_VIEN = "NHAN_VIEN";

    private String maNV;
    private String hoTen;
    private boolean gioiTinh; // true = Nam
    private String sdt;
    private String tenDangNhap;
    private String matKhau;
    private String chucVu; // QUAN_LY hoặc NHAN_VIEN
    private boolean trangThai; // true = đang làm

    public NhanVien() {
    }

    public NhanVien(String maNV, String hoTen, boolean gioiTinh, String sdt, String tenDangNhap, String matKhau,
            String chucVu, boolean trangThai) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.chucVu = chucVu;
        this.trangThai = trangThai;
    }

    public boolean laQuanLy() {
        return QUAN_LY.equals(chucVu);
    }

    public static String tenChucVu(String chucVu) {
        return QUAN_LY.equals(chucVu) ? "Quản lý" : "Nhân viên";
    }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public boolean isGioiTinh() { return gioiTinh; }
    public void setGioiTinh(boolean gioiTinh) { this.gioiTinh = gioiTinh; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }
}

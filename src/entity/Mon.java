package entity;

public class Mon {
    private String maMon;
    private String tenMon;
    private double gia;
    private LoaiMon loai;
    private String hinhAnh; // tên file ảnh trong thư mục image/mon, null nếu chưa có
    private boolean trangThai; // true = đang bán

    public Mon() {
    }

    public Mon(String maMon, String tenMon, double gia, LoaiMon loai, String hinhAnh, boolean trangThai) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.gia = gia;
        this.loai = loai;
        this.hinhAnh = hinhAnh;
        this.trangThai = trangThai;
    }

    public String getMaMon() { return maMon; }
    public void setMaMon(String maMon) { this.maMon = maMon; }
    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }
    public double getGia() { return gia; }
    public void setGia(double gia) { this.gia = gia; }
    public LoaiMon getLoai() { return loai; }
    public void setLoai(LoaiMon loai) { this.loai = loai; }
    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }
}

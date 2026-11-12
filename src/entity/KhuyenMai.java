package entity;

import java.time.LocalDate;

public class KhuyenMai {
    private String maKM;
    private String tenKM;
    private int phanTram; // giảm bao nhiêu %
    private LocalDate ngayBD;
    private LocalDate ngayKT;
    private boolean trangThai; // true = đang bật

    public KhuyenMai() {
    }

    public KhuyenMai(String maKM, String tenKM, int phanTram, LocalDate ngayBD, LocalDate ngayKT, boolean trangThai) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.phanTram = phanTram;
        this.ngayBD = ngayBD;
        this.ngayKT = ngayKT;
        this.trangThai = trangThai;
    }

    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) { this.maKM = maKM; }
    public String getTenKM() { return tenKM; }
    public void setTenKM(String tenKM) { this.tenKM = tenKM; }
    public int getPhanTram() { return phanTram; }
    public void setPhanTram(int phanTram) { this.phanTram = phanTram; }
    public LocalDate getNgayBD() { return ngayBD; }
    public void setNgayBD(LocalDate ngayBD) { this.ngayBD = ngayBD; }
    public LocalDate getNgayKT() { return ngayKT; }
    public void setNgayKT(LocalDate ngayKT) { this.ngayKT = ngayKT; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    // JComboBox hiển thị: "Giảm 10% toàn menu (-10%)". Mục "Không áp dụng" có phanTram = 0
    @Override
    public String toString() {
        return phanTram > 0 ? tenKM + " (-" + phanTram + "%)" : tenKM;
    }
}

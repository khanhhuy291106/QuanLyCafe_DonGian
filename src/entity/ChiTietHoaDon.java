package entity;

public class ChiTietHoaDon {
    private Mon mon;
    private int soLuong;
    private double donGia;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(Mon mon, int soLuong, double donGia) {
        this.mon = mon;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public double thanhTien() {
        return soLuong * donGia;
    }

    public Mon getMon() { return mon; }
    public void setMon(Mon mon) { this.mon = mon; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }
}

package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon {
    public static final String HOAN_THANH = "HOAN_THANH";
    public static final String DA_HUY = "DA_HUY";

    private String maHD;
    private LocalDateTime ngayLap;
    private NhanVien nhanVien;
    private KhuyenMai khuyenMai; // null nếu không áp dụng khuyến mãi
    private double giamGia; // số tiền được giảm
    private double tongTien; // số tiền khách phải trả (đã trừ giảm giá)
    private Double tienNhan; // tiền khách đưa; null với hóa đơn cũ chưa lưu thông tin này
    private String trangThai = HOAN_THANH;
    private List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();

    public HoaDon() {
    }

    public static String tenTrangThai(String trangThai) {
        return DA_HUY.equals(trangThai) ? "Đã hủy" : "Hoàn thành";
    }

    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }
    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }
    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
    public KhuyenMai getKhuyenMai() { return khuyenMai; }
    public void setKhuyenMai(KhuyenMai khuyenMai) { this.khuyenMai = khuyenMai; }
    public double getGiamGia() { return giamGia; }
    public void setGiamGia(double giamGia) { this.giamGia = giamGia; }
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
    public Double getTienNhan() { return tienNhan; }
    public void setTienNhan(Double tienNhan) { this.tienNhan = tienNhan; }
    public Double getTienThua() { return tienNhan == null ? null : tienNhan - tongTien; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public List<ChiTietHoaDon> getDsChiTiet() { return dsChiTiet; }
    public void setDsChiTiet(List<ChiTietHoaDon> dsChiTiet) { this.dsChiTiet = dsChiTiet; }
}

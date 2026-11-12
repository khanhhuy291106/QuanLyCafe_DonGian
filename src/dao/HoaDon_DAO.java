package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import connectDB.ConnectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhuyenMai;
import entity.Mon;
import entity.NhanVien;

public class HoaDon_DAO {

    // Lưu hóa đơn + các dòng chi tiết trong một transaction: lỗi thì hủy hết, không để hóa đơn dở dang
    public void them(HoaDon hd) throws SQLException {
        Connection con = ConnectDB.getConnection();
        hd.setMaHD(TaoMa.taoMa("HoaDon", "maHD", "HD", 4));
        String sqlHD = "INSERT INTO HoaDon(maHD, ngayLap, maNV, maKM, giamGia, tongTien, tienNhan, trangThai) VALUES (?,?,?,?,?,?,?,?)";
        String sqlCT = "INSERT INTO ChiTietHoaDon(maHD, maMon, soLuong, donGia) VALUES (?,?,?,?)";
        try {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sqlHD)) {
                ps.setString(1, hd.getMaHD());
                ps.setTimestamp(2, Timestamp.valueOf(hd.getNgayLap()));
                ps.setString(3, hd.getNhanVien().getMaNV());
                if (hd.getKhuyenMai() == null) {
                    ps.setNull(4, Types.VARCHAR);
                } else {
                    ps.setString(4, hd.getKhuyenMai().getMaKM());
                }
                ps.setDouble(5, hd.getGiamGia());
                ps.setDouble(6, hd.getTongTien());
                ps.setDouble(7, hd.getTienNhan());
                ps.setString(8, HoaDon.HOAN_THANH);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(sqlCT)) {
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                    ps.setString(1, hd.getMaHD());
                    ps.setString(2, ct.getMon().getMaMon());
                    ps.setInt(3, ct.getSoLuong());
                    ps.setDouble(4, ct.getDonGia());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    // Hóa đơn lập từ ngày tuNgay đến hết ngày denNgay
    public List<HoaDon> layTheoNgay(LocalDate tuNgay, LocalDate denNgay) throws SQLException {
        List<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT h.maHD, h.ngayLap, h.giamGia, h.tongTien, h.tienNhan, h.trangThai, nv.maNV, nv.hoTen, km.maKM, km.tenKM "
                + "FROM HoaDon h JOIN NhanVien nv ON h.maNV = nv.maNV "
                + "LEFT JOIN KhuyenMai km ON h.maKM = km.maKM "
                + "WHERE h.ngayLap >= ? AND h.ngayLap < ? ORDER BY h.ngayLap DESC";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(tuNgay.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(denNgay.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HoaDon hd = new HoaDon();
                    hd.setMaHD(rs.getString("maHD"));
                    hd.setNgayLap(rs.getTimestamp("ngayLap").toLocalDateTime());
                    hd.setGiamGia(rs.getDouble("giamGia"));
                    hd.setTongTien(rs.getDouble("tongTien"));
                    double tienNhan = rs.getDouble("tienNhan");
                    hd.setTienNhan(rs.wasNull() ? null : tienNhan);
                    hd.setTrangThai(rs.getString("trangThai"));
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("maNV"));
                    nv.setHoTen(rs.getString("hoTen"));
                    hd.setNhanVien(nv);
                    if (rs.getString("maKM") != null) {
                        KhuyenMai km = new KhuyenMai();
                        km.setMaKM(rs.getString("maKM"));
                        km.setTenKM(rs.getString("tenKM"));
                        hd.setKhuyenMai(km);
                    }
                    ds.add(hd);
                }
            }
        }
        return ds;
    }

    public List<ChiTietHoaDon> layChiTiet(String maHD) throws SQLException {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        String sql = "SELECT m.maMon, m.tenMon, ct.soLuong, ct.donGia "
                + "FROM ChiTietHoaDon ct JOIN Mon m ON ct.maMon = m.maMon WHERE ct.maHD = ? ORDER BY ct.maCT";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mon mon = new Mon();
                    mon.setMaMon(rs.getString("maMon"));
                    mon.setTenMon(rs.getString("tenMon"));
                    ds.add(new ChiTietHoaDon(mon, rs.getInt("soLuong"), rs.getDouble("donGia")));
                }
            }
        }
        return ds;
    }

    // Hủy đơn: chỉ đổi trạng thái, không xóa dữ liệu
    public void huy(String maHD) throws SQLException {
        String sql = "UPDATE HoaDon SET trangThai = 'DA_HUY' WHERE maHD = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHD);
            ps.executeUpdate();
        }
    }

    // Thống kê doanh thu theo ngày (không tính đơn đã hủy). Mỗi dòng: {LocalDate ngày, Integer số đơn, Double doanh thu}
    public List<Object[]> thongKeTheoNgay(LocalDate tuNgay, LocalDate denNgay) throws SQLException {
        List<Object[]> ds = new ArrayList<>();
        String sql = "SELECT CAST(ngayLap AS DATE) AS ngay, COUNT(*) AS soDon, SUM(tongTien) AS doanhThu "
                + "FROM HoaDon WHERE trangThai = 'HOAN_THANH' AND ngayLap >= ? AND ngayLap < ? "
                + "GROUP BY CAST(ngayLap AS DATE) ORDER BY ngay";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(tuNgay.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(denNgay.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(new Object[] { rs.getDate("ngay").toLocalDate(), rs.getInt("soDon"),
                            rs.getDouble("doanhThu") });
                }
            }
        }
        return ds;
    }
}

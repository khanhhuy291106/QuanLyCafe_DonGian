package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import connectDB.ConnectDB;
import entity.LoaiMon;
import entity.Mon;

public class Mon_DAO {
    private static final String SELECT = "SELECT m.maMon, m.tenMon, m.gia, m.hinhAnh, m.trangThai, l.maLoai, l.tenLoai "
            + "FROM Mon m JOIN LoaiMon l ON m.maLoai = l.maLoai ";

    private List<Mon> docDanhSach(String sql) throws SQLException {
        List<Mon> ds = new ArrayList<>();
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoaiMon loai = new LoaiMon(rs.getString("maLoai"), rs.getString("tenLoai"));
                ds.add(new Mon(rs.getString("maMon"), rs.getString("tenMon"), rs.getDouble("gia"), loai,
                        rs.getString("hinhAnh"), rs.getBoolean("trangThai")));
            }
        }
        return ds;
    }

    public List<Mon> layTatCa() throws SQLException {
        return docDanhSach(SELECT + "ORDER BY m.maMon");
    }

    // Chỉ các món đang bán (dùng cho màn hình bán hàng)
    public List<Mon> layDangBan() throws SQLException {
        return docDanhSach(SELECT + "WHERE m.trangThai = 1 ORDER BY m.maMon");
    }

    public void them(Mon mon) throws SQLException {
        mon.setMaMon(TaoMa.taoMa("Mon", "maMon", "MON", 3));
        String sql = "INSERT INTO Mon(maMon, tenMon, gia, maLoai, hinhAnh, trangThai) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, mon.getMaMon());
            ps.setString(2, mon.getTenMon());
            ps.setDouble(3, mon.getGia());
            ps.setString(4, mon.getLoai().getMaLoai());
            ps.setString(5, mon.getHinhAnh());
            ps.setBoolean(6, mon.isTrangThai());
            ps.executeUpdate();
        }
    }

    public void sua(Mon mon) throws SQLException {
        String sql = "UPDATE Mon SET tenMon=?, gia=?, maLoai=?, hinhAnh=?, trangThai=? WHERE maMon=?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, mon.getTenMon());
            ps.setDouble(2, mon.getGia());
            ps.setString(3, mon.getLoai().getMaLoai());
            ps.setString(4, mon.getHinhAnh());
            ps.setBoolean(5, mon.isTrangThai());
            ps.setString(6, mon.getMaMon());
            ps.executeUpdate();
        }
    }

    // Không xóa dữ liệu đã được bảng khác tham chiếu; bảo toàn lịch sử bán hàng.
    public boolean dangDuocSuDung(String ma) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ChiTietHoaDon WHERE maMon = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void xoa(String maMon) throws SQLException {
        if (dangDuocSuDung(maMon)) {
            throw new SQLException("Dữ liệu đã được sử dụng. Chuyển trạng thái món sang Ngừng bán thay vì xóa.", "23503");
        }
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement("DELETE FROM Mon WHERE maMon = ?")) {
            ps.setString(1, maMon);
            ps.executeUpdate();
        }
    }
}

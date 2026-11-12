package dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import connectDB.ConnectDB;
import entity.KhuyenMai;

public class KhuyenMai_DAO {

    private List<KhuyenMai> docDanhSach(String sql) throws SQLException {
        List<KhuyenMai> ds = new ArrayList<>();
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(new KhuyenMai(rs.getString("maKM"), rs.getString("tenKM"), rs.getInt("phanTram"),
                        rs.getDate("ngayBD").toLocalDate(), rs.getDate("ngayKT").toLocalDate(),
                        rs.getBoolean("trangThai")));
            }
        }
        return ds;
    }

    public List<KhuyenMai> layTatCa() throws SQLException {
        return docDanhSach("SELECT * FROM KhuyenMai ORDER BY maKM");
    }

    // Khuyến mãi đang bật và hôm nay nằm trong khoảng ngày áp dụng (dùng cho màn hình bán hàng)
    public List<KhuyenMai> layDangApDung() throws SQLException {
        return docDanhSach("SELECT * FROM KhuyenMai WHERE trangThai = 1 "
                + "AND CAST(GETDATE() AS DATE) BETWEEN ngayBD AND ngayKT ORDER BY maKM");
    }

    public void them(KhuyenMai km) throws SQLException {
        km.setMaKM(TaoMa.taoMa("KhuyenMai", "maKM", "KM", 3));
        String sql = "INSERT INTO KhuyenMai(maKM, tenKM, phanTram, ngayBD, ngayKT, trangThai) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, km.getMaKM());
            ps.setString(2, km.getTenKM());
            ps.setInt(3, km.getPhanTram());
            ps.setDate(4, Date.valueOf(km.getNgayBD()));
            ps.setDate(5, Date.valueOf(km.getNgayKT()));
            ps.setBoolean(6, km.isTrangThai());
            ps.executeUpdate();
        }
    }

    public void sua(KhuyenMai km) throws SQLException {
        String sql = "UPDATE KhuyenMai SET tenKM=?, phanTram=?, ngayBD=?, ngayKT=?, trangThai=? WHERE maKM=?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, km.getTenKM());
            ps.setInt(2, km.getPhanTram());
            ps.setDate(3, Date.valueOf(km.getNgayBD()));
            ps.setDate(4, Date.valueOf(km.getNgayKT()));
            ps.setBoolean(5, km.isTrangThai());
            ps.setString(6, km.getMaKM());
            ps.executeUpdate();
        }
    }

    // Không xóa dữ liệu đã được bảng khác tham chiếu; bảo toàn lịch sử bán hàng.
    public boolean dangDuocSuDung(String ma) throws SQLException {
        String sql = "SELECT COUNT(*) FROM HoaDon WHERE maKM = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void xoa(String maKM) throws SQLException {
        if (dangDuocSuDung(maKM)) {
            throw new SQLException("Dữ liệu đã được sử dụng. Tắt khuyến mãi thay vì xóa.", "23503");
        }
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement("DELETE FROM KhuyenMai WHERE maKM = ?")) {
            ps.setString(1, maKM);
            ps.executeUpdate();
        }
    }
}

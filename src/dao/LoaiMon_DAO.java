package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import connectDB.ConnectDB;
import entity.LoaiMon;

public class LoaiMon_DAO {

    public List<LoaiMon> layTatCa() throws SQLException {
        List<LoaiMon> ds = new ArrayList<>();
        String sql = "SELECT maLoai, tenLoai FROM LoaiMon ORDER BY maLoai";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(new LoaiMon(rs.getString("maLoai"), rs.getString("tenLoai")));
            }
        }
        return ds;
    }

    public void them(LoaiMon loai) throws SQLException {
        loai.setMaLoai(TaoMa.taoMa("LoaiMon", "maLoai", "L", 2));
        try (PreparedStatement ps = ConnectDB.getConnection()
                .prepareStatement("INSERT INTO LoaiMon(maLoai, tenLoai) VALUES (?,?)")) {
            ps.setString(1, loai.getMaLoai());
            ps.setString(2, loai.getTenLoai());
            ps.executeUpdate();
        }
    }

    public void sua(LoaiMon loai) throws SQLException {
        try (PreparedStatement ps = ConnectDB.getConnection()
                .prepareStatement("UPDATE LoaiMon SET tenLoai = ? WHERE maLoai = ?")) {
            ps.setString(1, loai.getTenLoai());
            ps.setString(2, loai.getMaLoai());
            ps.executeUpdate();
        }
    }

    // Không xóa dữ liệu đã được bảng khác tham chiếu; bảo toàn lịch sử bán hàng.
    public boolean dangDuocSuDung(String ma) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Mon WHERE maLoai = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void xoa(String maLoai) throws SQLException {
        if (dangDuocSuDung(maLoai)) {
            throw new SQLException("Dữ liệu đã được sử dụng. Chuyển món sang loại khác trước khi xóa loại này.", "23503");
        }
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement("DELETE FROM LoaiMon WHERE maLoai = ?")) {
            ps.setString(1, maLoai);
            ps.executeUpdate();
        }
    }
}

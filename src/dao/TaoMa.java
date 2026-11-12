package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import connectDB.ConnectDB;

// Sinh mã tự động: lấy mã lớn nhất hiện có rồi cộng 1. Ví dụ taoMa("LoaiMon", "maLoai", "L", 2) -> L05
public class TaoMa {
    public static String taoMa(String bang, String cot, String tienTo, int soChuSo) throws SQLException {
        String sql = "SELECT MAX(" + cot + ") FROM " + bang + " WHERE " + cot + " LIKE ?";
        int so = 0;
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, tienTo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString(1) != null) {
                    so = Integer.parseInt(rs.getString(1).substring(tienTo.length()));
                }
            }
        }
        return tienTo + String.format("%0" + soChuSo + "d", so + 1);
    }
}

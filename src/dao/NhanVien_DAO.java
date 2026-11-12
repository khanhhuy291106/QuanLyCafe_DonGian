package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import connectDB.ConnectDB;
import entity.NhanVien;

public class NhanVien_DAO {

    private NhanVien taoNhanVien(ResultSet rs) throws SQLException {
        return new NhanVien(rs.getString("maNV"), rs.getString("hoTen"), rs.getBoolean("gioiTinh"),
                rs.getString("sdt"), rs.getString("tenDangNhap"), rs.getString("matKhau"),
                rs.getString("chucVu"), rs.getBoolean("trangThai"));
    }

    // Trả về nhân viên nếu đúng tên đăng nhập + mật khẩu và đang làm việc, ngược lại trả về null
    public NhanVien dangNhap(String tenDangNhap, String matKhau) throws SQLException {
        String sql = "SELECT * FROM NhanVien WHERE tenDangNhap = ? AND matKhau = ? AND trangThai = 1";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? taoNhanVien(rs) : null;
            }
        }
    }

    public List<NhanVien> layTatCa() throws SQLException {
        List<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY maNV";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(taoNhanVien(rs));
            }
        }
        return ds;
    }

    public void them(NhanVien nv) throws SQLException {
        nv.setMaNV(TaoMa.taoMa("NhanVien", "maNV", "NV", 3));
        String sql = "INSERT INTO NhanVien(maNV, hoTen, gioiTinh, sdt, tenDangNhap, matKhau, chucVu, trangThai) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setBoolean(3, nv.isGioiTinh());
            ps.setString(4, nv.getSdt());
            ps.setString(5, nv.getTenDangNhap());
            ps.setString(6, nv.getMatKhau());
            ps.setString(7, nv.getChucVu());
            ps.setBoolean(8, nv.isTrangThai());
            ps.executeUpdate();
        }
    }

    public void sua(NhanVien nv) throws SQLException {
        String sql = "UPDATE NhanVien SET hoTen=?, gioiTinh=?, sdt=?, tenDangNhap=?, matKhau=?, chucVu=?, trangThai=? WHERE maNV=?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            ps.setBoolean(2, nv.isGioiTinh());
            ps.setString(3, nv.getSdt());
            ps.setString(4, nv.getTenDangNhap());
            ps.setString(5, nv.getMatKhau());
            ps.setString(6, nv.getChucVu());
            ps.setBoolean(7, nv.isTrangThai());
            ps.setString(8, nv.getMaNV());
            ps.executeUpdate();
        }
    }

    // Không xóa dữ liệu đã được bảng khác tham chiếu; bảo toàn lịch sử bán hàng.
    public boolean dangDuocSuDung(String ma) throws SQLException {
        String sql = "SELECT COUNT(*) FROM HoaDon WHERE maNV = ?";
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void xoa(String maNV) throws SQLException {
        if (dangDuocSuDung(maNV)) {
            throw new SQLException("Dữ liệu đã được sử dụng. Chuyển trạng thái nhân viên sang Nghỉ việc thay vì xóa.", "23503");
        }
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement("DELETE FROM NhanVien WHERE maNV = ?")) {
            ps.setString(1, maNV);
            ps.executeUpdate();
        }
    }
}

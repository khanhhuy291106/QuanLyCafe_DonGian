package gui;

import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import connectDB.ConnectDB;

public class Main {
    public static void main(String[] args) {
        try {
            ConnectDB.getInstance().connect();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Không kết nối được SQL Server.\nKiểm tra lại thông tin trong connectDB/ConnectDB.java "
                            + "và chắc chắn SQL Server đang chạy.\n(" + e.getMessage() + ")",
                    "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        TienIch.caiDatGiaoDien();
        SwingUtilities.invokeLater(() -> new DangNhap().setVisible(true));
    }
}

package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Kết nối SQL Server. Sửa url / username / pwd cho đúng máy của bạn.
public class ConnectDB {
    private static Connection con = null;
    private static ConnectDB instance = new ConnectDB();

    public static ConnectDB getInstance() {
        return instance;
    }

    public void connect() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Nếu dùng instance có tên thì viết: localhost\SQLEXPRESS (bỏ :1433)
        String url = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyCafe_DonGian;encrypt=true;trustServerCertificate=true";
        String username = "sa";
        String pwd = "sapassword";
        con = DriverManager.getConnection(url, username, pwd);
    }

    public void disconnect() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Connection getConnection() {
        return con;
    }
}

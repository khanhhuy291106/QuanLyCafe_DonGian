package gui;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import connectDB.ConnectDB;
import dao.NhanVien_DAO;
import entity.NhanVien;

public class DangNhap extends JFrame {
    private JTextField txtTenDN = new JTextField();
    private JPasswordField txtMatKhau = new JPasswordField();
    private NhanVien_DAO nvDao = new NhanVien_DAO();

    public DangNhap() {
        setTitle("Đăng nhập");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lblTieuDe = new JLabel("QUẢN LÝ QUÁN CAFE", SwingConstants.CENTER);
        lblTieuDe.setFont(TienIch.FONT_TIEU_DE);
        lblTieuDe.setForeground(TienIch.MAU_CHINH);
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel form = new JPanel(new GridLayout(2, 1, 0, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        form.add(TienIch.taoDong("Tên đăng nhập:", txtTenDN));
        form.add(TienIch.taoDong("Mật khẩu:", txtMatKhau));

        JButton btnDangNhap = TienIch.taoNut("Đăng nhập");
        JButton btnThoat = TienIch.taoNut("Thoát");
        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        pnlNut.add(btnDangNhap);
        pnlNut.add(btnThoat);

        add(lblTieuDe, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(pnlNut, BorderLayout.SOUTH);

        btnDangNhap.addActionListener(e -> dangNhap());
        txtMatKhau.addActionListener(e -> dangNhap()); // nhấn Enter ở ô mật khẩu
        btnThoat.addActionListener(e -> {
            ConnectDB.getInstance().disconnect();
            System.exit(0);
        });
    }

    private void dangNhap() {
        String tenDN = txtTenDN.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());
        if (tenDN.isEmpty() || matKhau.isEmpty()) {
            TienIch.canhBao(this, "Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }
        try {
            NhanVien nv = nvDao.dangNhap(tenDN, matKhau);
            if (nv == null) {
                TienIch.canhBao(this, "Sai tên đăng nhập, sai mật khẩu hoặc tài khoản đã nghỉ việc.");
                return;
            }
            new GiaoDienChinh(nv).setVisible(true);
            dispose();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }
}

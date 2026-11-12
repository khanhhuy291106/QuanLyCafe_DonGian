package gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import connectDB.ConnectDB;
import entity.NhanVien;

// Khung chính: các chức năng là các tab. Quản lý thấy tất cả, Nhân viên chỉ thấy Bán hàng và Hóa đơn.
public class GiaoDienChinh extends JFrame {
    private JTabbedPane tabs = new JTabbedPane();

    public GiaoDienChinh(NhanVien nv) {
        setTitle("Quản lý quán cafe");
        setSize(1150, 700);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Thanh trên: lời chào + nút đăng xuất
        JLabel lblChao = new JLabel("Xin chào, " + nv.getHoTen() + " (" + NhanVien.tenChucVu(nv.getChucVu()) + ")");
        lblChao.setFont(TienIch.FONT_DAM);
        lblChao.setForeground(Color.WHITE);
        JButton btnDangXuat = new JButton("Đăng xuất");
        JPanel pnlTren = new JPanel(new BorderLayout());
        pnlTren.setBackground(TienIch.MAU_CHINH);
        pnlTren.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        pnlTren.add(lblChao, BorderLayout.WEST);
        pnlTren.add(btnDangXuat, BorderLayout.EAST);

        // Các tab theo quyền
        tabs.setFont(TienIch.FONT_DAM);
        tabs.addTab("Bán hàng", new TabBanHang(nv));
        tabs.addTab("Hóa đơn", new TabHoaDon());
        if (nv.laQuanLy()) {
            tabs.addTab("Thực đơn", new TabThucDon());
            tabs.addTab("Nhân viên", new TabNhanVien(nv));
            tabs.addTab("Khuyến mãi", new TabKhuyenMai());
            tabs.addTab("Thống kê", new TabThongKe());
        }
        // Chuyển tab thì nạp lại dữ liệu mới nhất
        tabs.addChangeListener(e -> {
            Component c = tabs.getSelectedComponent();
            if (c instanceof TaiLai) {
                ((TaiLai) c).taiLai();
            }
        });

        // Alt+1..6 chuyển tab; Ctrl+F chuyển con trỏ đến ô tìm kiếm của tab hiện tại.
        JRootPane root = getRootPane();
        for (int i = 0; i < tabs.getTabCount() && i < 9; i++) {
            final int viTri = i;
            String key = "tab" + i;
            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1 + i, java.awt.event.InputEvent.ALT_DOWN_MASK), key);
            root.getActionMap().put(key, new AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) { tabs.setSelectedIndex(viTri); }
            });
        }
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK), "tim");
        root.getActionMap().put("tim", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                focusTimKiem(tabs.getSelectedComponent());
            }
        });

        add(pnlTren, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        btnDangXuat.addActionListener(e -> {
            if (TienIch.xacNhan(this, "Đăng xuất khỏi tài khoản?")) {
                dispose();
                new DangNhap().setVisible(true);
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (TienIch.xacNhan(GiaoDienChinh.this, "Thoát chương trình?")) {
                    ConnectDB.getInstance().disconnect();
                    System.exit(0);
                }
            }
        });
    }
    private boolean focusTimKiem(Component root) {
        if (root instanceof JTextField && "timKiem".equals(root.getName())) {
            root.requestFocusInWindow();
            ((JTextField) root).selectAll();
            return true;
        }
        if (root instanceof Container) {
            for (Component c : ((Container) root).getComponents()) {
                if (focusTimKiem(c)) return true;
            }
        }
        return false;
    }
}

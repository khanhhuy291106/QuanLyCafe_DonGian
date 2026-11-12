package gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

// Các hàm và hằng số dùng chung cho giao diện (font, màu, tạo nút, tạo bảng, báo lỗi...)
public class TienIch {
    public static final Font FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_DAM = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TIEU_DE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Color MAU_CHINH = new Color(111, 78, 55); // nâu cà phê
    public static final Color MAU_NEN = new Color(250, 245, 240);

    public static final String THU_MUC_ANH = "image/mon"; // ảnh món nằm trong thư mục này (tính từ thư mục gốc project)

    public static final DateTimeFormatter NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DecimalFormat DINH_DANG_TIEN = new DecimalFormat("#,##0");

    // Gọi một lần trong Main: font chung, màu nền, chữ tiếng Việt cho hộp thoại
    public static void caiDatGiaoDien() {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object k = keys.nextElement();
            if (UIManager.get(k) instanceof FontUIResource) {
                UIManager.put(k, new FontUIResource(FONT));
            }
        }
        UIManager.put("Panel.background", MAU_NEN);
        UIManager.put("OptionPane.background", MAU_NEN);
        UIManager.put("OptionPane.yesButtonText", "Có");
        UIManager.put("OptionPane.noButtonText", "Không");
        UIManager.put("OptionPane.okButtonText", "Đồng ý");
        UIManager.put("OptionPane.cancelButtonText", "Hủy");
    }

    // Tự cập nhật bảng ngay khi nhập từ khóa tìm kiếm.
    public static javax.swing.event.DocumentListener theoDoiTimKiem(Runnable thaoTac) {
        return new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { thaoTac.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { thaoTac.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { thaoTac.run(); }
        };
    }

    // Đọc ảnh món từ thư mục image/mon và thu nhỏ vừa khung rong x cao (giữ đúng tỉ lệ). Không có ảnh thì trả về null.
    public static ImageIcon docAnh(String tenFile, int rong, int cao) {
        if (tenFile == null || tenFile.isEmpty()) {
            return null;
        }
        try {
            BufferedImage anh = ImageIO.read(new File(THU_MUC_ANH, tenFile));
            if (anh == null) {
                return null;
            }
            double tiLe = Math.min((double) rong / anh.getWidth(), (double) cao / anh.getHeight());
            int w = Math.max(1, (int) (anh.getWidth() * tiLe));
            int h = Math.max(1, (int) (anh.getHeight() * tiLe));
            return new ImageIcon(anh.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            return null; // file bị xóa hoặc hỏng -> coi như chưa có ảnh
        }
    }

    // Hiện ảnh lên JLabel; không có ảnh thì hiện chữ "Chưa có ảnh"
    public static void hienAnh(JLabel lbl, String tenFile, int rong, int cao) {
        ImageIcon icon = docAnh(tenFile, rong, cao);
        lbl.setIcon(icon);
        lbl.setText(icon == null ? "Chưa có ảnh" : "");
    }

    public static String dinhDangTien(double so) {
        return DINH_DANG_TIEN.format(so) + " đ";
    }

    public static JButton taoNut(String ten) {
        JButton nut = new JButton(ten);
        nut.setFont(FONT_DAM);
        nut.setBackground(MAU_CHINH);
        nut.setForeground(Color.WHITE);
        nut.setFocusPainted(false);
        return nut;
    }

    // Bảng chỉ để xem, chọn được 1 dòng
    public static DefaultTableModel taoModel(String... cot) {
        return new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static JTable taoBang(DefaultTableModel model) {
        JTable bang = new JTable(model);
        bang.setRowHeight(26);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bang.getTableHeader().setFont(FONT_DAM);
        bang.getTableHeader().setReorderingAllowed(false);
        return bang;
    }

    // Một dòng của form: nhãn bên trái, ô nhập bên phải
    public static JPanel taoDong(String nhan, JComponent oNhap) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel(nhan);
        lbl.setPreferredSize(new Dimension(120, 28));
        p.add(lbl, BorderLayout.WEST);
        p.add(oNhap, BorderLayout.CENTER);
        return p;
    }

    // Ô chọn ngày (dd/MM/yyyy) dùng JSpinner có sẵn của Swing
    public static JSpinner taoChonNgay(LocalDate macDinh) {
        Date d = Date.from(macDinh.atStartOfDay(ZoneId.systemDefault()).toInstant());
        JSpinner sp = new JSpinner(new SpinnerDateModel(d, null, null, java.util.Calendar.DAY_OF_MONTH));
        sp.setEditor(new JSpinner.DateEditor(sp, "dd/MM/yyyy"));
        return sp;
    }

    public static LocalDate layNgay(JSpinner sp) {
        return ((Date) sp.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static void setNgay(JSpinner sp, LocalDate ngay) {
        sp.setValue(Date.from(ngay.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public static void thongBao(Component cha, String noiDung) {
        JOptionPane.showMessageDialog(cha, noiDung, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void canhBao(Component cha, String noiDung) {
        JOptionPane.showMessageDialog(cha, noiDung, "Chú ý", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean xacNhan(Component cha, String noiDung) {
        return JOptionPane.showConfirmDialog(cha, noiDung, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // Báo lỗi SQL bằng câu dễ hiểu (547 = vướng khóa ngoại, 2627/2601 = trùng dữ liệu)
    public static void baoLoi(Component cha, SQLException e) {
        e.printStackTrace();
        String nd;
        if (e.getErrorCode() == 547) {
            nd = "Dữ liệu này đang được dùng ở nơi khác (ví dụ đã có hóa đơn) nên không thể xóa.\n"
                    + "Hãy chuyển trạng thái sang \"Ngừng\" / \"Tắt\" thay vì xóa.";
        } else if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
            nd = "Dữ liệu bị trùng (tên hoặc tên đăng nhập đã tồn tại).";
        } else {
            nd = "Lỗi cơ sở dữ liệu: " + e.getMessage();
        }
        JOptionPane.showMessageDialog(cha, nd, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

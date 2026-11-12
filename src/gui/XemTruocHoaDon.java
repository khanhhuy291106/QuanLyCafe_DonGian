package gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.*;
import entity.ChiTietHoaDon;
import entity.HoaDon;

// Phieu thanh toan hien tren man hinh theo giao dien cua MinMart.
public class XemTruocHoaDon extends JDialog {
    private final HoaDon hoaDon;
    private final List<ChiTietHoaDon> chiTiet;
    private final JPanel pnlHoaDon = new JPanel();
    private static final int RONG_PHIEU = 460;

    public XemTruocHoaDon(Window cha, HoaDon hoaDon, List<ChiTietHoaDon> chiTiet) throws IOException {
        super(cha, "Phiếu thanh toán - " + hoaDon.getMaHD(), ModalityType.APPLICATION_MODAL);
        this.hoaDon = hoaDon;
        this.chiTiet = chiTiet;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        taoGiaoDien();
        setSize(600, 770);
        setMinimumSize(new Dimension(520, 480));
        setLocationRelativeTo(cha);
    }

    private void taoGiaoDien() {
        pnlHoaDon.setLayout(new BoxLayout(pnlHoaDon, BoxLayout.Y_AXIS));
        pnlHoaDon.setBackground(Color.WHITE);
        pnlHoaDon.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        pnlHoaDon.setPreferredSize(null);
        pnlHoaDon.add(nhanGiua("QUÁN CÀ PHÊ", new Font("Arial", Font.BOLD, 26)));
        pnlHoaDon.add(Box.createVerticalStrut(6));
        pnlHoaDon.add(nhanGiua("Cảm ơn quý khách đã sử dụng dịch vụ", new Font("Arial", Font.PLAIN, 13)));
        pnlHoaDon.add(Box.createVerticalStrut(16));
        pnlHoaDon.add(duongKe());
        pnlHoaDon.add(Box.createVerticalStrut(16));
        pnlHoaDon.add(nhanGiua("PHIẾU THANH TOÁN", new Font("Arial", Font.BOLD, 22)));
        pnlHoaDon.add(Box.createVerticalStrut(20));
        pnlHoaDon.add(dongThongTin("Mã hóa đơn:", hoaDon.getMaHD()));
        pnlHoaDon.add(dongThongTin("Ngày lập:", hoaDon.getNgayLap() == null ? "" : hoaDon.getNgayLap().format(TienIch.NGAY_GIO)));
        pnlHoaDon.add(dongThongTin("Nhân viên:", hoaDon.getNhanVien() == null ? "" : hoaDon.getNhanVien().getHoTen()));
        pnlHoaDon.add(dongThongTin("Trạng thái:", HoaDon.tenTrangThai(hoaDon.getTrangThai())));
        pnlHoaDon.add(Box.createVerticalStrut(15));
        pnlHoaDon.add(duongKe());
        pnlHoaDon.add(Box.createVerticalStrut(10));

        pnlHoaDon.add(dongCot("MÓN", "SL", "ĐƠN GIÁ", "THÀNH TIỀN", true));
        pnlHoaDon.add(Box.createVerticalStrut(8));
        double tongMon = 0;
        if (chiTiet != null) {
            for (ChiTietHoaDon ct : chiTiet) {
                String tenMon = ct.getMon() == null ? "Món" : ct.getMon().getTenMon();
                JLabel ten = new JLabel("  " + tenMon);
                ten.setFont(new Font("Arial", Font.PLAIN, 14));
                ten.setAlignmentX(Component.CENTER_ALIGNMENT);
                ten.setMaximumSize(new Dimension(RONG_PHIEU, 24));
                pnlHoaDon.add(ten);
                pnlHoaDon.add(dongCot("", String.valueOf(ct.getSoLuong()),
                        TienIch.dinhDangTien(ct.getDonGia()), TienIch.dinhDangTien(ct.thanhTien()), false));
                pnlHoaDon.add(Box.createVerticalStrut(7));
                tongMon += ct.thanhTien();
            }
        }
        pnlHoaDon.add(Box.createVerticalStrut(7));
        pnlHoaDon.add(duongKe());
        pnlHoaDon.add(Box.createVerticalStrut(12));
        pnlHoaDon.add(dongTongTien("Tạm tính:", TienIch.dinhDangTien(tongMon), false));
        if (hoaDon.getKhuyenMai() != null) {
            pnlHoaDon.add(dongThongTin("Khuyến mãi:", hoaDon.getKhuyenMai().getTenKM()));
        }
        pnlHoaDon.add(dongTongTien("Giảm giá:", "-" + TienIch.dinhDangTien(hoaDon.getGiamGia()), false));
        pnlHoaDon.add(Box.createVerticalStrut(8));
        pnlHoaDon.add(dongTongTien("TỔNG THANH TOÁN:", TienIch.dinhDangTien(hoaDon.getTongTien()), true));
        pnlHoaDon.add(Box.createVerticalStrut(8));
        pnlHoaDon.add(dongTongTien("Tiền khách đưa:", hoaDon.getTienNhan() == null ? "Chưa lưu"
                : TienIch.dinhDangTien(hoaDon.getTienNhan()), false));
        pnlHoaDon.add(dongTongTien("Tiền thừa:", hoaDon.getTienThua() == null ? "Chưa lưu"
                : TienIch.dinhDangTien(hoaDon.getTienThua()), true));
        pnlHoaDon.add(Box.createVerticalStrut(18));
        pnlHoaDon.add(duongKe());
        pnlHoaDon.add(Box.createVerticalStrut(14));
        pnlHoaDon.add(nhanGiua("Cảm ơn quý khách và hẹn gặp lại!", new Font("Arial", Font.ITALIC, 14)));
        pnlHoaDon.add(Box.createVerticalStrut(15));

        JPanel khung = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        khung.setBackground(new Color(230, 232, 235));
        pnlHoaDon.setPreferredSize(new Dimension(RONG_PHIEU, pnlHoaDon.getPreferredSize().height));
        khung.add(pnlHoaDon);
        JScrollPane cuon = new JScrollPane(khung);
        cuon.getVerticalScrollBar().setUnitIncrement(20);
        add(cuon, BorderLayout.CENTER);

        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnPNG = new JButton("Lưu hóa đơn (PNG)");
        JButton btnDong = new JButton("Đóng");
        btnPNG.addActionListener(e -> luuPNG());
        btnDong.addActionListener(e -> dispose());
        pnlNut.add(btnPNG);
        pnlNut.add(btnDong);
        add(pnlNut, BorderLayout.SOUTH);
    }

    private JLabel nhanGiua(String noiDung, Font font) {
        JLabel lbl = new JLabel(noiDung, SwingConstants.CENTER);
        lbl.setFont(font);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(RONG_PHIEU, font.getSize() + 15));
        return lbl;
    }

    private JComponent duongKe() {
        JSeparator ke = new JSeparator();
        ke.setMaximumSize(new Dimension(RONG_PHIEU, 1));
        ke.setAlignmentX(Component.CENTER_ALIGNMENT);
        return ke;
    }

    private JPanel dongThongTin(String nhan, String giaTri) {
        return dongHaiCot(nhan, giaTri, false);
    }

    private JPanel dongTongTien(String nhan, String giaTri, boolean dam) {
        return dongHaiCot(nhan, giaTri, dam);
    }

    private JPanel dongHaiCot(String nhan, String giaTri, boolean dam) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(RONG_PHIEU, dam ? 34 : 27));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        Font f = new Font("Arial", dam ? Font.BOLD : Font.PLAIN, dam ? 17 : 14);
        JLabel trai = new JLabel(nhan);
        trai.setFont(f);
        JLabel phai = new JLabel(giaTri == null ? "" : giaTri);
        phai.setFont(f);
        p.add(trai, BorderLayout.WEST);
        p.add(phai, BorderLayout.EAST);
        return p;
    }

    private JPanel dongCot(String mon, String soLuong, String donGia, String thanhTien, boolean tieuDe) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(RONG_PHIEU, 25));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        String[] ds = { mon, soLuong, donGia, thanhTien };
        int[] rong = { 115, 35, 125, 140 };
        for (int i = 0; i < ds.length; i++) {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = i;
            c.gridy = 0;
            c.weightx = i == 0 ? 1 : 0;
            c.fill = GridBagConstraints.HORIZONTAL;
            JLabel lbl = new JLabel(ds[i], i == 0 ? SwingConstants.LEFT : SwingConstants.RIGHT);
            lbl.setFont(new Font("Arial", tieuDe ? Font.BOLD : Font.PLAIN, 13));
            lbl.setPreferredSize(new Dimension(rong[i], 24));
            p.add(lbl, c);
        }
        return p;
    }

    // Chup toan bo noi dung phieu, ke ca phan nam ngoai khung cuon.
    private BufferedImage taoAnhHoaDon() {
        Dimension kichThuoc = pnlHoaDon.getPreferredSize();
        int rong = Math.max(RONG_PHIEU, kichThuoc.width);
        int cao = Math.max(1, kichThuoc.height);
        BufferedImage anh = new BufferedImage(rong, cao, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = anh.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, rong, cao);
        pnlHoaDon.setSize(rong, cao);
        pnlHoaDon.doLayout();
        pnlHoaDon.printAll(g);
        g.dispose();
        return anh;
    }

    private File chonFile(String duoi) {
        JFileChooser chon = new JFileChooser();
        chon.setDialogTitle("Lưu hóa đơn " + duoi.toUpperCase(Locale.ROOT));
        chon.setSelectedFile(new File("HoaDon_" + hoaDon.getMaHD() + "." + duoi));
        if (chon.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return null;
        File file = chon.getSelectedFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith("." + duoi)) {
            file = new File(file.getAbsolutePath() + "." + duoi);
        }
        if (file.exists() && !TienIch.xacNhan(this, "Ghi đè file " + file.getName() + "?")) return null;
        return file;
    }

    private void luuPNG() {
        File file = chonFile("png");
        if (file == null) return;
        try {
            if (!ImageIO.write(taoAnhHoaDon(), "png", file)) throw new IOException("Không hỗ trợ định dạng PNG.");
            JOptionPane.showMessageDialog(this, "Đã lưu hóa đơn:\n" + file.getAbsolutePath());
        } catch (IOException ex) {
            TienIch.canhBao(this, "Không lưu được ảnh: " + ex.getMessage());
        }
    }

}

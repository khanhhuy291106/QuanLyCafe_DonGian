package gui;

import java.awt.*;
import java.sql.SQLException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.HoaDon_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;

// Xem hóa đơn theo khoảng ngày, xem chi tiết từng hóa đơn, hủy đơn.
public class TabHoaDon extends JPanel implements TaiLai {
    private HoaDon_DAO hdDao = new HoaDon_DAO();
    private JSpinner spTuNgay = TienIch.taoChonNgay(LocalDate.now().minusDays(7));
    private JSpinner spDenNgay = TienIch.taoChonNgay(LocalDate.now());
    private DefaultTableModel modelHD = TienIch.taoModel("Mã HD", "Ngày lập", "Nhân viên", "Khuyến mãi", "Giảm giá",
            "Thành tiền", "Trạng thái");
    private JTable tblHD = TienIch.taoBang(modelHD);
    private DefaultTableModel modelCT = TienIch.taoModel("Món", "Số lượng", "Đơn giá", "Thành tiền");
    private JTable tblCT = TienIch.taoBang(modelCT);
    private List<HoaDon> ds = new ArrayList<>();
    private JTextField txtTim = new JTextField(11);
    private JComboBox<String> cboTrangThai = new JComboBox<>(new String[] { "Tất cả", "Hoàn thành", "Đã hủy" });
    private List<HoaDon> dsGoc = new ArrayList<>();

    public TabHoaDon() {
        setLayout(new BorderLayout(0, 10));
        txtTim.setName("timKiem");
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JButton btnLoc = TienIch.taoNut("Lọc");
        JButton btnHuy = TienIch.taoNut("Hủy đơn đã chọn");
        JButton btnXuat = TienIch.taoNut("Xem phiếu thanh toán");
        // Chia thanh dieu khien thanh hai hang, tranh nut xuat bi khuất khi cua so hep.
        JPanel pnlBoLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        pnlBoLoc.setOpaque(false);
        pnlBoLoc.add(new JLabel("Từ ngày:"));
        pnlBoLoc.add(spTuNgay);
        pnlBoLoc.add(new JLabel("Đến ngày:"));
        pnlBoLoc.add(spDenNgay);
        pnlBoLoc.add(btnLoc);

        JPanel pnlThaoTac = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        pnlThaoTac.setOpaque(false);
        pnlThaoTac.add(new JLabel("Mã HD / nhân viên:"));
        pnlThaoTac.add(txtTim);
        pnlThaoTac.add(new JLabel("Trạng thái:"));
        pnlThaoTac.add(cboTrangThai);
        pnlThaoTac.add(btnHuy);
        pnlThaoTac.add(btnXuat);

        JPanel pnlTren = new JPanel();
        pnlTren.setOpaque(false);
        pnlTren.setLayout(new BoxLayout(pnlTren, BoxLayout.Y_AXIS));
        pnlBoLoc.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlThaoTac.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTren.add(pnlBoLoc);
        pnlTren.add(pnlThaoTac);

        JPanel pnlCT = new JPanel(new BorderLayout(0, 4));
        pnlCT.setOpaque(false);
        pnlCT.add(new JLabel("Chi tiết hóa đơn đang chọn:"), BorderLayout.NORTH);
        pnlCT.add(new JScrollPane(tblCT), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tblHD), pnlCT);
        split.setResizeWeight(0.6);

        add(pnlTren, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        btnLoc.addActionListener(e -> taiLai());
        btnHuy.addActionListener(e -> huyDon());
        btnXuat.addActionListener(e -> xemPhieuThanhToan());
        txtTim.getDocument().addDocumentListener(TienIch.theoDoiTimKiem(() -> locDuLieu()));
        cboTrangThai.addActionListener(e -> locDuLieu());
        tblHD.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiChiTiet();
            }
        });
        taiLai();
    }

    @Override
    public void taiLai() {
        LocalDate tu = TienIch.layNgay(spTuNgay);
        LocalDate den = TienIch.layNgay(spDenNgay);
        if (den.isBefore(tu)) {
            TienIch.canhBao(this, "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
            return;
        }
        try {
            dsGoc = hdDao.layTheoNgay(tu, den);
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
            return;
        }
        locDuLieu();
    }

    private void locDuLieu() {
        String tim = txtTim.getText().trim().toLowerCase(java.util.Locale.ROOT);
        ds = new ArrayList<>();
        for (HoaDon hd : dsGoc) {
            boolean dungTen = (hd.getMaHD() + " " + hd.getNhanVien().getHoTen()).toLowerCase(java.util.Locale.ROOT).contains(tim);
            boolean dungTT = cboTrangThai.getSelectedIndex() == 0
                    || (cboTrangThai.getSelectedIndex() == 1 && HoaDon.HOAN_THANH.equals(hd.getTrangThai()))
                    || (cboTrangThai.getSelectedIndex() == 2 && HoaDon.DA_HUY.equals(hd.getTrangThai()));
            if (dungTen && dungTT) ds.add(hd);
        }
        modelHD.setRowCount(0);
        modelCT.setRowCount(0);
        for (HoaDon hd : ds) {
            modelHD.addRow(new Object[] { hd.getMaHD(), hd.getNgayLap().format(TienIch.NGAY_GIO),
                    hd.getNhanVien().getHoTen(), hd.getKhuyenMai() == null ? "" : hd.getKhuyenMai().getTenKM(),
                    TienIch.dinhDangTien(hd.getGiamGia()), TienIch.dinhDangTien(hd.getTongTien()),
                    HoaDon.tenTrangThai(hd.getTrangThai()) });
        }
    }

    private void hienThiChiTiet() {
        modelCT.setRowCount(0);
        int dong = tblHD.getSelectedRow();
        if (dong < 0) {
            return;
        }
        try {
            for (ChiTietHoaDon ct : hdDao.layChiTiet(ds.get(tblHD.convertRowIndexToModel(dong)).getMaHD())) {
                modelCT.addRow(new Object[] { ct.getMon().getTenMon(), ct.getSoLuong(),
                        TienIch.dinhDangTien(ct.getDonGia()), TienIch.dinhDangTien(ct.thanhTien()) });
            }
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void xemPhieuThanhToan() {
        int dong = tblHD.getSelectedRow();
        if (dong < 0) {
            TienIch.canhBao(this, "Hãy chọn hóa đơn cần xem.");
            return;
        }
        HoaDon hd = ds.get(tblHD.convertRowIndexToModel(dong));
        try {
            XemTruocHoaDon xem = new XemTruocHoaDon(SwingUtilities.getWindowAncestor(this),
                    hd, hdDao.layChiTiet(hd.getMaHD()));
            xem.setVisible(true);
        } catch (SQLException | IOException e) {
            TienIch.canhBao(this, "Không xem được hóa đơn: " + e.getMessage());
        }
    }

    private void huyDon() {
        int dong = tblHD.getSelectedRow();
        if (dong < 0) {
            TienIch.canhBao(this, "Hãy chọn hóa đơn cần hủy.");
            return;
        }
        HoaDon hd = ds.get(tblHD.convertRowIndexToModel(dong));
        if (HoaDon.DA_HUY.equals(hd.getTrangThai())) {
            TienIch.canhBao(this, "Hóa đơn này đã được hủy rồi.");
            return;
        }
        if (!TienIch.xacNhan(this, "Hủy hóa đơn " + hd.getMaHD() + " ?")) {
            return;
        }
        try {
            hdDao.huy(hd.getMaHD());
            taiLai();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }
}

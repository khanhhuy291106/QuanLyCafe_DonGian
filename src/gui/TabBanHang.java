package gui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.HoaDon_DAO;
import dao.KhuyenMai_DAO;
import dao.LoaiMon_DAO;
import dao.Mon_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhuyenMai;
import entity.LoaiMon;
import entity.Mon;
import entity.NhanVien;

// Bán hàng: bên trái là thực đơn, bên phải là giỏ hàng. Chọn món -> thêm vào giỏ -> chọn khuyến mãi -> thanh toán.
public class TabBanHang extends JPanel implements TaiLai {
    private NhanVien nv; // nhân viên đang đăng nhập
    private Mon_DAO monDao = new Mon_DAO();
    private LoaiMon_DAO loaiDao = new LoaiMon_DAO();
    private KhuyenMai_DAO kmDao = new KhuyenMai_DAO();
    private HoaDon_DAO hdDao = new HoaDon_DAO();

    // Thực đơn (bên trái)
    private JComboBox<LoaiMon> cboLoai = new JComboBox<>();
    private JTextField txtTim = new JTextField(15);
    private DefaultTableModel modelMon = TienIch.taoModel("Mã", "Tên món", "Loại", "Giá");
    private JTable tblMon = TienIch.taoBang(modelMon);
    private JLabel lblAnh = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
    private JSpinner spSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private List<Mon> dsMon = new ArrayList<>(); // tất cả món đang bán
    private List<Mon> dsHienThi = new ArrayList<>(); // món đang hiện trong bảng (sau khi lọc)

    // Giỏ hàng (bên phải)
    private DefaultTableModel modelGio = TienIch.taoModel("Mã món", "Tên món", "SL", "Đơn giá", "Thành tiền");
    private JTable tblGio = TienIch.taoBang(modelGio);
    private List<ChiTietHoaDon> gio = new ArrayList<>();
    private JComboBox<KhuyenMai> cboKM = new JComboBox<>();
    private JLabel lblTamTinh = new JLabel("0 đ");
    private JLabel lblGiam = new JLabel("0 đ");
    private JLabel lblThanhTien = new JLabel("0 đ");
    private JTextField txtTienNhan = new JTextField(12);
    private JLabel lblTienThua = new JLabel("—");
    private double giam = 0;
    private double thanhTien = 0;

    public TabBanHang(NhanVien nv) {
        this.nv = nv;
        txtTim.setName("timKiem");
        setLayout(new GridLayout(1, 2, 12, 0));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(taoPanelMenu());
        add(taoPanelGio());

        cboLoai.addActionListener(e -> locMon());
        cboKM.addActionListener(e -> tinhTien());
        txtTienNhan.getDocument().addDocumentListener(TienIch.theoDoiTimKiem(() -> tinhTienThua()));
        tblMon.getSelectionModel().addListSelectionListener(e -> hienAnhMon());
        txtTim.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                locMon();
            }
        });
        tblMon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    themVaoGio(); // nhấp đúp vào món để thêm nhanh
                }
            }
        });
        taiLai();
    }

    private JPanel taoPanelMenu() {
        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setOpaque(false);

        JPanel pnlLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlLoc.setOpaque(false);
        pnlLoc.add(new JLabel("Loại:"));
        pnlLoc.add(cboLoai);
        pnlLoc.add(new JLabel("Tìm món:"));
        pnlLoc.add(txtTim);

        JButton btnThem = TienIch.taoNut("Thêm vào giỏ");
        btnThem.addActionListener(e -> themVaoGio());
        JPanel pnlThem = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 40));
        pnlThem.setOpaque(false);
        pnlThem.add(new JLabel("Số lượng:"));
        pnlThem.add(spSoLuong);
        pnlThem.add(btnThem);

        pnl.add(pnlLoc, BorderLayout.NORTH);
        pnl.add(new JScrollPane(tblMon), BorderLayout.CENTER);

        // Khung ảnh của món đang chọn nằm bên trái ô "Số lượng"
        lblAnh.setPreferredSize(new Dimension(120, 120));
        lblAnh.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JPanel pnlDuoi = new JPanel(new BorderLayout(10, 0));
        pnlDuoi.setOpaque(false);
        pnlDuoi.add(lblAnh, BorderLayout.WEST);
        pnlDuoi.add(pnlThem, BorderLayout.CENTER);
        pnl.add(pnlDuoi, BorderLayout.SOUTH);
        return pnl;
    }

    private JPanel taoPanelGio() {
        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setOpaque(false);

        JButton btnXoaDong = TienIch.taoNut("Xóa món đã chọn");
        JButton btnXoaGio = TienIch.taoNut("Xóa cả giỏ");
        JButton btnThanhToan = TienIch.taoNut("THANH TOÁN");
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXoaDong.addActionListener(e -> xoaMonKhoiGio());
        btnXoaGio.addActionListener(e -> {
            gio.clear();
            hienThiGio();
        });
        btnThanhToan.addActionListener(e -> thanhToan());

        lblThanhTien.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblThanhTien.setForeground(TienIch.MAU_CHINH);

        JPanel pnlNutGio = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlNutGio.setOpaque(false);
        pnlNutGio.add(btnXoaDong);
        pnlNutGio.add(btnXoaGio);

        JPanel pnlTien = new JPanel(new GridLayout(0, 1, 0, 6));
        pnlTien.setOpaque(false);
        pnlTien.add(pnlNutGio);
        pnlTien.add(TienIch.taoDong("Khuyến mãi:", cboKM));
        pnlTien.add(TienIch.taoDong("Tạm tính:", lblTamTinh));
        pnlTien.add(TienIch.taoDong("Giảm giá:", lblGiam));
        pnlTien.add(TienIch.taoDong("Thành tiền:", lblThanhTien));
        pnlTien.add(TienIch.taoDong("Tiền khách đưa:", txtTienNhan));
        pnlTien.add(TienIch.taoDong("Tiền thừa:", lblTienThua));
        pnlTien.add(btnThanhToan);

        pnl.add(new JLabel("Giỏ hàng"), BorderLayout.NORTH);
        pnl.add(new JScrollPane(tblGio), BorderLayout.CENTER);
        pnl.add(pnlTien, BorderLayout.SOUTH);
        return pnl;
    }

    // Nạp lại thực đơn, loại món, khuyến mãi mỗi khi mở tab (phòng khi Quản lý vừa sửa)
    @Override
    public void taiLai() {
        try {
            dsMon = monDao.layDangBan();

            cboLoai.removeAllItems();
            cboLoai.addItem(new LoaiMon("", "Tất cả"));
            for (LoaiMon l : loaiDao.layTatCa()) {
                cboLoai.addItem(l);
            }

            cboKM.removeAllItems();
            cboKM.addItem(new KhuyenMai(null, "Không áp dụng", 0, null, null, true));
            for (KhuyenMai km : kmDao.layDangApDung()) {
                cboKM.addItem(km);
            }
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
        locMon();
        tinhTien();
    }

    // Lọc thực đơn theo loại và từ khóa tên món
    private void locMon() {
        LoaiMon loai = (LoaiMon) cboLoai.getSelectedItem();
        String tuKhoa = txtTim.getText().trim().toLowerCase();
        modelMon.setRowCount(0);
        dsHienThi.clear();
        for (Mon m : dsMon) {
            boolean dungLoai = loai == null || loai.getMaLoai().isEmpty()
                    || m.getLoai().getMaLoai().equals(loai.getMaLoai());
            boolean dungTen = m.getTenMon().toLowerCase().contains(tuKhoa);
            if (dungLoai && dungTen) {
                dsHienThi.add(m);
                modelMon.addRow(new Object[] { m.getMaMon(), m.getTenMon(), m.getLoai().getTenLoai(),
                        TienIch.dinhDangTien(m.getGia()) });
            }
        }
    }

    // Chọn món nào thì hiện ảnh món đó
    private void hienAnhMon() {
        int dong = tblMon.getSelectedRow();
        if (dong < 0 || dong >= dsHienThi.size()) {
            TienIch.hienAnh(lblAnh, null, 110, 110);
        } else {
            TienIch.hienAnh(lblAnh, dsHienThi.get(dong).getHinhAnh(), 110, 110);
        }
    }

    private void themVaoGio() {
        int dong = tblMon.getSelectedRow();
        if (dong < 0) {
            TienIch.canhBao(this, "Hãy chọn một món trong thực đơn.");
            return;
        }
        Mon mon = dsHienThi.get(dong);
        int soLuong = (Integer) spSoLuong.getValue();
        // Món đã có trong giỏ thì cộng thêm số lượng
        for (ChiTietHoaDon ct : gio) {
            if (ct.getMon().getMaMon().equals(mon.getMaMon())) {
                if (ct.getSoLuong() + soLuong > 99) {
                    TienIch.canhBao(this, "Mỗi món tối đa 99 phần trong một hóa đơn.");
                    return;
                }
                ct.setSoLuong(ct.getSoLuong() + soLuong);
                hienThiGio();
                return;
            }
        }
        gio.add(new ChiTietHoaDon(mon, soLuong, mon.getGia()));
        hienThiGio();
    }

    private void xoaMonKhoiGio() {
        int dong = tblGio.getSelectedRow();
        if (dong < 0) {
            TienIch.canhBao(this, "Hãy chọn món cần xóa trong giỏ hàng.");
            return;
        }
        gio.remove(dong);
        hienThiGio();
    }

    private void hienThiGio() {
        modelGio.setRowCount(0);
        for (ChiTietHoaDon ct : gio) {
            modelGio.addRow(new Object[] { ct.getMon().getMaMon(), ct.getMon().getTenMon(), ct.getSoLuong(),
                    TienIch.dinhDangTien(ct.getDonGia()), TienIch.dinhDangTien(ct.thanhTien()) });
        }
        tinhTien();
    }

    // Tạm tính - giảm giá (theo % của khuyến mãi đang chọn) = thành tiền
    private void tinhTien() {
        double tamTinh = 0;
        for (ChiTietHoaDon ct : gio) {
            tamTinh += ct.thanhTien();
        }
        KhuyenMai km = (KhuyenMai) cboKM.getSelectedItem();
        giam = (km == null) ? 0 : Math.round(tamTinh * km.getPhanTram() / 100.0);
        thanhTien = tamTinh - giam;
        lblTamTinh.setText(TienIch.dinhDangTien(tamTinh));
        lblGiam.setText(TienIch.dinhDangTien(giam));
        lblThanhTien.setText(TienIch.dinhDangTien(thanhTien));
        tinhTienThua();
    }

    // Chỉ nhận số tiền VND nguyên, không chứa dấu phân cách hay ký tự khác.
    private Double layTienNhan() {
        String so = txtTienNhan.getText().trim();
        if (!so.matches("[0-9]+")) return null;
        try {
            double tien = Double.parseDouble(so);
            return Double.isFinite(tien) && tien <= 999999999999.0 ? tien : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void tinhTienThua() {
        Double tienNhan = layTienNhan();
        if (tienNhan == null) {
            lblTienThua.setText("—");
        } else if (tienNhan < thanhTien) {
            lblTienThua.setText("Chưa đủ " + TienIch.dinhDangTien(thanhTien - tienNhan));
        } else {
            lblTienThua.setText(TienIch.dinhDangTien(tienNhan - thanhTien));
        }
    }

    private void thanhToan() {
        if (gio.isEmpty()) {
            TienIch.canhBao(this, "Giỏ hàng đang trống.");
            return;
        }
        Double tienNhan = layTienNhan();
        if (tienNhan == null) {
            TienIch.canhBao(this, "Hãy nhập tiền khách đưa bằng số VND nguyên, ví dụ: 100000.");
            txtTienNhan.requestFocusInWindow();
            return;
        }
        if (tienNhan < thanhTien) {
            TienIch.canhBao(this, "Tiền khách đưa chưa đủ. Còn thiếu "
                    + TienIch.dinhDangTien(thanhTien - tienNhan));
            txtTienNhan.requestFocusInWindow();
            return;
        }
        if (!TienIch.xacNhan(this, "Thanh toán " + TienIch.dinhDangTien(thanhTien)
                + "\nTiền nhận: " + TienIch.dinhDangTien(tienNhan)
                + "\nTiền thừa: " + TienIch.dinhDangTien(tienNhan - thanhTien) + " ?")) {
            return;
        }
        KhuyenMai km = (KhuyenMai) cboKM.getSelectedItem();
        HoaDon hd = new HoaDon();
        hd.setNgayLap(LocalDateTime.now());
        hd.setNhanVien(nv);
        hd.setKhuyenMai(km != null && km.getMaKM() != null ? km : null); // "Không áp dụng" -> null
        hd.setGiamGia(giam);
        hd.setTongTien(thanhTien);
        hd.setTienNhan(tienNhan);
        hd.setDsChiTiet(new ArrayList<>(gio));
        try {
            hdDao.them(hd);
            TienIch.thongBao(this, "Thanh toán thành công!\nMã hóa đơn: " + hd.getMaHD() + "\nSố tiền: "
                    + TienIch.dinhDangTien(hd.getTongTien())
                    + "\nTiền nhận: " + TienIch.dinhDangTien(hd.getTienNhan())
                    + "\nTiền thừa: " + TienIch.dinhDangTien(hd.getTienThua()));
            gio.clear();
            txtTienNhan.setText("");
            cboKM.setSelectedIndex(0);
            hienThiGio();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }
}

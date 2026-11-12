package gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import dao.LoaiMon_DAO;
import dao.Mon_DAO;
import entity.LoaiMon;
import entity.Mon;

// Quản lý món: xem, thêm, sửa, xóa, chọn ảnh. Món đã từng bán thì không xóa được -> chuyển sang "Ngừng bán".
public class QLMon extends JPanel implements TaiLai {
    private Mon_DAO monDao = new Mon_DAO();
    private LoaiMon_DAO loaiDao = new LoaiMon_DAO();
    private DefaultTableModel model = TienIch.taoModel("Mã món", "Tên món", "Loại", "Giá", "Có ảnh", "Trạng thái");
    private JTable tbl = TienIch.taoBang(model);
    private JTextField txtMa = new JTextField("(tự động)");
    private JTextField txtTen = new JTextField();
    private JTextField txtGia = new JTextField();
    private JComboBox<LoaiMon> cboLoai = new JComboBox<>();
    private JComboBox<String> cboTrangThai = new JComboBox<>(new String[] { "Đang bán", "Ngừng bán" });
    private JLabel lblAnh = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
    private String tenAnh = null; // tên file ảnh của món đang nhập (null = chưa có)
    private List<Mon> ds = new ArrayList<>();
    private List<Mon> dsGoc = new ArrayList<>();
    private JTextField txtTim = new JTextField(17);
    private JComboBox<String> cboLoc = new JComboBox<>(new String[] { "Tất cả", "Đang bán", "Ngừng bán" });

    public QLMon() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        txtMa.setEditable(false);
        txtTim.setName("timKiem");

        JPanel form = new JPanel(new GridLayout(5, 1, 0, 8));
        form.setOpaque(false);
        form.add(TienIch.taoDong("Mã món:", txtMa));
        form.add(TienIch.taoDong("Tên món:", txtTen));
        form.add(TienIch.taoDong("Giá (đ):", txtGia));
        form.add(TienIch.taoDong("Loại món:", cboLoai));
        form.add(TienIch.taoDong("Trạng thái:", cboTrangThai));

        JButton btnThem = TienIch.taoNut("Thêm");
        JButton btnSua = TienIch.taoNut("Sửa");
        JButton btnXoa = TienIch.taoNut("Xóa");
        JButton btnMoi = TienIch.taoNut("Làm mới");
        JPanel nut = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        nut.setOpaque(false);
        nut.add(btnThem);
        nut.add(btnSua);
        nut.add(btnXoa);
        nut.add(btnMoi);

        JPanel pnlTrai = new JPanel(new BorderLayout(0, 10));
        pnlTrai.setOpaque(false);
        pnlTrai.add(form, BorderLayout.NORTH);
        pnlTrai.add(nut, BorderLayout.CENTER);

        // Khung xem trước ảnh + nút chọn / bỏ ảnh
        lblAnh.setPreferredSize(new Dimension(180, 150));
        lblAnh.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JButton btnChonAnh = TienIch.taoNut("Chọn ảnh");
        JButton btnBoAnh = TienIch.taoNut("Bỏ ảnh");
        JPanel nutAnh = new JPanel(new GridLayout(1, 2, 6, 0));
        nutAnh.setOpaque(false);
        nutAnh.add(btnChonAnh);
        nutAnh.add(btnBoAnh);
        JPanel pnlAnh = new JPanel(new BorderLayout(0, 6));
        pnlAnh.setOpaque(false);
        pnlAnh.add(lblAnh, BorderLayout.CENTER);
        pnlAnh.add(nutAnh, BorderLayout.SOUTH);

        JPanel pnlTren = new JPanel(new BorderLayout(20, 0));
        pnlTren.setOpaque(false);
        pnlTren.add(pnlTrai, BorderLayout.CENTER);
        pnlTren.add(pnlAnh, BorderLayout.EAST);
        JPanel pnlLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlLoc.add(new JLabel("Tìm mã / tên:"));
        pnlLoc.add(txtTim);
        pnlLoc.add(new JLabel("Trạng thái:"));
        pnlLoc.add(cboLoc);
        pnlTren.add(pnlLoc, BorderLayout.SOUTH);
        add(pnlTren, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> sua());
        btnXoa.addActionListener(e -> xoa());
        btnMoi.addActionListener(e -> taiLai());
        txtTim.getDocument().addDocumentListener(TienIch.theoDoiTimKiem(() -> locDuLieu()));
        cboLoc.addActionListener(e -> locDuLieu());
        btnChonAnh.addActionListener(e -> chonAnh());
        btnBoAnh.addActionListener(e -> {
            tenAnh = null;
            TienIch.hienAnh(lblAnh, null, 170, 140);
        });
        tbl.getSelectionModel().addListSelectionListener(e -> {
            int dong = tbl.getSelectedRow();
            if (!e.getValueIsAdjusting() && dong >= 0) {
                Mon m = ds.get(dong);
                txtMa.setText(m.getMaMon());
                txtTen.setText(m.getTenMon());
                txtGia.setText(String.valueOf((long) m.getGia()));
                cboLoai.setSelectedItem(m.getLoai());
                cboTrangThai.setSelectedIndex(m.isTrangThai() ? 0 : 1);
                tenAnh = m.getHinhAnh();
                TienIch.hienAnh(lblAnh, tenAnh, 170, 140);
            }
        });
        taiLai();
    }

    private void locDuLieu() {
        String tim = txtTim.getText().trim().toLowerCase(java.util.Locale.ROOT);
        ds = new ArrayList<>();
        for (Mon m : dsGoc) {
            boolean dungTim = (m.getMaMon() + " " + m.getTenMon() + " " + m.getLoai().getTenLoai()).toLowerCase(java.util.Locale.ROOT).contains(tim);
            boolean dungTT = cboLoc.getSelectedIndex() == 0
                    || (cboLoc.getSelectedIndex() == 1 && m.isTrangThai())
                    || (cboLoc.getSelectedIndex() == 2 && !(m.isTrangThai()));
            if (dungTim && dungTT) ds.add(m);
        }
        model.setRowCount(0);
        for (Mon m : ds) {

            model.addRow(new Object[] { m.getMaMon(), m.getTenMon(), m.getLoai().getTenLoai(),
                    TienIch.dinhDangTien(m.getGia()), m.getHinhAnh() == null ? "" : "Có",
                    m.isTrangThai() ? "Đang bán" : "Ngừng bán" });
        }
    }

    @Override
    public void taiLai() {
        try {
            dsGoc = monDao.layTatCa();
            cboLoai.removeAllItems();
            for (LoaiMon l : loaiDao.layTatCa()) {
                cboLoai.addItem(l);
            }
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
            return;
        }
        locDuLieu();
        // Filter giữ nguyên trong lúc làm mới danh sách.

        txtMa.setText("(tự động)");
        txtTen.setText("");
        txtGia.setText("");
        cboTrangThai.setSelectedIndex(0);
        tenAnh = null;
        TienIch.hienAnh(lblAnh, null, 170, 140);
    }

    // Chọn file ảnh từ máy, chép vào thư mục image/mon của project rồi hiện xem trước.
    // Ảnh chỉ thật sự gắn vào món khi bấm Thêm / Sửa.
    private void chonAnh() {
        JFileChooser chon = new JFileChooser();
        chon.setFileFilter(new FileNameExtensionFilter("Ảnh (png, jpg, gif)", "png", "jpg", "jpeg", "gif"));
        if (chon.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File nguon = chon.getSelectedFile();
        try {
            if (ImageIO.read(nguon) == null) {
                TienIch.canhBao(this, "File này không phải ảnh hợp lệ.");
                return;
            }
            File thuMuc = new File(TienIch.THU_MUC_ANH);
            thuMuc.mkdirs();
            File dich = new File(thuMuc, nguon.getName());
            // Trùng tên với một ảnh khác thì thêm số phía trước để không ghi đè
            if (dich.exists() && !dich.getCanonicalPath().equals(nguon.getCanonicalPath())) {
                dich = new File(thuMuc, System.currentTimeMillis() + "_" + nguon.getName());
            }
            if (!dich.exists()) {
                Files.copy(nguon.toPath(), dich.toPath());
            }
            tenAnh = dich.getName();
            TienIch.hienAnh(lblAnh, tenAnh, 170, 140);
        } catch (IOException e) {
            TienIch.canhBao(this, "Không đọc hoặc chép được ảnh: " + e.getMessage());
        }
    }

    // Đọc dữ liệu từ form thành một Mon; trả về null nếu nhập sai (đã báo lỗi)
    private Mon docForm(String maMon) {
    	String ten = txtTen.getText().trim();
        String giaStr = txtGia.getText().trim();

        // Tên món
        if (ten.isEmpty()) {
            TienIch.canhBao(this, "Tên món không được để trống.");
            txtTen.requestFocus();
            return null;
        }
        if (!ten.matches("^[\\p{L}\\p{N}][\\p{L}\\p{M}\\p{N} ()&.,'+\\-]{0,99}$")) {
            TienIch.canhBao(this, "Tên món tối đa 100 ký tự, bắt đầu bằng chữ hoặc số, chỉ chứa chữ, số, khoảng trắng và các ký tự ( ) & . , ' + -");
            txtTen.requestFocus();
            return null;
        }

        // Giá
        if (giaStr.isEmpty()) {
            TienIch.canhBao(this, "Giá không được để trống.");
            txtGia.requestFocus();
            return null;
        }
        if (!giaStr.matches("^[1-9]\\d{0,2}(?:000|500)$")) {
            TienIch.canhBao(this, "Giá phải là số nguyên (không dấu chấm, phẩy, chữ), là bội số của 500, từ 1.000 đến 999.500. Ví dụ: 25000");
            txtGia.requestFocus();
            return null;
        }
        double gia = Double.parseDouble(giaStr); // đã qua regex nên chắc chắn là số hợp lệ

        // Loại món
        LoaiMon loai = (LoaiMon) cboLoai.getSelectedItem();
        if (loai == null) {
            TienIch.canhBao(this, "Chưa có loại món. Hãy thêm loại món trước.");
            return null;
        }
        return new Mon(maMon, ten, gia, loai, tenAnh, cboTrangThai.getSelectedIndex() == 0);
    }

    private void them() {
        Mon m = docForm("");
        if (m == null) {
            return;
        }
        try {
            monDao.them(m);
            taiLai();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void sua() {
        int dong = tbl.getSelectedRow();
        if (dong < 0) {
            TienIch.canhBao(this, "Hãy chọn món cần sửa.");
            return;
        }
        Mon m = docForm(ds.get(dong).getMaMon());
        if (m == null) {
            return;
        }
        try {
            monDao.sua(m);
            taiLai();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void xoa() {
        int dong = tbl.getSelectedRow();
        if (dong < 0) {
            TienIch.canhBao(this, "Hãy chọn món cần xóa.");
            return;
        }
        if (!TienIch.xacNhan(this, "Xóa món \"" + ds.get(dong).getTenMon() + "\" ?")) {
            return;
        }
        try {
            monDao.xoa(ds.get(dong).getMaMon());
            taiLai();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }
}

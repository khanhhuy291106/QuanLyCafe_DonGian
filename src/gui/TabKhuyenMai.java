package gui;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.KhuyenMai_DAO;
import entity.KhuyenMai;

// Quản lý khuyến mãi (giảm theo %, trong khoảng ngày). Chỉ Quản lý được vào.
public class TabKhuyenMai extends JPanel implements TaiLai {
    private KhuyenMai_DAO dao = new KhuyenMai_DAO();
    private DefaultTableModel model = TienIch.taoModel("Mã KM", "Tên khuyến mãi", "Giảm (%)", "Từ ngày", "Đến ngày",
            "Trạng thái");
    private JTable tbl = TienIch.taoBang(model);
    private JTextField txtMa = new JTextField("(tự động)");
    private JTextField txtTen = new JTextField();
    private JTextField txtPhanTram = new JTextField();
    private JSpinner spTuNgay = TienIch.taoChonNgay(LocalDate.now());
    private JSpinner spDenNgay = TienIch.taoChonNgay(LocalDate.now().plusDays(7));
    private JComboBox<String> cboTrangThai = new JComboBox<>(new String[] { "Đang bật", "Tắt" });
    private List<KhuyenMai> ds = new ArrayList<>();
    private List<KhuyenMai> dsGoc = new ArrayList<>();
    private JTextField txtTim = new JTextField(17);
    private JComboBox<String> cboLoc = new JComboBox<>(new String[] { "Tất cả", "Đang bật", "Tắt" });

    public TabKhuyenMai() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        txtMa.setEditable(false);
        txtTim.setName("timKiem");

        JPanel form = new JPanel(new GridLayout(3, 2, 20, 8));
        form.setOpaque(false);
        form.add(TienIch.taoDong("Mã KM:", txtMa));
        form.add(TienIch.taoDong("Tên KM:", txtTen));
        form.add(TienIch.taoDong("Giảm (%):", txtPhanTram));
        form.add(TienIch.taoDong("Trạng thái:", cboTrangThai));
        form.add(TienIch.taoDong("Từ ngày:", spTuNgay));
        form.add(TienIch.taoDong("Đến ngày:", spDenNgay));

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

        JPanel pnlTren = new JPanel(new BorderLayout(0, 10));
        pnlTren.setOpaque(false);
        pnlTren.add(form, BorderLayout.NORTH);
        pnlTren.add(nut, BorderLayout.CENTER);
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
        tbl.getSelectionModel().addListSelectionListener(e -> {
            int dong = tbl.getSelectedRow();
            if (!e.getValueIsAdjusting() && dong >= 0) {
                KhuyenMai km = ds.get(dong);
                txtMa.setText(km.getMaKM());
                txtTen.setText(km.getTenKM());
                txtPhanTram.setText(String.valueOf(km.getPhanTram()));
                TienIch.setNgay(spTuNgay, km.getNgayBD());
                TienIch.setNgay(spDenNgay, km.getNgayKT());
                cboTrangThai.setSelectedIndex(km.isTrangThai() ? 0 : 1);
            }
        });
        taiLai();
    }

    private void locDuLieu() {
        String tim = txtTim.getText().trim().toLowerCase(java.util.Locale.ROOT);
        ds = new ArrayList<>();
        for (KhuyenMai km : dsGoc) {
            boolean dungTim = (km.getMaKM() + " " + km.getTenKM()).toLowerCase(java.util.Locale.ROOT).contains(tim);
            boolean dungTT = cboLoc.getSelectedIndex() == 0
                    || (cboLoc.getSelectedIndex() == 1 && km.isTrangThai())
                    || (cboLoc.getSelectedIndex() == 2 && !(km.isTrangThai()));
            if (dungTim && dungTT) ds.add(km);
        }
        model.setRowCount(0);
        for (KhuyenMai km : ds) {

            model.addRow(new Object[] { km.getMaKM(), km.getTenKM(), km.getPhanTram(),
                    km.getNgayBD().format(TienIch.NGAY), km.getNgayKT().format(TienIch.NGAY),
                    km.isTrangThai() ? "Đang bật" : "Tắt" });
        }
    }

    @Override
    public void taiLai() {
        try {
            dsGoc = dao.layTatCa();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
            return;
        }
        locDuLieu();

        txtMa.setText("(tự động)");
        txtTen.setText("");
        txtPhanTram.setText("");
        TienIch.setNgay(spTuNgay, LocalDate.now());
        TienIch.setNgay(spDenNgay, LocalDate.now().plusDays(7));
        cboTrangThai.setSelectedIndex(0);
    }

    // Đọc form thành KhuyenMai; trả về null nếu nhập sai (đã báo lỗi)
    private KhuyenMai docForm(String maKM) {
    	 String ten = txtTen.getText().trim();
         String ptStr = txtPhanTram.getText().trim();

         // Tên khuyến mãi
         if (ten.isEmpty()) {
             TienIch.canhBao(this, "Tên khuyến mãi không được để trống.");
             txtTen.requestFocus();
             return null;
         }
         if (!ten.matches("^[\\p{L}\\p{N}][\\p{L}\\p{M}\\p{N} ()%&.,'+\\-]{0,99}$")) {
             TienIch.canhBao(this, "Tên khuyến mãi tối đa 100 ký tự, bắt đầu bằng chữ hoặc số, "
                     + "chỉ chứa chữ, số, khoảng trắng và các ký tự ( ) % & . , ' + -");
             txtTen.requestFocus();
             return null;
         }

         // Phần trăm giảm
         if (ptStr.isEmpty()) {
             TienIch.canhBao(this, "Phần trăm giảm không được để trống.");
             txtPhanTram.requestFocus();
             return null;
         }
         if (!ptStr.matches("^(?:100|[1-9]\\d?)$")) {
             TienIch.canhBao(this, "Phần trăm giảm phải là số nguyên từ 1 đến 100 "
                     + "(không có dấu %, dấu thập phân hoặc số 0 đứng đầu).");
             txtPhanTram.requestFocus();
             return null;
         }
         int phanTram = Integer.parseInt(ptStr); // đã qua regex nên chắc chắn hợp lệ

         // Ngày áp dụng (JSpinner nên không cần regex, kiểm tra bằng so sánh ngày)
         LocalDate tu = TienIch.layNgay(spTuNgay);
         LocalDate den = TienIch.layNgay(spDenNgay);
         if (den.isBefore(tu)) {
             TienIch.canhBao(this, "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
             return null;
         }
         if (maKM.isEmpty() && den.isBefore(LocalDate.now())) {
             TienIch.canhBao(this, "Không thể tạo khuyến mãi đã hết hạn (ngày kết thúc trước hôm nay).");
             return null;
         }

         return new KhuyenMai(maKM, ten, phanTram, tu, den, cboTrangThai.getSelectedIndex() == 0);
    }

    private void them() {
        KhuyenMai km = docForm("");
        if (km == null) {
            return;
        }
        try {
            dao.them(km);
            taiLai();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void sua() {
        int dong = tbl.getSelectedRow();
        if (dong < 0) {
            TienIch.canhBao(this, "Hãy chọn khuyến mãi cần sửa.");
            return;
        }
        KhuyenMai km = docForm(ds.get(dong).getMaKM());
        if (km == null) {
            return;
        }
        try {
            dao.sua(km);
            taiLai();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void xoa() {
        int dong = tbl.getSelectedRow();
        if (dong < 0) {
            TienIch.canhBao(this, "Hãy chọn khuyến mãi cần xóa.");
            return;
        }
        if (!TienIch.xacNhan(this, "Xóa khuyến mãi \"" + ds.get(dong).getTenKM() + "\" ?")) {
            return;
        }
        try {
            dao.xoa(ds.get(dong).getMaKM());
            taiLai();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }
}

package gui;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.NhanVien_DAO;
import entity.NhanVien;

// Quản lý nhân viên (kèm tên đăng nhập, mật khẩu, chức vụ). Chỉ Quản lý được vào.
public class TabNhanVien extends JPanel implements TaiLai {
    private NhanVien nvDangNhap; // người đang đăng nhập, để không cho tự xóa chính mình
    private NhanVien_DAO dao = new NhanVien_DAO();
    private DefaultTableModel model = TienIch.taoModel("Mã NV", "Họ tên", "Giới tính", "SĐT", "Tên đăng nhập",
            "Chức vụ", "Trạng thái");
    private JTable tbl = TienIch.taoBang(model);
    private JTextField txtMa = new JTextField("(tự động)");
    private JTextField txtHoTen = new JTextField();
    private JComboBox<String> cboGioiTinh = new JComboBox<>(new String[] { "Nam", "Nữ" });
    private JTextField txtSdt = new JTextField();
    private JTextField txtTenDN = new JTextField();
    private JPasswordField txtMatKhau = new JPasswordField();
    private JComboBox<String> cboChucVu = new JComboBox<>(new String[] { "Nhân viên", "Quản lý" });
    private JComboBox<String> cboTrangThai = new JComboBox<>(new String[] { "Đang làm", "Nghỉ việc" });
    private List<NhanVien> ds = new ArrayList<>();
    private List<NhanVien> dsGoc = new ArrayList<>();
    private JTextField txtTim = new JTextField(17);
    private JComboBox<String> cboLoc = new JComboBox<>(new String[] { "Tất cả", "Đang làm", "Nghỉ việc" });

    public TabNhanVien(NhanVien nvDangNhap) {
        this.nvDangNhap = nvDangNhap;
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        txtMa.setEditable(false);
        txtTim.setName("timKiem");

        // Form chia 2 cột cho gọn
        JPanel form = new JPanel(new GridLayout(4, 2, 20, 8));
        form.setOpaque(false);
        form.add(TienIch.taoDong("Mã NV:", txtMa));
        form.add(TienIch.taoDong("Họ tên:", txtHoTen));
        form.add(TienIch.taoDong("Giới tính:", cboGioiTinh));
        form.add(TienIch.taoDong("SĐT:", txtSdt));
        form.add(TienIch.taoDong("Tên đăng nhập:", txtTenDN));
        form.add(TienIch.taoDong("Mật khẩu:", txtMatKhau));
        form.add(TienIch.taoDong("Chức vụ:", cboChucVu));
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
                NhanVien nv = ds.get(dong);
                txtMa.setText(nv.getMaNV());
                txtHoTen.setText(nv.getHoTen());
                cboGioiTinh.setSelectedIndex(nv.isGioiTinh() ? 0 : 1);
                txtSdt.setText(nv.getSdt());
                txtTenDN.setText(nv.getTenDangNhap());
                txtMatKhau.setText(""); // Không hiển thị mật khẩu đã lưu
                cboChucVu.setSelectedIndex(nv.laQuanLy() ? 1 : 0);
                cboTrangThai.setSelectedIndex(nv.isTrangThai() ? 0 : 1);
            }
        });
        taiLai();
    }

    private void locDuLieu() {
        String tim = txtTim.getText().trim().toLowerCase(java.util.Locale.ROOT);
        ds = new ArrayList<>();
        for (NhanVien nv : dsGoc) {
            boolean dungTim = (nv.getMaNV() + " " + nv.getHoTen() + " " + nv.getSdt() + " " + nv.getTenDangNhap()).toLowerCase(java.util.Locale.ROOT).contains(tim);
            boolean dungTT = cboLoc.getSelectedIndex() == 0
                    || (cboLoc.getSelectedIndex() == 1 && nv.isTrangThai())
                    || (cboLoc.getSelectedIndex() == 2 && !(nv.isTrangThai()));
            if (dungTim && dungTT) ds.add(nv);
        }
        model.setRowCount(0);
        for (NhanVien nv : ds) {

            model.addRow(new Object[] { nv.getMaNV(), nv.getHoTen(), nv.isGioiTinh() ? "Nam" : "Nữ", nv.getSdt(),
                    nv.getTenDangNhap(), NhanVien.tenChucVu(nv.getChucVu()),
                    nv.isTrangThai() ? "Đang làm" : "Nghỉ việc" });
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
        txtHoTen.setText("");
        txtSdt.setText("");
        txtTenDN.setText("");
        txtMatKhau.setText("");
        cboGioiTinh.setSelectedIndex(0);
        cboChucVu.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
    }

    // Đọc form thành NhanVien; trả về null nếu nhập sai (đã báo lỗi)
    private NhanVien docForm(String maNV) {
    	String hoTen = txtHoTen.getText().trim();
        String sdt = txtSdt.getText().trim();
        String tenDN = txtTenDN.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword()).trim();
        boolean coNhapMatKhau = !matKhau.isEmpty(); // người dùng có gõ mật khẩu mới không

        // Họ tên
        if (hoTen.isEmpty()) {
            TienIch.canhBao(this, "Họ tên không được để trống.");
            txtHoTen.requestFocus();
            return null;
        }
        if (!hoTen.matches("^(?=.{1,100}$)\\p{Lu}[\\p{Ll}\\p{M}]*(?: \\p{Lu}[\\p{Ll}\\p{M}]*)+$")) {
            TienIch.canhBao(this, "Họ tên gồm ít nhất 2 từ, mỗi từ viết hoa chữ cái đầu, "
                    + "chỉ chứa chữ cái và 1 dấu cách giữa các từ (không có số, ký tự đặc biệt). "
                    + "Ví dụ: Nguyễn Văn An");
            txtHoTen.requestFocus();
            return null;
        }

        // Số điện thoại
        if (sdt.isEmpty()) {
            TienIch.canhBao(this, "Số điện thoại không được để trống.");
            txtSdt.requestFocus();
            return null;
        }
        if (!sdt.matches("^0[35789]\\d{8}$")) {
            TienIch.canhBao(this, "Số điện thoại gồm đúng 10 chữ số, bắt đầu bằng 03, 05, 07, 08 hoặc 09.");
            txtSdt.requestFocus();
            return null;
        }

        // Tên đăng nhập
        if (tenDN.isEmpty()) {
            TienIch.canhBao(this, "Tên đăng nhập không được để trống.");
            txtTenDN.requestFocus();
            return null;
        }
        if (!tenDN.matches("^[a-zA-Z][a-zA-Z0-9_.]{3,29}$")) {
            TienIch.canhBao(this, "Tên đăng nhập từ 4 đến 30 ký tự, bắt đầu bằng chữ cái, chỉ gồm chữ không dấu, số, dấu _ hoặc dấu chấm.");
            txtTenDN.requestFocus();
            return null;
        }
        for (NhanVien nvKhac : dsGoc) {
            if (!nvKhac.getMaNV().equals(maNV) && nvKhac.getTenDangNhap().equalsIgnoreCase(tenDN)) {
                TienIch.canhBao(this, "Tên đăng nhập \"" + tenDN + "\" đã có người dùng.");
                txtTenDN.requestFocus();
                return null;
            }
        }

        // Mật khẩu: khi thêm bắt buộc nhập; khi sửa để trống thì giữ mật khẩu cũ
        if (!coNhapMatKhau && maNV.isEmpty()) {
            TienIch.canhBao(this, "Mật khẩu không được để trống.");
            txtMatKhau.requestFocus();
            return null;
        }
        if (coNhapMatKhau && !matKhau.matches("^[\\x21-\\x7E]{6,30}$")) {
            TienIch.canhBao(this, "Mật khẩu từ 6 đến 30 ký tự, chỉ dùng chữ không dấu, số, ký tự đặc biệt thông thường, không có khoảng trắng.");
            txtMatKhau.requestFocus();
            return null;
        }
        if (!coNhapMatKhau) {
            for (NhanVien nvCu : dsGoc) {
                if (nvCu.getMaNV().equals(maNV)) {
                    matKhau = nvCu.getMatKhau();
                    break;
                }
            }
        }

        return new NhanVien(maNV, hoTen, cboGioiTinh.getSelectedIndex() == 0, sdt, tenDN, matKhau,
                cboChucVu.getSelectedIndex() == 1 ? NhanVien.QUAN_LY : NhanVien.NHAN_VIEN,
                cboTrangThai.getSelectedIndex() == 0);
    }

    private void them() {
        NhanVien nv = docForm("");
        if (nv == null) {
            return;
        }
        try {
            dao.them(nv);
            taiLai();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void sua() {
        int dong = tbl.getSelectedRow();
        if (dong < 0) {
            TienIch.canhBao(this, "Hãy chọn nhân viên cần sửa.");
            return;
        }
        NhanVien nv = docForm(ds.get(dong).getMaNV());
        if (nv == null) {
            return;
        }
        if (nv.getMaNV().equals(nvDangNhap.getMaNV()) && (!nv.isTrangThai() || !nv.laQuanLy())) {
            TienIch.canhBao(this, "Không thể tự khóa tài khoản hoặc tự bỏ quyền quản lý đang đăng nhập.");
            return;
        }
        try {
            dao.sua(nv);
            taiLai();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void xoa() {
        int dong = tbl.getSelectedRow();
        if (dong < 0) {
            TienIch.canhBao(this, "Hãy chọn nhân viên cần xóa.");
            return;
        }
        NhanVien nv = ds.get(dong);
        if (nv.getMaNV().equals(nvDangNhap.getMaNV())) {
            TienIch.canhBao(this, "Không thể xóa tài khoản đang đăng nhập.");
            return;
        }
        if (!TienIch.xacNhan(this, "Xóa nhân viên \"" + nv.getHoTen() + "\" ?")) {
            return;
        }
        try {
            dao.xoa(nv.getMaNV());
            taiLai();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }
}

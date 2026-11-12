package gui;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.LoaiMon_DAO;
import entity.LoaiMon;

// Quản lý loại món: xem, thêm, sửa, xóa
public class QLLoaiMon extends JPanel implements TaiLai {
    private LoaiMon_DAO dao = new LoaiMon_DAO();
    private DefaultTableModel model = TienIch.taoModel("Mã loại", "Tên loại");
    private JTable tbl = TienIch.taoBang(model);
    private JTextField txtMa = new JTextField("(tự động)");
    private JTextField txtTen = new JTextField();
    private List<LoaiMon> ds = new ArrayList<>();
    private List<LoaiMon> dsGoc = new ArrayList<>();
    private JTextField txtTim = new JTextField(17);

    public QLLoaiMon() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        txtMa.setEditable(false);
        txtTim.setName("timKiem");

        JPanel form = new JPanel(new GridLayout(2, 1, 0, 8));
        form.setOpaque(false);
        form.add(TienIch.taoDong("Mã loại:", txtMa));
        form.add(TienIch.taoDong("Tên loại:", txtTen));

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
        pnlTren.add(pnlLoc, BorderLayout.SOUTH);
        add(pnlTren, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> sua());
        btnXoa.addActionListener(e -> xoa());
        btnMoi.addActionListener(e -> taiLai());
        txtTim.getDocument().addDocumentListener(TienIch.theoDoiTimKiem(() -> locDuLieu()));
        tbl.getSelectionModel().addListSelectionListener(e -> {
            int dong = tbl.getSelectedRow();
            if (!e.getValueIsAdjusting() && dong >= 0) {
                txtMa.setText(ds.get(dong).getMaLoai());
                txtTen.setText(ds.get(dong).getTenLoai());
            }
        });
        taiLai();
    }

    private void locDuLieu() {
        String tim = txtTim.getText().trim().toLowerCase(java.util.Locale.ROOT);
        ds = new ArrayList<>();
        for (LoaiMon l : dsGoc) {
            boolean dungTim = (l.getMaLoai() + " " + l.getTenLoai()).toLowerCase(java.util.Locale.ROOT).contains(tim);
            if (dungTim) ds.add(l);
        }
        model.setRowCount(0);
        for (LoaiMon l : ds) {

            model.addRow(new Object[] { l.getMaLoai(), l.getTenLoai() });
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
    }

    private void them() {
    	String ten = txtTen.getText().trim();
        if (ten.isEmpty()) {
            TienIch.canhBao(this, "Tên loại không được để trống.");
            txtTen.requestFocus();
            return;
        }
        if (!ten.matches("^(?=.{2,50}$)\\p{L}[\\p{L}\\p{M}]*(?: [\\p{L}\\p{M}]+)*$")) {
            TienIch.canhBao(this, "Tên loại từ 2 đến 50 ký tự, chỉ gồm chữ cái và khoảng trắng "
                    + "(không có số, ký tự đặc biệt hoặc 2 dấu cách liền nhau).");
            txtTen.requestFocus();
            return;
        }
        for (LoaiMon l : ds) {
            if (l.getTenLoai().equalsIgnoreCase(ten)) {
                TienIch.canhBao(this, "Loại món \"" + ten + "\" đã tồn tại.");
                txtTen.requestFocus();
                return;
            }
        }
        try {
            dao.them(new LoaiMon("", ten));
            taiLai();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void sua() {
    	 int dong = tbl.getSelectedRow();
         if (dong < 0) {
             TienIch.canhBao(this, "Hãy chọn loại món cần sửa.");
             return;
         }
         String ten = txtTen.getText().trim();
         if (ten.isEmpty()) {
             TienIch.canhBao(this, "Tên loại không được để trống.");
             txtTen.requestFocus();
             return;
         }
         if (!ten.matches("^(?=.{2,50}$)\\p{L}[\\p{L}\\p{M}]*(?: [\\p{L}\\p{M}]+)*$")) {
             TienIch.canhBao(this, "Tên loại từ 2 đến 50 ký tự, chỉ gồm chữ cái và khoảng trắng "
                     + "(không có số, ký tự đặc biệt hoặc 2 dấu cách liền nhau).");
             txtTen.requestFocus();
             return;
         }
         String maDangSua = ds.get(dong).getMaLoai();
         for (LoaiMon l : ds) {
             if (!l.getMaLoai().equals(maDangSua) && l.getTenLoai().equalsIgnoreCase(ten)) {
                 TienIch.canhBao(this, "Loại món \"" + ten + "\" đã tồn tại.");
                 txtTen.requestFocus();
                 return;
             }
         }
         try {
             dao.sua(new LoaiMon(maDangSua, ten));
             taiLai();
         } catch (SQLException e) {
             TienIch.baoLoi(this, e);
         }
    }

    private void xoa() {
        int dong = tbl.getSelectedRow();
        if (dong < 0) {
            TienIch.canhBao(this, "Hãy chọn loại món cần xóa.");
            return;
        }
        if (!TienIch.xacNhan(this, "Xóa loại món \"" + ds.get(dong).getTenLoai() + "\" ?")) {
            return;
        }
        try {
            dao.xoa(ds.get(dong).getMaLoai());
            taiLai();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }
}

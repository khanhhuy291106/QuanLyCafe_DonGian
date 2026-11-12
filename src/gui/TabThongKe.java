package gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.HoaDon_DAO;

// Thống kê doanh thu theo ngày (không tính đơn đã hủy) và xuất ra file CSV. Chỉ Quản lý được vào.
public class TabThongKe extends JPanel {
    private HoaDon_DAO hdDao = new HoaDon_DAO();
    private JSpinner spTuNgay = TienIch.taoChonNgay(LocalDate.now().minusDays(7));
    private JSpinner spDenNgay = TienIch.taoChonNgay(LocalDate.now());
    private DefaultTableModel model = TienIch.taoModel("Ngày", "Số đơn", "Doanh thu");
    private JTable tbl = TienIch.taoBang(model);
    private JLabel lblTong = new JLabel("Chọn khoảng ngày rồi bấm \"Thống kê\".");
    private List<Object[]> ketQua = new ArrayList<>(); // kết quả lần thống kê gần nhất

    public TabThongKe() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JButton btnThongKe = TienIch.taoNut("Thống kê");
        JButton btnCsv = TienIch.taoNut("Xuất CSV");
        JPanel pnlTren = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlTren.setOpaque(false);
        pnlTren.add(new JLabel("Từ ngày:"));
        pnlTren.add(spTuNgay);
        pnlTren.add(new JLabel("Đến ngày:"));
        pnlTren.add(spDenNgay);
        pnlTren.add(btnThongKe);
        pnlTren.add(btnCsv);

        lblTong.setFont(TienIch.FONT_DAM);
        add(pnlTren, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);
        add(lblTong, BorderLayout.SOUTH);

        btnThongKe.addActionListener(e -> thongKe());
        btnCsv.addActionListener(e -> xuatCsv());
    }

    private void thongKe() {
        LocalDate tu = TienIch.layNgay(spTuNgay);
        LocalDate den = TienIch.layNgay(spDenNgay);
        if (den.isBefore(tu)) {
            TienIch.canhBao(this, "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
            return;
        }
        try {
            ketQua = hdDao.thongKeTheoNgay(tu, den);
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
            return;
        }
        model.setRowCount(0);
        int tongDon = 0;
        double tongTien = 0;
        for (Object[] dong : ketQua) {
            model.addRow(new Object[] { ((LocalDate) dong[0]).format(TienIch.NGAY), dong[1],
                    TienIch.dinhDangTien((Double) dong[2]) });
            tongDon += (Integer) dong[1];
            tongTien += (Double) dong[2];
        }
        lblTong.setText("Tổng số đơn: " + tongDon + "   |   Tổng doanh thu: " + TienIch.dinhDangTien(tongTien));
    }

    private void xuatCsv() {
        if (ketQua.isEmpty()) {
            TienIch.canhBao(this, "Chưa có dữ liệu. Hãy bấm \"Thống kê\" trước.");
            return;
        }
        JFileChooser chon = new JFileChooser();
        chon.setSelectedFile(new File("thong_ke_doanh_thu.csv"));
        if (chon.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chon.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getPath() + ".csv");
        }
        if (file.exists() && !TienIch.xacNhan(this, "File đã tồn tại. Ghi đè?")) {
            return;
        }
        // Ghi UTF-8 có BOM để Excel đọc đúng tiếng Việt
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new java.io.FileOutputStream(file), StandardCharsets.UTF_8))) {
            out.print('\uFEFF');
            out.println("Ngày,Số đơn,Doanh thu");
            int tongDon = 0;
            double tongTien = 0;
            for (Object[] dong : ketQua) {
                out.println(((LocalDate) dong[0]).format(TienIch.NGAY) + "," + dong[1] + "," + ((Double) dong[2]).longValue());
                tongDon += (Integer) dong[1];
                tongTien += (Double) dong[2];
            }
            out.println("Tổng," + tongDon + "," + (long) tongTien);
            TienIch.thongBao(this, "Đã xuất file:\n" + file.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Không ghi được file: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

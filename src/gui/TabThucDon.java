package gui;

import java.awt.*;
import javax.swing.*;

// Thực đơn gồm 2 tab con: Món và Loại món (chỉ Quản lý được vào)
public class TabThucDon extends JPanel implements TaiLai {
    private JTabbedPane tabs = new JTabbedPane();

    public TabThucDon() {
        setLayout(new BorderLayout());
        tabs.addTab("Món", new QLMon());
        tabs.addTab("Loại món", new QLLoaiMon());
        tabs.addChangeListener(e -> taiLai());
        add(tabs, BorderLayout.CENTER);
    }

    
    @Override
    public void taiLai() {
        Component c = tabs.getSelectedComponent();
        if (c instanceof TaiLai) {
            ((TaiLai) c).taiLai();
        }
    }
}

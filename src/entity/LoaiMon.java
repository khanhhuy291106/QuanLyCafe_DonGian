package entity;

public class LoaiMon {
    private String maLoai;
    private String tenLoai;

    public LoaiMon() {
    }

    public LoaiMon(String maLoai, String tenLoai) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
    }

    public String getMaLoai() { return maLoai; }
    public void setMaLoai(String maLoai) { this.maLoai = maLoai; }
    public String getTenLoai() { return tenLoai; }
    public void setTenLoai(String tenLoai) { this.tenLoai = tenLoai; }

    // So sánh theo mã để JComboBox.setSelectedItem() chọn đúng loại
    @Override
    public boolean equals(Object o) {
        return o instanceof LoaiMon && maLoai != null && maLoai.equals(((LoaiMon) o).maLoai);
    }

    @Override
    public int hashCode() {
        return maLoai == null ? 0 : maLoai.hashCode();
    }

    // JComboBox hiển thị tên loại
    @Override
    public String toString() {
        return tenLoai;
    }
}

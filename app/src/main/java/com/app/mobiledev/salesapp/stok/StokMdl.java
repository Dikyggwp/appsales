package com.app.mobiledev.salesapp.stok;

public class StokMdl {

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getStok() {
        return stok;
    }

    public void setStok(String stok) {
        this.stok = stok;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    private String kode;
    private String nama;
    private String stok;
    private String ket;

    public String getHrg() {
        return hrg;
    }

    public void setHrg(String hrg) {
        this.hrg = hrg;
    }

    public String getGdg() {
        return gdg;
    }

    public void setGdg(String gdg) {
        this.gdg = gdg;
    }

    private String hrg;
    private String gdg;
}

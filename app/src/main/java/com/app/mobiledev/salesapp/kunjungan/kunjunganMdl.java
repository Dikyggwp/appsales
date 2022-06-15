package com.app.mobiledev.salesapp.kunjungan;

public class kunjunganMdl {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String tgl;
    private String outlet;
    private String alamat;
    private String status;

    public String getOutlet_up() {
        return outlet_up;
    }

    public void setOutlet_up(String outlet_up) {
        this.outlet_up = outlet_up;
    }

    public String getAlamat_up() {
        return alamat_up;
    }

    public void setAlamat_up(String alamat_up) {
        this.alamat_up = alamat_up;
    }

    public String getAlasan_unplan() {
        return alasan_unplan;
    }

    public void setAlasan_unplan(String alasan_unplan) {
        this.alasan_unplan = alasan_unplan;
    }

    private String outlet_up;
    private String alamat_up;
    private String alasan_unplan;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getOutlet() {
        return outlet;
    }

    public void setOutlet(String outlet) {
        this.outlet = outlet;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }


}
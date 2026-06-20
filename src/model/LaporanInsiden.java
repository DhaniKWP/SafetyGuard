/*
 * Click nbfs:
 * Click nbfs:
 */
package model;

/**
 *
 * @author macbook
 */
public class LaporanInsiden {
    private String id_insiden;
    private String tanggal_kejadian;
    private String lokasi_kejadian;
    private String kategori_insiden;
    private String deskripsi_kejadian;
    private String id_pelapor;
    private String status_investigasi;
    public LaporanInsiden() {}
    public LaporanInsiden(String id_insiden, String tanggal_kejadian, String lokasi_kejadian, 
                          String kategori_insiden, String deskripsi_kejadian, 
                          String id_pelapor, String status_investigasi) {
        this.id_insiden = id_insiden;
        this.tanggal_kejadian = tanggal_kejadian;
        this.lokasi_kejadian = lokasi_kejadian;
        this.kategori_insiden = kategori_insiden;
        this.deskripsi_kejadian = deskripsi_kejadian;
        this.id_pelapor = id_pelapor;
        this.status_investigasi = status_investigasi;
    }
    public String getIdInsiden() { return id_insiden; }
    public void setIdInsiden(String id_insiden) { this.id_insiden = id_insiden; }
    public String getTanggalKejadian() { return tanggal_kejadian; }
    public void setTanggalKejadian(String tanggal_kejadian) { this.tanggal_kejadian = tanggal_kejadian; }
    public String getLokasiKejadian() { return lokasi_kejadian; }
    public void setLokasiKejadian(String lokasi_kejadian) { this.lokasi_kejadian = lokasi_kejadian; }
    public String getKategoriInsiden() { return kategori_insiden; }
    public void setKategoriInsiden(String kategori_insiden) { this.kategori_insiden = kategori_insiden; }
    public String getDeskripsiKejadian() { return deskripsi_kejadian; }
    public void setDeskripsiKejadian(String deskripsi_kejadian) { this.deskripsi_kejadian = deskripsi_kejadian; }
    public String getIdPelapor() { return id_pelapor; }
    public void setIdPelapor(String id_pelapor) { this.id_pelapor = id_pelapor; }
    public String getStatusInvestigasi() { return status_investigasi; }
    public void setStatusInvestigasi(String status_investigasi) { this.status_investigasi = status_investigasi; }
}

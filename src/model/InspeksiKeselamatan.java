/*
 * Click nbfs:
 * Click nbfs:
 */
package model;

/**
 *
 * @author macbook
 */
public class InspeksiKeselamatan {
    private String id_inspeksi;
    private String tanggal_inspeksi;
    private String area_inspeksi;
    private String id_inspektur;
    private String temuan_bahaya;
    private int skor_kepatuhan;
    private String rekomendasi;
    public InspeksiKeselamatan() {}
    public InspeksiKeselamatan(String id_inspeksi, String tanggal_inspeksi, String area_inspeksi, 
                               String id_inspektur, String temuan_bahaya, int skor_kepatuhan, String rekomendasi) {
        this.id_inspeksi = id_inspeksi;
        this.tanggal_inspeksi = tanggal_inspeksi;
        this.area_inspeksi = area_inspeksi;
        this.id_inspektur = id_inspektur;
        this.temuan_bahaya = temuan_bahaya;
        this.skor_kepatuhan = skor_kepatuhan;
        this.rekomendasi = rekomendasi;
    }
    public String getIdInspeksi() { return id_inspeksi; }
    public void setIdInspeksi(String id_inspeksi) { this.id_inspeksi = id_inspeksi; }
    public String getTanggalInspeksi() { return tanggal_inspeksi; }
    public void setTanggalInspeksi(String tanggal_inspeksi) { this.tanggal_inspeksi = tanggal_inspeksi; }
    public String getAreaInspeksi() { return area_inspeksi; }
    public void setAreaInspeksi(String area_inspeksi) { this.area_inspeksi = area_inspeksi; }
    public String getIdInspektur() { return id_inspektur; }
    public void setIdInspektur(String id_inspektur) { this.id_inspektur = id_inspektur; }
    public String getTemuanBahaya() { return temuan_bahaya; }
    public void setTemuanBahaya(String temuan_bahaya) { this.temuan_bahaya = temuan_bahaya; }
    public int getSkorKepatuhan() { return skor_kepatuhan; }
    public void setSkorKepatuhan(int skor_kepatuhan) { this.skor_kepatuhan = skor_kepatuhan; }
    public String getRekomendasi() { return rekomendasi; }
    public void setRekomendasi(String rekomendasi) { this.rekomendasi = rekomendasi; }
}

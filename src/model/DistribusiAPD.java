/*
 * Click nbfs:
 * Click nbfs:
 */
package model;

/**
 *
 * @author macbook
 */
public class DistribusiAPD {
    private String id_distribusi;
    private String id_karyawan;
    private String jenis_apd;
    private String tanggal_pembagian;
    private int jumlah;
    private String kondisi_apd;
    public DistribusiAPD() {}
    public DistribusiAPD(String id_distribusi, String id_karyawan, String jenis_apd, 
                         String tanggal_pembagian, int jumlah, String kondisi_apd) {
        this.id_distribusi = id_distribusi;
        this.id_karyawan = id_karyawan;
        this.jenis_apd = jenis_apd;
        this.tanggal_pembagian = tanggal_pembagian;
        this.jumlah = jumlah;
        this.kondisi_apd = kondisi_apd;
    }
    public String getIdDistribusi() { return id_distribusi; }
    public void setIdDistribusi(String id_distribusi) { this.id_distribusi = id_distribusi; }
    public String getIdKaryawan() { return id_karyawan; }
    public void setIdKaryawan(String id_karyawan) { this.id_karyawan = id_karyawan; }
    public String getJenisApd() { return jenis_apd; }
    public void setJenisApd(String jenis_apd) { this.jenis_apd = jenis_apd; }
    public String getTanggalPembagian() { return tanggal_pembagian; }
    public void setTanggalPembagian(String tanggal_pembagian) { this.tanggal_pembagian = tanggal_pembagian; }
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
    public String getKondisiApd() { return kondisi_apd; }
    public void setKondisiApd(String kondisi_apd) { this.kondisi_apd = kondisi_apd; }
}

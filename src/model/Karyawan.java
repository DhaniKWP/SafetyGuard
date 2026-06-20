/*
 * Click nbfs:
 * Click nbfs:
 */
package model;

/**
 *
 * @author macbook
 */
public class Karyawan {
    private String id_karyawan;
    private String nama_lengkap;
    private String departemen;
    private String jabatan;
    private String sertifikasi_k3;
    
    public Karyawan() {}
    public Karyawan(String id_karyawan, String nama_lengkap, String departemen, String jabatan, String sertifikasi_k3) {
        this.id_karyawan = id_karyawan;
        this.nama_lengkap = nama_lengkap;
        this.departemen = departemen;
        this.jabatan = jabatan;
        this.sertifikasi_k3 = sertifikasi_k3;
    }
    public String getIdKaryawan() { return id_karyawan; }
    public void setIdKaryawan(String id_karyawan) { this.id_karyawan = id_karyawan; }
    public String getNamaLengkap() { return nama_lengkap; }
    public void setNamaLengkap(String nama_lengkap) { this.nama_lengkap = nama_lengkap; }
    public String getDepartemen() { return departemen; }
    public void setDepartemen(String departemen) { this.departemen = departemen; }
    public String getJabatan() { return jabatan; }
    public void setJabatan(String jabatan) { this.jabatan = jabatan; }
    public String getSertifikasiK3() { return sertifikasi_k3; }
    public void setSertifikasiK3(String sertifikasi_k3) { this.sertifikasi_k3 = sertifikasi_k3; }
    @Override
    public String toString() {
        return nama_lengkap + " - " + jabatan + " (" + departemen + ")";
    }
}

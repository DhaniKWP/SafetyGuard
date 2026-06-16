/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author macbook
 */
public class TindakanPerbaikan {
    private String id_tindakan;
    private String id_insiden;
    private String tindakan_detail;
    private String id_penanggung_jawab;
    private String deadline;
    private String tanggal_selesai;
    private String status_tindakan;
    public TindakanPerbaikan() {}
    public TindakanPerbaikan(String id_tindakan, String id_insiden, String tindakan_detail, 
                             String id_penanggung_jawab, String deadline, 
                             String tanggal_selesai, String status_tindakan) {
        this.id_tindakan = id_tindakan;
        this.id_insiden = id_insiden;
        this.tindakan_detail = tindakan_detail;
        this.id_penanggung_jawab = id_penanggung_jawab;
        this.deadline = deadline;
        this.tanggal_selesai = tanggal_selesai;
        this.status_tindakan = status_tindakan;
    }
    // Getter dan Setter
    public String getIdTindakan() { return id_tindakan; }
    public void setIdTindakan(String id_tindakan) { this.id_tindakan = id_tindakan; }
    public String getIdInsiden() { return id_insiden; }
    public void setIdInsiden(String id_insiden) { this.id_insiden = id_insiden; }
    public String getTindakanDetail() { return tindakan_detail; }
    public void setTindakanDetail(String tindakan_detail) { this.tindakan_detail = tindakan_detail; }
    public String getIdPenanggungJawab() { return id_penanggung_jawab; }
    public void setIdPenanggungJawab(String id_penanggung_jawab) { this.id_penanggung_jawab = id_penanggung_jawab; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getTanggalSelesai() { return tanggal_selesai; }
    public void setTanggalSelesai(String tanggal_selesai) { this.tanggal_selesai = tanggal_selesai; }
    public String getStatusTindakan() { return status_tindakan; }
    public void setStatusTindakan(String status_tindakan) { this.status_tindakan = status_tindakan; }
}

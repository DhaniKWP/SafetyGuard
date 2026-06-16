/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author macbook
 */
public class Pengguna {
    private String username;
    private String password;
    private String id_karyawan;
    private String peran;
    
    public Pengguna() {}
    public Pengguna(String username, String password, String id_karyawan, String peran) {
        this.username = username;
        this.password = password;
        this.id_karyawan = id_karyawan;
        this.peran = peran;
    }
    
    // Getter dan Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getIdKaryawan() { return id_karyawan; }
    public void setIdKaryawan(String id_karyawan) { this.id_karyawan = id_karyawan; }
    public String getPeran() { return peran; }
    public void setPeran(String peran) { this.peran = peran; }
}

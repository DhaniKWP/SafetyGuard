/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import helper.SupabaseClient;
import java.lang.reflect.Type;
import java.util.List;
import model.Karyawan;
/**
 *
 * @author macbook
 */
public class KaryawanDAO {
    private final Gson gson = new Gson();
    // READ (Get All Karyawan)
    public List<Karyawan> getAll() {
        try {
            String jsonResponse = SupabaseClient.get("/karyawan");
            Type listType = new TypeToken<List<Karyawan>>() {}.getType();
            return gson.fromJson(jsonResponse, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // CREATE (Insert Karyawan Baru)
    public boolean insert(Karyawan karyawan) {
        try {
            karyawan.setIdKaryawan(null);
            
            String jsonPayload = gson.toJson(karyawan);
            return SupabaseClient.post("/karyawan", jsonPayload);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // UPDATE (Edit Karyawan berdasarkan ID)
    public boolean update(Karyawan karyawan) {
        try {
            String id = karyawan.getIdKaryawan();
            
            String jsonPayload = gson.toJson(karyawan);
            
            String endpoint = "/karyawan?id_karyawan=eq." + id;
            return SupabaseClient.patch(endpoint, jsonPayload);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // DELETE (Hapus Karyawan berdasarkan ID)
    public boolean delete(String id) {
        try {
            String endpoint = "/karyawan?id_karyawan=eq." + id;
            return SupabaseClient.delete(endpoint);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

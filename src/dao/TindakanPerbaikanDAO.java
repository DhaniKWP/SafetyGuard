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
import model.TindakanPerbaikan;

/**
 *
 * @author macbook
 */
public class TindakanPerbaikanDAO {
    private final Gson gson = new Gson();
    public List<TindakanPerbaikan> getAll() {
        try {
            String jsonResponse = SupabaseClient.get("/tindakan_perbaikan");
            Type listType = new TypeToken<List<TindakanPerbaikan>>() {}.getType();
            return gson.fromJson(jsonResponse, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean insert(TindakanPerbaikan tindakan) {
        try {
            tindakan.setIdTindakan(null); // Biar di-generate otomatis oleh Supabase
            String jsonPayload = gson.toJson(tindakan);
            return SupabaseClient.post("/tindakan_perbaikan", jsonPayload);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(TindakanPerbaikan tindakan) {
        try {
            String id = tindakan.getIdTindakan();
            String jsonPayload = gson.toJson(tindakan);
            String endpoint = "/tindakan_perbaikan?id_tindakan=eq." + id;
            return SupabaseClient.patch(endpoint, jsonPayload);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String id) {
        try {
            String endpoint = "/tindakan_perbaikan?id_tindakan=eq." + id;
            return SupabaseClient.delete(endpoint);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

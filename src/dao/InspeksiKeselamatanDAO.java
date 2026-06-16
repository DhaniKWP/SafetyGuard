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
import model.InspeksiKeselamatan;

/**
 *
 * @author macbook
 */
public class InspeksiKeselamatanDAO {
    private final Gson gson = new Gson();
    public List<InspeksiKeselamatan> getAll() {
        try {
            String jsonResponse = SupabaseClient.get("/inspeksi_keselamatan");
            Type listType = new TypeToken<List<InspeksiKeselamatan>>() {}.getType();
            return gson.fromJson(jsonResponse, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean insert(InspeksiKeselamatan inspeksi) {
        try {
            inspeksi.setIdInspeksi(null);
            String jsonPayload = gson.toJson(inspeksi);
            return SupabaseClient.post("/inspeksi_keselamatan", jsonPayload);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(InspeksiKeselamatan inspeksi) {
        try {
            String id = inspeksi.getIdInspeksi();
            String jsonPayload = gson.toJson(inspeksi);
            String endpoint = "/inspeksi_keselamatan?id_inspeksi=eq." + id;
            return SupabaseClient.patch(endpoint, jsonPayload);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String id) {
        try {
            String endpoint = "/inspeksi_keselamatan?id_inspeksi=eq." + id;
            return SupabaseClient.delete(endpoint);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

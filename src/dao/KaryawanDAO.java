/*
 * Click nbfs:
 * Click nbfs:
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

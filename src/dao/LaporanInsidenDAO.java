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
import model.LaporanInsiden;

/**
 *
 * @author macbook
 */
public class LaporanInsidenDAO {
    private final Gson gson = new Gson();
    public List<LaporanInsiden> getAll() {
        try {
            String jsonResponse = SupabaseClient.get("/laporan_insiden");
            Type listType = new TypeToken<List<LaporanInsiden>>() {}.getType();
            return gson.fromJson(jsonResponse, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean insert(LaporanInsiden insiden) {
        try {
            insiden.setIdInsiden(null);
            String jsonPayload = gson.toJson(insiden);
            return SupabaseClient.post("/laporan_insiden", jsonPayload);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(LaporanInsiden insiden) {
        try {
            String id = insiden.getIdInsiden();
            String jsonPayload = gson.toJson(insiden);
            String endpoint = "/laporan_insiden?id_insiden=eq." + id;
            return SupabaseClient.patch(endpoint, jsonPayload);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String id) {
        try {
            String endpoint = "/laporan_insiden?id_insiden=eq." + id;
            return SupabaseClient.delete(endpoint);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

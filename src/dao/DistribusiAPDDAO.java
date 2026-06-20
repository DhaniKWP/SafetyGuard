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
import model.DistribusiAPD;
/**
 *
 * @author macbook
 */
public class DistribusiAPDDAO {
    private final Gson gson = new Gson();
    public List<DistribusiAPD> getAll() {
        try {
            String jsonResponse = SupabaseClient.get("/distribusi_apd");
            Type listType = new TypeToken<List<DistribusiAPD>>() {}.getType();
            return gson.fromJson(jsonResponse, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean insert(DistribusiAPD distribusi) {
        try {
            distribusi.setIdDistribusi(null);
            String jsonPayload = gson.toJson(distribusi);
            return SupabaseClient.post("/distribusi_apd", jsonPayload);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(DistribusiAPD distribusi) {
        try {
            String id = distribusi.getIdDistribusi();
            String jsonPayload = gson.toJson(distribusi);
            String endpoint = "/distribusi_apd?id_distribusi=eq." + id;
            return SupabaseClient.patch(endpoint, jsonPayload);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String id) {
        try {
            String endpoint = "/distribusi_apd?id_distribusi=eq." + id;
            return SupabaseClient.delete(endpoint);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

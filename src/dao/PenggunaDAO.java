/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import helper.PasswordHasher;
import helper.SupabaseClient;
import java.lang.reflect.Type;
import java.util.List;
import model.Pengguna;
/**
 *
 * @author macbook
 */
public class PenggunaDAO {
    private final Gson gson = new Gson();

    public Pengguna login(String username, String password) {
        try {
            String endpoint = "/pengguna?username=eq." + username;
            String jsonResponse = SupabaseClient.get(endpoint);
            Type listType = new TypeToken<List<Pengguna>>() {}.getType();
            List<Pengguna> list = gson.fromJson(jsonResponse, listType);
            if (list != null && !list.isEmpty()) {
                Pengguna user = list.get(0);
                
                String hashedInput = PasswordHasher.hashPassword(password);
                
                if (user.getPassword().equals(hashedInput)) {
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

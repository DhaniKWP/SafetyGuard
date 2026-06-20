/*
 * Click nbfs:
 * Click nbfs:
 */
package helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author macbook
 */
public class SupabaseClient {
    private static final String BASE_URL = "https://trnzarbhylbyyyddwvmf.supabase.co/rest/v1";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRybnphcmJoeWxieXl5ZGR3dm1mIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODE1ODc1MDksImV4cCI6MjA5NzE2MzUwOX0.FiCLOJyN2UNULbrtnp15PdvFFUgvL6eUnhBIdwkF3nM";
    
    public static String get(String endpoint) throws Exception {
        URL obj = new URL(BASE_URL + endpoint);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("apikey", API_KEY);
        con.setRequestProperty("Authorization", "Bearer " + API_KEY);
        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new Exception("GET Gagal: HTTP error code : " + responseCode);
        }
    }
    
    public static boolean post(String endpoint, String jsonPayload) throws Exception {
        URL obj = new URL(BASE_URL + endpoint);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("apikey", API_KEY);
        con.setRequestProperty("Authorization", "Bearer " + API_KEY);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Prefer", "return=representation");
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int responseCode = con.getResponseCode();
        return (responseCode == 201 || responseCode == 200);
    }
    
    public static boolean patch(String endpointWithQuery, String jsonPayload) throws Exception {
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(BASE_URL + endpointWithQuery))
                .header("apikey", API_KEY)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .method("PATCH", java.net.http.HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
                
        java.net.http.HttpResponse<String> response = java.net.http.HttpClient.newHttpClient()
                .send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
                
        int responseCode = response.statusCode();
        if (responseCode >= 400) {
            System.err.println("Supabase PATCH Error Code: " + responseCode);
            System.err.println("Supabase Error Msg: " + response.body());
        }
        return (responseCode == 200 || responseCode == 204);
    }

    public static boolean delete(String endpointWithQuery) throws Exception {
        URL obj = new URL(BASE_URL + endpointWithQuery);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("DELETE");
        con.setRequestProperty("apikey", API_KEY);
        con.setRequestProperty("Authorization", "Bearer " + API_KEY);
        int responseCode = con.getResponseCode();
        return (responseCode == 200 || responseCode == 204);
    }
}

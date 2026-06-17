import dao.*;
import model.*;
import helper.*;
import java.util.List;

public class Seeder {
    public static void main(String[] args) {
        try {
            System.out.println("Fetching existing users...");
            String json = SupabaseClient.get("/pengguna");
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

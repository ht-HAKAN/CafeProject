package application;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        // Veritabanı bağlantısını test et
        try (Connection connection = MySQLConnection.connect()) {
            if (connection != null) {
                System.out.println("Bağlantı başarılı!");

                // Bağlantı başarılıysa veritabanında bir sorgu çalıştır
                Statement statement = connection.createStatement();
                String query = "SELECT 1"; // Basit bir sorgu 
                ResultSet resultSet = statement.executeQuery(query);

                if (resultSet.next()) {
                    System.out.println("Veritabanına başarılı bir şekilde erişildi!");
                } else {
                    System.out.println("Veritabanına erişim sağlanamadı.");
                }
            } else {
                System.out.println("Bağlantı başarısız.");
            }
        } catch (SQLException e) {
            System.out.println("Bağlantı hatası: " + e.getMessage());
        }
    }
}

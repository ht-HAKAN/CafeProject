package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/cafeproject"; // Veritabanı adı
    private static final String USER = "root"; // MySQL kullanıcı adı
    private static final String PASSWORD = ""; // MySQL şifresi (genelde XAMPP'te boştur)

    // Veritabanına bağlanmayı sağlayan metot
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("MySQL bağlantısı başarılı!");
        } catch (SQLException e) {
            System.err.println("Veritabanına bağlanırken hata oluştu: " + e.getMessage());
        }
        return conn;
    }
}

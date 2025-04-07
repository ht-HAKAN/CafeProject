package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/cafeproject"; // Veritabanı ismi
    private static final String USER = "root"; // Kullanıcı adı
    private static final String PASSWORD = ""; // Şifre (eğer varsa buraya yaz)

    public static Connection connect() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Veritabanına bağlanırken hata oluştu!", e);
        }
    }
}

package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Клас для управління підключенням до бази даних MySQL
 */
public class DatabaseConnection {

    // Параметри підключення до бази даних
    // ⚠️ ВАЖЛИВО: Змініть ці значення на свої!
    private static final String URL = "jdbc:mysql://localhost:3306/payment_system";
    private static final String USER = "root";  // Ваш логін MySQL
    private static final String PASSWORD = "19810707";  // Ваш пароль MySQL

    /**
     * Метод для отримання підключення до бази даних
     * @return Connection - об'єкт підключення
     * @throws SQLException якщо не вдалося підключитися
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Завантаження драйвера MySQL (для старіших версій JDBC)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Створення підключення
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("✅ Підключення до бази даних успішне!");
            return connection;

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Драйвер MySQL не знайдено!");
            System.err.println("Перевірте, чи додано mysql-connector-java.jar до проекту");
            throw new SQLException("Драйвер не знайдено", e);
        } catch (SQLException e) {
            System.err.println("❌ Помилка підключення до бази даних!");
            System.err.println("Перевірте:");
            System.err.println("1. Чи запущений MySQL сервер?");
            System.err.println("2. Чи правильні URL, логін та пароль?");
            System.err.println("3. Чи існує база даних 'payment_system'?");
            throw e;
        }
    }

    /**
     * Метод для закриття підключення
     * @param connection - підключення для закриття
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✅ Підключення закрито");
            } catch (SQLException e) {
                System.err.println("❌ Помилка при закритті підключення: " + e.getMessage());
            }
        }
    }

    /**
     * Метод для перевірки підключення
     * @return true якщо підключення успішне, false якщо ні
     */
    public static boolean testConnection() {
        try {
            Connection connection = getConnection();
            if (connection != null && !connection.isClosed()) {
                System.out.println("🎉 Тест підключення пройшов успішно!");
                closeConnection(connection);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Тест підключення не вдався: " + e.getMessage());
        }
        return false;
    }
}
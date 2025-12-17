package dao;

import model.User;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реалізація інтерфейсу UserDAO
 * Виконує всі операції з користувачами в базі даних
 */
public class UserDAOImpl implements UserDAO {

    private Connection connection;

    public UserDAOImpl() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("❌ Помилка підключення до БД", e);
        }
    }



    @Override
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, email, phone, " +
                     "password_hash, date_of_birth, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getPasswordHash() != null ? user.getPasswordHash() : "default_hash");
            stmt.setDate(6, user.getDateOfBirth());
            stmt.setBoolean(7, user.isActive());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Отримуємо згенерований ID
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
                System.out.println("✅ Користувача додано: " + user.getFullName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка при додаванні користувача: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка при отриманні користувача: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка при пошуку користувача за email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            System.out.println("✅ Знайдено користувачів: " + users.size());
            
        } catch (SQLException e) {
            System.err.println("❌ Помилка при отриманні всіх користувачів: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public List<User> getActiveUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_active = TRUE ORDER BY last_name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            System.out.println("✅ Знайдено активних користувачів: " + users.size());
            
        } catch (SQLException e) {
            System.err.println("❌ Помилка при отриманні активних користувачів: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, " +
                     "phone = ?, date_of_birth = ?, is_active = ? WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.setDate(5, user.getDateOfBirth());
            stmt.setBoolean(6, user.isActive());
            stmt.setInt(7, user.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Дані користувача оновлено: " + user.getFullName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка при оновленні користувача: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Користувача видалено (ID: " + userId + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка при видаленні користувача: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deactivateUser(int userId) {
        String sql = "UPDATE users SET is_active = FALSE WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Користувача деактивовано (ID: " + userId + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка при деактивації користувача: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<User> searchByLastName(String lastName) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE last_name LIKE ? ORDER BY last_name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + lastName + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            System.out.println("✅ Знайдено користувачів з прізвищем '" + lastName + "': " + users.size());
            
        } catch (SQLException e) {
            System.err.println("❌ Помилка при пошуку користувачів: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка при підрахунку користувачів: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Допоміжний метод для витягування користувача з ResultSet
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setDateOfBirth(rs.getDate("date_of_birth"));
        user.setRegistrationDate(rs.getTimestamp("registration_date"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }
}
package dao;

import model.Payment;
import util.DatabaseConnection;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Реалізація інтерфейсу PaymentDAO
 */
public class PaymentDAOImpl implements PaymentDAO {

    private Connection connection;

    public PaymentDAOImpl() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("❌ Помилка підключення до БД", e);
        }
    }

    /**
     * Генерація унікального референс-номера для платежу
     * Формат: PAY-YYYYMMDD-HHMMSS-XXX (XXX - випадкове число)
     */
    private String generateReferenceNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        int random = (int) (Math.random() * 1000);
        return String.format("PAY-%s-%03d", timestamp, random);
    }

    @Override
    public boolean createPayment(Payment payment) {
        // Генерація унікального референс-номера
        String referenceNumber = generateReferenceNumber();

        String sql = "INSERT INTO payments (sender_account_id, recipient_account_id, " +
                "category_id, status_id, amount, currency, description, commission, reference_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getSenderAccountId());

            if (payment.getRecipientAccountId() != null) {
                stmt.setInt(2, payment.getRecipientAccountId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setInt(3, payment.getCategoryId());
            stmt.setInt(4, payment.getStatusId());
            stmt.setBigDecimal(5, payment.getAmount());
            stmt.setString(6, payment.getCurrency());
            stmt.setString(7, payment.getDescription());
            stmt.setBigDecimal(8, payment.getCommission());
            stmt.setString(9, referenceNumber);  // Встановлюємо згенерований референс

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    payment.setPaymentId(rs.getInt(1));
                    payment.setReferenceNumber(referenceNumber);  // Зберігаємо в об'єкт
                }
                System.out.println("✅ Платіж створено (ID: " + payment.getPaymentId() +
                        ", Референс: " + referenceNumber + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка при створенні платежу: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Payment getPaymentById(int paymentId) {
        String sql = "SELECT p.*, pc.category_name, ps.status_name, " +
                "CONCAT(u1.first_name, ' ', u1.last_name) AS sender_name, " +
                "CONCAT(u2.first_name, ' ', u2.last_name) AS recipient_name " +
                "FROM payments p " +
                "JOIN payment_categories pc ON p.category_id = pc.category_id " +
                "JOIN payment_statuses ps ON p.status_id = ps.status_id " +
                "JOIN accounts a1 ON p.sender_account_id = a1.account_id " +
                "JOIN users u1 ON a1.user_id = u1.user_id " +
                "LEFT JOIN accounts a2 ON p.recipient_account_id = a2.account_id " +
                "LEFT JOIN users u2 ON a2.user_id = u2.user_id " +
                "WHERE p.payment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractPaymentFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка при отриманні платежу: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.*, pc.category_name, ps.status_name, " +
                "CONCAT(u1.first_name, ' ', u1.last_name) AS sender_name, " +
                "CONCAT(u2.first_name, ' ', u2.last_name) AS recipient_name " +
                "FROM payments p " +
                "JOIN payment_categories pc ON p.category_id = pc.category_id " +
                "JOIN payment_statuses ps ON p.status_id = ps.status_id " +
                "JOIN accounts a1 ON p.sender_account_id = a1.account_id " +
                "JOIN users u1 ON a1.user_id = u1.user_id " +
                "LEFT JOIN accounts a2 ON p.recipient_account_id = a2.account_id " +
                "LEFT JOIN users u2 ON a2.user_id = u2.user_id " +
                "ORDER BY p.payment_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
            System.out.println("✅ Знайдено платежів: " + payments.size());

        } catch (SQLException e) {
            System.err.println("❌ Помилка при отриманні всіх платежів: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public List<Payment> getPaymentsByStatus(int statusId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.*, pc.category_name, ps.status_name, " +
                "CONCAT(u1.first_name, ' ', u1.last_name) AS sender_name, " +
                "CONCAT(u2.first_name, ' ', u2.last_name) AS recipient_name " +
                "FROM payments p " +
                "JOIN payment_categories pc ON p.category_id = pc.category_id " +
                "JOIN payment_statuses ps ON p.status_id = ps.status_id " +
                "JOIN accounts a1 ON p.sender_account_id = a1.account_id " +
                "JOIN users u1 ON a1.user_id = u1.user_id " +
                "LEFT JOIN accounts a2 ON p.recipient_account_id = a2.account_id " +
                "LEFT JOIN users u2 ON a2.user_id = u2.user_id " +
                "WHERE p.status_id = ? " +
                "ORDER BY p.payment_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, statusId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
            System.out.println("✅ Знайдено платежів зі статусом " + statusId + ": " + payments.size());

        } catch (SQLException e) {
            System.err.println("❌ Помилка при отриманні платежів за статусом: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public List<Payment> getPaymentsByUser(int userId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.*, pc.category_name, ps.status_name, " +
                "CONCAT(u1.first_name, ' ', u1.last_name) AS sender_name, " +
                "CONCAT(u2.first_name, ' ', u2.last_name) AS recipient_name " +
                "FROM payments p " +
                "JOIN payment_categories pc ON p.category_id = pc.category_id " +
                "JOIN payment_statuses ps ON p.status_id = ps.status_id " +
                "JOIN accounts a1 ON p.sender_account_id = a1.account_id " +
                "JOIN users u1 ON a1.user_id = u1.user_id " +
                "LEFT JOIN accounts a2 ON p.recipient_account_id = a2.account_id " +
                "LEFT JOIN users u2 ON a2.user_id = u2.user_id " +
                "WHERE a1.user_id = ? " +
                "ORDER BY p.payment_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Помилка при отриманні платежів користувача: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public List<Payment> getPaymentsByAccount(int accountId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.*, pc.category_name, ps.status_name " +
                "FROM payments p " +
                "JOIN payment_categories pc ON p.category_id = pc.category_id " +
                "JOIN payment_statuses ps ON p.status_id = ps.status_id " +
                "WHERE p.sender_account_id = ? " +
                "ORDER BY p.payment_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setSenderAccountId(rs.getInt("sender_account_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setCurrency(rs.getString("currency"));
                payment.setDescription(rs.getString("description"));
                payment.setPaymentDate(rs.getTimestamp("payment_date"));
                payment.setCategoryName(rs.getString("category_name"));
                payment.setStatusName(rs.getString("status_name"));
                payment.setReferenceNumber(rs.getString("reference_number"));
                payments.add(payment);
            }

        } catch (SQLException e) {
            System.err.println("❌ Помилка при отриманні платежів рахунку: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public List<Payment> getPaymentsByCategory(int categoryId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.*, pc.category_name, ps.status_name " +
                "FROM payments p " +
                "JOIN payment_categories pc ON p.category_id = pc.category_id " +
                "JOIN payment_statuses ps ON p.status_id = ps.status_id " +
                "WHERE p.category_id = ? " +
                "ORDER BY p.payment_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setDescription(rs.getString("description"));
                payment.setPaymentDate(rs.getTimestamp("payment_date"));
                payment.setCategoryName(rs.getString("category_name"));
                payment.setStatusName(rs.getString("status_name"));
                payment.setReferenceNumber(rs.getString("reference_number"));
                payments.add(payment);
            }

        } catch (SQLException e) {
            System.err.println("❌ Помилка при отриманні платежів за категорією: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public boolean updatePaymentStatus(int paymentId, int newStatusId) {
        String sql = "UPDATE payments SET status_id = ? WHERE payment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newStatusId);
            stmt.setInt(2, paymentId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Статус платежу оновлено (ID: " + paymentId + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка при оновленні статусу платежу: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean cancelPayment(int paymentId) {
        // Статус "Скасовано" має ID = 4
        return updatePaymentStatus(paymentId, 4);
    }

    @Override
    public BigDecimal getTotalPaymentsByUser(int userId) {
        String sql = "SELECT SUM(p.amount) AS total " +
                "FROM payments p " +
                "JOIN accounts a ON p.sender_account_id = a.account_id " +
                "WHERE a.user_id = ? AND p.status_id = 3"; // Тільки виконані

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка при підрахунку суми платежів: " + e.getMessage());
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public List<String> getPaymentStatisticsByCategory() {
        List<String> stats = new ArrayList<>();
        String sql = "SELECT pc.category_name, COUNT(p.payment_id) AS count, " +
                "SUM(p.amount) AS total " +
                "FROM payment_categories pc " +
                "LEFT JOIN payments p ON pc.category_id = p.category_id AND p.status_id = 3 " +
                "GROUP BY pc.category_id, pc.category_name " +
                "ORDER BY total DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String stat = String.format("%s: %d платежів, сума: %.2f UAH",
                        rs.getString("category_name"),
                        rs.getInt("count"),
                        rs.getBigDecimal("total") != null ? rs.getBigDecimal("total") : BigDecimal.ZERO);
                stats.add(stat);
            }

        } catch (SQLException e) {
            System.err.println("❌ Помилка при отриманні статистики: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }

    @Override
    public int countPaymentsByStatus(int statusId) {
        String sql = "SELECT COUNT(*) FROM payments WHERE status_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, statusId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Помилка при підрахунку платежів: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Допоміжний метод для витягування платежу з ResultSet
     */
    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setSenderAccountId(rs.getInt("sender_account_id"));

        int recipientId = rs.getInt("recipient_account_id");
        payment.setRecipientAccountId(rs.wasNull() ? null : recipientId);

        payment.setCategoryId(rs.getInt("category_id"));
        payment.setStatusId(rs.getInt("status_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setCurrency(rs.getString("currency"));
        payment.setDescription(rs.getString("description"));
        payment.setPaymentDate(rs.getTimestamp("payment_date"));
        payment.setCompletionDate(rs.getTimestamp("completion_date"));
        payment.setCommission(rs.getBigDecimal("commission"));
        payment.setReferenceNumber(rs.getString("reference_number"));

        // Додаткові поля
        payment.setCategoryName(rs.getString("category_name"));
        payment.setStatusName(rs.getString("status_name"));

        try {
            payment.setSenderName(rs.getString("sender_name"));
            payment.setRecipientName(rs.getString("recipient_name"));
        } catch (SQLException e) {
            // Ігноруємо, якщо поля не існують у запиті
        }

        return payment;
    }
}
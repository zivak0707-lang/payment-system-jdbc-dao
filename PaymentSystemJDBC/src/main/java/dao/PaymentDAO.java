package dao;

import model.Payment;
import java.math.BigDecimal;
import java.util.List;

/**
 * Інтерфейс PaymentDAO - визначає методи для роботи з платежами
 */
public interface PaymentDAO {
    
    /**
     * Створити новий платіж
     * @param payment - об'єкт платежу
     * @return true якщо успішно
     */
    boolean createPayment(Payment payment);
    
    /**
     * Отримати платіж за ID
     * @param paymentId - ID платежу
     * @return об'єкт Payment або null
     */
    Payment getPaymentById(int paymentId);
    
    /**
     * Отримати всі платежі
     * @return список всіх платежів
     */
    List<Payment> getAllPayments();
    
    /**
     * Отримати платежі за статусом
     * @param statusId - ID статусу
     * @return список платежів
     */
    List<Payment> getPaymentsByStatus(int statusId);
    
    /**
     * Отримати платежі користувача (як відправника)
     * @param userId - ID користувача
     * @return список платежів
     */
    List<Payment> getPaymentsByUser(int userId);
    
    /**
     * Отримати платежі з рахунку
     * @param accountId - ID рахунку
     * @return список платежів
     */
    List<Payment> getPaymentsByAccount(int accountId);
    
    /**
     * Отримати платежі за категорією
     * @param categoryId - ID категорії
     * @return список платежів
     */
    List<Payment> getPaymentsByCategory(int categoryId);
    
    /**
     * Оновити статус платежу
     * @param paymentId - ID платежу
     * @param newStatusId - новий ID статусу
     * @return true якщо успішно
     */
    boolean updatePaymentStatus(int paymentId, int newStatusId);
    
    /**
     * Скасувати платіж
     * @param paymentId - ID платежу
     * @return true якщо успішно
     */
    boolean cancelPayment(int paymentId);
    
    /**
     * Отримати загальну суму платежів користувача
     * @param userId - ID користувача
     * @return сума платежів
     */
    BigDecimal getTotalPaymentsByUser(int userId);
    
    /**
     * Отримати статистику платежів за категоріями
     * @return список з даними статистики
     */
    List<String> getPaymentStatisticsByCategory();
    
    /**
     * Підрахувати кількість платежів за статусом
     * @param statusId - ID статусу
     * @return кількість платежів
     */
    int countPaymentsByStatus(int statusId);
}
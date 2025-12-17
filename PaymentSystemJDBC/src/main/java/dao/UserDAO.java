package dao;

import model.User;
import java.util.List;

/**
 * Інтерфейс UserDAO - визначає методи для роботи з користувачами
 * CRUD операції: Create, Read, Update, Delete
 */
public interface UserDAO {
    
    /**
     * Додати нового користувача
     * @param user - об'єкт користувача
     * @return true якщо успішно, false якщо помилка
     */
    boolean addUser(User user);
    
    /**
     * Отримати користувача за ID
     * @param userId - ID користувача
     * @return об'єкт User або null
     */
    User getUserById(int userId);
    
    /**
     * Отримати користувача за email
     * @param email - email користувача
     * @return об'єкт User або null
     */
    User getUserByEmail(String email);
    
    /**
     * Отримати всіх користувачів
     * @return список всіх користувачів
     */
    List<User> getAllUsers();
    
    /**
     * Отримати тільки активних користувачів
     * @return список активних користувачів
     */
    List<User> getActiveUsers();
    
    /**
     * Оновити дані користувача
     * @param user - об'єкт з новими даними
     * @return true якщо успішно, false якщо помилка
     */
    boolean updateUser(User user);
    
    /**
     * Видалити користувача за ID
     * @param userId - ID користувача
     * @return true якщо успішно, false якщо помилка
     */
    boolean deleteUser(int userId);
    
    /**
     * Деактивувати користувача (м'яке видалення)
     * @param userId - ID користувача
     * @return true якщо успішно, false якщо помилка
     */
    boolean deactivateUser(int userId);
    
    /**
     * Пошук користувачів за прізвищем
     * @param lastName - прізвище для пошуку
     * @return список знайдених користувачів
     */
    List<User> searchByLastName(String lastName);
    
    /**
     * Підрахувати загальну кількість користувачів
     * @return кількість користувачів
     */
    int countUsers();
}
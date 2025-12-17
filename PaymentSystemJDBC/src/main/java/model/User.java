package model;

import java.sql.Timestamp;
import java.sql.Date;

/**
 * Модель користувача (відповідає таблиці users)
 */
public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String passwordHash;
    private Date dateOfBirth;
    private Timestamp registrationDate;
    private Timestamp lastLogin;
    private boolean isActive;

    // Конструктор без параметрів
    public User() {
    }

    // Конструктор з основними параметрами
    public User(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    // Повний конструктор
    public User(int userId, String firstName, String lastName, String email,
                String phone, String passwordHash, Date dateOfBirth,
                Timestamp registrationDate, Timestamp lastLogin, boolean isActive) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.dateOfBirth = dateOfBirth;
        this.registrationDate = registrationDate;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
    }

    // Геттери та сеттери
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Метод для виводу інформації
    @Override
    public String toString() {
        return String.format("User{id=%d, ПІБ='%s %s', email='%s', телефон='%s', активний=%s}",
                userId, firstName, lastName, email, phone, isActive ? "Так" : "Ні");
    }

    // Метод для отримання повного імені
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
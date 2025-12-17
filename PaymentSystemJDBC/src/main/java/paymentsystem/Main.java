import dao.*;
import model.*;
import util.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Головний клас для тестування системи платежів
 */
public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static UserDAO userDAO = new UserDAOImpl();
    private static PaymentDAO paymentDAO = new PaymentDAOImpl();

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║   СИСТЕМА ПЛАТЕЖІВ - Лабораторна робота №7       ║");
        System.out.println("║   Тестування JDBC для БД payment_system          ║");
        System.out.println("╚═══════════════════════════════════════════════════╝\n");

        // Крок 1: Тест підключення
        if (!testConnection()) {
            System.err.println("❌ Не вдалося підключитися до бази даних!");
            System.err.println("Перевірте налаштування в DatabaseConnection.java");
            return;
        }

        // Головне меню
        boolean running = true;
        while (running) {
            showMenu();
            int choice = getIntInput("Виберіть опцію: ");

            switch (choice) {
                case 1:
                    testUserOperations();
                    break;
                case 2:
                    testPaymentOperations();
                    break;
                case 3:
                    showAllUsers();
                    break;
                case 4:
                    showAllPayments();
                    break;
                case 5:
                    searchUser();
                    break;
                case 6:
                    showPaymentsByUser();
                    break;
                case 7:
                    showStatistics();
                    break;
                case 8:
                    testCreateUser();
                    break;
                case 9:
                    testCreatePayment();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("❌ Невірний вибір!");
            }

            if (running) {
                System.out.println("\nНатисніть Enter для продовження...");
                scanner.nextLine();
            }
        }

        // Закриття з'єднання
        System.out.println("\n✅ Програма завершена. До побачення!");
    }

    /**
     * Показати головне меню
     */
    private static void showMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                     ГОЛОВНЕ МЕНЮ");
        System.out.println("=".repeat(60));
        System.out.println("1. 🧪 Тест операцій з користувачами (CRUD)");
        System.out.println("2. 🧪 Тест операцій з платежами (CRUD)");
        System.out.println("3. 👥 Показати всіх користувачів");
        System.out.println("4. 💰 Показати всі платежі");
        System.out.println("5. 🔍 Пошук користувача");
        System.out.println("6. 📊 Показати платежі користувача");
        System.out.println("7. 📈 Статистика платежів");
        System.out.println("8. ➕ Додати нового користувача");
        System.out.println("9. ➕ Створити новий платіж");
        System.out.println("0. 🚪 Вихід");
        System.out.println("=".repeat(60));
    }

    /**
     * Тест підключення до БД
     */
    private static boolean testConnection() {
        System.out.println("\n📡 КРОК 1: Перевірка підключення до бази даних...");
        System.out.println("-".repeat(60));
        return DatabaseConnection.testConnection();
    }

    /**
     * Тестування операцій з користувачами
     */
    private static void testUserOperations() {
        System.out.println("\n🧪 ТЕСТ: Операції з користувачами (CRUD)");
        System.out.println("=".repeat(60));

        // CREATE - Додавання
        System.out.println("\n1️⃣ CREATE - Додавання нового користувача:");
        User newUser = new User("Тестовий", "Користувач",
                "test@example.com", "+380501111111");
        newUser.setPasswordHash("test_hash_123");
        newUser.setDateOfBirth(Date.valueOf("1995-01-01"));
        newUser.setActive(true);

        if (userDAO.addUser(newUser)) {
            System.out.println("   → ID нового користувача: " + newUser.getUserId());
        }

        // READ - Читання
        System.out.println("\n2️⃣ READ - Отримання користувача за ID:");
        User foundUser = userDAO.getUserById(newUser.getUserId());
        if (foundUser != null) {
            System.out.println("   → " + foundUser);
        }

        // UPDATE - Оновлення
        System.out.println("\n3️⃣ UPDATE - Оновлення даних користувача:");
        foundUser.setPhone("+380502222222");
        foundUser.setEmail("updated@example.com");
        userDAO.updateUser(foundUser);

        // READ після UPDATE
        System.out.println("\n4️⃣ READ після UPDATE:");
        User updatedUser = userDAO.getUserById(foundUser.getUserId());
        if (updatedUser != null) {
            System.out.println("   → " + updatedUser);
        }

        // DELETE - Видалення
        System.out.println("\n5️⃣ DELETE - Видалення користувача:");
        userDAO.deleteUser(newUser.getUserId());

        System.out.println("\n✅ Тест операцій з користувачами завершено!");
    }

    /**
     * Тестування операцій з платежами
     */
    private static void testPaymentOperations() {
        System.out.println("\n🧪 ТЕСТ: Операції з платежами");
        System.out.println("=".repeat(60));

        // CREATE - Створення платежу
        System.out.println("\n1️⃣ CREATE - Створення нового платежу:");
        Payment newPayment = new Payment(
                1,              // sender_account_id
                null,           // recipient_account_id (NULL для оплати послуг)
                2,              // category_id (Мобільний зв'язок)
                1,              // status_id (Очікує обробки)
                new BigDecimal("100.00"),
                "Тестовий платіж через JDBC"
        );

        if (paymentDAO.createPayment(newPayment)) {
            System.out.println("   → ID нового платежу: " + newPayment.getPaymentId());
        }

        // READ - Читання
        System.out.println("\n2️⃣ READ - Отримання платежу за ID:");
        Payment foundPayment = paymentDAO.getPaymentById(newPayment.getPaymentId());
        if (foundPayment != null) {
            System.out.println("   → " + foundPayment);
        }

        // UPDATE - Оновлення статусу
        System.out.println("\n3️⃣ UPDATE - Оновлення статусу платежу:");
        paymentDAO.updatePaymentStatus(newPayment.getPaymentId(), 3); // Виконано

        // READ після UPDATE
        System.out.println("\n4️⃣ READ після UPDATE:");
        Payment updatedPayment = paymentDAO.getPaymentById(newPayment.getPaymentId());
        if (updatedPayment != null) {
            System.out.println("   → Новий статус: " + updatedPayment.getStatusName());
        }

        System.out.println("\n✅ Тест операцій з платежами завершено!");
    }

    /**
     * Показати всіх користувачів
     */
    private static void showAllUsers() {
        System.out.println("\n👥 Список всіх користувачів:");
        System.out.println("=".repeat(60));

        List<User> users = userDAO.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("❌ Користувачів не знайдено");
        } else {
            System.out.printf("%-5s %-20s %-30s %-15s %-10s%n",
                    "ID", "ПІБ", "Email", "Телефон", "Активний");
            System.out.println("-".repeat(60));

            for (User user : users) {
                System.out.printf("%-5d %-20s %-30s %-15s %-10s%n",
                        user.getUserId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.isActive() ? "Так" : "Ні");
            }

            System.out.println("\n📊 Всього користувачів: " + users.size());
        }
    }

    /**
     * Показати всі платежі
     */
    private static void showAllPayments() {
        System.out.println("\n💰 Список всіх платежів:");
        System.out.println("=".repeat(60));

        List<Payment> payments = paymentDAO.getAllPayments();

        if (payments.isEmpty()) {
            System.out.println("❌ Платежів не знайдено");
        } else {
            for (int i = 0; i < Math.min(payments.size(), 10); i++) {
                Payment p = payments.get(i);
                System.out.printf("%d. [%s] %s грн - %s (Статус: %s)%n",
                        i + 1,
                        p.getReferenceNumber(),
                        p.getAmount(),
                        p.getDescription(),
                        p.getStatusName());
            }

            System.out.println("\n📊 Всього платежів: " + payments.size());
            if (payments.size() > 10) {
                System.out.println("   (показано перші 10)");
            }
        }
    }

    /**
     * Пошук користувача
     */
    private static void searchUser() {
        System.out.print("\n🔍 Введіть прізвище для пошуку: ");
        String lastName = scanner.nextLine();

        List<User> users = userDAO.searchByLastName(lastName);

        if (users.isEmpty()) {
            System.out.println("❌ Користувачів з таким прізвищем не знайдено");
        } else {
            System.out.println("\n✅ Знайдено користувачів: " + users.size());
            for (User user : users) {
                System.out.println("   → " + user);
            }
        }
    }

    /**
     * Показати платежі користувача
     */
    private static void showPaymentsByUser() {
        int userId = getIntInput("\n💳 Введіть ID користувача: ");

        User user = userDAO.getUserById(userId);
        if (user == null) {
            System.out.println("❌ Користувача з таким ID не знайдено");
            return;
        }

        System.out.println("\n📊 Платежі користувача: " + user.getFullName());
        System.out.println("=".repeat(60));

        List<Payment> payments = paymentDAO.getPaymentsByUser(userId);

        if (payments.isEmpty()) {
            System.out.println("❌ Платежів не знайдено");
        } else {
            for (Payment p : payments) {
                System.out.printf("• %s грн - %s [%s]%n",
                        p.getAmount(),
                        p.getDescription(),
                        p.getStatusName());
            }

            BigDecimal total = paymentDAO.getTotalPaymentsByUser(userId);
            System.out.println("\n💰 Загальна сума виконаних платежів: " + total + " UAH");
        }
    }

    /**
     * Показати статистику
     */
    private static void showStatistics() {
        System.out.println("\n📈 СТАТИСТИКА СИСТЕМИ ПЛАТЕЖІВ");
        System.out.println("=".repeat(60));

        // Кількість користувачів
        int totalUsers = userDAO.countUsers();
        System.out.println("👥 Всього користувачів: " + totalUsers);

        // Кількість платежів за статусами
        System.out.println("\n💰 Платежі за статусами:");
        String[] statuses = {"Очікує", "В обробці", "Виконано", "Скасовано", "Відхилено"};
        for (int i = 1; i <= 5; i++) {
            int count = paymentDAO.countPaymentsByStatus(i);
            System.out.printf("   %s: %d%n", statuses[i-1], count);
        }

        // Статистика по категоріях
        System.out.println("\n📊 Статистика по категоріях:");
        List<String> stats = paymentDAO.getPaymentStatisticsByCategory();
        for (String stat : stats) {
            System.out.println("   " + stat);
        }
    }

    /**
     * Створити нового користувача (інтерактивно)
     */
    private static void testCreateUser() {
        System.out.println("\n➕ ДОДАВАННЯ НОВОГО КОРИСТУВАЧА");
        System.out.println("=".repeat(60));

        System.out.print("Ім'я: ");
        String firstName = scanner.nextLine();

        System.out.print("Прізвище: ");
        String lastName = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Телефон: ");
        String phone = scanner.nextLine();

        User user = new User(firstName, lastName, email, phone);
        user.setPasswordHash("default_hash");
        user.setActive(true);

        if (userDAO.addUser(user)) {
            System.out.println("\n✅ Користувача успішно додано! ID: " + user.getUserId());
        } else {
            System.out.println("\n❌ Помилка при додаванні користувача");
        }
    }

    /**
     * Створити новий платіж (інтерактивно)
     */
    private static void testCreatePayment() {
        System.out.println("\n➕ СТВОРЕННЯ НОВОГО ПЛАТЕЖУ");
        System.out.println("=".repeat(60));

        int senderAccountId = getIntInput("ID рахунку відправника: ");

        System.out.print("ID рахунку одержувача (Enter якщо немає): ");
        String recipientStr = scanner.nextLine();
        Integer recipientAccountId = recipientStr.isEmpty() ? null : Integer.parseInt(recipientStr);

        int categoryId = getIntInput("ID категорії (1-10): ");

        System.out.print("Сума: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());

        System.out.print("Опис: ");
        String description = scanner.nextLine();

        Payment payment = new Payment(senderAccountId, recipientAccountId,
                categoryId, 1, amount, description);

        if (paymentDAO.createPayment(payment)) {
            System.out.println("\n✅ Платіж успішно створено! ID: " + payment.getPaymentId());
        } else {
            System.out.println("\n❌ Помилка при створенні платежу");
        }
    }

    /**
     * Допоміжний метод для введення цілих чисел
     */
    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Помилка! Введіть число: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Очистити буфер
        return value;
    }
}
package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Модель платежу (відповідає таблиці payments)
 */
public class Payment {
    private int paymentId;
    private int senderAccountId;
    private Integer recipientAccountId;  // може бути NULL
    private int categoryId;
    private int statusId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private Timestamp paymentDate;
    private Timestamp completionDate;
    private BigDecimal commission;
    private String referenceNumber;
    
    // Додаткові поля для відображення
    private String categoryName;
    private String statusName;
    private String senderName;
    private String recipientName;

    // Конструктор без параметрів
    public Payment() {
    }

    // Конструктор для створення нового платежу
    public Payment(int senderAccountId, Integer recipientAccountId, int categoryId, 
                   int statusId, BigDecimal amount, String description) {
        this.senderAccountId = senderAccountId;
        this.recipientAccountId = recipientAccountId;
        this.categoryId = categoryId;
        this.statusId = statusId;
        this.amount = amount;
        this.description = description;
        this.currency = "UAH";
        this.commission = BigDecimal.ZERO;
    }

    // Повний конструктор
    public Payment(int paymentId, int senderAccountId, Integer recipientAccountId,
                   int categoryId, int statusId, BigDecimal amount, String currency,
                   String description, Timestamp paymentDate, Timestamp completionDate,
                   BigDecimal commission, String referenceNumber) {
        this.paymentId = paymentId;
        this.senderAccountId = senderAccountId;
        this.recipientAccountId = recipientAccountId;
        this.categoryId = categoryId;
        this.statusId = statusId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.paymentDate = paymentDate;
        this.completionDate = completionDate;
        this.commission = commission;
        this.referenceNumber = referenceNumber;
    }

    // Геттери та сеттери
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getSenderAccountId() {
        return senderAccountId;
    }

    public void setSenderAccountId(int senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public Integer getRecipientAccountId() {
        return recipientAccountId;
    }

    public void setRecipientAccountId(Integer recipientAccountId) {
        this.recipientAccountId = recipientAccountId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Timestamp getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Timestamp completionDate) {
        this.completionDate = completionDate;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    @Override
    public String toString() {
        return String.format("Payment{id=%d, референс='%s', сума=%s %s, статус='%s', дата=%s}",
                paymentId, referenceNumber, amount, currency, statusName, paymentDate);
    }
}
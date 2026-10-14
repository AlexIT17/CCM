package com.example.ccm.data;

public class Product {
    public final long id;
    public final long bankId;
    public final String type;   // "Кредит" | "Вклад" | "Ипотека"
    public final String name;

    public Product(long id, long bankId, String type, String name) {
        this.id = id;
        this.bankId = bankId;
        this.type = type;
        this.name = name;
    }
}
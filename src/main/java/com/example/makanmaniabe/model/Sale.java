package com.example.makanmaniabe.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToMany
    @JoinTable(name = "sale_items", joinColumns = @JoinColumn(name = "sale_id"), inverseJoinColumns = @JoinColumn(name = "food_item_id"))
    private Set<FoodItem> items = new HashSet<>();

    @Column(nullable = false)
    private String deliveryStatus;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Transient
    private double totalAmount;

    @Transient
    private int totalItems;

}



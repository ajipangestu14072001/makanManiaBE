package com.example.makanmaniabe.payload;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class SaleRequest {
    private Set<UUID> items;
    private String deliveryStatus;
}

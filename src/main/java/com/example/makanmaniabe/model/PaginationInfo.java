package com.example.makanmaniabe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationInfo {
    private int page;
    private int limit;
    private long totalData;
    private int totalPages;
}

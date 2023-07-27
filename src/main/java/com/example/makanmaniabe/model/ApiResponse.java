package com.example.makanmaniabe.model;

import lombok.Data;

import java.util.List;

@Data
public class ApiResponse<T> {
    private int statusCode;
    private String message;
    private T data;
    private PaginationInfo paginationInfo;

    public ApiResponse(int statusCode, String message, T data, PaginationInfo paginationInfo) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.paginationInfo = paginationInfo;
    }

    public ApiResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

}



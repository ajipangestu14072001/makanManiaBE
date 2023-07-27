package com.example.makanmaniabe.controller;

import com.example.makanmaniabe.model.ApiResponse;
import com.example.makanmaniabe.model.FoodItem;
import com.example.makanmaniabe.model.PaginationInfo;
import com.example.makanmaniabe.model.Sale;
import com.example.makanmaniabe.payload.SaleRequest;
import com.example.makanmaniabe.repository.FoodItemRepository;
import com.example.makanmaniabe.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleRepository saleRepository;
    private final FoodItemRepository foodItemRepository;

    @Autowired
    public SaleController(SaleRepository saleRepository, FoodItemRepository foodItemRepository) {
        this.saleRepository = saleRepository;
        this.foodItemRepository = foodItemRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Sale>>> getAllSales(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<Sale> salePage = saleRepository.findAll(pageable);

        PaginationInfo paginationInfo = new PaginationInfo(
                page,
                limit,
                salePage.getTotalElements(),
                salePage.getTotalPages()
        );

        ApiResponse<List<Sale>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Retrieved all sales",
                salePage.getContent(),
                paginationInfo
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Sale>> getSaleById(@PathVariable UUID id) {
        Optional<Sale> sale = saleRepository.findById(id);
        if (sale.isPresent()) {
            ApiResponse<Sale> response = new ApiResponse<>(HttpStatus.OK.value(), "Retrieved sale by ID", sale.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<Sale> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Sale not found with ID: " + id, null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Sale>> createSale(@RequestBody SaleRequest saleRequest) {
        Sale newSale = new Sale();

        Set<FoodItem> items = new HashSet<>();
        double totalAmount = 0;
        for (UUID itemId : saleRequest.getItems()) {
            Optional<FoodItem> optionalFoodItem = foodItemRepository.findById(itemId);
            if (optionalFoodItem.isPresent()) {
                FoodItem foodItem = optionalFoodItem.get();
                items.add(foodItem);
                totalAmount += foodItem.getPrice();
            }
        }
        newSale.setItems(items);
        newSale.setTotalAmount(totalAmount);
        newSale.setTotalItems(items.size());
        newSale.setDeliveryStatus(saleRequest.getDeliveryStatus());
        newSale.setOrderDate(LocalDate.now());
        Sale savedSale = saleRepository.save(newSale);

        ApiResponse<Sale> response = new ApiResponse<>(HttpStatus.CREATED.value(), "Sale created successfully", savedSale);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Sale>> updateSale(@PathVariable UUID id, @RequestBody Sale updatedSale) {
        Optional<Sale> optionalSale = saleRepository.findById(id);
        if (optionalSale.isPresent()) {
            Sale existingSale = optionalSale.get();
            existingSale.setItems(updatedSale.getItems());
            existingSale.setDeliveryStatus(updatedSale.getDeliveryStatus());
            existingSale.setOrderDate(updatedSale.getOrderDate());

            Sale updatedSaleData = saleRepository.save(existingSale);
            ApiResponse<Sale> response = new ApiResponse<>(HttpStatus.OK.value(), "Sale updated successfully", updatedSaleData);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<Sale> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Sale not found with ID: " + id, null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSale(@PathVariable UUID id) {
        Optional<Sale> optionalSale = saleRepository.findById(id);
        if (optionalSale.isPresent()) {
            saleRepository.deleteById(id);
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "Sale deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } else {
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Sale not found with ID: " + id, null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}


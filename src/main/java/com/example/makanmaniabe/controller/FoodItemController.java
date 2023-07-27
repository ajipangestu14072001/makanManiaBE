package com.example.makanmaniabe.controller;

import com.example.makanmaniabe.model.ApiResponse;
import com.example.makanmaniabe.model.FoodItem;
import com.example.makanmaniabe.model.PaginationInfo;
import com.example.makanmaniabe.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/fooditems")
public class FoodItemController {

    private final FoodItemRepository foodItemRepository;

    @Autowired
    public FoodItemController(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Iterable<FoodItem>>> getAllFoodItems(@RequestParam(defaultValue = "1") int page,
                                                                           @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<FoodItem> foodItemsPage = foodItemRepository.findAll(pageable);

        PaginationInfo paginationInfo = new PaginationInfo(page, limit, foodItemsPage.getTotalElements(), foodItemsPage.getTotalPages());

        ApiResponse<Iterable<FoodItem>> response = new ApiResponse<>(HttpStatus.OK.value(), "Retrieved all food items", foodItemsPage.getContent(), paginationInfo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FoodItem>> addFoodItem(@RequestBody FoodItem foodItem) {
        FoodItem newFoodItem = foodItemRepository.save(foodItem);

        ApiResponse<FoodItem> response = new ApiResponse<>(200, "Items Insert successfully!", newFoodItem);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodItem>> getFoodItemById(@PathVariable UUID id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FoodItem not found with id: " + id));

        ApiResponse<FoodItem> response = new ApiResponse<>(HttpStatus.OK.value(), "Retrieved food item by ID", foodItem);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodItem>> deleteFoodItem(@PathVariable UUID id) {
        Optional<FoodItem> optionalFoodItem = foodItemRepository.findById(id);
        if (optionalFoodItem.isPresent()) {
            FoodItem foodItem = optionalFoodItem.get();
            foodItemRepository.deleteById(id);
            ApiResponse<FoodItem> response = new ApiResponse<>(HttpStatus.OK.value(), "Food item deleted successfully", foodItem);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<FoodItem> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Food item not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodItem>> updateFoodItem(@PathVariable UUID id, @RequestBody FoodItem updatedFoodItem) {
        Optional<FoodItem> optionalFoodItem = foodItemRepository.findById(id);
        if (optionalFoodItem.isPresent()) {
            FoodItem existingFoodItem = optionalFoodItem.get();
            existingFoodItem.setName(updatedFoodItem.getName());
            existingFoodItem.setImageUrl(updatedFoodItem.getImageUrl());
            existingFoodItem.setDeliveryTime(updatedFoodItem.getDeliveryTime());
            existingFoodItem.setDistance(updatedFoodItem.getDistance());
            existingFoodItem.setRating(updatedFoodItem.getRating());
            FoodItem updatedItem = foodItemRepository.save(existingFoodItem);

            ApiResponse<FoodItem> response = new ApiResponse<>(HttpStatus.OK.value(), "Food item updated successfully", updatedItem);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<FoodItem> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Food item not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}




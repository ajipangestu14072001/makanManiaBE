package com.example.makanmaniabe.controller;

import com.example.makanmaniabe.model.ApiResponse;
import com.example.makanmaniabe.model.Cart;
import com.example.makanmaniabe.model.FoodItem;
import com.example.makanmaniabe.model.User;
import com.example.makanmaniabe.repository.CartRepository;
import com.example.makanmaniabe.repository.FoodItemRepository;
import com.example.makanmaniabe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartRepository cartRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public CartController(CartRepository cartRepository, FoodItemRepository foodItemRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<ApiResponse<Cart>> getCartById(@PathVariable UUID cartId) {
        Optional<Cart> cart = cartRepository.findById(cartId);
        if (cart.isPresent()) {
            ApiResponse<Cart> response = new ApiResponse<>(HttpStatus.OK.value(), "Retrieved cart by ID", cart.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<Cart> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Cart not found with ID: " + cartId, null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{userId}/add-item/{itemId}")
    public ResponseEntity<ApiResponse<Cart>> addItemToCart(@PathVariable UUID userId, @PathVariable UUID itemId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<FoodItem> itemOptional = foodItemRepository.findById(itemId);

        if (userOptional.isPresent() && itemOptional.isPresent()) {
            User user = userOptional.get();
            FoodItem item = itemOptional.get();

            Cart cart;
            if (user.getCart() != null) {
                cart = user.getCart();
            } else {
                cart = new Cart();
                cart.setUser(user);
            }

            cart.getItems().add(item);
            cart.setTotalItems(cart.getTotalItems() + 1);
            cart.setTotalAmount(cart.getTotalAmount() + item.getPrice());

            Cart savedCart = cartRepository.save(cart);

            ApiResponse<Cart> response = new ApiResponse<>(HttpStatus.OK.value(), "Item added to cart successfully", savedCart);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<Cart> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User or item not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{userId}/remove-item/{itemId}")
    public ResponseEntity<ApiResponse<Cart>> removeItemFromCart(@PathVariable UUID userId, @PathVariable UUID itemId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<FoodItem> itemOptional = foodItemRepository.findById(itemId);

        if (userOptional.isPresent() && itemOptional.isPresent()) {
            User user = userOptional.get();
            FoodItem item = itemOptional.get();

            if (user.getCart() != null) {
                Cart cart = user.getCart();
                if (cart.getItems().contains(item)) {
                    cart.getItems().remove(item);
                    cart.setTotalItems(cart.getTotalItems() - 1);
                    cart.setTotalAmount(cart.getTotalAmount() - item.getPrice());

                    Cart savedCart = cartRepository.save(cart);

                    ApiResponse<Cart> response = new ApiResponse<>(HttpStatus.OK.value(), "Item removed from cart successfully", savedCart);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }

            ApiResponse<Cart> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Item not found in cart", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            ApiResponse<Cart> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User or item not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}


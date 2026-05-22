package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.AddItemCartRequest;
import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.dtos.UpdateCartItemRequest;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
public class CartController {
    CartService cartService;
    @PostMapping
    public ResponseEntity<CartDto>createCart(
            UriComponentsBuilder uriBuilder
    ){
        var cartDto  = cartService.createCart();
        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);

    }
    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addToCart(@PathVariable(name = "cartId")UUID id, @RequestBody AddItemCartRequest request){
        var cartItemDto = cartService.addToCart(id,request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);

    }
    @GetMapping("/{cartId}")
    public CartDto getCart(@PathVariable(name = "cartId") UUID id){
        return cartService.getCart(id);
    }
    @PutMapping("/{cartId}/items/{productId}")
    public CartItemDto updateCartItem(
            @PathVariable(name = "cartId") UUID  cartId,
            @PathVariable(name = "productId") Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
            ){
        return cartService.updateCartItem(cartId,productId,request.getQuantity());
    }
    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> removeItem(
            @PathVariable(name = "cartId") UUID cartId,
            @PathVariable(name = "productId") Long productId
    ){
        cartService.removeItem(cartId,productId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> clearCart(
            @PathVariable UUID cartId
    ){
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }
    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleCartNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error","Cart not found")
        );
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleProductNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error","Product not found in the cart")
        );
    }
}

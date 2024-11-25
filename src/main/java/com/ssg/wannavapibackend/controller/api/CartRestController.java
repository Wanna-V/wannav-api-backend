package com.ssg.wannavapibackend.controller.api;

import com.ssg.wannavapibackend.dto.request.CartItemQuantityUpdateDTO;
import com.ssg.wannavapibackend.dto.request.CartRequestDTO;
import com.ssg.wannavapibackend.dto.response.CartResponseDTO;
import com.ssg.wannavapibackend.service.CartService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartRestController {

    private final CartService cartService;
    final Long userId = 1L; // Security 적용 후 삭제 예정

    @PostMapping()
    public ResponseEntity<Map<String, String>> addCartItem(
        @RequestBody @Valid CartRequestDTO requestDTO) {
        cartService.addCartItem(userId, requestDTO);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getCartItemList() {

        List<CartResponseDTO> cartItems = cartService.getCartItemList(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", cartItems);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping()
    public ResponseEntity<Map<String, String>> updateCartItemQuantity(@RequestBody @Valid
    CartItemQuantityUpdateDTO updateDTO) {
        cartService.updateCartItemQuantity(updateDTO);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Map<String, String>> deleteCartItem(@PathVariable Long cartId) {
        cartService.deleteCartItem(cartId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<Map<String, String>> deleteCartItems(@RequestBody List<Long> cartIds) {
        cartService.deleteCartItems(cartIds);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

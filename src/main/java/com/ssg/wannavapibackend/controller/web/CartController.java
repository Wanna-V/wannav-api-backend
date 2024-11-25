package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.dto.response.CartResponseDTO;
import com.ssg.wannavapibackend.service.CartService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @GetMapping()
    public String getCartList(Model model) {
        Long userId = 1L; // Security 적용 후 삭제 예정

        List<CartResponseDTO> cartItems = cartService.getCartItemList(userId);
        model.addAttribute("cartItems", cartItems);

        return "cart/cart";
    }
}

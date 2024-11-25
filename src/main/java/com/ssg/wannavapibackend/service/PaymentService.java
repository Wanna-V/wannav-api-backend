package com.ssg.wannavapibackend.service;


import com.ssg.wannavapibackend.dto.request.ProductPurchaseRequestDTO;
import com.ssg.wannavapibackend.dto.request.PaymentConfirmRequestDTO;
import com.ssg.wannavapibackend.dto.response.CheckoutResponseDTO;
import com.ssg.wannavapibackend.dto.response.PaymentConfirmResponseDTO;
import com.ssg.wannavapibackend.dto.response.PaymentResponseDTO;
import java.util.List;

public interface PaymentService {

    CheckoutResponseDTO processCartCheckout(Long userId, List<Long> cartIds);

    CheckoutResponseDTO processDirectProductCheckout(Long userId,
        ProductPurchaseRequestDTO productRequestDTO);

    PaymentResponseDTO generateOrderId();

    PaymentConfirmResponseDTO sendRequest(Long userId, PaymentConfirmRequestDTO requestDTO);

    void decrease(Long productId, int quantity);
}

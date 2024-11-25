package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.domain.Product;
import com.ssg.wannavapibackend.dto.request.CartItemQuantityUpdateDTO;
import com.ssg.wannavapibackend.dto.request.CartRequestDTO;
import com.ssg.wannavapibackend.dto.response.ProductResponseDTO;
import java.util.List;

public interface ProductService {

    List<ProductResponseDTO> getProductList();

    Product getProduct(Long productId);

}

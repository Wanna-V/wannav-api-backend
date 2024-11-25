package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.domain.Restaurant;
import com.ssg.wannavapibackend.dto.request.OCRRequestDTO;
import com.ssg.wannavapibackend.dto.response.OCRResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface OCRService {

    OCRResponseDTO findReceiptData(OCRRequestDTO ocrRequest);

    Restaurant findCorrectRestaurant(String name);

    LocalDate findCorrectVisitDate(String visitDate);
}

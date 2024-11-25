package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.domain.Restaurant;
import com.ssg.wannavapibackend.dto.request.OCRRequestDTO;
import com.ssg.wannavapibackend.dto.response.OCRResponseDTO;
import com.ssg.wannavapibackend.repository.RestaurantRepository;
import com.ssg.wannavapibackend.service.OCRService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class OCRServiceImpl implements OCRService {

    private final RestaurantRepository restaurantRepository;

    @Value("${naver.ocr.invoke_url}")
    private String url;

    @Value("${naver.ocr.secret_key}")
    private String secretKey;

    /**
     * 영수증 정보 조회 - 식당명, 주소, 방문일자 추출
     *
     * @param ocrRequest
     * @return
     */
    public OCRResponseDTO findReceiptData(OCRRequestDTO ocrRequest) {
        RestTemplate restTemplate = new RestTemplate();

        //Request - Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-OCR-SECRET", secretKey);

        //Request - Body
        HttpEntity<OCRRequestDTO> body = new HttpEntity<>(ocrRequest, headers);

        return restTemplate.postForObject(url, body, OCRResponseDTO.class);
    }

    /**
     * 영수증 정보에서 가져온 식당명을 기반으로 하여 DB에 저장된 식당 조회
     *
     * @param name
     * @return
     */
    public Restaurant findCorrectRestaurant(String name) {
        //괄호 앞, 괄호 내 단어 추출
        Pattern pattern = Pattern.compile("^(\\S+)\\s*(?:\\(([^)]+)\\))?$");
        Matcher matcher = pattern.matcher(name);

        if (matcher.find()) {
            if (matcher.group(2) != null) {
                return restaurantRepository.findByNameContainingAndNameContaining(matcher.group(1), matcher.group(2));
            } else {
                return restaurantRepository.findByNameContaining(name);
            }
        }
        return null;
    }

    /**
     * 영수증 정보에서 가져온 방문일자 타입 변환
     * @param visitDate
     * @return
     */
    public LocalDate findCorrectVisitDate(String visitDate) {
        Pattern pattern = Pattern.compile("^(\\d{4})[-./](\\d{1,2})[-./](\\d{1,2})$");
        Matcher matcher = pattern.matcher(visitDate);

        if (matcher.find()) {
            return LocalDate.of(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
        }
        return null;
    }
}
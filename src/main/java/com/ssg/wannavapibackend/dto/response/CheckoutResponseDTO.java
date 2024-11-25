package com.ssg.wannavapibackend.dto.response;

import com.ssg.wannavapibackend.domain.Address;
import com.ssg.wannavapibackend.domain.UserCoupon;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponseDTO {

    // toss 클라이언트 키
    private String clientKey;

    // 배송 정보
    private String name;
    private String phone;
    private Address address;

    private String email;

    // 유저 포인트
    private Long point;

    // 보유 쿠폰
    private List<AvailableUserCouponResponseDTO> coupons;

    // 상품 정보
    public List<PaymentItemResponseDTO> products;
}

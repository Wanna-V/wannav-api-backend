package com.ssg.wannavapibackend.dto.response;

import com.ssg.wannavapibackend.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponeseDTO {
    /**
     * 예약할 식당의 좌석 데이터
     */
    private Long id;
    private Boolean reservable;
    private Integer guestCount;
    private Integer seatCount;
}

package com.ssg.wannavapibackend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDateResponseDTO {
    /**
     * 식당, 영업일, 좌석, 예약 테이블 데이터
     */
    private Long restaurantId;

    private Integer guestAccount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationDate;

    @JsonFormat(pattern = "HH:mm")
    private List<LocalTime> reservationTimes;
}

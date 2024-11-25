package com.ssg.wannavapibackend.dto.response;

import com.ssg.wannavapibackend.common.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageResponseDTO {

    private String username;
    private Integer point;
    private Grade grade;
    private Integer reviewCount;
    private Integer couponCount;
}

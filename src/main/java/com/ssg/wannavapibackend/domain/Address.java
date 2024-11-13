package com.ssg.wannavapibackend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Address {
    private String zipCode;

    private String roadAddress;

    private String landLotAddress;

    private String detailAddress;
}

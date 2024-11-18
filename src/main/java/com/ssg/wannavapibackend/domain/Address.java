package com.ssg.wannavapibackend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // 값 타입 비교 ==비교 가능하게끔
public class Address {
    
  private String roadAddress;
  private String landLotAddress;
  private String detailsAddress;
  private String zipCode;

    
}

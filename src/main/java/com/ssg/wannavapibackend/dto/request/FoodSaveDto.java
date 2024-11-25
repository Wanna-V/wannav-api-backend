package com.ssg.wannavapibackend.dto.request;

import lombok.Data;

@Data
public class FoodSaveDto {

  private String name;
  private Integer price;
  private String image; //음식 사진은 한장

}

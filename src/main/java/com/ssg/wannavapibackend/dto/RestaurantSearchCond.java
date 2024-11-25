package com.ssg.wannavapibackend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantSearchCond {

  /**
   * where 동적 조건
   */
  //별점 순 , 좋아요 순 이건 정렬 정보이니 뭐 상관 없지 ㅇㅇ
  private Integer startPrice; //조건 : 이거보다 크고 roe
  private Integer endPrice; //조건 : 이거보다 작음 loe
  private Boolean isOpen; // 조건 : eq
  private Boolean canPark; // 조건 : eq
  private String roadAddress;
  private List<Integer> rates;
  private List<String> containFoodTypes; //여러 포함음식 체크박스 중 하나 설계
  private List<String> restaurantTypes; // 조건 : forEach로 돌려가면서 ₩일치하는지
  private List<String> provideServiceTypes;
  private List<String> moodTypes;

  /**
   * orderBy 동적 조건
   */
  private Boolean isCreatedAtChecked; //등록일자 : 최신 순 정렬 위함
  private Boolean isRateChecked; // 평균 별점 높은 순 정렬 위함
  private Boolean isLikesChecked; // 좋아요 많은 순 정렬 위함
  private Boolean isReviewCountChecked; // 리뷰 많은 순 정렬 위함




}

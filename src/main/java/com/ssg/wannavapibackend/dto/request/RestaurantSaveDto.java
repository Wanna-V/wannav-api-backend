package com.ssg.wannavapibackend.dto.request;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class RestaurantSaveDto {

  private String restaurantName;
  private String businessNum;
  private Set<String> restaurantTypes = new HashSet<>();
  private Set<String> containFoodTypes = new HashSet<>();
  private Set<String> provideServiceTypes = new HashSet<>();
  private Set<String> moodTypes = new HashSet<>();
  private String roadNameAddress;
  private String landLotAddress;
  private String zipcode;
  private String detailsAddress;
  private Boolean canPark;
  private String reservationTimeGap;
  private Boolean isPenalty;
  private String image; // 식당 사진

  /**
   * BusinessDay DTO
   */
  private List<LocalTime> openTimes = new ArrayList<>();
  private List<LocalTime> closeTimes = new ArrayList<>();
  private List<LocalTime> breakStartTimes = new ArrayList<>();
  private List<LocalTime> breakEndTimes = new ArrayList<>();
  private List<LocalTime> lastOrderTimes = new ArrayList<>();
  private List<Boolean> isDayOffList = new ArrayList<>();

  /**
   * Food DTO
   */
  private List<FoodSaveDto> foodSaveDtoList = new ArrayList<>();


}

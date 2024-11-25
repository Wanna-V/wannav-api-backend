package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.common.ContainFoodType;
import com.ssg.wannavapibackend.common.MoodType;
import com.ssg.wannavapibackend.common.ProvideServiceType;
import com.ssg.wannavapibackend.common.RestaurantType;
import com.ssg.wannavapibackend.domain.Restaurant;
import com.ssg.wannavapibackend.dto.RestaurantSearchCond;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/restaurants")
public class RestaurantController {


  private final RestaurantService restaurantService;

  @ModelAttribute("containFoodTypes")
  public ContainFoodType[] containFoodTypes() {
    return ContainFoodType.values();
  }

  @ModelAttribute("provideServiceTypes")
  public ProvideServiceType[] provideServiceTypes() {
    return ProvideServiceType.values();
  }

  @ModelAttribute("restaurantTypes")
  public RestaurantType[] restaurantTypes() {
    return RestaurantType.values();
  }

  @ModelAttribute("moodTypes")
  public MoodType[] moodTypes() {
    return MoodType.values();
  }


  @ModelAttribute("sortConditions")
  public Map<String, String> sortConditions() {
    Map<String, String> sortConditions = new HashMap<>();
    sortConditions.put("NEW", "최신 순");
    sortConditions.put("RATE", "평점 순");
    sortConditions.put("LIKE", "좋아요 순");
    sortConditions.put("REVIEW", "리뷰 순");
    return sortConditions;
  }


  //restaurant
  @GetMapping
  public String getRestaurants(
      @ModelAttribute("restaurantSearchCond") RestaurantSearchCond restaurantSearchCond,
      Model model) {
    List<Restaurant> restaurants = restaurantService.findRestaurants(restaurantSearchCond);
    model.addAttribute("restaurants", restaurants);
    return "restaurant/restaurants";
  }

  @GetMapping("/{id}")
  public String getRestaurant(@PathVariable Long id, Model model) {
    model.addAttribute("restaurant", restaurantService.findOne(id));
    return "restaurant/restaurant";
  }

  //UrlResource 자체가 필요 없음 , 어차피 Url직접 웹에서 링크로 조회해서 띄우는 것임 ㅇㅇ 내 서버로 들어와서 DB에 접근해서 띄우는 게 아닌 !




}

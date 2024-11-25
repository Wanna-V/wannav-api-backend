package com.ssg.wannavapibackend.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@ToString(exclude = {"restaurant" , "reviewTags"})
public class Review {

  @Id @GeneratedValue
  private Long id;

  private Integer rating;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurant_id")
  private Restaurant restaurant;

  @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
  private List<ReviewTag> reviewTags = new ArrayList<>();


  /**
   * 연관관계 편의 메서드
   */
  public void addRestaurant(Restaurant restaurant){
    this.restaurant = restaurant;
    restaurant.getReviews().add(this);
  }





}

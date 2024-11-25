package com.ssg.wannavapibackend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.NumberFormat;

@Entity
@Getter @Setter
@NoArgsConstructor
@ToString(exclude = "restaurant")
public class Food {

  @Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String image;

  @NumberFormat(pattern = "#,###원")
  private int price;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurant_id")  //현재 테이블에서 정의할 FK 아이디 이름 기준임
  private Restaurant restaurant;

  public Food(String name , String image , int price){
    this.name =name;
    this.image = image;
    this.price = price;
  }



}

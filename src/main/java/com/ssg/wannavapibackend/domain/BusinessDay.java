package com.ssg.wannavapibackend.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.time.LocalTime;

@Entity
@Getter @Setter(AccessLevel.PRIVATE)
public class BusinessDay {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dayOfWeek; //요일

    @DateTimeFormat(pattern = "hh:mm" , iso = ISO.TIME)
    private LocalTime openTime; //영업 시작 시간

    @DateTimeFormat(pattern = "hh:mm" , iso = ISO.TIME)
    private LocalTime closeTime; //종료 시간

    @DateTimeFormat(pattern = "hh:mm", iso = ISO.TIME)
    private LocalTime breakStartTime; //브레이크 댄스 타임 시작 시간

    @DateTimeFormat(pattern = "hh:mm", iso = ISO.TIME)
    private LocalTime breakEndTime; //브레이크 댄스 타임 종료 시간

    @DateTimeFormat(pattern = "hh:mm" , iso = ISO.TIME)
    private LocalTime lastOrderTime; //라스트 오더 시간

    private Boolean isClose; //문 닫는 날인지 , 이때는 전부 null 값임

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id") //restaurant_id
    private Restaurant restaurant;



}

package com.ssg.wannavapibackend.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Getter @Setter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@ToString(exclude = "restaurant")
public class BusinessDay {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "day_of_week")
  private String dayOfWeek; //특정 요일에 대한 영업 시작 시간 ~ 라스트 오더 시간 배치할 것임

  @DateTimeFormat(pattern = "HH:mm:ss", iso = ISO.TIME)
  @Column(name = "open_time")
  private LocalTime openTime; //영업 시작 시간

  @DateTimeFormat(pattern = "HH:mm:ss" , iso = ISO.TIME)
  @Column(name = "close_time")
  private LocalTime closeTime; //종료 시간

  @DateTimeFormat(pattern = "HH:mm:ss", iso = ISO.TIME)
  @Column(name = "break_start_time")
  private LocalTime breakStartTime; //브레이크 댄스 타임 시작 시간

  @DateTimeFormat(pattern = "HH:mm:ss", iso = ISO.TIME)
  @Column(name = "break_end_time")
  private LocalTime breakEndTime; //브레이크 댄스 타임 종료 시간

  @DateTimeFormat(pattern = "HH:mm:ss" , iso = ISO.TIME)
  @Column(name = "last_order_time")
  private LocalTime lastOrderTime; //라스트 오더 시간

  @Column(name = "is_day_off")
  private Boolean isDayOff; //문 닫는 날인지 , 이때는 전부 null 값임

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurant_id") //restaurant_id
  private Restaurant restaurant;


  public static List<BusinessDay> createBusinessDays(List<LocalTime> openTimes , List<LocalTime> closeTimes , List<LocalTime> breakStartTimes ,
      List<LocalTime> breakEndTimes , List<LocalTime> lastOrderTimes , List<Boolean> isDayOffList){
    List<BusinessDay> businessDayList = new ArrayList<>();
    String[] dayOfWeeks = {"월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};
    for (int i = 0; i < dayOfWeeks.length; i++) {
      BusinessDay businessDay = new BusinessDay();
      businessDay.setDayOfWeek(dayOfWeeks[i]);
      businessDay.setOpenTime(openTimes.get(i));
      businessDay.setCloseTime(closeTimes.get(i));
      businessDay.setBreakStartTime(breakStartTimes.get(i));
      businessDay.setBreakEndTime(breakEndTimes.get(i));
      businessDay.setLastOrderTime(lastOrderTimes.get(i));
      businessDay.setIsDayOff(isDayOffList.get(i));
      businessDayList.add(businessDay);
    }
    return businessDayList;
  }


}

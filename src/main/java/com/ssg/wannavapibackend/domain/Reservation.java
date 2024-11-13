package com.ssg.wannavapibackend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

//    @ManyToOne
//    @JoinColumn(name="restaurant_id")
//    private Restaurant restaurant;

//    @OneToOne
//    @JoinColumn(name = "payment_id")  // 결제 정보가 하나의 예약에만 속할 때
//    private Payment payment;

    @Column(name="is_reservable")
    private Boolean reservable;

    @Column(name="quest_count")
    private Integer guest;

    @Column(name="scheduled_at")
    private LocalDateTime scheduled;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;
}
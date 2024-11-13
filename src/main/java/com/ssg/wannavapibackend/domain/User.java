package com.ssg.wannavapibackend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String profile;

    private String email;

    private String name;

    private String phone;

    @Embedded
    private Address address;

    @Column(name="referral_code")
    private String code;

    private Long point;

    private Boolean consent;

    @Column(name="is_unregistered")
    private Boolean unregistered;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updateAt;

    @Column(name="unresgisterd_at")
    private LocalDateTime unregisteredAt;

    @OneToMany(mappedBy = "user")
    private List<UserCoupon> userCoupons;

    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations;
}

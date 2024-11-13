package com.ssg.wannavapibackend.domain;

import com.ssg.wannavapibackend.domain.Admin;
import com.ssg.wannavapibackend.domain.Coupon;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="created_by_id")
    private Admin createdBy;

    @ManyToOne
    @JoinColumn(name="updated_by_id")
    private Admin updatedBy;

    private String title;

    private String thumbnail;

    private String detail;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "event")
    private List<Coupon> coupons;
}
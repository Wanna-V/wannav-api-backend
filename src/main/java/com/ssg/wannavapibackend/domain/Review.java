package com.ssg.wannavapibackend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer rating;
    private String content;
    private String image;

    @Temporal(TemporalType.DATE)
    @JoinColumn(name = "visit_date")
    private LocalDate visitDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(name = "created_at")
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(name = "updated_at")
    private LocalDateTime updatedAt;

    @JoinColumn(name = "is_active")
    private Boolean isActive;
    private String note;

    @OneToMany(mappedBy = "review")
    private List<ReviewTag> reviewTags;
}

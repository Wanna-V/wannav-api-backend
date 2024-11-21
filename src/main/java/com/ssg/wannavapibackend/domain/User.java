package com.ssg.wannavapibackend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String username;

    @Column(nullable = false)
    private String profile;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 10)
    private String name;

    @Column(length = 15)
    private String phone;

    @Embedded
    private Address address;

    @Column(name="referral_code", length = 6, unique = true)
    private String code;

    @ColumnDefault("0")
    private Long point;

    @Column(nullable = false)
    private Boolean consent;

    @Column(name="is_unregistered", nullable = false)
    @ColumnDefault("0")
    private Boolean unregistered;

    @Column(name="created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateAt;

    @Column(name="unregistered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime unregisteredAt;
}

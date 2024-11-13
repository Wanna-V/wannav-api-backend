package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

}

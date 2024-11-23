package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.UserCoupon;
import com.ssg.wannavapibackend.dto.request.UserCouponRequestDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import org.springframework.data.repository.query.Param;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    @Query("SELECT uc FROM UserCoupon uc " +
        "JOIN uc.coupon c " +
        "WHERE uc.user.id = :userId " +
        "AND c.active = true " +
        "AND c.endDate >= CURRENT_TIMESTAMP " +
        "AND uc.used = false")
    List<UserCoupon> findAllByUserIdAndEndDate(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserCoupon uc SET uc.used = :isUsed WHERE uc.user.id = :userId AND uc.coupon.id = :couponId")
    void updateCouponStatus(@Param("isUsed") Boolean isUsed,
        @Param("userId") Long userId, @Param("couponId") Long couponId);

}

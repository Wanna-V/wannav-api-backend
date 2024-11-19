package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.domain.*;
import com.ssg.wannavapibackend.dto.request.MyPageUpdateDTO;
import com.ssg.wannavapibackend.dto.request.MyReservationRequestDTO;
import com.ssg.wannavapibackend.dto.response.MyLikesResponseDTO;
import com.ssg.wannavapibackend.dto.response.MyPageResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MyPageService {

    MyPageResponseDTO findMyPage(Long userId);

    User findUserInfo(Long userId);

    void updateMyPage(Long userId, MyPageUpdateDTO myPageUpdateDTO);

    List<MyLikesResponseDTO> findMyLikes(Long userId);

    List<Reservation> findMyReservations(Long userId, MyReservationRequestDTO myReservationRequestDTO);

    Reservation findMyReservation(Long reservationId);

    List<Payment> findMyOrders(Long userId);

    Payment findMyOrdersDetails(Long paymentId);

    List<PointLog> findMyPoints(Long userId);

    List<UserCoupon> findMyCoupons(Long userId);

    List<Review> findMyReviews(Long userId);
}

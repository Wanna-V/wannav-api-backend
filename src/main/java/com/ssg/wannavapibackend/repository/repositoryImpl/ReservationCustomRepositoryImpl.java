package com.ssg.wannavapibackend.repository.repositoryImpl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssg.wannavapibackend.domain.QReservation;
import com.ssg.wannavapibackend.domain.QRestaurant;
import com.ssg.wannavapibackend.domain.Reservation;
import com.ssg.wannavapibackend.dto.request.MyReservationRequestDTO;
import com.ssg.wannavapibackend.repository.ReservationCustomRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ReservationCustomRepositoryImpl implements ReservationCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final QReservation reservation = QReservation.reservation;

    @Override
    public List<Reservation> findAllById(Long userId, MyReservationRequestDTO myReservationRequestDTO) {

        JPAQuery<Reservation> result = queryFactory
                .selectFrom(reservation)
                .innerJoin(reservation.restaurant, QRestaurant.restaurant)
                .where(reservation.user.id.eq(userId));

        if (myReservationRequestDTO.getType() == null || myReservationRequestDTO.getType().equals("new")) {
            result.orderBy(reservation.createdAt.desc());
        } else if (myReservationRequestDTO.getType().equals("old")) {
            result.orderBy(reservation.createdAt.asc());
        }
        return result.fetchJoin().fetch();
    }
}

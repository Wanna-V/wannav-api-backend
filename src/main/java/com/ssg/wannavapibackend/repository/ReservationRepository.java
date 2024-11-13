package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}

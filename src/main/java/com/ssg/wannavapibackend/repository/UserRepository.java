package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}

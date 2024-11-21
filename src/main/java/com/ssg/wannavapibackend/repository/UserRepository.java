package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByCode(String code);

    @Query("SELECT u.id FROM User u WHERE u.username = :userName AND u.email = :email")
    Long findIdByUsernameAndEmail(String userName, String email);

    User findUserByEmail(String email);
}

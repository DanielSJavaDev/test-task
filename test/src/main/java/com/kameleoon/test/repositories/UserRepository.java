package com.kameleoon.test.repositories;

import com.kameleoon.test.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email =?1")
    User findUser(String email);

    @Query("SELECT u FROM User u WHERE u.id =?1")
    User findUserById(Long id);
}

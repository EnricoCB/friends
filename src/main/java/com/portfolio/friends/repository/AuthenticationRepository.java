package com.portfolio.friends.repository;

import com.portfolio.friends.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationRepository extends JpaRepository<User, Long> {
    public UserDetails findByUsername(String username);
}

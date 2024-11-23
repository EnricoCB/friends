package com.portfolio.friends.repository;

import com.portfolio.friends.entity.Friendship;
import com.portfolio.friends.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Page<Friendship> findByReceiver(User receiver, Pageable pageable);
    Page<Friendship> findByRequester(User requester, Pageable pageable);

}

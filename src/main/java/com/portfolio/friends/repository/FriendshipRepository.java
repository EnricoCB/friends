package com.portfolio.friends.repository;

import com.portfolio.friends.entity.Friendship;
import com.portfolio.friends.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByReceiver(User receiver);
    Optional<Friendship> findByReceiverAndAcceptedTrueAndRequesterAndAcceptedTrue(User user, User user2);
    Page<Friendship> findByReceiverAndAcceptedTrueOrRequesterAndAcceptedTrue(User user, User user2, Pageable pageable);
    Page<Friendship> findByReceiver(User receiver, Pageable pageable);
    Page<Friendship> findByRequesterAndAcceptedFalse(User requester, Pageable pageable);
    Optional<Friendship> findByRequesterAndReceiver(User requester, User receiver);

}

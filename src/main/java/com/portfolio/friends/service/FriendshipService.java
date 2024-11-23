package com.portfolio.friends.service;

import com.portfolio.friends.entity.Friendship;
import com.portfolio.friends.entity.User;
import com.portfolio.friends.repository.FriendshipRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FriendshipService {

    FriendshipRepository friendshipRepository;

    public Friendship friendshipRequest(User request, User reciever){
        return friendshipRepository.save(new Friendship(request, reciever));
    }
    

    public Page<Friendship> getReceivedRequests(User receiver, Pageable pageable) {
        return friendshipRepository.findByReceiver(receiver, pageable);
    }

    public Page<Friendship> getSentRequests(User requester, Pageable pageable) {
        return friendshipRepository.findByRequester(requester, pageable);
    }
}

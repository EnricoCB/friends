package com.portfolio.friends.service;

import com.portfolio.friends.entity.Friendship;
import com.portfolio.friends.entity.User;
import com.portfolio.friends.exception.*;
import com.portfolio.friends.repository.FriendshipRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class FriendshipService {

    FriendshipRepository friendshipRepository;
    UserService userService;

    public void friendshipRequest(User request, User receiver) {

        if(receiver.getVisibility() == User.ProfileVisibility.HIDDEN){
            throw new PrivateProfileException("private profile");
        }

        if (friendshipRepository.findByRequesterAndReceiver(request, receiver).isPresent()) {
            throw new RequestAlreadyExistsException("Request already exists");
        }

        if (friendshipRepository.findByReceiverAndAcceptedTrueAndRequesterAndAcceptedTrue(receiver, request).isPresent() ||
                friendshipRepository.findByReceiverAndAcceptedTrueAndRequesterAndAcceptedTrue(request, receiver).isPresent()) {
            throw new FriendshipAlreadyExistsException("Amizade já existente");
        }

        friendshipRepository.findByRequesterAndReceiver(receiver, request).ifPresentOrElse(
                friendship -> {
                    friendship.setAccepted(true);
                    friendshipRepository.save(friendship);
                },
                () -> friendshipRepository.save(new Friendship(request, receiver))
        );
    }


    public void friendshipAccept(User requester, User receiver) {
        Friendship friendship = friendshipRepository.findByReceiverAndAcceptedFalse(receiver)
                .stream()
                .filter(f -> f.getRequester().equals(requester))
                .findFirst()
                .orElseThrow(() -> new RequestNotFoundException("Request not found"));
        friendship.setAccepted(true);
        friendshipRepository.save(friendship);
    }

    public Page<Friendship> getReceivedRequests(User receiver, Pageable pageable) {
        return friendshipRepository.findByReceiver(receiver, pageable);
    }

    public Page<Friendship> getSentRequests(User requester, Pageable pageable) {
        return friendshipRepository.findByRequesterAndAcceptedFalse(requester, pageable);
    }

    public Page<Friendship> getAcceptedFriendships(User receiver, Pageable pageable) {
        return friendshipRepository.findByReceiverAndAcceptedTrueOrRequesterAndAcceptedTrue(receiver, receiver, pageable);
    }

    public void declineFriendship(User requester, User receiver) {
        friendshipRepository.findByRequesterAndReceiver(requester, receiver).ifPresentOrElse(
                friendship -> {
                    requester.setUsersDeclineFriendship(requester.getUsersDeclineFriendship() + 1);

                    if(requester.getUsersDeclineFriendship() == 3){
                        requester.setBadge(User.Badge.FOREVER_ALONE);
                    }
                    userService.saveUser(requester);
                    friendshipRepository.delete(friendship);
                },
                () -> {
                    throw new RequestNotFoundException("Request not found");
                }
        );

    }

    public void cancelRequestFriendship(User receiver, User requester) {
        friendshipRepository.findByRequesterAndReceiver(requester, receiver).ifPresentOrElse(
                friendship -> {
                    friendshipRepository.delete(friendship);
                },
                () -> {
                    throw new RequestNotFoundException("Request not found");
                }
        );

    }

    public void undoFriendship(User requester, User receiver) {
        Optional<Friendship> friendship = friendshipRepository
                .findByRequesterAndReceiver(requester, receiver)
                .or(() -> friendshipRepository.findByRequesterAndReceiver(receiver, requester));

        if (friendship.isPresent()) {
            friendshipRepository.delete(friendship.get());
        } else {
            throw new FriendshipNotFoundException("Friendship not found");
        }
    }

}

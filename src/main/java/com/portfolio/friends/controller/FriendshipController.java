package com.portfolio.friends.controller;
import com.portfolio.friends.dto.UserDTO;
import com.portfolio.friends.entity.Friendship;
import com.portfolio.friends.entity.User;
import com.portfolio.friends.service.FriendshipService;
import com.portfolio.friends.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/friendship")
public class FriendshipController {

    UserService userService;
    FriendshipService friendshipService;

    @PostMapping("/request")
    public ResponseEntity<Void> request(@RequestBody UserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User reciever = userService.findByUsername(dto.username());
        User request = userService.findByUsername(authentication.getName());
        friendshipService.friendshipRequest(request, reciever);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/accept")
    public ResponseEntity<Void> accept(@RequestBody UserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User reciever = userService.findByUsername(authentication.getName());
        User request = userService.findByUsername(dto.username());
        friendshipService.friendshipAccept(request, reciever);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/receiver")
    public ResponseEntity<Page<UserDTO>> getReceivedRequests(@PageableDefault(size = 5) Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User receiver = userService.findByUsername(authentication.getName());
        Page<Friendship> friendships = friendshipService.getReceivedRequests(receiver, pageable);
        Page<UserDTO> userDTOPage = friendships.map(friendship -> new UserDTO(friendship.getRequester().getUsername()));
        return ResponseEntity.ok(userDTOPage);
    }

    @GetMapping("/requester")
    public ResponseEntity<Page<UserDTO>> requester(@PageableDefault(size = 5) Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());
        Page<Friendship> friendships = friendshipService.getSentRequests(user, pageable);
        Page<UserDTO> userDTOPage = friendships.map(friendship -> new UserDTO(friendship.getRequester().getUsername()));
        return ResponseEntity.ok(userDTOPage);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<UserDTO>> list(@PageableDefault(size = 5) Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User receiver = userService.findByUsername(authentication.getName());
        Page<Friendship> friendships = friendshipService.getAcceptedFriendships(receiver, pageable);
        Page<UserDTO> friendDTOPage = friendships.map(friendship -> {
            User friend = friendship.getRequester().equals(receiver)
                    ? friendship.getReceiver()
                    : friendship.getRequester();
            return new UserDTO(friend.getUsername());
        });
        return ResponseEntity.ok(friendDTOPage);
    }

    @DeleteMapping("/decline")
    public ResponseEntity<Void> decline(@RequestBody UserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User reciever = userService.findByUsername(authentication.getName());
        User requester = userService.findByUsername(dto.username());
        friendshipService.declineFriendship(requester, reciever);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<Void> cancel(@RequestBody UserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User reciever = userService.findByUsername(authentication.getName());
        User requester = userService.findByUsername(dto.username());
        friendshipService.cancelRequestFriendship(reciever, requester);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/undo")
    public ResponseEntity<Void> undo(@RequestBody UserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User reciever = userService.findByUsername(authentication.getName());
        User requester = userService.findByUsername(dto.username());
        friendshipService.undoFriendship(reciever, requester);
        return ResponseEntity.noContent().build();
    }

}

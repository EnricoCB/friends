package com.portfolio.friends.controller;

import com.portfolio.friends.dto.ListUserDTO;
import com.portfolio.friends.dto.UserDTO;
import com.portfolio.friends.entity.User;
import com.portfolio.friends.service.FriendshipService;
import com.portfolio.friends.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/friendship")
public class FriendshipController {

    UserService userService;
    FriendshipService friendshipService;

    @PostMapping("/request")
    public ResponseEntity request(@RequestBody UserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User reciever = userService.findByUsername(dto.username());
        User request = userService.findByUsername(authentication.getName());
        friendshipService.friendshipRequest(request, reciever);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/accept")
    public ResponseEntity accept(@RequestBody UserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User reciever = userService.findByUsername(authentication.getName());
        User request = userService.findByUsername(dto.username());
        friendshipService.friendshipAccept(request, reciever);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/receiver")
    public ListUserDTO receiver() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User receiver = userService.findByUsername(authentication.getName());

        List<UserDTO> userDTOs = receiver.getReceivedRequests().stream()
                .map(friendship -> new UserDTO(friendship.getRequester().getUsername()))
                .toList();

        return new ListUserDTO(userDTOs);
    }

    @GetMapping("/requester")
    public ListUserDTO requester() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());

        List<UserDTO> userDTOs = user.getSentRequests().stream()
                .map(friendship -> new UserDTO(friendship.getReceiver().getUsername()))
                .toList();

        return new ListUserDTO(userDTOs);
    }

}

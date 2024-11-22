package com.portfolio.friends.controller;

import com.portfolio.friends.dto.AuthenticationDTO;
import com.portfolio.friends.dto.LoginResponseDTO;
import com.portfolio.friends.dto.UserDTO;
import com.portfolio.friends.entity.User;
import com.portfolio.friends.infra.security.TokenService;
import com.portfolio.friends.repository.UserRepository;
import com.portfolio.friends.service.FriendshipService;
import com.portfolio.friends.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final UserService userService;
    private final FriendshipService friendshipService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationDTO dto) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody AuthenticationDTO dto) {
        if(this.userRepository.findByUsername(dto.username()) != null) return ResponseEntity.badRequest().build();
        String encryptPassword = new BCryptPasswordEncoder().encode(dto.password());
        User user = new User(dto.username(), encryptPassword);

        this.userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request")
    public ResponseEntity request(@RequestBody UserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User reciever = userService.findByUsername(dto.username());
        User request = userService.findByUsername(authentication.getName());
        friendshipService.friendshipRequest(request, reciever);
        return ResponseEntity.ok().build();
    }
}

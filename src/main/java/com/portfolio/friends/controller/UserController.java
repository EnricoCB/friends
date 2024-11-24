package com.portfolio.friends.controller;

import com.portfolio.friends.dto.AuthenticationDTO;
import com.portfolio.friends.dto.LoginResponseDTO;
import com.portfolio.friends.dto.UserDTO;
import com.portfolio.friends.dto.UserProfileDTO;
import com.portfolio.friends.entity.Friendship;
import com.portfolio.friends.entity.User;
import com.portfolio.friends.infra.security.TokenService;
import com.portfolio.friends.repository.UserRepository;
import com.portfolio.friends.service.FriendshipService;
import com.portfolio.friends.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public ResponseEntity<LoginResponseDTO> login(@RequestBody AuthenticationDTO dto) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody AuthenticationDTO dto) {
        if(this.userRepository.findByUsername(dto.username()) != null) return ResponseEntity.badRequest().build();
        String encryptPassword = new BCryptPasswordEncoder().encode(dto.password());
        User user = new User(dto.username(), encryptPassword);

        this.userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> profile(@RequestBody UserDTO dto, @PageableDefault(size = 5) Pageable pageable) {
        User user = userService.findByUsername(dto.username());
        Page<Friendship> friendships = friendshipService.getAcceptedFriendships(user, pageable);
        Page<String> friendUsernames = friendships.map(friendship -> {
            User friend = friendship.getRequester().equals(user)
                    ? friendship.getReceiver()
                    : friendship.getRequester();
            return friend.getUsername();
        });

        UserProfileDTO profile = new UserProfileDTO(user.getUsername(), friendUsernames);

        return ResponseEntity.ok(profile);
    }
}

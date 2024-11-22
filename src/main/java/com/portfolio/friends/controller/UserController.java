package com.portfolio.friends.controller;

import com.portfolio.friends.dto.AuthenticationDTO;
import com.portfolio.friends.dto.LoginResponseDTO;
import com.portfolio.friends.entity.User;
import com.portfolio.friends.infra.security.TokenService;
import com.portfolio.friends.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;

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
}

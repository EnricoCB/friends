package com.portfolio.friends.service;

import com.portfolio.friends.entity.User;
import com.portfolio.friends.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void updateVisibility(User user, User.ProfileVisibility newVisibility) {
        user.setVisibility(newVisibility);
        userRepository.save(user);
    }

}

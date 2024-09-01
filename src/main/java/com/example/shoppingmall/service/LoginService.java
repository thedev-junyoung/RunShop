package com.example.shoppingmall.service;

import com.example.shoppingmall.model.dto.user.UsersDetails;
import com.example.shoppingmall.model.entity.User;
import com.example.shoppingmall.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return new UsersDetails(user);
        }
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}

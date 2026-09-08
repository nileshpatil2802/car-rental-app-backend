package com.My_Car_Rental_Application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.My_Car_Rental_Application.entity.UserRequest;
import com.My_Car_Rental_Application.repository.HomeRepository;
import org.springframework.security.core.userdetails.User;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private HomeRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        UserRequest user = userRepository.findByEmail(email);
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // must be BCrypt encoded
                .roles(user.getRole())        // e.g. "ADMIN" or "USER"
                .build();
    }
}

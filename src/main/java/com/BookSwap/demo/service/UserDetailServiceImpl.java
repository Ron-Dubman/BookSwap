package com.BookSwap.demo.service;

import com.BookSwap.demo.model.User;
import com.BookSwap.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public UserDetailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from DB
        User user = userRepository.findByUsername(username)
           .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));

        // Convert to Spring Security UserDetails
        return org.springframework.security.core.userdetails.User
           .withUsername(user.getUsername())
           .password(user.getPassword()) // Pass the HASHED password
           .authorities(user.getRole().name()) // e.g., "ROLE_MEMBER"
           .build();
    }

}

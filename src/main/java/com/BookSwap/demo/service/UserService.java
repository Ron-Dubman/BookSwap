package com.BookSwap.demo.service;

import com.BookSwap.demo.model.User;
import com.BookSwap.demo.repository.UserRepository;
import com.BookSwap.demo.model.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean isFirstUser(){
        return userRepository.count() == 0;
    }

    public User registerUser(String username, String rawPassword, String email, boolean isAdmin){
        if(userRepository.findByUsername(username).isPresent())
            throw new IllegalArgumentException("Username already exists!");
        
        String hashedPassword = passwordEncoder.encode(rawPassword);

        Role roles = Role.ROLE_MEMBER;

        if (isAdmin){
            roles = Role.ROLE_ADMIN;
        }

        User user = new User(username, hashedPassword, email, roles);
        return userRepository.save(user);
    }

    public User registerFirstAdmin(String username, String rawPassword, String email){
        if(!isFirstUser()){
            throw new IllegalArgumentException("First user already exists!");
        }
        return registerUser(username,rawPassword,email,true);
    }
}

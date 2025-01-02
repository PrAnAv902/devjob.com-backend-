package com.devjobs.portal.service;


import com.devjobs.portal.entities.User;
import com.devjobs.portal.repositories.RepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


//using this to extract data from "basic auth"

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private RepositoryImpl userRepositoryImpl;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepositoryImpl.getUser(email);
        if(user != null){
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .build();
            return userDetails;
        }
        return null;
    }
}

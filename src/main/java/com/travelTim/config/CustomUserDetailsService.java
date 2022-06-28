package com.travelTim.config;

import com.travelTim.user.UserDAO;
import com.travelTim.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDAO userDAO;

    @Autowired
    public CustomUserDetailsService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> userOptional = userDAO.findUserEntityByEmail(email);
        if (userOptional.isEmpty()){
            throw new UsernameNotFoundException("User with email " + email + " was not found");
        }
        return new User(userOptional.get().getEmail(), userOptional.get().getPassword(), new ArrayList<>());
    }
}

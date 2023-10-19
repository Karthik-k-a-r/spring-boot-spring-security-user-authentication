package com.craft.userAuth.service.impl;

import com.craft.userAuth.constants.Constants;
import com.craft.userAuth.enums.Roles;
import com.craft.userAuth.exception.CustomException;
import com.craft.userAuth.model.entity.User;
import com.craft.userAuth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl  implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional
                .orElseThrow(() -> new CustomException(MessageFormat.format(Constants.ErrorMessages.USER_NOT_FOUND,email),
                        Constants.ErrorCodes.USER_NOT_FOUND_CODE));
        List<SimpleGrantedAuthority> simpleGrantedAuthorities=new ArrayList<>();
        if(user.getRole().getName() == Roles.ADMIN){
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(Roles.ADMIN.name()));
        }else{
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(Roles.USER.name()));
        }


        return new org.springframework.security
                .core.userdetails.User(user.getEmail(), user.getPassword(),
                user.isActive(), true, true,
                true, simpleGrantedAuthorities);
    }
}

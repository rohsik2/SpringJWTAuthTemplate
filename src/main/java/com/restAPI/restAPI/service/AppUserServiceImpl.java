package com.restAPI.restAPI.service;

import com.restAPI.restAPI.domain.AppUser;
import com.restAPI.restAPI.domain.Role;
import com.restAPI.restAPI.repository.AppUserRepo;
import com.restAPI.restAPI.repository.RoleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service @RequiredArgsConstructor @Transactional @Slf4j
public class AppUserServiceImpl implements AppUserService, UserDetailsService {
    private final AppUserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    // TODO : SimpleGrantedAuthority, security.core.userdetails.User 알아보기 // 58:32
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepo.findByUsername(username);
        if (user == null){
            log.error("User not found in database");
            throw new UsernameNotFoundException("User not found in database");
        }else{
            log.info("User found in database : {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
    @Override
    public AppUser saveUser(AppUser user) {
        log.info("Saving new user {} to the db", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        addRoleToUser(user.getUsername(), "ROLE_USER");
        log.info("Role of user {}", user.getRoles().toString());
        return user;
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the db", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
        AppUser user = userRepo.findByUsername(username);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public AppUser getAppUser(String username) {
        log.info("Fetching user {}", username);
        return userRepo.findByUsername(username);
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("Fetching all Users");
        return userRepo.findAll();
    }

}

package com.restAPI.restAPI.service;

import com.restAPI.restAPI.domain.AppUser;
import com.restAPI.restAPI.domain.Role;

import java.util.List;

public interface AppUserService {
    AppUser saveUser(AppUser user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    AppUser getAppUser(String username);
    List<AppUser> getUsers();

}

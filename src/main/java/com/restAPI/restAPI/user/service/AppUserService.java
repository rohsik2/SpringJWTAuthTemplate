package com.restAPI.restAPI.user.service;

import com.restAPI.restAPI.user.domain.AppUser;
import com.restAPI.restAPI.user.domain.Role;

import java.util.List;

public interface AppUserService {
    AppUser saveUser(AppUser user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    AppUser getAppUser(String username);
    List<AppUser> getUsers();

}

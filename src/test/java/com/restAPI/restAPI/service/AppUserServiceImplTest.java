package com.restAPI.restAPI.service;

import com.restAPI.restAPI.user.domain.AppUser;
import com.restAPI.restAPI.user.domain.Role;
import com.restAPI.restAPI.user.repository.AppUserRepo;
import com.restAPI.restAPI.user.repository.RoleRepo;
import com.restAPI.restAPI.user.service.AppUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest @Transactional
class AppUserServiceImplTest {
    @Autowired
    private AppUserServiceImpl appUserService;
    @Autowired
    private AppUserRepo appUserRepo;
    @Autowired
    private RoleRepo roleRepo;

    @BeforeEach
    public void beforeEach(){
        appUserService.saveUser(new AppUser(null, "rohsikbak", "rohsikbak", "rohsik123",  new ArrayList<Role>()));
    }

    @Test
    void loadUserByUsername() {
        //given

        //when
        AppUser user = appUserService.getAppUser("rohsikbak");

        //then
        assertEquals(user.getUsername(), "rohsikbak");
    }

    @Test
    void saveUser() {
        //given
        appUserService.saveUser(new AppUser(null, "handsome", "handsome", "handsome", new ArrayList<>()));

        //when :
        try {
            appUserService.saveUser(new AppUser(null, "rohsikbak", "rohsikbak", "qwoeirnqoweibr", new ArrayList<Role>()));
        }

        //then
        catch(Exception e){
            assertEquals(e.getClass(), DataIntegrityViolationException.class);
        }
        assertEquals("handsome", appUserService.loadUserByUsername("handsome").getUsername());
    }

    @Test
    void saveRole() {
        //given

        //when
        try {
            roleRepo.save(new Role(null, "ROLE_ADMIN"));
        }

        //then
        catch(Exception e){
            Role admin = roleRepo.findByName("ROLE_ADMIN");
            assertEquals("ROLE_ADMIN", admin.getName());
        }
    }

    @Test
    void addRoleToUser() {
        //given

        //when
        AppUser user = appUserService.getAppUser("rohsikbak");
        appUserService.addRoleToUser("rohsikbak", "ROLE_ADMIN");

        //then
        assertEquals(user.getRoles().size(), 1);
    }

}
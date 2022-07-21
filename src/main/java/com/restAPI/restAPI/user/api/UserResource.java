package com.restAPI.restAPI.user.api;

import com.auth0.jwt.JWT;
import com.restAPI.restAPI.user.domain.AppUser;
import com.restAPI.restAPI.user.domain.Role;
import com.restAPI.restAPI.user.service.AppUserService;
import com.restAPI.restAPI.utility.Utility;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mapping.callback.ReactiveEntityCallbacks.create;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor @Slf4j
public class UserResource {
    private final AppUserService appUserService;

    @GetMapping("/users")
    @ApiOperation(value="모든 사용자 정보를 조회", notes="모든 사용자 정보를 조회합니다.")
    public ResponseEntity<List<AppUser>> getUsers(){
        return ResponseEntity.ok().body(appUserService.getUsers());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUser(HttpServletRequest req, @PathVariable(value="username") String username){
        String token = req.getHeader(HttpHeaders.AUTHORIZATION).substring("Bearer ".length());
        String tokenUsername = Utility.getUsernameFromToken(token);
        if(username.equals(tokenUsername)){
            return ResponseEntity.ok().body(appUserService.getAppUser(username));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body("path username not same with token's username");
    }

    @PostMapping("/user")
    public ResponseEntity<?> saveUser(AppUserForm userForm){
        log.info("Save user {} request reached", userForm.getName());
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user").toUriString());
        try {
            AppUser newUser = new AppUser(null, userForm.getName(), userForm.getUsername(), userForm.getPassword(), new ArrayList<>());
            appUserService.saveUser(newUser);
        }
        catch(Exception e){
            if(e.getClass() == ConstraintViolationException.class)
                return ResponseEntity.badRequest().body("Username already Exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.created(uri).build();
    }
    //TODO : Making Register Function
    //TODO : Making User Change Function
    //직접 Request Body로 Entity를 요청해도 되나?

    @PostMapping("/role")
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role").toUriString());
        return ResponseEntity.created(uri).body(appUserService.saveRole(role));
    }

    @PostMapping("/role/addtouser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form){
        appUserService.addRoleToUser(form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/oauth/refresh")
    @ApiOperation(value="My custom access Token Refresher", notes="My custom access Token Refresher")
    public ResponseEntity<?> accessHandler(AccessRequestForm accessRequestForm) throws IOException {
        log.info(accessRequestForm.toString());
        if(accessRequestForm.getGrantType().equals("refresh_token")){
            try{
                String refreshToken = accessRequestForm.getRefreshToken();
                String username = Utility.getUsernameFromToken(refreshToken);
                String issuer = Utility.getIssuerFromToken(refreshToken);
                if(username == null || issuer == null || !username.equals(accessRequestForm.getUsername())){
                    return ResponseEntity.badRequest().body("Wrong Token (maybe expired)");
                }

                log.info("Refreshing Started username {}", username);
                AppUser user = appUserService.getAppUser(username);
                String accessToken = JWT.create()
                        .withSubject(username)
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(issuer)
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(Utility.getAlgorithm());
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                return ResponseEntity.ok().body(tokens);
            }
            catch (Exception e){
                log.error("Error while Refreshing Token Custom {}", e.getMessage());
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        else{
            ResponseEntity.badRequest().body("Wrong Grant type");
        }
        return null;
    }
}


@Data
class AccessRequestForm{
    private String grantType;
    private String username;
    private String password;
    private String refreshToken;
}


@Data
class RoleToUserForm{
    private String username;
    private String roleName;
}

@Data
class AppUserForm{
    private String name;
    private String username;
    private String password;
}
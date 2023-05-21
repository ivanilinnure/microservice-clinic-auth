package com.clinic.auth.controller;

import com.clinic.auth.dto.AuthenticationRequestDto;
import com.clinic.auth.dto.UserDto;
import com.clinic.auth.model.User;
import com.clinic.auth.security.jwt.JwtTokenProvider;
import com.clinic.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid AuthenticationRequestDto requestDto) {
        try {
            String username = requestDto.getUsername();
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User with username: " + username + "is not exist");
            }
            return getMapResponseEntity(user, username, requestDto.getPassword());

        } catch (AuthenticationException exception) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody @Validated(UserDto.New.class) UserDto userDto) {
        User register = userService.registerUser(userDto.toUser());
        String username = userDto.getUsername();
        return getMapResponseEntity(register, username, userDto.getPassword());
    }

    private ResponseEntity<Map<String, String>> getMapResponseEntity(User user, String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        String token = jwtTokenProvider.createToken(username, user.getRoles(), user.getId());
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", token);
        return ResponseEntity.ok(response);
    }

}

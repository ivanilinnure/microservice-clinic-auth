package com.clinic.auth.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.clinic.auth.dto.UserDto;
import com.clinic.auth.model.User;
import com.clinic.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/users")
@Slf4j
@CrossOrigin
public class UserRestControllerV1 {

    private final UserService userService;

    @JsonView(UserDto.Details.class)
    @GetMapping(value = "/me")
    public ResponseEntity<UserDto> getUserById(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        UserDto result = UserDto.fromUser(user);
        return ResponseEntity.ok(result);
    }

}

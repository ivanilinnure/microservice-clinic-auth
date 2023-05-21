package com.clinic.auth.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.clinic.auth.dto.UserDto;
import com.clinic.auth.model.User;
import com.clinic.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/admin/")
@CrossOrigin
public class AdminRestControllerV1 {

    private final UserService userService;

    @JsonView(UserDto.AdminDetails.class)
    @GetMapping(value = "users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(name = "id") UUID id) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        UserDto result = UserDto.fromUser(user);
        return ResponseEntity.ok(result);
    }

    @JsonView(UserDto.AdminDetails.class)
    @PostMapping(value = "/doctors")
    public ResponseEntity<UserDto> registerDoctor(@RequestBody @Validated(value = {UserDto.New.class}) UserDto userDto) {
        User register = userService.registerDoctor(userDto.toUser());
        return ResponseEntity.ok(UserDto.fromUser(register));
    }

}

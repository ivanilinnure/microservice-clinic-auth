package com.clinic.auth.service;

import com.clinic.auth.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User registerUser(User user);

    User registerDoctor(User user);

    List<User> getAll();

    User findByUsername(String username);

    User findById(UUID id);

    void delete(UUID id);

}

package com.example.metadata.service;

import com.example.metadata.entity.User;
import java.util.Optional;

public interface UserService {
  Optional<User> findUser(String userName);
}

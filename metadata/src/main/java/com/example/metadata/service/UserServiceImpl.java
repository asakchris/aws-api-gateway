package com.example.metadata.service;

import com.example.metadata.entity.User;
import com.example.metadata.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository repository;

  @Override
  public Optional<User> findUser(String userName) {
    return repository.findByUserName(userName);
  }
}

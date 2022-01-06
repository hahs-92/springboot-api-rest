package com.springboot.course.dao;

import com.springboot.course.model.UserModel;

import java.util.List;
import java.util.Optional;

public interface IUserDAO {
    List<UserModel> getAll();
    Optional<UserModel> getUser(Long userId);
    void delete(Long userId);
    UserModel save(UserModel user);
    UserModel update(Long userId, UserModel user);
}

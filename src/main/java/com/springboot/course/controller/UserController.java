package com.springboot.course.controller;

import com.springboot.course.dao.UserDAOImpl;
import com.springboot.course.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserDAOImpl userDAO;

    @Autowired
    public UserController(UserDAOImpl userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping("/")
    ResponseEntity<List<UserModel>> GetAll() {
        return new ResponseEntity<>(userDAO.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    ResponseEntity<UserModel> getUser(@PathVariable Long userId) {
        try {
            if(userDAO.getUser(userId).isPresent()) {
                return new ResponseEntity<>(userDAO.getUser(userId).get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new UserModel(), HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return  new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

    @DeleteMapping("/{userId}")
    ResponseEntity deleteUser(@PathVariable Long userId) {
        try {
            userDAO.delete(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return  new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping("/")
    ResponseEntity<UserModel> save(@RequestBody UserModel user) {
        try {
            return new ResponseEntity<>(userDAO.save(user), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return  new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

    @PutMapping("/{userId}")
    ResponseEntity<UserModel> update(@PathVariable Long userId, @RequestBody UserModel user) {
        try {
            return new ResponseEntity<>(userDAO.update(userId, user), HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }
}

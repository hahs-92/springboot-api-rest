package com.springboot.course.controller;

import com.springboot.course.dao.UserDAOImpl;
import com.springboot.course.model.UserModel;
import com.springboot.course.utils.JWTUtil;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserDAOImpl userDAO;
    private final JWTUtil jwtUtil;

    @Autowired
    public UserController(UserDAOImpl userDAO, JWTUtil jwtUtil) {
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
    }



    @GetMapping("/")
    ResponseEntity<List<UserModel>> GetAll(@RequestHeader(value = "Authorization") String token) {
        try {
            //verificar el token
            if (!jwtUtil.checkToken(token)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            Long userId = Long.parseLong(jwtUtil.getKey(token));
            //verificar que exista el usuario con ese  id
            Optional<UserModel> user = userDAO.getUser(userId);
            if(user.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(userDAO.getAll(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/{userId}")
    ResponseEntity<UserModel> getUser(@PathVariable Long userId , @RequestHeader(value = "Authorization") String token) {
        try {
            //verificar el token
            if (!jwtUtil.checkToken(token)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            if(userDAO.getUser(userId).isPresent()) {
                return new ResponseEntity<>(userDAO.getUser(userId).get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new UserModel(), HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return  new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{userId}")
    ResponseEntity deleteUser(@PathVariable Long userId, @RequestHeader(value = "Authorization") String token) {
        try {
            //verificar el token
            if (!jwtUtil.checkToken(token)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            userDAO.delete(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return  new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/")
    ResponseEntity<UserModel> save(@RequestBody UserModel user) {
        try {
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
            String hash = argon2.hash(3,1024, 1  ,user.getPassword());//3 -> iteraciones 1024 -> memoria
            user.setPassword(hash);
            return new ResponseEntity<>(userDAO.save(user), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return  new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{userId}")
    ResponseEntity<UserModel> update(@PathVariable Long userId, @RequestBody UserModel user, @RequestHeader(value = "Authorization") String token) {
        try {
            //verificar el token
            if (!jwtUtil.checkToken(token)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(userDAO.update(userId, user), HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

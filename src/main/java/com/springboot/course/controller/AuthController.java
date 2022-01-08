package com.springboot.course.controller;

import com.springboot.course.dao.IUserDAO;
import com.springboot.course.model.UserModel;
import com.springboot.course.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IUserDAO userDAO;
    private final JWTUtil jwtUtil;

    @Autowired
    public AuthController(IUserDAO userDAO, JWTUtil jwtUtil) {
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserModel user) {
        try {
            UserModel userLog = userDAO.getUserByCredentials(user);
           if(userLog != null) {
               String jwtToken =  jwtUtil
                       .create(String.valueOf(userLog.getId()), userLog.getEmail());
               return  new ResponseEntity<>( jwtToken,HttpStatus.ACCEPTED);
           } else {
               return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
           }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

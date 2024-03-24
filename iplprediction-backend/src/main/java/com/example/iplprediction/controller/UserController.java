package com.example.iplprediction.controller;

import com.example.iplprediction.dao.UserDao;
import com.example.iplprediction.dto.UserDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping
    @CrossOrigin("*")
    public ResponseEntity<Integer> getUser(@RequestParam("username") String username) {
        Integer userId = null;
        if (StringUtils.hasText(username)) {
            // check if username already exists
            userId = userDao.findByUserName(username);
            if (userId == null) {
                return new ResponseEntity<>(HttpStatusCode.valueOf(404));
            }
        }
        return new ResponseEntity<>(userId, HttpStatusCode.valueOf(200));
    }

    @PostMapping
    @CrossOrigin("*")
    public ResponseEntity<Integer> createNewUser(@RequestParam("username") String username) {
        Integer userId = null;
        if (StringUtils.hasText(username)) {
            // check if username already exists
            userId = userDao.findByUserName(username);
            if (userId != null) {
                return new ResponseEntity<>(HttpStatusCode.valueOf(400));
            }
            userId = userDao.save(username);
        }

        return new ResponseEntity<>(userId, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/all")
    @CrossOrigin("*")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> userDtoList = userDao.findAll();
        return ResponseEntity.ok(userDtoList.stream()
                .sorted(Comparator.comparingInt(UserDto::getPoints).reversed())
                .collect(Collectors.toList()));
    }
}

package com.example.iplprediction.controller;

import com.example.iplprediction.dao.UserDao;
import com.example.iplprediction.dto.UserDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
        String trimmedLowerCaseUsername = username.trim().toLowerCase();
        Integer userId = null;
        if (StringUtils.hasText(trimmedLowerCaseUsername)) {
            // check if username already exists
            userId = userDao.findByUserName(trimmedLowerCaseUsername);
            if (userId == null) {
                return new ResponseEntity<>(HttpStatusCode.valueOf(404));
            }
        }
        return new ResponseEntity<>(userId, HttpStatusCode.valueOf(200));
    }

    @PostMapping
    @CrossOrigin("*")
    public ResponseEntity<Integer> createNewUser(@RequestParam("username") String username) {
        String trimmedLowerCaseUsername = username.trim().toLowerCase();
        Integer userId = null;
        if (StringUtils.hasText(trimmedLowerCaseUsername)) {
            // check if username already exists
            userId = userDao.findByUserName(trimmedLowerCaseUsername);
            if (userId != null) {
                return new ResponseEntity<>(HttpStatusCode.valueOf(400));
            }
            userId = userDao.save(trimmedLowerCaseUsername);
        }

        return new ResponseEntity<>(userId, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/all")
    @CrossOrigin("*")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> userDtoList = userDao.findAll();
        AtomicInteger rank = new AtomicInteger(1);
        AtomicInteger previousPoints = new AtomicInteger(Integer.MIN_VALUE);
        return ResponseEntity.ok(userDtoList.stream()
                .sorted(Comparator.comparingInt(UserDto::getPoints).reversed())
                .map(userDto -> {
                    if (previousPoints.get() == Integer.MIN_VALUE || previousPoints.get() == userDto.getPoints()) {
                        userDto.setRank(rank.get());
                    } else if (previousPoints.get() > userDto.getPoints()) {
                        rank.set(rank.get()  + 1);
                        userDto.setRank(rank.get());
                    }
                    previousPoints.set(userDto.getPoints());
                    return userDto;
                })
                .collect(Collectors.toList()));
    }
}

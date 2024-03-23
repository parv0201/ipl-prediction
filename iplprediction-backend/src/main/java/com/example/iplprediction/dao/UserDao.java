package com.example.iplprediction.dao;

import com.example.iplprediction.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Repository
@Slf4j
public class UserDao {

    private static final String INSERT_QUERY = "INSERT INTO USERS (USER_NAME) VALUES (?)";
    private static final String GET_ID_QUERY = "SELECT ID FROM USERS WHERE USER_NAME = ?";
    private static final String GET_ALL_QUERY = "SELECT ID, USER_NAME, POINTS FROM USERS";
    private static final String UPDATE_SCORE_QUERY = "UPDATE USERS SET POINTS = ? WHERE ID = ?";

    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer save(String userName) {
        jdbcTemplate.update(INSERT_QUERY, userName);
        log.info("User {} inserted", userName);
        return jdbcTemplate.queryForObject(GET_ID_QUERY, Integer.class, userName);
    }

    public Integer findByUserName(String username) {
        try {
            return jdbcTemplate.queryForObject(GET_ID_QUERY, Integer.class, username);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public List<UserDto> findAll() {
        List<UserDto> userDtos = new ArrayList<>();
        try {
            jdbcTemplate.query(GET_ALL_QUERY, rs -> {
                UserDto userDto = new UserDto();
                userDto.setUserId(rs.getInt(1));
                userDto.setUsername(rs.getString(2));
                userDto.setPoints(rs.getInt(3));
                userDtos.add(userDto);
            });
            return userDtos;
        } catch (EmptyResultDataAccessException ex) {
            return userDtos;
        }
    }

    public void updateScore(Integer userId, Integer score) {
        jdbcTemplate.update(UPDATE_SCORE_QUERY, ps -> {
            ps.setInt(1, score);
            ps.setInt(2, userId);
        });
    }
}

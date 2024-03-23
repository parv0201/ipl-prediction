package com.example.iplprediction.dao;

import com.example.iplprediction.dto.MatchDto;
import com.example.iplprediction.dto.MatchPredictionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class PredictionDao {

    private static final String INSERT_QUERY = "INSERT INTO PREDICTION (USER_ID, MATCH_ID, TEAM_ID) VALUES (?, ?, ?)";

    private static final String UPDATE_QUERY = "UPDATE PREDICTION SET TEAM_ID = ? WHERE USER_ID = ? AND MATCH_ID = ?";
    private static final String GET_PREDICTION_QUERY = "SELECT TEAM_ID FROM PREDICTION WHERE USER_ID = ? AND MATCH_ID = ?";

    private static final String GET_PREDICTIONS_FOR_MATCH = "SELECT p.USER_ID, p.TEAM_ID, u.USER_NAME, t.SHORT_NAME FROM PREDICTION p " +
            "JOIN USERS u ON p.USER_ID = u.ID JOIN TEAMS t ON p.TEAM_ID = t.ID WHERE p.MATCH_ID = ?";

    private final JdbcTemplate jdbcTemplate;

    public PredictionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Integer userId, Integer matchId, Integer teamId) {
        jdbcTemplate.update(INSERT_QUERY, userId, matchId, teamId);
        log.info("Prediction inserted for userId {}", userId);
    }

    public void update(Integer userId, Integer matchId, Integer teamId) {
        jdbcTemplate.update(UPDATE_QUERY, teamId, userId, matchId);
        log.info("Prediction updated for userId {}", userId);
    }

    public Integer getPredictedTeamId(Integer userId, Integer matchId) {
        try {
            return jdbcTemplate.queryForObject(GET_PREDICTION_QUERY, Integer.class, userId, matchId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public List<MatchPredictionDto> getAllPredictionsForMatch(Integer matchId) {
        List<MatchPredictionDto> matchPredictionDtoList = new ArrayList<>();

        try {
            jdbcTemplate.query(GET_PREDICTIONS_FOR_MATCH, rs -> {
                MatchPredictionDto matchPredictionDto = new MatchPredictionDto();
                matchPredictionDto.setUserId(rs.getInt(1));
                matchPredictionDto.setTeamId(rs.getInt(2));
                matchPredictionDto.setUsername(rs.getString(3));
                matchPredictionDto.setTeam(rs.getString(4));
                matchPredictionDtoList.add(matchPredictionDto);
            }, matchId);
            return matchPredictionDtoList;
        } catch (EmptyResultDataAccessException ex) {
            return matchPredictionDtoList;
        }
    }
}

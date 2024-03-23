package com.example.iplprediction.dao;

import com.example.iplprediction.cricbuzz.model.MatchInfo;
import com.example.iplprediction.dto.MatchDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Array;
import java.sql.*;
import java.time.Instant;
import java.util.*;

@Repository
@Slf4j
public class MatchesDao {

    private static final String INSERT_QUERY = "INSERT INTO MATCHES VALUES (?, ?, ?, ?, ?, ?)";
    private static final String GET_ALL_QUERY = "SELECT * FROM MATCHES";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM MATCHES WHERE ID = ?";
    private static final String GET_ALL_BETWEEN_DATES = "SELECT * FROM MATCHES WHERE START_TIME > ? AND START_TIME < ?";
    private static final Integer DEFAULT_SCORE = 2;
    private static final Integer DEFAULT_MINUS_SCORE = 0;

    private static final Set<Integer> specialTeamsSet1 = new HashSet<>(Arrays.asList(58, 62, 59));
    private static final Set<Integer> specialTeamsSet2 = new HashSet<>(Arrays.asList(65, 63));

    private final JdbcTemplate jdbcTemplate;

    public MatchesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAll(List<MatchInfo> matchInfoList) {
        int[] insertedRows = jdbcTemplate.batchUpdate(INSERT_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                MatchInfo matchInfo = matchInfoList.get(i);
                ps.setInt(1, matchInfo.getMatchId());
                ps.setInt(2, (Integer) matchInfo.getTeam1().get("teamId"));
                ps.setInt(3, (Integer) matchInfo.getTeam2().get("teamId"));
                ps.setTimestamp(4, new Timestamp(matchInfo.getStartDate()));
                ps.setInt(5, isSpecialMatch((Integer) matchInfo.getTeam1().get("teamId"),
                        (Integer) matchInfo.getTeam2().get("teamId")) ? 4 : DEFAULT_SCORE);
                ps.setInt(6, isSpecialMatch((Integer) matchInfo.getTeam1().get("teamId"),
                        (Integer) matchInfo.getTeam2().get("teamId")) ? 2 : DEFAULT_MINUS_SCORE);
            }

            @Override
            public int getBatchSize() {
                return matchInfoList.size();
            }
        });

        log.info("{} rows inserted in MATCHES table", Arrays.stream(insertedRows).sum());
    }

    public List<MatchDto> getAllMatches() {
        List<MatchDto> matchDtoList = new ArrayList<>();
        jdbcTemplate.query(GET_ALL_QUERY, rs -> {
            MatchDto matchDto = new MatchDto();
            matchDto.setMatchId(rs.getInt(1));
            matchDto.setTeam1Id(rs.getInt(2));
            matchDto.setTeam2Id(rs.getInt(3));
            matchDto.setMatchStartDate(rs.getTimestamp(4));
            matchDto.setPoints(rs.getInt(5));
            matchDto.setMinusPoints(rs.getInt(6));
            matchDtoList.add(matchDto);
        });

        return matchDtoList;
    }

    public MatchDto findMatchById(Integer matchId) {
        return jdbcTemplate.queryForObject(GET_BY_ID_QUERY, (rs, rowNum) -> {
            MatchDto matchDto1 = new MatchDto();
            matchDto1.setMatchId(rs.getInt(1));
            matchDto1.setTeam1Id(rs.getInt(2));
            matchDto1.setTeam2Id(rs.getInt(3));
            matchDto1.setMatchStartDate(rs.getTimestamp(4));
            matchDto1.setPoints(rs.getInt(5));
            matchDto1.setMinusPoints(rs.getInt(6));
            return matchDto1;
        }, matchId);
    }

    public List<MatchDto> findMatchBetweenDates(Timestamp fromTimestamp, Timestamp toTimestamp) {
        List<MatchDto> matchDtoList = new ArrayList<>();
        try {
            jdbcTemplate.query(GET_ALL_BETWEEN_DATES, rs -> {
                MatchDto matchDto = new MatchDto();
                matchDto.setMatchId(rs.getInt(1));
                matchDto.setTeam1Id(rs.getInt(2));
                matchDto.setTeam2Id(rs.getInt(3));
                matchDto.setMatchStartDate(rs.getTimestamp(4));
                matchDto.setPoints(rs.getInt(5));
                matchDto.setMinusPoints(rs.getInt(6));
                matchDtoList.add(matchDto);
            }, fromTimestamp, toTimestamp);
        } catch (EmptyResultDataAccessException ex) {
            log.error("No matches found between {} and {}", fromTimestamp, toTimestamp);
        }

        return matchDtoList;
    }

    private boolean isSpecialMatch(Integer team1id, Integer team2id) {
        return (specialTeamsSet1.contains(team1id) && specialTeamsSet1.contains(team2id))
                ||
                (specialTeamsSet2.contains(team1id) && specialTeamsSet2.contains(team2id));
    }
}

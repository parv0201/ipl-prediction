package com.example.iplprediction.dao;

import com.example.iplprediction.cricbuzz.model.Team;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Repository
@Slf4j
public class TeamsDao {

    private static final String INSERT_QUERY = "INSERT INTO TEAMS VALUES (?, ?, ?)";
    private static final String GET_QUERY = "SELECT NAME FROM TEAMS WHERE ID = ?";

    private final JdbcTemplate jdbcTemplate;

    public TeamsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAll(List<Team> teams) {
        int[] rowsAffected = jdbcTemplate.batchUpdate(INSERT_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Team team = teams.get(i);
                ps.setInt(1, team.getTeamId());
                ps.setString(2, team.getTeamName());
                ps.setString(3, team.getTeamShortName());
            }

            @Override
            public int getBatchSize() {
                return teams.size();
            }
        });

        log.info("{} rows inserted in TEAMS table", Arrays.stream(rowsAffected).sum());
    }

    public String getTeamName(Integer teamId) {
        return jdbcTemplate.queryForObject(GET_QUERY, String.class, teamId);
    }
}

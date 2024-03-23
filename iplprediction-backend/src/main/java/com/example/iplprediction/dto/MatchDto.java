package com.example.iplprediction.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
public class MatchDto {
    private Integer matchId;
    private Integer team1Id;
    private Integer team2Id;
    private Timestamp matchStartDate;
    private Integer points;
    private Integer minusPoints;
}

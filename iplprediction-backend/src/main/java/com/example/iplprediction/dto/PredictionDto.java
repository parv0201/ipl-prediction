package com.example.iplprediction.dto;

import lombok.Data;

import java.util.List;

@Data
public class PredictionDto {
    private Integer matchId;
    private String team1;
    private Integer team1id;
    private String team2;
    private Integer team2id;
    private Integer points;
    private Integer minusPoints;
    private Integer predictedTeamId;
    private boolean isPredictionAllowed;
    private List<MatchPredictionDto> predictions;
}

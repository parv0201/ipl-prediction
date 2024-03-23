package com.example.iplprediction.dto;

import lombok.Data;

@Data
public class MatchPredictionDto {
    private Integer userId;
    private Integer teamId;
    private String username;
    private String team;
}

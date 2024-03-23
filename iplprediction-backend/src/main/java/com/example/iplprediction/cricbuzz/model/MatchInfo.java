package com.example.iplprediction.cricbuzz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchInfo {
    private Integer matchId;
    private Map<String, Object> team1;
    private Map<String, Object> team2;
    private long startDate;
    private Map<String, Object> result;
}

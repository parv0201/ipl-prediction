package com.example.iplprediction.cricbuzz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MatchResponse {
    private MatchInfo matchInfo;
}

package com.example.iplprediction.cricbuzz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AllMatchesResponse {
    private List<MatchDetails> matchDetails;
}

package com.example.iplprediction.cricbuzz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {
    private Integer teamId;
    private String teamName;
    @JsonProperty("teamSName")
    private String teamShortName;
}

package com.example.iplprediction.cricbuzz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamsResponse {

    @JsonProperty("list")
    private List<Team> teams;
}

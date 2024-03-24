package com.example.iplprediction.controller;

import com.example.iplprediction.service.DataPopulationService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("/migration")
@RestController
public class DataMigrationController {

    private final DataPopulationService dataPopulationService;

    public DataMigrationController(DataPopulationService dataPopulationService) {
        this.dataPopulationService = dataPopulationService;
    }

    @GetMapping("/team")
    @CrossOrigin("*")
    public String populateTeamData() throws IOException {
        dataPopulationService.populateTeamsData();
        return "Success";
    }

    @GetMapping("/match")
    @CrossOrigin("*")
    public String populateMatchData() throws IOException {
        dataPopulationService.populateMatchData();
        return "Success";
    }

    @GetMapping("/testData")
    @CrossOrigin("*")
    public String testData() throws IOException {
        dataPopulationService.addTestData();
        return "Success";
    }
}

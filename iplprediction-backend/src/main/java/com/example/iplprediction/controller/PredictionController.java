package com.example.iplprediction.controller;

import com.example.iplprediction.dto.MatchPredictionDto;
import com.example.iplprediction.dto.PredictionDto;
import com.example.iplprediction.service.PredictionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/predictions")
@CrossOrigin("*")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping
    public ResponseEntity<List<PredictionDto>> getAllPredictionsForDay(@RequestHeader("userId") Integer userId) {
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(predictionService.getAllPredictions(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> predictWinnerTeam(@RequestHeader("userId") Integer userId, @RequestHeader("matchId") Integer matchId,
                                                    @RequestHeader("teamId") Integer teamId) {
        String predictionResponse = predictionService.savePrediction(userId, matchId, teamId);
        if (predictionResponse.equalsIgnoreCase("Success")) {
            return ResponseEntity.ok(predictionResponse);
        } else {
            return new ResponseEntity<>(predictionResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<List<MatchPredictionDto>> getAllPredictionsForMatch(@PathVariable("matchId") Integer matchId) {
        return ResponseEntity.ok(predictionService.getAllPredictionsForMatch(matchId));
    }
}

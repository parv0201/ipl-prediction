package com.example.iplprediction.service;

import com.example.iplprediction.dao.MatchesDao;
import com.example.iplprediction.dao.PredictionDao;
import com.example.iplprediction.dao.TeamsDao;
import com.example.iplprediction.dto.MatchDto;
import com.example.iplprediction.dto.MatchPredictionDto;
import com.example.iplprediction.dto.PredictionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class PredictionService {

    private final MatchesDao matchesDao;
    private final PredictionDao predictionDao;
    private final TeamsDao teamsDao;

    public PredictionService(MatchesDao matchesDao, PredictionDao predictionDao, TeamsDao teamsDao) {
        this.matchesDao = matchesDao;
        this.predictionDao = predictionDao;
        this.teamsDao = teamsDao;
    }

    public List<PredictionDto> getAllPredictions(Integer userid) {

        List<PredictionDto> predictionDtos = new ArrayList<>();

        List<MatchDto> matchDtoList = matchesDao.getAllMatches();
        matchDtoList.forEach(matchDto -> {
            if (isMatchHappeningToday(matchDto.getMatchStartDate())) {
                PredictionDto predictionDto = new PredictionDto();
                predictionDto.setMatchId(matchDto.getMatchId());
                predictionDto.setTeam1id(matchDto.getTeam1Id());
                predictionDto.setTeam1(teamsDao.getTeamName(matchDto.getTeam1Id()));
                predictionDto.setTeam2id(matchDto.getTeam2Id());
                predictionDto.setTeam2(teamsDao.getTeamName(matchDto.getTeam2Id()));
                predictionDto.setPredictedTeamId(predictionDao.getPredictedTeamId(userid, matchDto.getMatchId()));
                predictionDto.setPoints(matchDto.getPoints());
                predictionDto.setMinusPoints(matchDto.getMinusPoints());
                predictionDto.setPredictionAllowed(Timestamp.valueOf(Instant.now().atZone(ZoneId.of("Asia/Kolkata")).toLocalDateTime())
                        .before(matchDto.getMatchStartDate()));
                List<MatchPredictionDto> matchPredictionDtoList = getAllPredictionsForMatch(matchDto.getMatchId());
                predictionDto.setPredictions(matchPredictionDtoList);
                predictionDtos.add(predictionDto);
            }
        });

        return predictionDtos;
    }

    private boolean isMatchHappeningToday(Date matchStartDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY");
        String matchDate = formatter.format(Instant.ofEpochMilli(matchStartDate.getTime())
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toLocalDate());
        String todayDate = formatter.format(LocalDate.now(ZoneId.of("Asia/Kolkata")));

        log.info("matchDate {}", matchDate);
        log.info("todayDate {}", todayDate);
        return matchDate.equalsIgnoreCase(todayDate);
    }

    public String savePrediction(Integer userId, Integer matchId, Integer teamId) {
        // check if prediction is allowed
        MatchDto matchDto = matchesDao.findMatchById(matchId);

        if (Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))).after(matchDto.getMatchStartDate())) {
            return "Prediction is not allowed. Match is already started";
        }

        // check if prediction for userId and match already exists
        Integer predictedMatchId = predictionDao.getPredictedTeamId(userId, matchId);
        if (predictedMatchId != null) {
            predictionDao.update(userId, matchId, teamId);
        } else {
            predictionDao.save(userId, matchId, teamId);
        }
        return "Success";
    }

    public List<MatchPredictionDto> getAllPredictionsForMatch(Integer matchId) {
        return predictionDao.getAllPredictionsForMatch(matchId);
    }
}

package com.example.iplprediction.service;

import com.example.iplprediction.cricbuzz.CricbuzzService;
import com.example.iplprediction.cricbuzz.model.MatchResponse;
import com.example.iplprediction.dao.MatchesDao;
import com.example.iplprediction.dao.UserDao;
import com.example.iplprediction.dto.MatchDto;
import com.example.iplprediction.dto.MatchPredictionDto;
import com.example.iplprediction.dto.UserDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.iplprediction.util.AppConstants.RAPID_API_HOST;
import static com.example.iplprediction.util.AppConstants.RAPID_API_KEY;

@Slf4j
@Service
public class PointsCalculationService {

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${rapidapi.host}")
    private String host;

    private final CricbuzzService cricbuzzService;
    private final PredictionService predictionService;
    private final MatchesDao matchesDao;
    private final UserDao userDao;

    private Map<String, String> headers;

    @PostConstruct
    public void setup() {
        headers = new HashMap<>();
        headers.put(RAPID_API_KEY, apiKey);
        headers.put(RAPID_API_HOST, host);
    }

    public PointsCalculationService(CricbuzzService cricbuzzService, PredictionService predictionService, MatchesDao matchesDao, UserDao userDao) {
        this.cricbuzzService = cricbuzzService;
        this.predictionService = predictionService;
        this.matchesDao = matchesDao;
        this.userDao = userDao;
    }

    @Scheduled(cron = "0 0 2 * * *", zone ="Asia/Kolkata")
    public void calculatePoints() throws IOException {
        // get all previous day matches
        Timestamp previousDayTimeStamp =
                Timestamp.valueOf(Instant.now().atZone(ZoneId.of("Asia/Kolkata")).minusDays(1).toLocalDateTime());
        Timestamp todayTimeStamp =
                Timestamp.valueOf(Instant.now().atZone(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
        List<MatchDto> matchDtoList = matchesDao.findMatchBetweenDates(previousDayTimeStamp, todayTimeStamp);

        for (MatchDto matchDto : matchDtoList) {
            // find winning team id
            Integer winningTeamId = getWinningTeamFromCricbuzz(matchDto.getMatchId());
            Map<Integer, MatchPredictionDto> predictionsMap = predictionService
                    .getAllPredictionsForMatch(matchDto.getMatchId())
                    .stream()
                    .collect(Collectors.toMap(MatchPredictionDto::getUserId, Function.identity()));
            List<UserDto> allUsers = userDao.findAll();

            allUsers.forEach(user -> {
                Integer updatedPoints = user.getPoints();
                MatchPredictionDto matchPredictionDto = predictionsMap.get(user.getUserId());
                if (matchPredictionDto == null) {
                    // User did not predict the team. Minus scoring
                    updatedPoints -= 10;
                } else {
                    Integer predictedTeamId = matchPredictionDto.getTeamId();
                    if (predictedTeamId.intValue() == winningTeamId.intValue()) {
                        updatedPoints += matchDto.getPoints();
                    } else {
                        if (matchDto.getMinusPoints() > 0) {
                            updatedPoints -= matchDto.getMinusPoints();
                        }
                    }
                }
                userDao.updateScore(user.getUserId(), updatedPoints);
                log.info("Successfully updated points for user {} - {}", user.getUsername(), updatedPoints);
            });
        }
    }

    private Integer getWinningTeamFromCricbuzz(Integer matchId) throws IOException {
        // Get the result
        MatchResponse matchResponse = cricbuzzService.getMatch(headers, matchId)
                .execute().body();
        Map<String, Object> matchResult = matchResponse.getMatchInfo().getResult();
        return (Integer) matchResult.get("winningteamId");
    }
}

package com.example.iplprediction.service;

import com.example.iplprediction.cricbuzz.CricbuzzService;
import com.example.iplprediction.cricbuzz.model.*;
import com.example.iplprediction.dao.MatchesDao;
import com.example.iplprediction.dao.PredictionDao;
import com.example.iplprediction.dao.TeamsDao;
import com.example.iplprediction.dao.UserDao;
import com.example.iplprediction.dto.MatchDto;
import com.example.iplprediction.enums.TeamShortName;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.iplprediction.util.AppConstants.RAPID_API_HOST;
import static com.example.iplprediction.util.AppConstants.RAPID_API_KEY;

@Service
@Slf4j
public class DataPopulationService {

    private static final String LEAGUE = "league";
    public static final int IPL_SERIES_ID = 7607;
    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${rapidapi.host}")
    private String host;

    private final CricbuzzService cricbuzzService;
    private final TeamsDao teamsDao;
    private final MatchesDao matchesDao;
    private final UserDao userDao;
    private final PredictionDao predictionDao;
    private Map<String, String> headers;

    public DataPopulationService(CricbuzzService cricbuzzService, TeamsDao teamsDao, MatchesDao matchesDao,
                                 UserDao userDao, PredictionDao predictionDao) {
        this.cricbuzzService = cricbuzzService;
        this.teamsDao = teamsDao;
        this.matchesDao = matchesDao;
        this.userDao = userDao;
        this.predictionDao = predictionDao;
    }

    @PostConstruct
    public void setup() {
        headers = new HashMap<>();
        headers.put(RAPID_API_KEY, apiKey);
        headers.put(RAPID_API_HOST, host);
    }


    public void populateTeamsData() throws IOException {
        // Get all teams from cricbuzz
        TeamsResponse response = cricbuzzService.getAllTeams(headers, LEAGUE)
                .execute().body();
        // filter and get all IPL teams
        List<Team> iplTeams = response.getTeams().stream()
                .filter(team -> StringUtils.hasText(team.getTeamShortName()))
                .filter(team -> TeamShortName.isValidShortName(team.getTeamShortName()))
                .collect(Collectors.toList());

        log.info("IPL Teams : {}", iplTeams);

        teamsDao.saveAll(iplTeams);
    }

    public void populateMatchData() throws IOException {
        Set<Integer> matchIds = matchesDao.getAllMatches().stream().map(MatchDto::getMatchId).collect(Collectors.toSet());

        AllMatchesResponse allMatchesResponse = cricbuzzService.getAllMatchesForSeries(headers, IPL_SERIES_ID)
                .execute().body();

        // Map response to required object
        List<MatchInfo> matchInfoList = new ArrayList<>();
        for (MatchDetails matchDetails : allMatchesResponse.getMatchDetails()) {
            if (matchDetails.getMatchDetailsMap() != null) {
                //noinspection unchecked
                List<Map<String, Object>> matchInfosMapList = (List<Map<String, Object>>) matchDetails.getMatchDetailsMap().get("match");

                for (Map<String, Object> matchInfoMap : matchInfosMapList) {
                    Map<String, Object> singleMatchInfoMap = (Map<String, Object>) matchInfoMap.get("matchInfo");
                    if (!matchIds.contains((Integer) singleMatchInfoMap.get("matchId"))) {
                        MatchInfo matchInfo = new MatchInfo();
                        matchInfo.setMatchId((Integer) singleMatchInfoMap.get("matchId"));
                        matchInfo.setTeam1((Map<String, Object>) singleMatchInfoMap.get("team1"));
                        matchInfo.setTeam2((Map<String, Object>) singleMatchInfoMap.get("team2"));
                        matchInfo.setStartDate(Long.parseLong((String) singleMatchInfoMap.get("startDate")));
                        matchInfoList.add(matchInfo);
                    }
                }
            }
        }

        log.info("{}", matchInfoList);
        matchesDao.saveAll(matchInfoList);
    }

    public void addTestData() {
        //Integer id1 = userDao.save("pk0201");
        //userDao.save("pk0102");
        predictionDao.save(4, 89654, 58);
        predictionDao.save(4, 89661, 61);
    }
}

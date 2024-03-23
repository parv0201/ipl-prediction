package com.example.iplprediction.cricbuzz;

import com.example.iplprediction.cricbuzz.model.AllMatchesResponse;
import com.example.iplprediction.cricbuzz.model.MatchResponse;
import com.example.iplprediction.cricbuzz.model.TeamsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Path;

import java.util.Map;

public interface CricbuzzService {

    @GET("series/v1/{seriesId}")
    Call<AllMatchesResponse> getAllMatchesForSeries(@HeaderMap Map<String, String> requestHeaders,
                                                    @Path("seriesId") Integer seriesId);

    @GET("teams/v1/{type}")
    Call<TeamsResponse> getAllTeams(@HeaderMap Map<String, String> requestHeaders, @Path("type") String seriesType);

    @GET("/mcenter/v1/{matchId}")
    Call<MatchResponse> getMatch(@HeaderMap Map<String, String> requestHeaders, @Path("matchId") Integer matchId);

}

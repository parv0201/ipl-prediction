package com.example.iplprediction.config;

import com.example.iplprediction.cricbuzz.CricbuzzService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class RetrofitConfig {
    @Value("${rapidapi.baseUrl}")
    private String baseUrl;

    @Bean
    CricbuzzService cricbuzzService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(CricbuzzService.class);
    }
}

package com.example.iplprediction;

import com.example.iplprediction.service.DataPopulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IplPredictionApplication implements CommandLineRunner {

	@Autowired
	private DataPopulationService dataPopulationService;

	public static void main(String[] args) {
		SpringApplication.run(IplPredictionApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		dataPopulationService.populateTeamsData();
//		dataPopulationService.populateMatchData();
//		dataPopulationService.addTestData();
	}
}

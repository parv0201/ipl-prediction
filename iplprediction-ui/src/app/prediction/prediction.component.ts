import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';

import Swal from 'sweetalert2';

export interface Prediction {
    name: string;
    team: number;
}

export class Match {
    constructor(
        public matchId: number,
        public team1: String,
        public team1id: number,
        public team2: String,
        public team2id: number,
        public points: number,
        public minusPoints: number,
        public predictedTeamId: number,
        public predictionAllowed: boolean,
        public predictions: Prediction[],
        public showAllPredictions = false
    ) {

    }
}

@Component({
    selector: 'app-prediction',
    templateUrl: './prediction.component.html',
    styleUrls: ['./prediction.component.css']
})
export class PredictionComponent implements OnInit {

    gridColumns = 1;
    matches: Match[] | undefined;
    username;

    displayedColumns: string[] = ['username', 'team'];


    constructor(
        private http: HttpClient,
        private router: Router,
    ) {
        this.username = localStorage.getItem("username");
    }

    public ngOnInit(): void {
        let userId = localStorage.getItem("userId");
        if (userId == undefined) {
            this.router.navigateByUrl("/login");
            return;
        }
        const httpHeaders: HttpHeaders = new HttpHeaders({
            userId: String(userId)
        });

        this.http.get<Match[]>('http://ec2-3-110-118-151.ap-south-1.compute.amazonaws.com:8080/predictions', {
            headers: httpHeaders
        }).subscribe({
            next: (allMatches: Match[]) => {
                this.matches = allMatches;
                this.gridColumns = this.matches.length;
            },
            error: (error) => {
                console.log("Error occurred");
                //this.loginValid = false;
            }
        });
    }

    onMatchButtonClick(matchId: number, teamId: number) {
        const httpHeaders: HttpHeaders = new HttpHeaders({
            userId: String(localStorage.getItem("userId")),
            "matchId": matchId,
            "teamId": teamId
        });

        this.http.post<String>('http://ec2-3-110-118-151.ap-south-1.compute.amazonaws.com:8080/predictions', null, {
            headers: httpHeaders,
            responseType: 'text' as 'json'
        }).subscribe({
            next: (data: String) => {
                console.log(data);
                window.location.reload();
            },
            error: (error) => {
                console.log(error);
                Swal.fire({
                    title: 'Oops!',
                    text: 'Prediction not allowed since Match is already started',
                    icon: 'error',
                    confirmButtonText: 'OK',
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.reload();
                    }
                })
            }
        });
    }

    onShowAllPredictionsClick(match: Match) {
        match.showAllPredictions = true;
    }

    onHideAllPredictionsClick(match: Match) {
        match.showAllPredictions = false;
    }

    onLeaderboardClick() {
        console.log("clicked");
        this.router.navigateByUrl("/leaderboard");
    }
}
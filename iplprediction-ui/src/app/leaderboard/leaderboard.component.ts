import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

export interface User {
    username: string;
    points: number;
}

@Component({
    selector: 'app-leaderboard',
    templateUrl: './leaderboard.component.html',
    styleUrls: ['./leaderboard.component.css']
})
export class LeaderboardComponent implements OnInit {
    users: User[] = [];
    displayedColumns: string[] = ['username', 'points'];

    constructor(private http: HttpClient, private router: Router) {
    }

    public ngOnInit(): void {
        this.http.get<User[]>('https://ec2-13-126-165-22.ap-south-1.compute.amazonaws.com:8080/user/all')
            .subscribe({
                next: (users: User[]) => {
                    this.users = users;
                },
                error: (error) => {
                    console.log("Error occurred");
                }
            });
    }

    onPredictionsClick() {
        this.router.navigateByUrl("/prediction");
    }
}
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

export interface User {
    rank: number,
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
    displayedColumns: string[] = ['rank', 'username', 'points'];

    constructor(private http: HttpClient, private router: Router) {
    }

    public ngOnInit(): void {
        this.http.get<User[]>('http://ec2-43-204-215-77.ap-south-1.compute.amazonaws.com:8080/user/all')
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
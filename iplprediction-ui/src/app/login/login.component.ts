import { Component, OnDestroy, OnInit, ɵprovideZonelessChangeDetection } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
//import { AuthService } from '../auth.service';
import { filter, Subject, take, takeUntil } from 'rxjs';
import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  public loginValid = true;
  public username = '';

  private _destroySub$ = new Subject<void>();
  private readonly returnUrl: string;

  constructor(
    private _route: ActivatedRoute,
    private _router: Router,
    private http: HttpClient
  ) {
    this.returnUrl = this._route.snapshot.queryParams['returnUrl'] || '/game';
  }

  ngOnInit(): void {
    if (localStorage.getItem("userId") != undefined) {
      this._router.navigateByUrl("/prediction");
    }
  }

  public ngOnDestroy(): void {
    this._destroySub$.next();
  }

  public onSubmit(): void {
    this.loginValid = true;

    this.http.get('https://ec2-13-126-165-22.ap-south-1.compute.amazonaws.com:8080/user?username=' + this.username, {
      headers : {
        "Access-Control-Allow-Origin": "*"
      }
    })
      .subscribe({
        next: (data) => {
          localStorage.setItem("userId", String(data));
          localStorage.setItem("username", String(this.username));
          this._router.navigateByUrl("/prediction");
        },
        error: (error) => {
          console.log("Username does not exist!");
          this.loginValid = false;
        }
      });
  }
}
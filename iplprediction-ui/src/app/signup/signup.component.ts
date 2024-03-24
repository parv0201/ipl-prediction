import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { filter, Subject, take, takeUntil } from 'rxjs';
import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnDestroy {
  public signUpValid = true;
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

  public ngOnDestroy(): void {
    this._destroySub$.next();
  }

  public onSubmit(): void {
    this.signUpValid = true;

    this.http.post('http://localhost:8080/user?username=' + this.username, null)
      .subscribe({
        next: (data) => {
          localStorage.setItem("userId", String(data));
          localStorage.setItem("username", String(this.username));
          this._router.navigateByUrl("/prediction");
        },
        error: (error) => {
          console.log("Username already exist!");
          this.signUpValid = false;
        }
      });
  }
}
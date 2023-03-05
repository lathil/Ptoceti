import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from '../services/authentication.service';
import {ActivatedRoute, Router} from '@angular/router';
import {first} from 'rxjs/operators';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  name: string;
  password: string;

  loginInvalid: boolean;

  showPassword = false;

  constructor(private router: Router, private route: ActivatedRoute, private authenticationService: AuthenticationService) {
    this.loginInvalid = false;
  }

  ngOnInit(): void {
  }

  onFormSubmit(): void {
    this.authenticationService.doLogin(this.name, this.password).pipe(first()).subscribe({
      next: (result) => {
        if (result) {
          this.loginInvalid = false;
          const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
          this.router.navigate([returnUrl || '/']);
        }
      },
      error: error => {
        this.loginInvalid = true;
      }
    });
  }

  toggleShowPassword(): void {
    this.showPassword = !this.showPassword;
  }
}

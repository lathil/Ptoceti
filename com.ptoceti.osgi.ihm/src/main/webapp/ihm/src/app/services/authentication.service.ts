import {Injectable} from '@angular/core';
import {LoginService} from '../auth';
import {BehaviorSubject, Observable} from 'rxjs';
import {first, map} from 'rxjs/operators';
import {Router} from '@angular/router';
import {JwtHelperService} from '@auth0/angular-jwt';
import {JwtStorage} from './jwt-storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private readonly authenticated: BehaviorSubject<boolean> = new BehaviorSubject(false);

  private jwtHelper: JwtHelperService = new JwtHelperService();

  constructor(private jwtstorage: JwtStorage, private loginService: LoginService, private router: Router) {
    if (this.jwtstorage.get() !== null) {
      this.authenticated.next(true);
    }

  }

  doLogin(username: string, password: string): Observable<boolean> {
    const basicHeader: string = 'Basic ' + btoa(username + ':' + password);
    return this.loginService.login(basicHeader).pipe(map(credential => {
      if (credential.token) {
        this.jwtstorage.put(credential.token);
        this.authenticated.next(true);
        return true;
      }
      return false;
    }));
  }

  isAuthenticated(): Observable<boolean> {
    return this.authenticated.asObservable();
  }

  doLogout(): void {
    if (this.jwtstorage.get() != null) {
      this.loginService.logout().pipe(first()).subscribe({
        next: response => {
          this.jwtstorage.clear();
          this.authenticated.next(false);
          this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
        }
      });
    }
  }

  doHandleUnAuthorised(): void {
    this.jwtstorage.clear();
    this.authenticated.next(false);
  }

  getUser(): string {
    if (this.isAuthenticated()) {
      let jwtDecoded: any;
      jwtDecoded = this.jwtHelper.decodeToken(this.jwtstorage.get());
      if (!jwtDecoded || !jwtDecoded.hasOwnProperty('sub')) {
        return null;
      }
      return jwtDecoded.sub;
    }

    return null;
  }

  getUserRoles(): string[] {
    if (this.isAuthenticated()) {
      let jwtDecoded: any;
      jwtDecoded = this.jwtHelper.decodeToken(this.jwtstorage.get());
      if (!jwtDecoded || !jwtDecoded.hasOwnProperty('roles')) {
        return null;
      }
      return jwtDecoded.roles;
    }

    return null;
  }

  hasRole(role: string): boolean {
    const userRoles = this.getUserRoles();
    if (userRoles == null) {
      return false;
    }
    if (userRoles.includes(role)) {
      return true;
    }
    return false;
  }

  hasAnyRole(roles: string[]): boolean {
    const userRoles = this.getUserRoles();
    if (userRoles == null) {
      return false;
    }
    return roles.filter(role => userRoles.includes(role)).length > 0;
  }
}


import {Injectable} from '@angular/core';
import {Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {AuthenticationService} from '../../services/authentication.service';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';

@Injectable({providedIn: 'root'})
export class AuthGuard implements CanActivate {
  constructor(
    private router: Router,
    private authenticationService: AuthenticationService
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {

    return this.authenticationService.isAuthenticated().pipe(
      map((isAuthorized: boolean) => {
        if (!isAuthorized) {
          this.router.navigate(['/login'], {queryParams: {returnUrl: state.url}});
          return false;
        }
        const userRoles = this.authenticationService.getUserRoles();
        if (route.data.roles && route.data.roles.filter(role => userRoles.includes(role)).length < 1) {
          // role not authorised
          return false;
        }
        return true;
      })
    );
  }
}

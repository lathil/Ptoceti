import {Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthenticationService} from '../services/authentication.service';
import {catchError, tap} from 'rxjs/operators';

@Injectable()
export class AuthenticationErrorInterceptor implements HttpInterceptor {

  constructor(private authenticationService: AuthenticationService) {
  }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      tap({
        error: err => {
          if ([401].indexOf(err.status) !== -1) {
            this.authenticationService.doHandleUnAuthorised();
          }
          ;
        }
      })
    );
  }
}

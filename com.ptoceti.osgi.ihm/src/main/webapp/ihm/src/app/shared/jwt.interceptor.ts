import {Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthenticationService} from '../services/authentication.service';
import {AuthConfiguration} from '../auth';
import {ApiConfiguration} from '../api';
import {JwtStorage} from '../services/jwt-storage.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor(private jwtstorage: JwtStorage, private authenticationService: AuthenticationService, private authConfiguration: AuthConfiguration, private apiConfiguration: ApiConfiguration) {
  }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (this.jwtstorage.get() != null) {
      const isApiUrl = request.url.startsWith(this.authConfiguration.basePath) || request.url.startsWith(this.apiConfiguration.basePath);
      if (isApiUrl) {
        request = request.clone({
          setHeaders: {
            Authorization: `Bearer ${this.jwtstorage.get()}`
          }
        });
      }
    }

    return next.handle(request);
  }
}

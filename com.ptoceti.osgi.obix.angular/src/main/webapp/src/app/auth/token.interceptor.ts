

import { Injectable, Injector } from '@angular/core';

import { Router } from '@angular/router';

import {Observable} from 'rxjs';
import {tap} from "rxjs/operators";

//Oauth2
import { OAuthService } from 'angular-oauth2-oidc';

import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpResponse, HttpErrorResponse } from '@angular/common/http';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

    oauthService: OAuthService;
    router: Router;
    injector: Injector;


    constructor( injector: Injector) {
        //this.oauthService = oauthService;
        this.router = injector.get(Router);
        this.injector = injector;
    }

    intercept( request: HttpRequest<any>, next: HttpHandler ): Observable<HttpEvent<any>> {

        this.oauthService = this.injector.get(OAuthService);
        this.router = this.injector.get(Router);
        
        request = request.clone( {
            setHeaders: {
                Authorization: 'Bearer ' + this.oauthService.getAccessToken(),
            }
        } );

        return next.handle(request).pipe(tap(event => {
            if ( event instanceof HttpResponse ) {
                // do stuff with response if you want
            }
        }, ( err: any ) => {
            if ( err instanceof HttpErrorResponse ) {
                if ( err.status === 401 || err.status === 403) {
                    // redirect to the login route
                    this.router.navigate( ['./pages/login'],  { queryParams: { returnUrl: this.router.routerState.snapshot.url }} )
                }
            }
        }));
    }
}
import { Component } from '@angular/core';

import { Router } from '@angular/router';

//Oauth2
import { OAuthService } from 'angular-oauth2-oidc';
import { Md5 } from 'ts-md5/dist/md5'

@Component( {
    templateUrl: 'login.component.html'
} )
export class LoginComponent {

    login: string;
    password: string;

    router: Router;
    oauthService: OAuthService;

    constructor( oauthService: OAuthService, router: Router) {
        this.oauthService = oauthService;
        this.router = router;
    }
    
    doLogin(){
        
        let md5 : any = Md5.hashStr(this.password).toString();
    
        this.oauthService.fetchTokenUsingPasswordFlow(this.login, Md5.hashStr(this.password).toString()).then((resp) =>{
            console.debug('login ok');
            this.router.navigate(['./dashboard'])
        })
    }

}

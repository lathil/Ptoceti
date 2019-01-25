import { Component, OnInit } from '@angular/core';

import { Router, Route, ActivatedRoute} from '@angular/router';

//Oauth2
import { OAuthService } from 'angular-oauth2-oidc';
import { Md5 } from 'ts-md5/dist/md5'

@Component( {
    templateUrl: 'login.component.html'
} )
export class LoginComponent implements OnInit {

    login: string;
    password: string;

    returnUrl : string;

    router: Router;
    route: ActivatedRoute;
    oauthService: OAuthService;

    constructor( oauthService: OAuthService, router: Router, route: ActivatedRoute) {
        this.oauthService = oauthService;
        this.router = router;
        this.route = route;
    }
    
    ngOnInit(): void {
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    }
    
    doLogin(){
        
        let md5 : any = Md5.hashStr(this.password).toString();
    
        this.oauthService.fetchTokenUsingPasswordFlow(this.login, Md5.hashStr(this.password).toString()).then((resp) =>{
            console.debug('login ok');
            this.router.navigateByUrl(this.returnUrl);
            
        })
    }

}

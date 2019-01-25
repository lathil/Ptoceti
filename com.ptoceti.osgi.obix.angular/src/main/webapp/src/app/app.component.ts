import { Component, OnInit } from '@angular/core';
import { ViewEncapsulation } from '@angular/core';

import { Router, RouterState, ActivatedRoute, ParamMap } from '@angular/router';

//Oauth2
import { OAuthService  } from 'angular-oauth2-oidc';



//Cookie
import { CookieService } from './cookie/cookie.service';

import { AppConfig } from './app.config';


import { LobbyService } from './obix/obix.lobbyservice';
import { AboutService } from './obix/obix.aboutservice';
import { SearchService } from './obix/obix.searchservice';
import { WatchesService } from './obix/obix.watchesservice';
import { HistoriesService } from './obix/obix.historiesservice';
import { AlarmsService } from './obix/obix.alarmsservice';


import { Obj,Lobby, WatchService, HistoryService, AlarmService } from './obix/obix';
import { Subscription } from "rxjs/Subscription";
import { Observable } from "rxjs/Rx";


@Component({
  // tslint:disable-next-line
  selector: 'body',
  styleUrls: ['../scss/style.scss'],
  encapsulation: ViewEncapsulation.None,
  template: '<router-outlet></router-outlet>'
})
export class AppComponent implements OnInit{ 
    
    router: Router;
    lobbyService : LobbyService;
    aboutService : AboutService;
    watchesService : WatchesService;
    historiesService : HistoriesService;
    alarmsService : AlarmsService;
    searchService : SearchService;

    lobby : Lobby;
    watchservice : WatchService;
    historyservice : HistoryService;
    alarmservice : AlarmService;

    config : AppConfig;

    oauthService: OAuthService;
    cookieService: CookieService;
    errorMessage: string;


    constructor (router: Router, oauthService: OAuthService, cookieService: CookieService, lobbyService : LobbyService, aboutService: AboutService, searchService :SearchService, watchesService : WatchesService, historiesService : HistoriesService, alarmsService : AlarmsService, config : AppConfig ){
        
        this.router = router;
        
        this.lobbyService = lobbyService;
        this.aboutService = aboutService;
        this.searchService = searchService;
        this.watchesService = watchesService;
        this.historiesService = historiesService;
        this.alarmsService = alarmsService;
        
        this.oauthService = oauthService;
        this.cookieService = cookieService;
        this.config = config;
        
        // fix window location
        let location : any = window.location;
        // keep current host if ref url is on localhost
        let url : URL = new URL(this.config.getConfig('oauthTokenUrl'));
        if( url.hostname == 'localhost'){
            url.hostname = location.hostname;
        }
        // keep window location protocol
        url.protocol = location.protocol;
        url.port = location.port || (location.protocol === 'https:' ? '443' : '80');
        
        // url of the tojen provider
        this.oauthService.tokenEndpoint = url.toString();
        // other end will refuse if needed
        this.oauthService.requireHttps = false;
        // URL of the SPA to redirect the user to after login
        this.oauthService.redirectUri = window.location.href;
        // The SPA's id. The SPA is registerd with this id at the auth-server
        this.oauthService.clientId = this.cookieService.getCookie("clientid");
        // set to true, to receive also an id_token via OpenId Connect (OIDC) in addition to the
        // OAuth2-based access_token
        this.oauthService.oidc = false; // ID_Token
        // set the scope for the permissions the client should request
        this.oauthService.scope = "owner";
     // setup automatic refresh of tokens
        this.oauthService.events.filter(e => e.type === 'token_expires').subscribe( event => {
            this.oauthService.refreshToken();
        });
        
        
    }
    
    ngOnInit(){
        //called after the constructor and called  after the first ngOnChanges() 
        
        
        if( !this.oauthService.hasValidAccessToken() && this.config.getConfig("secure") === true){
            this.router.navigate(['./pages/login'])
        }
        
        // fix window location
        let location : any = window.location;
        // keep current host if ref url is on localhost
        let url : URL = new URL(this.config.getConfig('lobbyUrl'));
        if( url.hostname == 'localhost'){
            url.hostname = location.hostname;
        }
        // keep window location protocol
        url.protocol = location.protocol;
        url.port = location.port || (location.protocol === 'https:' ? '443' : '80');
        
        let rootUrl : string = url.toString();
        let loobyRoot : string = url.toString();
    
        this.lobbyService.initialize(rootUrl, loobyRoot);
        this.lobbyService.getLobby().flatMap( lobby => {
            this.lobby = lobby;
            
            let aboutUrl : string = this.lobby.getAbout().getUrl(rootUrl);
            this.aboutService.initialize(rootUrl, aboutUrl);
            
            let searchUrl : string = this.lobby.getSearchOp().getUrl(rootUrl);
            this.searchService.initialize(rootUrl, searchUrl);
            
            let watchserviceUrl : string = this.lobby.getWatchServiceRef().getUrl(rootUrl);
            this.watchesService.initialize(rootUrl, watchserviceUrl);
            
            let historyserviceUrl : string = this.lobby.getHistoryServiceRef().getUrl(rootUrl);
            this.historiesService.initialize(rootUrl, historyserviceUrl)
            
            let alarmserviceUrl : string = this.lobby.getAlarmServiceRef().getUrl(rootUrl);
            this.alarmsService.initialize(rootUrl, alarmserviceUrl)
            
            return Observable.zip( this.watchesService.getWatchService(), this.historiesService.getHistoryService(), this.alarmsService.getAlarmService(),
                    (watchservice: WatchService, historyservice: HistoryService, alarmservice: AlarmService) => {
                        this.watchservice = watchservice;
                        this.historyservice = historyservice;
                        this.alarmservice = alarmservice;
                    } )
            
        }).subscribe( () => {
            console.log( 'Application is inilitalized.');
            this.watchesService.getCurrentWatchID().subscribe((currentWatchId) => {
                if( currentWatchId != null) {
                    this.router.navigate(["main/dashboard/watch", currentWatchId]);
                } else this.router.navigate(["main/watches"]);
            })
        });
        
    }
    

}

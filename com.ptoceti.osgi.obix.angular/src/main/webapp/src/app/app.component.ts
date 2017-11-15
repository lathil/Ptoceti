import { Component, OnInit } from '@angular/core';
import { ViewEncapsulation } from '@angular/core';


import { AppConfig } from './app.config';


import { LobbyService } from './obix/obix.lobbyservice';
import { AboutService } from './obix/obix.aboutservice';
import { SearchService } from './obix/obix.searchservice';
import { WatchesService } from './obix/obix.watchesservice';
import { HistoriesService } from './obix/obix.historiesservice';
import { AlarmsService } from './obix/obix.alarmsservice';

import { Obj,Lobby, WatchService, HistoryService, AlarmService } from './obix/obix';


@Component({
  // tslint:disable-next-line
  selector: 'body',
  styleUrls: ['../scss/style.scss'],
  encapsulation: ViewEncapsulation.None,
  template: '<router-outlet></router-outlet>'
})
export class AppComponent implements OnInit{ 
    
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

    errorMessage: string;

    constructor (lobbyService : LobbyService, aboutService: AboutService, searchService :SearchService, watchesService : WatchesService, historiesService : HistoriesService, alarmsService : AlarmsService, config : AppConfig ){
        this.lobbyService = lobbyService;
        this.aboutService = aboutService;
        this.searchService = searchService;
        this.watchesService = watchesService;
        this.historiesService = historiesService;
        this.alarmsService = alarmsService;
        this.config = config;
    }
    
    ngOnInit(){
        //called after the constructor and called  after the first ngOnChanges() 
        
        let rootUrl : string = this.config.getConfig('lobbyUrl');
        let loobyRoot : string = this.config.getConfig('lobbyUrl');
    
        this.lobbyService.initialize(rootUrl, loobyRoot);
        this.lobbyService.getLobby().subscribe(lobby => {
            this.lobby = lobby;
            
            let aboutUrl : string = this.lobby.getAbout().getUrl(rootUrl);
            this.aboutService.initialize(rootUrl, aboutUrl);
            
            let searchUrl : string = this.lobby.getSearchOp().getUrl(rootUrl);
            this.searchService.initialize(rootUrl, searchUrl);
            
            let watchserviceUrl : string = this.lobby.getWatchServiceRef().getUrl(rootUrl);
            this.watchesService.initialize(rootUrl, watchserviceUrl);
            this.watchesService.getWatchService().subscribe( watchservice => {
               this.watchservice = watchservice;
            });
            
            let historyserviceUrl : string = this.lobby.getHistoryServiceRef().getUrl(rootUrl);
            this.historiesService.initialize(rootUrl, historyserviceUrl)
            this.historiesService.getHistoryService().subscribe( historyservice => {
                this.historyservice = historyservice;
            });
            
            let alarmserviceUrl : string = this.lobby.getAlarmServiceRef().getUrl(rootUrl);
            this.alarmsService.initialize(rootUrl, alarmserviceUrl)
            this.alarmsService.getAlarmService().subscribe( alarmservice => {
               this.alarmservice = alarmservice;
            });
            
        });
        
    }
    

}



      
import { NgModule }      from '@angular/core';

import { HttpClientModule } from '@angular/common/http';

//Services


import { LobbyService } from './obix.lobbyservice';
import { AboutService } from './obix.aboutservice';
import { WatchesService } from './obix.watchesservice';
import { HistoriesService } from './obix.historiesservice';
import { AlarmsService } from './obix.alarmsservice';
import { SearchService } from './obix.searchservice';

import { Action } from '../obix/obix.services-commons';



@NgModule({
    imports: [
        HttpClientModule
            ],

    providers: [
                { provide: LobbyService, useClass: LobbyService },
                { provide : AboutService, useClass: AboutService},
                { provide: WatchesService, useClass: WatchesService},
                { provide: HistoriesService, useClass: HistoriesService},
                { provide: AlarmsService, useClass: AlarmsService},
                { provide: SearchService, useClass: SearchService }
          ],
})
export class ObixModule {
    
}


      
import { NgModule }      from '@angular/core';

import { HttpModule } from '@angular/http';
import { AsyncLocalStorageModule } from 'angular-async-local-storage';

//Services


import { LobbyService } from './obix.lobbyservice';
import { AboutService } from './obix.aboutservice';
import { WatchesService } from './obix.watchesservice';
import { HistoriesService } from './obix.historiesservice';
import { AlarmsService } from './obix.alarmsservice';
import { SearchService } from './obix.searchservice';



@NgModule({
    imports: [
              HttpModule,
              AsyncLocalStorageModule
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
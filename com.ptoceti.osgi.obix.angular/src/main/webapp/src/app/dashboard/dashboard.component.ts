import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { WatchAction, WatchesService } from '../obix/obix.watchesservice';

@Component( {
    templateUrl: './dashboard.component.html'
} )
export class DashboardComponent {

    watchesService: WatchesService;
    router : Router;

    constructor( watchesService: WatchesService, router: Router) { 
        this.watchesService = watchesService;
        this.router = router;
    }

    ngAfterViewInit(): void {
        // Re-bounce on dashboard watches
        this.watchesService.getCurrentWatchID().subscribe(( currentWatchId ) => {
            if ( currentWatchId != null ) {
                this.router.navigate( ["main/dashboard/watch", currentWatchId] );
            } 
        } )
    }
}

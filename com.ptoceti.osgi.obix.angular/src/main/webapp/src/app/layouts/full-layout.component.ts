import { Component, OnInit } from '@angular/core';
import { Router, RouterState, ActivatedRoute, ParamMap } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';

import { AboutService } from '../obix/obix.aboutservice';
import { WatchesService } from '../obix/obix.watchesservice';
import { About } from '../obix/obix';

@Component( {
    selector: 'app-dashboard',
    templateUrl: './full-layout.component.html'
} )
export class FullLayoutComponent implements OnInit {

    public disabled = false;
    public status: { isopen: boolean } = { isopen: false };

    aboutService: AboutService;
    watchesService: WatchesService;
    
    router: Router;

    productUrl: string;
    productName: string;

    subscription: Subscription;

    public toggled( open: boolean ): void {
        console.log( 'Dropdown is now: ', open );
    }

    public toggleDropdown( $event: MouseEvent ): void {
        $event.preventDefault();
        $event.stopPropagation();
        this.status.isopen = !this.status.isopen;
    }

    constructor( aboutService: AboutService, watchesService: WatchesService, router: Router ) {
        this.aboutService = aboutService;
        this.watchesService = watchesService;
        this.router = router;
    }

    ngOnInit(): void {


        this.subscription = this.aboutService.getAbout().subscribe(( about ) => {
            this.productUrl = about.getProductUrl().val;
            this.productName = about.getProductName().val;
        } );

        if ( this.router.routerState.snapshot.url == "/" ) {
            this.watchesService.getCurrentWatchID().subscribe((currentWatchId) => {
                if( currentWatchId != null) {
                    this.router.navigate(["/dashboard/watch", currentWatchId]);
                } else this.router.navigate(["watches"]);
            })
        }
    }

    ngOnDestroy() {
        if ( this.subscription ) {
            this.subscription.unsubscribe();
        }
    }
}

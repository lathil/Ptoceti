import { Component, OnInit, AfterViewInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs/Subscription';


import { Obj, Ref, Watch, SearchOut, Status, Contract, Uri } from '../obix/obix';

import { WatchAction, Action, WatchesService } from '../obix/obix.watchesservice';


export class Period {
    value: string;
    name: string;
    constructor( name: string, value: string ) {
        this.name = name; this.value = value;
    }
}

@Component( {
    templateUrl: 'watches.component.html'
} )
export class WatchesComponent implements OnInit, AfterViewInit {

    watchesService: WatchesService;

    watchAddStreamSubscription: Subscription;

    watchList: Array<Watch> = new Array<Watch>();


    //contract used to indicate to search which objects to search for.
    contractFilter: Contract;


    editPeriod: Period = null;

    periods: Array<Period> = [{ name: "1 day", value: "P1D" }, { name: "1 week", value: "P1W" }, { name: "1 month", value: "P1M" }, { name: " 1 year", value: "P1Y" }];


    constructor( watchesService: WatchesService ) {
        this.watchesService = watchesService;
        
        this.contractFilter = new Contract( [new Uri( "obix:Watch" )] );
    }

    ngOnInit() {

        this.watchAddStreamSubscription = this.watchesService.getWatchStream().subscribe( watchAction => {
            if ( watchAction.action == Action.Add ) {
                this.watchList.push( watchAction.watch );
            } else if ( watchAction.action == Action.Delete ) {
                let elemIndex = this.watchList.findIndex( elem => elem.href.val == watchAction.watch.href.val );
                if ( elemIndex > -1 ) {
                    this.watchList.splice( elemIndex, 1 );
              
                }
            } else if ( watchAction.action == Action.Reset ) {
                this.watchList.splice( 0, this.watchList.length );
            } else if ( watchAction.action == Action.Update ) {
                let elemIndex = this.watchList.findIndex( elem => elem.href.val == watchAction.watch.href.val );
                if ( elemIndex > -1 ) {
                    this.watchList[elemIndex] = watchAction.watch;
                   
                }
            }
        } )

    }

    ngOnDestroy() {
        if ( this.watchAddStreamSubscription ) {
            this.watchAddStreamSubscription.unsubscribe();
        }
    }

    ngAfterViewInit() {
        this.watchesService.getWatchesList();
    }

    
    onAdd( ref: Ref ) {
       
    }

    onRefresh() {
        this.watchesService.refreshWatchList();
    }
    
    onCreate(){
        this.watchesService.createWatch();
    }

    onRemove(watch: Watch) {
        this.watchesService.deleteWatch( watch.href.val );
    }


}
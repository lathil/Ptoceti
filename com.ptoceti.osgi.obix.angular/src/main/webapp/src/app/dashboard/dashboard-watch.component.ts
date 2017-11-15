import { Component, Input, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';

import 'rxjs/add/operator/switchMap';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';

import { Obj, Ref, Watch, SearchOut, Contract, Uri } from '../obix/obix';
import { WatchAction, Action, WatchesService } from '../obix/obix.watchesservice';

import { SearchComponent } from '../search/search.component';

import { ItemLoader } from '../items/item-loader.component';

@Component( {
    templateUrl: 'dashboard-watch.component.html'
} )
export class DashboardWatchComponent implements OnInit, OnDestroy, AfterViewInit {

    route: ActivatedRoute;
    watchesService: WatchesService;

    displayedWatch: Watch;

    watchContentSubscription: Subscription;
    watchContentList: Array<Obj> = new Array<Obj>();

    currentWatchUrl: string;

    //  contract used to indicate to search which objects to search for.
    contractFilter: Contract;

    interval: any;

    constructor( route: ActivatedRoute, watchesService: WatchesService ) {
        this.route = route;
        this.watchesService = watchesService;
        
        this.contractFilter = new Contract( [new Uri( "ptoceti:MeasurePoint" ), new Uri( "ptoceti:SwitchPoint" ), new Uri( "ptoceti:ReferencePoint" ), new Uri( "ptoceti:DigitPoint" )] );
    }

    ngOnInit() {

        this.watchContentSubscription = this.watchesService.getWatchContentStream().subscribe( watchContentAction => {
            if ( watchContentAction.action == Action.Add ) {
                this.watchContentList.push( watchContentAction.obj );
            } else if ( watchContentAction.action == Action.Delete ) {
                let elemIndex = this.watchContentList.findIndex( elem => elem.href.val == watchContentAction.obj.href.val );
                if ( elemIndex > -1 ) {
                    this.watchContentList.splice( elemIndex, 1 );
                }
            } else if ( watchContentAction.action == Action.Reset ) {
                this.watchContentList.splice( 0, this.watchContentList.length );
            } else if ( watchContentAction.action == Action.Update ) {
                let elemIndex = this.watchContentList.findIndex( elem => elem.href.val == watchContentAction.obj.href.val );
                if ( elemIndex > -1 ) {
                    this.watchContentList[elemIndex] = watchContentAction.obj;
                }
            }
        } );

        this.currentWatchUrl = this.route.snapshot.paramMap.get( 'url' );

        /*
        this.route.paramMap.switchMap(( params: ParamMap ) => {
            let watchUrl = params.get( 'url' );
        });
        */
    }

    ngOnDestroy() {
        if ( this.watchContentSubscription ) {
            this.watchContentSubscription.unsubscribe();
        }
    }

    ngAfterViewInit(): void {
        this.watchesService.getPoolRefresh( this.currentWatchUrl );

        this.interval = setInterval(() => { this.doPoolChanges() }, 3000 );
    }

    /**
     * Event listener for add action from search component
     * 
     * @param ref what to search for
     */
    onAdd( ref: Ref ) {
        let elemIndex = this.watchContentList.findIndex( elem => elem.href.val == ref.href.val );
        // check if item is already in list.
        if ( elemIndex < 0 ) {
            this.watchesService.addWatchItem( this.currentWatchUrl, ref.href.val );
        }
    }

    onRefresh() {
        this.watchesService.forcePoolRefresh( this.currentWatchUrl );
    }
    
    onEdit(obj: Obj){
        console.log( 'onEdit' );
    }
    
    onRemove(obj: Obj){
        console.log( 'onRemove' );
        this.watchesService.removeWatchItem(this.currentWatchUrl, obj.href.val);
    }

    doPoolChanges() {
        this.watchesService.getPoolChanges( this.currentWatchUrl );
    }
}

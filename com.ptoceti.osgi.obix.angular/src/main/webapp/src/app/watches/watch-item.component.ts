import { Component, OnInit, AfterViewInit, Input, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';


import { Obj, Ref, Watch, SearchOut, Status } from '../obix/obix';

import { WatchesService } from '../obix/obix.watchesservice';


export class Period {
    value: string;
    name: string;
    constructor( name: string, value: string ) {
        this.name = name; this.value = value;
    }
}

@Component( {
    selector: 'watchitem',
    templateUrl: 'watch-item.component.html'
} )
export class WatchItemComponent implements OnInit, AfterViewInit {

    @Input( 'watch' ) watch: Watch;
    @Output() onRemove = new EventEmitter<Watch>();
    
    itemCount: Number = 0;
    itemCountsubscription: Subscription;
    
    
    editPeriod: Period = null;
    periods: Array<Period> = [{ name: "1 day", value: "P1D" }, { name: "1 week", value: "P1W" }, { name: "1 month", value: "P1M" }, { name: " 1 year", value: "P1Y" }];


    isCollapsed: boolean = true;

    watchesService: WatchesService;

    constructor( watchesService: WatchesService) {
        this.watchesService = watchesService;
        
    }

    ngOnInit() {

    }

    ngOnDestroy() {
        if ( this.itemCountsubscription ) {
            this.itemCountsubscription.unsubscribe();
        }
        
    }

    ngAfterViewInit() {
        this.itemCountsubscription = this.watchesService.getWatchCount( this.watch.href.val ).subscribe(( count ) => {
            this.itemCount = count;
        } );
    }

    onRemoveClick() {
        this.onRemove.emit( this.watch );
    }



    getStatusIcon(): string {

        if ( this.watch.status ) {
            let statustoLower = this.watch.status.toLowerCase();
            if ( statustoLower == Status.DISABLED ) return "glyphicon fa-ban";
            if ( statustoLower == Status.FAULT ) return "glyphicon fa-bomb";
            if ( statustoLower == Status.DOWN ) return "glyphicon fa-exclamation-triangle";
            if ( statustoLower == Status.UNAKEDALARM ) return "glyphicon fa-exclamation";
            if ( statustoLower == Status.ALARM ) return "glyphicon fa-bell";
            if ( statustoLower == Status.UNACKED ) return "glyphicon fa-exclamation";
            if ( statustoLower == Status.OVERRIDEN ) return "glyphicon fa-eraser";
            if ( statustoLower == Status.OK ) return "glyphicon fa-check";
        }

        return "fa-check";
    }

}
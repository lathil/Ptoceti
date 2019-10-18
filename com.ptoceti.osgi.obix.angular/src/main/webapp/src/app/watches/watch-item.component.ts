import { Component, OnInit, AfterViewInit, Input, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';

import {Observable, Subscription} from 'rxjs';


import { Obj, Ref, Watch, SearchOut, Status } from '../obix/obix';

import { WatchesService } from '../obix/obix.watchesservice';

import {
    faCheck,
    faBan,
    faBomb,
    faExclamationTriangle,
    faExclamation,
    faBell,
    faEraser,
    faArrowAltCircleRight
} from '@fortawesome/free-solid-svg-icons';

export class Period {
    value: string;
    name: string;
    constructor( name: string, value: string ) {
        this.name = name; this.value = value;
    }
}

@Component( {
    selector: 'watchitem',
    templateUrl: './watch-item.component.html'
} )
export class WatchItemComponent implements OnInit, AfterViewInit {

    @Input( 'watch' ) watch: Watch;
    @Output() onRemove = new EventEmitter<Watch>();

    faCheck = faCheck;
    faBan = faBan;
    faBomb = faBomb;
    faExclamationTriangle = faExclamationTriangle;
    faExclamation = faExclamation;
    faBell = faBell;
    faEraser = faEraser;
    faArrowAltCircleRight = faArrowAltCircleRight;

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


    getStatusIcon(): any {

        if ( this.watch.status ) {
            let statustoLower = this.watch.status.toLowerCase();
            if (statustoLower == Status.DISABLED) return faBan;
            if (statustoLower == Status.FAULT) return faBomb;
            if (statustoLower == Status.DOWN) return faExclamationTriangle;
            if (statustoLower == Status.UNAKEDALARM) return faExclamation;
            if (statustoLower == Status.ALARM) return faBell;
            if (statustoLower == Status.UNACKED) return faExclamation;
            if (statustoLower == Status.OVERRIDEN) return faEraser;
            if (statustoLower == Status.OK) return faCheck;
        }

        return faCheck;
    }

}
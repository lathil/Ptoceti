import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {Subscription} from 'rxjs';

import { Obj, Ref, Alarm, SearchOut, Status, Contract, Uri } from '../obix/obix';

import { AlarmsService,  } from '../obix/obix.alarmsservice';

import { Action } from '../obix/obix.services-commons';

import * as moment from 'moment'


@Component( {
    templateUrl: './alarms.component.html'
} )
export class AlarmsComponent implements OnInit, AfterViewInit {
    
    
    
    // the alarm service for all interaction with back end
    alarmsService: AlarmsService;
    //the list of alarms in the main list
    alarmsList: Array<Alarm> = new Array<Alarm>();

    alarmStreamSubscription: Subscription;
    

    //contract used to indicate to search which objects to search for.
    contractFilter: Contract;

    constructor( alarmsService: AlarmsService ) {
        this.alarmsService = alarmsService;
        this.contractFilter = new Contract( [new Uri( "obix:Alarm" )] );
        
    }

    ngOnInit() {
        
     // subscribe to publication of histories
        this.alarmStreamSubscription = this.alarmsService.getAlarmStream().subscribe( alarmAction => {
            if ( alarmAction.action == Action.Add ) {
                this.alarmsList.push( alarmAction.alarm );
            } else if ( alarmAction.action == Action.Delete ) {
                let elemIndex = this.alarmsList.findIndex( elem => elem.href.val == alarmAction.alarm.href.val );
                if ( elemIndex > -1 ) {
                    this.alarmsList.splice( elemIndex, 1 );

                }
            } else if ( alarmAction.action == Action.Reset ) {
                this.alarmsList.splice( 0, this.alarmsList.length );
            } else if ( alarmAction.action == Action.Update ) {
                let elemIndex = this.alarmsList.findIndex( elem => elem.href.val == alarmAction.alarm.href.val );
                if ( elemIndex > -1 ) {
                    this.alarmsList[elemIndex] = alarmAction.alarm;

                }
            }
        } )
    }

    ngOnDestroy() {
        if ( this.alarmStreamSubscription ) {
            this.alarmStreamSubscription.unsubscribe();
        }
    }

    ngAfterViewInit() {
        this.alarmsService.getAlarmsList();
    }

    onAdd( ref: Ref ) {

    }

    onRefresh() {
        this.alarmsService.refreshAlarmList();
    }

    onCreate() {

    }

    onDelete( history: History ) {

    }

    onRemove( history: History ) {

    }

    onSave( history: History ) {

    }
    
    getStatusIcon(alarm: Alarm): string {

        if ( alarm.status ) {
            let statustoLower = alarm.status.toLowerCase();
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
import { Component, Input, OnInit, OnDestroy, AfterViewInit, ViewChild } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';

import 'rxjs/add/operator/switchMap';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';

import { Obj, Ref, Watch, SearchOut, Contract, Uri, MeasurePoint } from '../obix/obix';
import { WatchAction, WatchesService } from '../obix/obix.watchesservice';
import { HistoryAction, HistoriesService } from '../obix/obix.historiesservice';
import { AlarmAction, AlarmsService } from '../obix/obix.alarmsservice';

import { Action } from '../obix/obix.services-commons';

import { SearchComponent } from '../search/search.component';

import { ItemLoader } from '../items/item-loader.component';
import { ItemEditNameComponent } from '../items/item-editname.component';

import { LineChartDataList, MultiLineChartComponent } from '../d3/d3-multiline-chart.component';
import { LineChartData } from '../d3/d3-line-chart.component';

import * as moment from 'moment'

@Component( {
    templateUrl: 'dashboard-watch.component.html'
} )
export class DashboardWatchComponent implements OnInit, OnDestroy, AfterViewInit {

    route: ActivatedRoute;
    watchesService: WatchesService;
    historiesService: HistoriesService;
    alarmsService : AlarmsService;
    

    displayedWatch: Watch;

    watchContentSubscription: Subscription;
    watchContentList: Array<Obj> = new Array<Obj>();

    currentWatchUrl: string;

    //  contract used to indicate to search which objects to search for.
    contractFilter: Contract;

    interval: any;

    //the D3 component for displaying histories
    @ViewChild( '#d3multilineChart' )
    d3LineChart: MultiLineChartComponent;
    // the array for chart data for eachhistories
    chartDataList: Array<LineChartDataList> = [];

    historyRollupStreamSubscription : Subscription;
    
    constructor( route: ActivatedRoute, watchesService: WatchesService, historiesService: HistoriesService, alarmsService : AlarmsService ) {
        this.route = route;
        this.watchesService = watchesService;
        this.historiesService = historiesService;
        this.alarmsService = alarmsService;
        
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
        if( this.currentWatchUrl.length > 0){
            this.watchesService.saveCurrentWatchId(this.currentWatchUrl);
        }
        
        this.historyRollupStreamSubscription = this.historiesService.getHistoryRollupStream().subscribe (historyRollupAction => {
            if( historyRollupAction.action == Action.Add || historyRollupAction.action == Action.Update) {
                    let newChartData : Array<LineChartData> = [];
                    for( let record of historyRollupAction.historyRollupRecords){
                        //this.lineChartData.splice(0, this.lineChartData.length)
                        // average
                        let val: any = record.getAvg().val;
                        newChartData.push( new LineChartData( val, new Date(record.getStart().val)));
                    }
                    
                    let list: LineChartDataList = new LineChartDataList(historyRollupAction.history.displayName);
                    list.values = newChartData;
                    
                    let newChartDataList = new Array<LineChartDataList>();
                    this.chartDataList.forEach(elem =>  {if( elem.name != list.name){newChartDataList.push(elem)}});
                    newChartDataList.push(list);
                    
                    this.chartDataList = newChartDataList;
                
            } else if( historyRollupAction.action == Action.Delete ){
                let newChartDataList = new Array<LineChartDataList>();
                this.chartDataList.forEach(elem =>  {if( elem.name != historyRollupAction.history.displayName){newChartDataList.push(elem)}});
                
                this.chartDataList = newChartDataList;
            }
        });
    }

    ngOnDestroy() {
        if ( this.watchContentSubscription ) {
            this.watchContentSubscription.unsubscribe();
        }
        
        if( this.historyRollupStreamSubscription ) {
            this.historyRollupStreamSubscription.unsubscribe();
        }
        
        if( this.interval !== undefined){
            clearInterval(this.interval);
        }
    }

    ngAfterViewInit(): void {
        
        this.watchesService.getPoolRefresh( this.currentWatchUrl );

        //this.interval = setInterval(() => { this.doPoolChanges() }, 3000 );
    }

    /**
     * Update the history chart with obj's history if it has one
     * 
     * @param obj
     */
    updateHistoryChart(obj: Obj){
        let historyRef : Ref= obj.childrens.find(function(this, value, index, obj) : boolean {return value.name == "history"}) as Ref;
        if( historyRef !== undefined) {
            let end =  moment().millisecond(0);  
            let start = moment(end).subtract(24,'hours').minute(0).second(0).millisecond(0);
            let bsRangeValue = [start.toDate(), end.toDate()];
            this.historiesService.refreshNamedHistoryRollup(historyRef, bsRangeValue[0], bsRangeValue[1], "24");
        } 
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
    
    onSave (obj: Obj) {
        this.watchesService.saveWatchItem(obj);
    }
    
    addHistory(obj: Obj) {
        this.historiesService.createHistory(obj).subscribe((historyAction) => {
            if( historyAction.action == Action.Add){
                this.watchesService.updateWatchItem(obj);
            }
        });
    }
    
    canHaveHistory(obj: Obj){
       if( obj.is.contains( (new MeasurePoint()).is.uris[0])){
           return true
       } else {
           return false;
       }
    }
    
    removeHistory(obj: Obj) {
        let historyRef : Ref= obj.childrens.find(function(this, value, index, obj) : boolean {return value.name == "history"}) as Ref;
    
        this.historiesService.deleteHistoy(historyRef).subscribe((historyAction) => {
            if( historyAction.action == Action.Delete){
                this.watchesService.updateWatchItem(obj);
                this.historiesService.hideHistoryRollup(historyRef);
            }
        });
    }
    
    hasHistory(obj :Obj): boolean{
        let historyRef : Ref= obj.childrens.find(function(this, value, index, obj) : boolean {return value.name == "history"}) as Ref;
        if( historyRef !== undefined) {
            return true;
        } else return false;
    }
    
    addAlarm(obj: Obj) {
        this.alarmsService.createAlarm(obj).subscribe((alarmAction) => {
            if( alarmAction.action == Action.Add){
                this.watchesService.updateWatchItem(obj);
            }
        });
    }
    
    removeAlarm(obj: Obj) {
        this.alarmsService.deleteAlarm(obj).subscribe((alarmAction) => {
            if( alarmAction.action == Action.Delete){
                this.watchesService.updateWatchItem(obj);
            }
        });
    }
    
    hasAlarm(obj: Obj): boolean{
        let alarmRef : Ref= obj.childrens.find(function(this, value, index, obj) : boolean {return value.name == "alarm"}) as Ref;
        if( alarmRef !== undefined) {
            return true;
        } else return false;
    }
    
    canHaveAlarm(obj: Obj){
        if( obj.is.contains( (new MeasurePoint()).is.uris[0])){
            return true
        } else {
            return false;
        }
     }

    doPoolChanges() {
        this.watchesService.getPoolChanges( this.currentWatchUrl );
    }
}

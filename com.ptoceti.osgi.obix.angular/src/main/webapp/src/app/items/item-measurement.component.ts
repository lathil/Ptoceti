import { Component, Input, OnInit, OnDestroy, AfterViewInit,  AfterContentInit, OnChanges, SimpleChange, ViewChild } from '@angular/core';

import { Obj, Status, Real, Ref, HistoryRollupRecord } from '../obix/obix';
import { HistoryAction, HistoriesService } from '../obix/obix.historiesservice';
import { Action } from '../obix/obix.services-commons';

import { Item } from './item.component';

import { Subscription } from 'rxjs/Subscription';


import { LineChartComponent, LineChartData } from '../d3/d3-line-chart.component';
import { LineChartDataList, MultiLineChartComponent } from '../d3/d3-multiline-chart.component';

import * as moment from 'moment'

@Component( {
    templateUrl: 'item-measurement.component.html'
} )
export class ItemMeasurement extends Item implements OnInit,AfterViewInit , AfterContentInit, OnDestroy, OnChanges{

    obj: Real;
    historiesService: HistoriesService;

    lineChartData : Array<LineChartData> = [];

    historyRollupStreamSubscription : Subscription;

    @ViewChild('#d3lineChart')
    d3LineChart : LineChartComponent;

    constructor( historiesService: HistoriesService ){
        super();
        this.historiesService = historiesService;
    }
    
    ngOnInit() {
        
        this.historyRollupStreamSubscription = this.historiesService.getHistoryRollupStream().subscribe (historyRollupAction => {
            if( historyRollupAction.action == Action.Add || historyRollupAction.action == Action.Update) {
                   
                let historyRef : Ref= this.obj.childrens.find(function(this, value, index, obj) : boolean {return value.name == "history"}) as Ref;
                if( historyRollupAction.history.href.val = historyRef.href.val){
                    let newChartData : Array<LineChartData> = [];
                    for( let record of historyRollupAction.historyRollupRecords){
                        //this.lineChartData.splice(0, this.lineChartData.length)
                        newChartData.push( new LineChartData( record.getAvg().val, new Date(record.getStart().val)));
                    }
                    this.lineChartData = newChartData;
                }
            } 
        });
        
    }
    
    ngOnDestroy(): void {
        if( this.historyRollupStreamSubscription ) {
            this.historyRollupStreamSubscription.unsubscribe();
        }
    }
    
    ngAfterContentInit(): void {
        
    }
    
    ngAfterViewInit(): void {
        if( this.hasHistory()){
            this.updateHistoryChart();
        }
        
    }

    ngOnChanges( changes: { [propKey: string]: SimpleChange } ) {
        console.log( 'onChanges' );
    }
    
    updateHistoryChart(){
        let historyRef : Ref= this.obj.childrens.find(function(this, value, index, obj) : boolean {return value.name == "history"}) as Ref;
        if(historyRef !== undefined) {
            
            let end =  moment().millisecond(0);  
            let start = moment(end).subtract(24,'hours').minute(0).second(0).millisecond(0);
            let bsRangeValue = [start.toDate(), end.toDate()];
            this.historiesService.refreshNamedHistoryRollup(historyRef, bsRangeValue[0], bsRangeValue[1], "24");
            
        }
    }
    
    hasHistory(): boolean{
        let historyRef : Ref= this.obj.childrens.find(function(this, value, index, obj) : boolean {return value.name == "history"}) as Ref;
        if( historyRef !== undefined) {
            return true;
        } else return false;
    }

}

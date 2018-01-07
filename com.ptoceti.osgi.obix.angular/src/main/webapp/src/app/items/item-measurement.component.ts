import { Component, Input, OnInit, OnDestroy, AfterViewInit,  AfterContentInit, OnChanges, SimpleChange, ViewChild } from '@angular/core';

import { Obj, Status, Real, Ref, HistoryRollupRecord } from '../obix/obix';
import { HistoryAction, HistoriesService } from '../obix/obix.historiesservice';
import { Item } from './item.component';

import { LineChartComponent, LineChartData } from '../d3/d3-line-chart.component';

@Component( {
    templateUrl: 'item-measurement.component.html'
} )
export class ItemMeasurement extends Item implements OnInit,AfterViewInit , AfterContentInit, OnDestroy, OnChanges{

    obj: Real;
    historiesService: HistoriesService;

    lineChartData : Array<LineChartData> = [];

    @ViewChild('#d3lineChart')
    d3LineChart : LineChartComponent;

    constructor( historiesService: HistoriesService ){
        super();
        this.historiesService = historiesService;
    }
    
    ngOnInit() {
        
    }
    
    ngOnDestroy(): void {
        
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
            
            this.historiesService.getLast24History(historyRef).subscribe((historyRollupOut) => {
                
                let historyRollupRecords : Array<HistoryRollupRecord> = historyRollupOut.getDataList().childrens as Array<HistoryRollupRecord>;
                let newChartData : Array<LineChartData> = [];
                for( let record of historyRollupRecords){
                    //this.lineChartData.splice(0, this.lineChartData.length)
                    newChartData.push( new LineChartData( record.getMax().val, new Date(record.getStart().val)));
                }
                this.lineChartData = newChartData;
                
            }, (error) => {
                console.log( error );
            })
            
            
        }
    }
    
    hasHistory(): boolean{
        let historyRef : Ref= this.obj.childrens.find(function(this, value, index, obj) : boolean {return value.name == "history"}) as Ref;
        if( historyRef !== undefined) {
            return true;
        } else return false;
    }

}

import { Component, OnInit, AfterViewInit, ViewChild, Input, Output, EventEmitter } from '@angular/core';

import {faAngleDoubleUp, faAngleDoubleDown} from '@fortawesome/free-solid-svg-icons';

import { LineChartDataList, MultiLineChartComponent } from '../d3/d3-multiline-chart.component';
import { LineChartData } from '../d3/d3-line-chart.component';

@Component( {
    selector: 'historyitem',
    templateUrl: 'history-item.component.html'
} )
export class HistoryItemComponent implements OnInit {
    
 // the D3 component for displaying histories
    @ViewChild( '#d3multilineChart' )
    d3LineChart: MultiLineChartComponent;
    // the array for chart data for eachhistories
    @Input() chartDataList: Array<LineChartDataList> = [];
    
    @Input() itemId: number;
    
    @Output() onSplit = new EventEmitter<number>();
    @Output()onRetract = new EventEmitter<number>();

    faAngleDoubleUp = faAngleDoubleUp;
    faAngleDoubleDown = faAngleDoubleDown;
    
     
    ngOnInit(): void {
        
    }
    
    canRetract(): boolean {
        if(this.itemId > 0) return true;
        else return false;
    }
    
    canSplit(): boolean{
        if(this.chartDataList.length > 1) return true;
        else return false;
    }
    
    retract(){
        this.onRetract.emit(this.itemId);
    }
    
    split() {
        this.onSplit.emit(this.itemId);
    }
}
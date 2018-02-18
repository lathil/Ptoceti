import { Component, Renderer, OnChanges, AfterViewInit, ElementRef, ViewChild, ViewEncapsulation, SimpleChanges, OnInit, Input, HostListener } from '@angular/core';

import { LineChartData } from './d3-line-chart.component';

import * as moment from 'moment'
import * as D3 from 'd3';

export class LineChartDataList {
    values: Array<LineChartData>;
    name: String;

    constructor( name: string ) {
        this.name = name;
        this.values = new Array<LineChartData>();
    }
}

@Component( {
    selector: 'd3-multiline-chart',
    templateUrl: './d3-multiline-chart.component.html',
    styleUrls: ['./d3-multiline-chart.component.css'],
    encapsulation: ViewEncapsulation.None
} )
export class MultiLineChartComponent implements OnInit, OnChanges, AfterViewInit {

    @Input() data: Array<LineChartDataList>;

    margin: any = { top: 0, bottom: 15, left: 0, right: 0 };
    chart: any;
    xAxis: any;
    yAxis: any;
    width: number;
    height: number;
    xScale: any;
    yScale: any;
    colors: any;

    @ViewChild( 'chart' ) private chartContainer: ElementRef;
    
    private renderer: Renderer;
    
    constructor(renderer: Renderer){
        this.renderer = renderer;
    }

    ngOnInit(): void {
        this.createChart();
        if ( this.data.length > 1 ) {
            this.updateChart();
        }
    }

    ngAfterViewInit(): void {

    }

    ngOnChanges( changes: SimpleChanges ): void {
        if ( this.chart ) {
            if ( changes['data'] ) {
                this.updateChart();
            }
        }
    }


    createChart() {

        let element = this.chartContainer.nativeElement;
        let newHeight = (element.clientWidth * 4) / 16;
        
        this.width = element.offsetWidth - this.margin.left - this.margin.right;
        this.height = newHeight - this.margin.top - this.margin.bottom;
        let svg = D3.select( element ).append( 'svg' )
            .attr( 'width', this.width )
            .attr( 'height', this.height );

        this.chart = svg.append( 'g' )
            .attr( 'class', 'linechart' )
            .attr( 'transform', `translate(${this.margin.left}, ${this.margin.top})` );

        this.xScale = D3.scaleTime().range( [0, this.width] );
        this.yScale = D3.scaleLinear().range( [this.height - this.margin.bottom, 0] );
        
        this.xAxis = svg.append("g")
        .attr("class","xaxis")
        .attr("transform", "translate(0," + (this.height - this.margin.bottom) + ")")
     
        this.yAxis = svg.append("g")
        .attr("class","yaxis")
        .attr("transform", "translate(" + (this.margin.left) + ",0)")

    }

    @HostListener( 'window:resize', ['$event'] )
    resize() {

        let element = this.chartContainer.nativeElement;
        
        let newHeight = (element.clientWidth * 4) / 16;
        
        this.width = element.offsetWidth - this.margin.left - this.margin.right;
        //this.height = element.offsetHeight - this.margin.top - this.margin.bottom;
        this.height = newHeight - this.margin.top - this.margin.bottom;

        D3.selectAll( 'svg' )
            .attr( "width", this.width )
            .attr( "height", this.height );

        this.xScale = D3.scaleTime().range( [0, this.width] );
        this.yScale = D3.scaleLinear().range( [this.height - this.margin.bottom, 0] );
        
        this.xAxis.attr("transform", "translate(0," + (this.height - this.margin.bottom) + ")")
        this.yAxis.attr("transform", "translate(" + (this.margin.left) + ",0)")

        this.updateChart();
    }

    updateChart() {
        
        let xMinDate : any = D3.min( this.data, function( d: LineChartDataList ) {
            return D3.min( d.values, function( d: LineChartData ) { return d.timeStamp; } );
        } );
    
        let xMaxDate : any = D3.max( this.data, function( d: LineChartDataList ) {
            return D3.max( d.values, function( d: LineChartData ) { return d.timeStamp; } );
        } );
    
        this.xScale.domain( [xMinDate, xMaxDate ] );

        this.yScale.domain( [
            D3.min( this.data, function( d: LineChartDataList ) {
                return D3.min( d.values, function( d: LineChartData ) { return d.data; } );
            } ),
            D3.max( this.data, function( d: LineChartDataList ) {
                return D3.max( d.values, function( d: LineChartData ) { return d.data; } );
            } )] );

        let self = this;
        
     // define the area
        var area = D3.area().curve(D3.curveLinear)
            .x(function(d: any) { return self.xScale(d.timeStamp); })
            .y0(this.height - this.margin.bottom)
            .y1(function(d:any) { return self.yScale(d.data); });
        
        let line = D3.line().curve( D3.curveLinear )
            .x( function( d: any ) { return self.xScale( d.timeStamp );} )
            .y( function( d: any ) {return self.yScale( d.data );} );
        
        let days = moment(xMaxDate).diff(xMinDate, 'days');
        
        let interval : any = D3.timeDay;
        if( days <= 1) { // single Day
            let hours = moment(xMaxDate).diff(xMinDate, 'hours');
            if( hours >= 12) interval = D3.timeHour.every(2);
            else interval = D3.timeHour.every(1);
        } else if( days <= 2) { // two days
            interval = D3.timeHour.every(2);
        } else if( days <= 7) { // a week
            interval = D3.timeDay.every(1);
        } else if( days <= 31) { // a month
            interval = D3.timeDay.every(3);
        } else if( days <= 180 ) {
            interval = D3.timeWeek.every(1);
        } else if ( days <= 360) {
            interval = D3.timeMonth.every(1);
        } else if ( days <= 360 * 10) {
            interval = D3.timeMonth.every(6);
        } else {
            interval = D3.timeYear.every(1);
        }
        
        let xAxis = D3.axisBottom(self.xScale).ticks(interval);
        let yAxis = D3.axisRight(self.yScale).ticks(5).tickSize( this.width);
        
        let color = D3.scaleOrdinal(D3.schemeCategory10);
        
        let selectionarea = this.chart.selectAll( "path.area" ).data( this.data ).attr( "d", function( d ) { return area( d.values ); } ).style("fill", function(d,i){return D3.color(color(i)).brighter(5).toString()});
        selectionarea.enter().append( "path" ).attr( "class", "area" ).attr( "d", function( d ) { return area( d.values ); } ).style("fill", function(d,i){return D3.color(color(i)).brighter(5).toString()});
        selectionarea.exit().remove();
        
        let selectionline = this.chart.selectAll( "path.line" ).data( this.data ).attr( "d", function( d ) { return line( d.values ); } ).style("stroke", function(d,i){return color(i)});
        selectionline.enter().append( "path" ).attr( "class", "line" ).attr( "d", function( d ) { return line( d.values ); } ).style("stroke", function(d,i){return color(i)});
        selectionline.exit().remove();
        
        this.xAxis.call(xAxis);
        this.xAxis.selectAll(".domain").attr("stroke", "#777").attr("stroke-dasharray", "2,2");
        
        this.yAxis.call(yAxis);
        this.yAxis.select(".domain").remove();
        this.yAxis.selectAll(".tick line").attr("stroke", "#777").attr("stroke-dasharray", "2,2");
        this.yAxis.selectAll(".tick text").attr("x", 4).attr("dy", -4);
   
    }
}
import { Component, Renderer, OnChanges, AfterViewInit, ElementRef, ViewChild, HostBinding, ViewEncapsulation, SimpleChanges, OnInit, Input, HostListener } from '@angular/core';

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
    
    @Input("fill") showFill : boolean = false;
    
    @Input("tooltip") showTooltip : boolean = false;
    
    @Input("legend") showLegend : boolean = false;

    margin: any = { top: 0, bottom: 15, left: 0, right: 0 };
    chart: any;
    tooltip : any;
    legend : any;
    li : any;
    lb : any;
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
        if ( this.data.length > 0 ) {
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
        
        this.tooltip =  D3.select( element).append('div').attr("class", "d3tooltip");
        
        let svg = D3.select( element ).append( 'svg' )
            .attr( 'width', this.width )
            .attr( 'height', this.height );

        this.chart = svg.append( 'g' )
            .attr( 'class', 'linechart' )
            .attr( 'transform', `translate(${this.margin.left}, ${this.margin.top})` );

        if( this.showLegend){
            this.legend = svg.append('g');
            this.legend.attr('class', 'legend')
            .attr('transform', 'translate(50,20)')
            .attr('font-size','12px')
            
            //this.lb = this.legend.append("rect").classed("legend-box",true);
            this.li = this.legend.append("g").classed("legend-items", true);
        }
        
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

        D3.select(element).select('svg')
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
        
        // define the area
        if( this.showFill == true) {
            var area = D3.area().curve(D3.curveLinear)
                .x(function(d: any) { return self.xScale(d.timeStamp); })
                .y0(this.height - this.margin.bottom)
                .y1(function(d:any) { return self.yScale(d.data); });
            
            let selectionarea = this.chart.selectAll( "path.area" ).data( this.data ).attr( "d", function( d ) { return area( d.values ); } ).style("fill", function(d,i){return D3.color(color(i)).brighter(5).toString()});
            selectionarea.enter().append( "path" ).attr( "class", "area" ).attr( "d", function( d ) { return area( d.values ); } ).style("fill", function(d,i){return D3.color(color(i)).brighter(5).toString()});
            selectionarea.exit().remove();
        }
        
        let tooltipSelection = this.tooltip;
        
        // define the lines
        let line = D3.line().curve( D3.curveLinear )
        .x( function( d: any ) { return self.xScale( d.timeStamp );} )
        .y( function( d: any ) {return self.yScale( d.data );} );
        
        
        let selectionline = this.chart.selectAll( "path.line" ).data( this.data ).attr("data-legend",function(d){return d.name}).attr( "d", function( d ) { return line( d.values ); } ).style("stroke", function(d,i){return color(i)});
        selectionline.enter().append( "path" ).attr( "class", "line" ).attr("data-legend",function(d){return d.name}).attr( "d", function( d ) { return line( d.values ); } ).style("stroke", function(d,i){return color(i)});
        selectionline.exit().remove();
        
        if( this.showTooltip){
            selectionline.on("mouseover", function(d,i){
                return tooltipSelection.style("visibility","visible");
            }).on("mouseout", function(d,i){
                return tooltipSelection.style("visibility","hidden");
            }).on("mousemove", function(d,i){
                
                let boudingRect = self.chartContainer.nativeElement.getBoundingClientRect();
                
                
                let xValue = self.xScale.invert(D3.event.pageX);
                let bisect = D3.bisector(function(d:any){ return d.timeStamp; }).right;
                let index = bisect( d.values, xValue);
                tooltipSelection.text( d.values[index].data );
                return tooltipSelection.style("top",(D3.event.pageY-10 - boudingRect.top - window.scrollY) + "px").style("left",(D3.event.pageX+10 - boudingRect.left - window.scrollX ) + "px");
            });
        }
        
        
        this.xAxis.call(xAxis);
        this.xAxis.selectAll(".domain").attr("stroke", "#777").attr("stroke-dasharray", "2,2");
        
        this.yAxis.call(yAxis);
        this.yAxis.select(".domain").remove();
        this.yAxis.selectAll(".tick line").attr("stroke", "#777").attr("stroke-dasharray", "2,2");
        this.yAxis.selectAll(".tick text").attr("x", 4).attr("dy", -4);
        
        if( this.showLegend){
            this.d3Legend(this);
        }
   
    }
    
    d3Legend(self: MultiLineChartComponent){
        
      
        let items = {};
        let legendPadding : any= self.legend.attr("data-style_padding") || 5;
        
        
        self.chart.selectAll("[data-legend]").each( function(){
            let self = D3.select(this);
            items[self.attr("data-legend")] = {
                    pos: self.attr("data-legend-pos") || this.getBBox().y,
                    color : self.attr("data-legend-color") != undefined ? self.attr("data-legend-color") : self.style("fill") != 'none' ? self.style("fill") : self.style("stroke")
            }
        })
        
        items = D3.entries(items).sort(function(a : any,b : any){ return a.value.pos - b.value.pos})
        
            
        
        let selectionLiCircle = this.li.selectAll("circle").data(items, function(d){return d.key}).attr("cy",function(d: any,i: any){return i-0.25+"em"}).attr("cx",0).attr("r","0.4em").style("fill",function(d){ return d.value.color});
        let selectionLiText = this.li.selectAll("text").data(items, function(d){return d.key}).attr("y", function(d,i){return i+"em"}).attr("x", "1em").text(function(d){;return d.key});
        selectionLiCircle.enter().append("circle").attr("cy",function(d: any,i: any){return i-0.25+"em"}).attr("cx",0).attr("r","0.4em").style("fill",function(d){ return d.value.color});
        selectionLiText.enter().append("text").attr("y", function(d,i){return i+"em"}).attr("x", "1em").text(function(d){;return d.key});
        selectionLiCircle.exit().remove();
        selectionLiText.exit().remove();
 
        //let lbbox = self.legend.selectAll(".legend-items").node().getBBox();
        //this.lb.attr("x",(lbbox.x-legendPadding))
        //    .attr("y",(lbbox.y-legendPadding))
        //    .attr("height", (lbbox.height + 2 * legendPadding))
        //    .attr("width", (lbbox.width + 2 * legendPadding))
    }
}
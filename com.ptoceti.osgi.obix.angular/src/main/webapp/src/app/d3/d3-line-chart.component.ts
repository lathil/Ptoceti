import { Component, OnChanges, AfterViewInit, ElementRef, ViewChild, ViewEncapsulation, SimpleChanges, OnInit, Input, HostListener } from '@angular/core';

import * as D3 from 'd3';


export class LineChartData {
    data: number;
    timeStamp: Date;

    constructor(data: number, timeStamp: Date){
        this.data = data;
        this.timeStamp = timeStamp;
    }
}

@Component( {
    selector: 'd3-line-chart',
    templateUrl: './d3-line-chart.component.html',
    styleUrls: ['./d3-line-chart.component.css'],
    encapsulation: ViewEncapsulation.None
} )
export class LineChartComponent implements OnInit, OnChanges, AfterViewInit {

    @Input() data: Array<LineChartData>;

    margin: any = { top: 0, bottom: 5, left: 0, right: 0 };
    chart: any;
    width: number;
    height: number;
    xScale: any;
    yScale: any;
    colors: any;

    @ViewChild( 'chart' ) private chartContainer: ElementRef;

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
            if (changes['data']){
                this.updateChart();
            }
        }
    }


    createChart() {

        let element = this.chartContainer.nativeElement;
        this.width = element.offsetWidth - this.margin.left - this.margin.right;
        this.height = element.offsetHeight - this.margin.top - this.margin.bottom;
        let svg = D3.select( element ).append( 'svg' )
            .attr( 'width', element.offsetWidth )
            .attr( 'height', element.offsetHeight );

        this.chart = svg.append( 'g' )
            .attr('class', 'linechart')
            .attr( 'transform', `translate(${this.margin.left}, ${this.margin.top})` );

        this.xScale = D3.scaleTime().range( [0, this.width] );
        this.yScale = D3.scaleLinear().range( [this.height, 0] );

    }
    
    @HostListener('window:resize', ['$event'])
    resize(){
        
        let element = this.chartContainer.nativeElement;
        this.width = element.offsetWidth - this.margin.left - this.margin.right;
        this.height = element.offsetHeight - this.margin.top - this.margin.bottom;
        
        D3.selectAll('svg')
        .attr("width", this.width)
        .attr("height", this.height);
        
        this.xScale = D3.scaleTime().range( [0, this.width] );
        this.yScale = D3.scaleLinear().range( [this.height, 0] );
        
        this.updateChart();
    }

    updateChart() {
        
        this.xScale.domain(D3.extent(this.data, function(d: LineChartData) { 
            return d.timeStamp;
            }));
        
        this.yScale.domain(D3.extent(this.data, function(d: LineChartData) { 
            return d.data; 
            }));
        
        
        let self = this;
        let line = D3.line()
            .curve(D3.curveBasis)
            .x(function(d: LineChartData) { 
                let p = self.xScale(d.timeStamp); 
                return p;
            })
            .y(function(d: LineChartData) { 
                let q = self.yScale(d.data); 
                return q
            });
        
        let selection = this.chart.selectAll("path").data([this.data]).attr("d", function(d){return line(d);});
        selection.enter().append("path").attr("class", "line").attr("d", function(d){return line(d);});
        selection.exit().remove();
       
    }
}
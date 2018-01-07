import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MomentModule } from 'angular2-moment';

import { LineChartComponent} from './d3-line-chart.component';

@NgModule(
    {
        imports: [ CommonModule, FormsModule, MomentModule ],
        declarations: [LineChartComponent],
        exports: [LineChartComponent]
    }
)
export class D3Module {
   
}
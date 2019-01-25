import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MomentModule } from 'angular2-moment';

import { LineChartComponent} from './d3-line-chart.component';
import { MultiLineChartComponent} from './d3-multiline-chart.component';
import { Ng2KnobDirective} from './angular2-knob.directive';

import { BoundSensorModule } from 'angular-bound-sensor';

@NgModule(
    {
        imports: [ CommonModule, FormsModule, MomentModule, BoundSensorModule ],
        declarations: [LineChartComponent, MultiLineChartComponent, Ng2KnobDirective],
        exports: [LineChartComponent, MultiLineChartComponent, Ng2KnobDirective]
    }
)
export class D3Module {
   
}
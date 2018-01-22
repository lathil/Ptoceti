import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MomentModule } from 'angular2-moment';

import { LineChartComponent} from './d3-line-chart.component';
import { Ng2KnobDirective} from './angular2-knob.directive';

@NgModule(
    {
        imports: [ CommonModule, FormsModule, MomentModule ],
        declarations: [LineChartComponent, Ng2KnobDirective],
        exports: [LineChartComponent, Ng2KnobDirective]
    }
)
export class D3Module {
   
}
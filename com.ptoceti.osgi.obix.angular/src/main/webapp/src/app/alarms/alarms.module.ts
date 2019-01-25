import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MomentModule } from 'angular2-moment';

import { D3Module} from '../d3/d3.module';

import { CollapseModule } from 'ngx-bootstrap/collapse';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';

import { AlarmsComponent} from './alarms.component'
import { AlarmsRoutingModule } from './alarms-routing.module';

import { SearchModule } from '../search/search.module';


@NgModule({
    imports: [
        CommonModule, FormsModule, MomentModule, BsDatepickerModule.forRoot(), CollapseModule.forRoot(), AlarmsRoutingModule, SearchModule, D3Module   
    ],
    declarations: [ AlarmsComponent ],
    exports : []
  })
export class AlarmsModule {
    
}
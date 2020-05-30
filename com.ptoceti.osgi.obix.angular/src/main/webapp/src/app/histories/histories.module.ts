
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MomentModule } from 'angular2-moment';

import { D3Module} from '../d3/d3.module';

import { CollapseModule } from 'ngx-bootstrap/collapse';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';

import { HistoriesComponent} from './histories.component'
import { HistoriesRoutingModule } from './histories-routing.module';
import { HistoryItemComponent } from './history-item.component';

import { SearchModule } from '../search/search.module';


@NgModule({
    imports: [
        CommonModule, FormsModule, MomentModule, BsDatepickerModule.forRoot(), CollapseModule.forRoot(), HistoriesRoutingModule, SearchModule, D3Module   
    ],
    declarations: [ HistoriesComponent, HistoryItemComponent ],
    exports : []
  })
export class HistoriesModule {
    
}
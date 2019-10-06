import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';

import { DashboardComponent } from './dashboard.component';
import { DashboardWatchComponent} from './dashboard-watch.component';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { SearchModule } from '../search/search.module';
import { ItemModule } from '../items/item.module';

import { D3Module} from '../d3/d3.module';

@NgModule({
  imports: [
    CommonModule,
    DashboardRoutingModule,
    SearchModule,
      ItemModule, FontAwesomeModule,
    D3Module
  ],
  declarations: [ DashboardComponent, DashboardWatchComponent ]
})
export class DashboardModule { }

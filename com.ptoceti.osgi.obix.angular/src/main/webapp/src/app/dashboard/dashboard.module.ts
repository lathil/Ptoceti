import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardComponent } from './dashboard.component';
import { DashboardWatchComponent} from './dashboard-watch.component';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { SearchModule } from '../search/search.module';
import { ItemModule } from '../items/item.module';

@NgModule({
  imports: [
    CommonModule,
    DashboardRoutingModule,
    SearchModule,
    ItemModule
  ],
  declarations: [ DashboardComponent, DashboardWatchComponent ]
})
export class DashboardModule { }

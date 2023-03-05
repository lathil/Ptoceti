import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DashboardComponent} from './dashboard.component';
import {DashboardRoutingModule} from './dashboard-routing.module';
import {WiresListComponent} from './wires-list.component';
import {ThingsListComponent} from './things-list.component';
import {SharedModule} from '../shared/shared.module';

@NgModule({
  declarations: [DashboardComponent, WiresListComponent, ThingsListComponent],
  imports: [
    CommonModule,
    SharedModule,
    DashboardRoutingModule
  ],
})
export class DashboardModule {
}

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DashboardComponent } from './dashboard.component';
import { DashboardWatchComponent } from './dashboard-watch.component';

const routes: Routes = [
{ path: 'watch/:url', component: DashboardWatchComponent},
  { path: '', component: DashboardComponent, data: { title: 'Dashboard'}}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule {}

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AlarmsComponent } from './alarms.component';

const routes: Routes = [
  {
    path: '',
    component: AlarmsComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AlarmsRoutingModule {}
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AlarmsComponent } from './alarms.component';

const routes: Routes = [
  {
    path: '',
    component: AlarmsComponent,
    data: { title: 'Alarms' }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AlarmsRoutingModule {}
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HistoriesComponent } from './histories.component';

const routes: Routes = [
  {
    path: '',
    component: HistoriesComponent,
    data: { title: 'Histories' }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HistoriesRoutingModule {}
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { WatchesComponent } from './watches.component';

const routes: Routes = [
  {
    path: '',
    component: WatchesComponent,
    data: { title: 'Watches' }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class WatchesRoutingModule {}
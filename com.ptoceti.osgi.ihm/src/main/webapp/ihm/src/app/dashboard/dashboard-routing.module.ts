import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

import {DashboardComponent} from './dashboard.component';
import {ThingsResolver} from '../shared/guards/things-resolver.guard';
import {WiresResolver} from '../shared/guards/wires-resolver.guard';

const routes: Routes = [
  {
    path: '', component: DashboardComponent,
    resolve: {
      thingsLoaded: ThingsResolver,
      wiresLoaded: WiresResolver
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule {
}

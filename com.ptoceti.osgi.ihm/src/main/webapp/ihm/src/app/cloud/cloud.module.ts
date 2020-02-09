import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {CloudComponent} from './cloud.component';
import {CloudRoutingModule} from './cloud-routing.module';
import {SharedModule} from '../shared/shared.module';

@NgModule({
  declarations: [CloudComponent],
  imports: [
    CommonModule,
    SharedModule,
    CloudRoutingModule
  ]
})
export class CloudModule {
}

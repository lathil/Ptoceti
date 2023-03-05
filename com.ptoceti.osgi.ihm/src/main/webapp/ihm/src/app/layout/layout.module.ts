import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Routes, RouterModule} from '@angular/router';

import {SharedModule} from '../shared/shared.module';

import {LayoutComponent} from './layout.component';
import {NgbCollapseModule, NgbNavModule} from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [LayoutComponent],
  exports: [
    LayoutComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    SharedModule,
    NgbCollapseModule,
    NgbNavModule
  ]
})
export class LayoutModule {
}

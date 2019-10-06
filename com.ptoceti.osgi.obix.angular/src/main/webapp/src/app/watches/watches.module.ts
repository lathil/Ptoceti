
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MomentModule } from 'angular2-moment';

import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';

import { CollapseModule } from 'ngx-bootstrap/collapse';

import { WatchesComponent } from './watches.component';
import { WatchItemComponent} from './watch-item.component';
import { WatchesRoutingModule } from './watches-routing.module';

import { SearchModule } from '../search/search.module';

import { WatchEditNameComponent } from './watch-editname.component';
import { WatchEditLeaseComponent } from './watch-editlease.component';

@NgModule({
    imports: [
        CommonModule, FormsModule, MomentModule, CollapseModule.forRoot(), WatchesRoutingModule, SearchModule, FontAwesomeModule
    ],
    declarations: [ WatchesComponent, WatchItemComponent,  WatchEditNameComponent, WatchEditLeaseComponent ],
    exports : []
  })
export class WatchesModule {
    
}
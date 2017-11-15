
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MomentModule } from 'angular2-moment';

import { WatchesComponent } from './watches.component';
import { WatchItemComponent} from './watch-item.component';
import { WatchesRoutingModule } from './watches-routing.module';

import { SearchModule } from '../search/search.module';

@NgModule({
    imports: [
        CommonModule, FormsModule, MomentModule, WatchesRoutingModule, SearchModule   
    ],
    declarations: [ WatchesComponent, WatchItemComponent ],
    exports : []
    
  })
export class WatchesModule {
    
}
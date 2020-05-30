
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CollapseModule } from 'ngx-bootstrap/collapse';

import { SearchComponent } from './search.component';

@NgModule({
    imports: [CommonModule, FormsModule,CollapseModule],
    declarations: [ SearchComponent ],
    exports : [SearchComponent]
    
  })
export class SearchModule {
    
}
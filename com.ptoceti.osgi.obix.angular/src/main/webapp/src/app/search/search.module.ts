
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CollapseModule } from 'ngx-bootstrap/collapse';

import { SearchComponent } from './search.component';

import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';

@NgModule({
    imports: [CommonModule, FormsModule, CollapseModule, FontAwesomeModule],
    declarations: [ SearchComponent ],
    exports : [SearchComponent]
    
  })
export class SearchModule {
    
}
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MomentModule } from 'angular2-moment';

import { D3Module} from '../d3/d3.module';


import { Item } from './item.component';
import { ItemLoader} from './item-loader.component';
import { ItemMeasurement } from './item-measurement.component';
import { ItemSwitch} from './item-switch.component';
import { ItemReference} from './item-reference.component';
import { ItemDigit} from './item-digit.component';
import { ItemEditNameComponent} from './item-editname.component';


@NgModule(
    {
        imports: [ CommonModule, FormsModule, MomentModule, D3Module ],
        declarations: [Item, ItemLoader, ItemMeasurement, ItemReference, ItemSwitch, ItemDigit, ItemEditNameComponent],
        entryComponents : [Item, ItemMeasurement, ItemReference, ItemSwitch, ItemDigit],
        exports: [ItemLoader, ItemEditNameComponent]
    }
)
export class ItemModule {
   
}
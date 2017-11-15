import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MomentModule } from 'angular2-moment';

import { Item } from './item.component';
import { ItemLoader} from './item-loader.component';
import { ItemMeasurement } from './item-measurement.component';
import { ItemSwitch} from './item-switch.component';
import { ItemReference} from './item-reference.component';
import { ItemDigit} from './item-digit.component';


@NgModule(
    {
        imports: [ CommonModule, FormsModule, MomentModule],
        declarations: [Item, ItemLoader, ItemMeasurement, ItemReference, ItemSwitch, ItemDigit],
        entryComponents : [Item, ItemMeasurement, ItemReference, ItemSwitch, ItemDigit],
        exports: [ItemLoader]
    }
)
export class ItemModule {
   
}
import { Component, Input, OnInit, OnChanges, SimpleChange } from '@angular/core';

import { Obj, Status, Real } from '../obix/obix';

import { Item } from './item.component';

@Component( {
    templateUrl: 'item-measurement.component.html'
} )
export class ItemMeasurement extends Item implements OnInit, OnChanges {

    obj: Real;


    ngOnInit() {
        console.log( 'oninit' );
    }

    ngOnChanges( changes: { [propKey: string]: SimpleChange } ) {
        console.log( 'onChanges' );
    }
    

}

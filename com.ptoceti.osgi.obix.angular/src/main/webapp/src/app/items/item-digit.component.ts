import { Component, Input, OnInit, OnChanges, SimpleChange, EventEmitter } from '@angular/core';

import { Obj, Status, Bool } from '../obix/obix';

import { Item } from './item.component';

@Component( {
    templateUrl: './item-digit.component.html'
} )
export class ItemDigit extends Item implements OnInit, OnChanges {

    obj: Bool;


    ngOnInit() {
        console.log( 'oninit' );
    }

    ngOnChanges( changes: { [propKey: string]: SimpleChange } ) {
        console.log( 'onChanges' );
    }

    getToggleIcon(): any {

        if( this.obj.val ){
            return this.faToggleOn;
        } else {
            return this.faToggleOff;
        }
        
    }

}

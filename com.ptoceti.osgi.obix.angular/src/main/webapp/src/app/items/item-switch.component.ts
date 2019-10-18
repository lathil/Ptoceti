import { Component, Input, OnInit, OnChanges, SimpleChange } from '@angular/core';

import { Obj, Status, Bool } from '../obix/obix';

import { Item } from './item.component';

@Component( {
    templateUrl: './item-switch.component.html'
} )
export class ItemSwitch extends Item implements OnInit, OnChanges {

    obj: Bool;


    ngOnInit() {
        console.log( 'oninit' );
    }

    ngOnChanges( changes: { [propKey: string]: SimpleChange } ) {
        console.log( 'onChanges' );
    }
    
    onChange(checked: boolean){
        this.obj.val = checked;
        console.log( 'onChanges' );
    }

}

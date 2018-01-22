import { Component, Input, OnInit, OnChanges, SimpleChange } from '@angular/core';

import { Obj, Status, Real } from '../obix/obix';

import { Item } from './item.component';

@Component( {
    templateUrl: 'item-reference.component.html'
} )
export class ItemReference extends Item implements OnInit, OnChanges {

    obj: Real;

    options = {
        readOnly: false,
        animate : { enabled: true, duration: 1000, ease: 'bounce' },
        size: 140,
        unit: '',
        textColor: '#ffffff',
        fontSize: '20',
        fontWeigth: '',
        fontFamily: '',
        valueformat: '',
        value: 0,
        max: 100,
        trackWidth: 19,
        barCap: 25,
        barWidth: 20,
        trackColor: '#FFFFFF',
        barColor: '#808080',
        scale: { enabled: true, type: 'lines', color: '#FFFFFF', width: 4, quantity: 20, height: 10, spaceWidth: 15 },
        subText: {
            enabled: false,
            fontFamily: '',
            font: '14',
            fontWeight: '',
            color: '#000000',
            offset: 7
        },
        skin : {
            type : 'tron',
            width : 5,
            color: '#ffffff',
            spaceWidth: 3
        }
    }

    ngOnInit() {
        console.log( 'oninit' );
        this.options.unit = this.getUnit();
    }

    ngOnChanges( changes: { [propKey: string]: SimpleChange } ) {
        console.log( 'onChanges' );
    }

    onChange() {
        //this.obj.val = range;
        console.log( 'onChanges' );
    }
    
    onKnobChanged(newValue: number){
        console.log( 'onKnobChanged' );
        this.obj.val = newValue
        this.onSave.emit(this.obj);
    }
}

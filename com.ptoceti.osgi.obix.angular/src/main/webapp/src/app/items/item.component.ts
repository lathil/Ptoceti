import { Component, Input, Output, OnInit, OnChanges, SimpleChange, EventEmitter } from '@angular/core';

import {
    faCheck,
    faBan,
    faBomb,
    faExclamationTriangle,
    faExclamation,
    faBell,
    faEraser,
    faEdit,
    faToggleOff,
    faToggleOn,
    faTimes
} from '@fortawesome/free-solid-svg-icons';

import { Obj, Status, Ref } from '../obix/obix';

@Component( {
    templateUrl: 'item.component.html'
} )
export class Item implements OnInit, OnChanges {

    obj: Obj;

    static units = {
        "meter": "m",
        "seconds": "s",
        "kilo": "kg",
        "Kelvin": "\u00B0K",
        "Ampere": "A",
        "mol": "mol",
        "candela": "Cd",
        "ms": "ms",
        "ms2": "ms2",
        "m2": "m2",
        "m3": "m3",
        "Hertz": "Hz",
        "NewTon": "N",
        "pascal": "Pa",
        "Joule": "J",
        "Watt": "W",
        "Coulomb": "C",
        "Volt": "V",
        "Farad": "F",
        "Ohms": "Ohm",
        "Siemens": "S",
        "Weber": "Wb",
        "Tesla": "Ts",
        "lux": "Lx",
        "Gray": "Gy",
        "katal": "kat",
        "celsius": "\u00B0C",
        "farenheit": "\u00B0F",
        "percent": "%",
        "1": ""
    }

    faCheck = faCheck;
    faBan = faBan;
    faBomb = faBomb;
    faExclamationTriangle = faExclamationTriangle;
    faExclamation = faExclamation;
    faBell = faBell;
    faEraser = faEraser;
    faEdit = faEdit;
    faToggleOff = faToggleOff;
    faToggleOn = faToggleOn;
    faTimes = faTimes;

    onEdit = new EventEmitter<Obj>();
    onRemove = new EventEmitter<Obj>();
    onSave = new EventEmitter<Obj>();

    constructor() { }

    ngOnInit() {
        console.log( 'oninit' );
    }

    ngOnChanges( changes: { [propKey: string]: SimpleChange } ) {
        console.log( 'onChanges' );
    }

    getUnit(): string {

        let unitname: string = (this.obj as any).unit.val;
        if ( unitname.lastIndexOf( "obix:Unit/" ) > -1 ) {
            return Item.units[unitname.substr( unitname.lastIndexOf( '/' ) + 1 )];
        }
    }

    getStatusIcon(): any {

        if ( this.obj.status ) {
            let statustoLower = this.obj.status.toLowerCase();
            if (statustoLower == Status.DISABLED) return faBan;
            if (statustoLower == Status.FAULT) return faBomb;
            if (statustoLower == Status.DOWN) return faExclamationTriangle;
            if (statustoLower == Status.UNAKEDALARM) return faExclamation;
            if (statustoLower == Status.ALARM) return faBell;
            if (statustoLower == Status.UNACKED) return faExclamation;
            if (statustoLower == Status.OVERRIDEN) return faEraser;
            if (statustoLower == Status.OK) return faCheck;
        }

        return faCheck;
    }

   
    
    onEditClick(){
        this.onEdit.emit( this.obj );
    }
    
    onRemoveClick(){
        this.onRemove.emit( this.obj );
    }
}

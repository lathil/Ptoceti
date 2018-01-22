
import { Component, ComponentFactoryResolver, ComponentFactory, ViewChild, ViewContainerRef, ComponentRef, Input, Output, OnInit, EventEmitter, OnDestroy, AfterViewInit,  AfterContentInit } from '@angular/core';
import { Obj, Ref, Watch, Uri, SearchOut } from '../obix/obix';

import { Item } from './item.component';
import { ItemMeasurement } from './item-measurement.component';
import { ItemSwitch} from './item-switch.component';
import { ItemReference} from './item-reference.component';
import { ItemDigit} from './item-digit.component';


@Component( { selector: 'item-loader',
    template: '<ng-template #loadanchor ></ng-template>'
    //styles: [':host { flex: 1 1 auto }']
} )
export class ItemLoader implements OnInit, AfterViewInit , AfterContentInit, OnDestroy {

    @Input('obj') obj: Obj;

    @Output() onEdit = new EventEmitter<Obj>();
    @Output() onRemove = new EventEmitter<Obj>();
    @Output() onSave = new EventEmitter<Obj>();
    
    cfr: ComponentFactoryResolver

    componentRef: ComponentRef<Item>;
    
    @ViewChild('loadanchor', { read: ViewContainerRef }) viewContainerRef: ViewContainerRef;

    constructor( cfr: ComponentFactoryResolver ) {
        this.cfr = cfr;
    }

    ngOnInit() {

    }

    ngAfterViewInit() {
        
    }
    
    ngAfterContentInit(){
        
        let compFactory: ComponentFactory<any>;
    
        if( this.obj.is.contains(new Uri( "ptoceti:MeasurePoint" ))){
            compFactory = this.cfr.resolveComponentFactory( ItemMeasurement );
        } else if (this.obj.is.contains(new Uri( "ptoceti:SwitchPoint" ))) {
            compFactory = this.cfr.resolveComponentFactory( ItemSwitch );
        } else if (this.obj.is.contains(new Uri( "ptoceti:ReferencePoint" ))) {
            compFactory = this.cfr.resolveComponentFactory( ItemReference );
        } else if (this.obj.is.contains(new Uri( "ptoceti:DigitPoint" ))) {
            compFactory = this.cfr.resolveComponentFactory( ItemDigit );
        } else {
            compFactory = this.cfr.resolveComponentFactory( Item );
        }
        
        this.viewContainerRef.clear();
        this.componentRef = this.viewContainerRef.createComponent( compFactory );
        
        this.componentRef.instance.obj = this.obj;
        
        this.componentRef.instance.onEdit.subscribe(this.onEdit);
        this.componentRef.instance.onRemove.subscribe(this.onRemove);
        this.componentRef.instance.onSave.subscribe(this.onSave);
        
    }
    
    ngOnDestroy() {
        if ( this.componentRef ) {
            this.componentRef.destroy();
        }
    }
    
    
}
import {Component, Output, Input, EventEmitter, ViewChild, ElementRef, Renderer} from '@angular/core';
import { FormsModule} from "@angular/forms";

import { Obj } from '../obix/obix';

@Component({
    selector: 'itemeditname',
    templateUrl: './item-editname.component.html'
})

export class ItemEditNameComponent {
   
    // inline edit form control
    @ViewChild('editItemNameControl') editItemNameControl;
    @Output() public onSave:EventEmitter<Obj> = new EventEmitter<Obj>();
    @Input( 'item' ) item: Obj;

    value:string = '';
    private editing:boolean = false;
    

    constructor(element: ElementRef, private _renderer:Renderer) {
       
    }

    // Method to display the inline edit form and hide the <a> element
    edit(value){
        this.value = this.item.displayName || this.item.name;  // Store original value in case the form is cancelled
        this.editing = true;

        // Automatically focus input
        setTimeout( _ => this._renderer.invokeElementMethod(this.editItemNameControl.nativeElement, 'focus', []));
    }

    // Method to display the editable value as text and emit save event to host
    onSubmit(value){
        this.item.displayName = value;
        this.onSave.emit(this.item);
        this.editing = false;
    }

    // Method to reset the editable value
    cancel(value:any){
        this.value = this.item.displayName || this.item.name
        this.editing = false;
    }

}
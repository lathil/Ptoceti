import {Component, Output, Input, EventEmitter, ViewChild, ElementRef, Renderer} from '@angular/core';
import { FormsModule} from "@angular/forms";

import { Watch } from '../obix/obix';

@Component({
    selector: 'watcheditname',
    templateUrl: './watch-editname.component.html'
})

export class WatchEditNameComponent {

    // inline edit form control
    @ViewChild('editNameControl') editNameControl;
    @Output() public onSave:EventEmitter<Watch> = new EventEmitter<Watch>();
    @Input( 'watch' ) watch: Watch;

    value:string = '';
    private editing:boolean = false;
    

    constructor(element: ElementRef, private _renderer:Renderer) {}

    // Method to display the inline edit form and hide the <a> element
    edit(value){
        this.value = this.watch.displayName || this.watch.name;  // Store original value in case the form is cancelled
        this.editing = true;

        // Automatically focus input
        setTimeout( _ => this._renderer.invokeElementMethod(this.editNameControl.nativeElement, 'focus', []));
    }

    // Method to display the editable value as text and emit save event to host
    onSubmit(value){
        this.watch.displayName = value;
        this.onSave.emit(this.watch);
        this.editing = false;
    }

    // Method to reset the editable value
    cancel(value:any){
        this.value = this.watch.displayName || this.watch.name
        this.editing = false;
    }

}
import {Component, Output, Input, EventEmitter, ViewChild, ElementRef, Renderer, OnInit, AfterViewInit} from '@angular/core';
import { FormsModule} from "@angular/forms";

import * as moment from 'moment'

import { Watch } from '../obix/obix';

@Component({
    selector: 'watcheditlease',
    templateUrl: 'watch-editlease.component.html'
})

export class WatchEditLeaseComponent implements OnInit, AfterViewInit {
    

    // inline edit form control
    @ViewChild('editLeaseControl') editLeaseControl;
    @Output() public onSave:EventEmitter<Watch> = new EventEmitter<Watch>();
    @Input( 'watch' ) watch: Watch;

    value:string = '';
    private editing:boolean = false;
    
    years:number = 0;
    months:number = 0;
    days:number = 0;
    

    constructor(element: ElementRef, private _renderer:Renderer) {}
    
    ngOnInit(): void {
        let endOfLease = moment().add( moment.duration(this.watch.getLease().val)); 
        this.value = endOfLease.format("dddd, MMMM Do YYYY, h:mm:ss a");
        
        this.months = moment.duration(this.watch.getLease().val).months();
        this.years = moment.duration(this.watch.getLease().val).years();
        this.days = moment.duration(this.watch.getLease().val).days();
    }
    
    ngAfterViewInit(): void {
       
    }

    // Method to display the inline edit form and hide the <a> element
    edit(value){
            
        // Store original value in case the form is cancelled
        this.editing = true;

        // Automatically focus input
        setTimeout( _ => this._renderer.invokeElementMethod(this.editLeaseControl.nativeElement, 'focus', []));
    }

    // Method to display the editable value as text and emit save event to host
    onSubmit(){
        
        this.watch.getLease().val =  moment.duration({ days: this.days, months: this.months, years: this.years }).toISOString();
        this.onSave.emit(this.watch);
        this.editing = false;
    }

    // Method to reset the editable value
    cancel(){
        let endOfLease = moment().add( moment.duration(this.watch.getLease().val)); 
        this.value = endOfLease.format("dddd, MMMM Do YYYY, h:mm:ss a");
        this.editing = false;
    }
    
    keyPress(event: any) {
        const pattern = /[0-9]/;
        let inputChar = String.fromCharCode(event.charCode);

        if (!pattern.test(inputChar)) {
          // invalid character, prevent input
          event.preventDefault();
        }
    }
    
    addYear(){
        this.years = this.years + 1;
    }
    
    subYear(){
        if( this.years > 0){
            this.years = this.years - 1;
        }
    }
    
    addMonth() {
        if( this.months == 12){
            this.years = this.years + 1;
            this.months = 0;
        } else {
            this.months = this.months + 1;
        }
    }

    subMonth(){
        if( this.months == 1){
            if( this.years > 1){
                this.years = this.years - 1;
            }
            this.months = 0;
        }
    }
    
    addDay(){
        if( this.days == 30){
            this.months = this.months + 1;
            this.days = 0;
        } else {
            this.days = this.days + 1;
        }
    }
    
    subDay(){
        if( this.days == 1){
            if( this.months > 1){
                this.months = this.months - 1;
            }
            this.days = 0;
        }
    }
    
    
}


      
import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[search-host]',
})
export class SearchDirective {
  constructor(public viewContainerRef: ViewContainerRef) { }
}
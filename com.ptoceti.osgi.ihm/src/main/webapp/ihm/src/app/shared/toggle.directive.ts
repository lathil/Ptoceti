import {
  AfterContentInit,
  ContentChild,
  Directive,
  ElementRef, EventEmitter,
  HostListener,
  Input,
  OnInit, Output,
  Renderer2
} from '@angular/core';

@Directive({
  selector: '[appToggleOn]'
})
export class ToggleOnDirective implements OnInit, AfterContentInit {
  constructor(private el: ElementRef, private renderer: Renderer2) {
  }

  display(on: boolean): void {
    if (!on) {
      this.renderer.removeClass(this.el.nativeElement, 'd-inline');
      this.renderer.addClass(this.el.nativeElement, 'd-none');
    } else {
      this.renderer.removeClass(this.el.nativeElement, 'd-none');
      this.renderer.addClass(this.el.nativeElement, 'd-inline');
    }
  }

  ngAfterContentInit(): void {
    this.display(false);
  }

  ngOnInit(): void {
    this.display(false);
  }
}

@Directive({
  selector: '[appToggleOff]'
})
export class ToggleOffDirective implements OnInit, AfterContentInit {
  constructor(private el: ElementRef, private renderer: Renderer2) {
  }

  display(on: boolean): void {
    if (!on) {
      this.renderer.removeClass(this.el.nativeElement, 'd-inline');
      this.renderer.addClass(this.el.nativeElement, 'd-none');
    } else {
      this.renderer.removeClass(this.el.nativeElement, 'd-none');
      this.renderer.addClass(this.el.nativeElement, 'd-inline');
    }
  }

  ngAfterContentInit(): void {
    this.display(true);
  }

  ngOnInit(): void {
    this.display(true);
  }
}

@Directive({
  selector: '[appToggle]',
  host: {
    'class': 'dropdown',
  },
})
export class ToggleDirective implements OnInit {

  @ContentChild(ToggleOnDirective, {static: false}) private toggleOn: ToggleOnDirective;
  @ContentChild(ToggleOffDirective, {static: false}) private toggleOff: ToggleOffDirective;

  constructor(private el: ElementRef) {
  }

  @Input('on') on = false;

  @Output() doToggle = new EventEmitter<boolean>();

  @HostListener('click')
  onClick(): void {
    this.toggle();
  }

  ngOnInit(): void {
  }

  toggle(): void {
    if (this.on) {
      this.setOff();
    } else {
      this.setOn();
    }
  }

  setOn(): void {
    if (!this.on) {
      this.on = true;
      this.toggleOff.display(false);
      this.toggleOn.display(true);
      this.doToggle.emit(true);
    }
  }

  setOff(): void {
    if (this.on) {
      this.on = false;
      this.toggleOn.display(false);
      this.toggleOff.display(true);
      this.doToggle.emit(false);
    }
  }
}

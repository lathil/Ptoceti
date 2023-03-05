import {Component, Input, OnInit, Output} from '@angular/core';
import {ThingWrapper} from '../api';

@Component({
  selector: 'app-things-list',
  templateUrl: './things-list.component.html'
})
export class ThingsListComponent {

  @Input() things: ThingWrapper[];

}

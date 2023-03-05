import {Component, Input, OnInit, Output} from '@angular/core';
import {WireWrapper} from '../api';

@Component({
  selector: 'app-wires-list',
  templateUrl: './wires-list.component.html'
})
export class WiresListComponent {

  @Input() wires: WireWrapper[];

}

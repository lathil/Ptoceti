import {Component, OnInit} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {selectAllWires} from '../state/wires.selectors';
import {Observable} from 'rxjs';
import {ThingWrapper, WireWrapper} from '../api';
import {selectAllThings} from '../state/things.selectors';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {


  store: Store;
  wires$: Observable<WireWrapper[]>;
  things$: Observable<ThingWrapper[]>;

  constructor(store: Store) {
    this.store = store;
    this.wires$ = this.store.pipe(select(selectAllWires));
    this.things$ = this.store.pipe(select(selectAllThings));
  }

  ngOnInit(): void {
    console.log('DashboardComponent initialisation.');
  }

}

import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {ThingWrapper} from '../../api';
import {select, Store} from '@ngrx/store';
import {selectThingById} from '../../state/things.selectors';

@Component({
  selector: 'app-thing',
  templateUrl: './thing.component.html',
  styleUrls: ['./thing.component.scss']
})
export class ThingComponent implements OnInit {
  thingId: string;
  activatedRoute: ActivatedRoute;
  thing$: Observable<ThingWrapper>;

  store: Store;

  constructor(store: Store, activatedRoute: ActivatedRoute) {
    this.activatedRoute = activatedRoute;
    this.store = store;
  }

  ngOnInit(): void {
    this.thingId = this.activatedRoute.snapshot.paramMap.get('id');
    this.thing$ = this.store.pipe(select(selectThingById(this.thingId)));
  }

}

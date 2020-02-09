import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {DriverWrapper} from '../../api';
import {select, Store} from '@ngrx/store';
import {selectDriverById} from '../../state/drivers.selectors';

@Component({
  selector: 'app-driver',
  templateUrl: './driver.component.html',
  styleUrls: ['./driver.component.scss']
})
export class DriverComponent implements OnInit {

  driverId: string;
  activatedRoute: ActivatedRoute;

  driver$: Observable<DriverWrapper>;
  driver: DriverWrapper;
  store: Store;

  constructor(store: Store, activatedRoute: ActivatedRoute) {
    this.activatedRoute = activatedRoute;
    this.store = store;
  }

  ngOnInit(): void {
    this.driverId = this.activatedRoute.snapshot.paramMap.get('id');
    this.driver$ = this.store.pipe(select(selectDriverById(this.driverId)));

    this.driver$.subscribe(driver => this.driver = driver);
    console.log('got device');
  }

}

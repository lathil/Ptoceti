import {Component, OnInit} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {DeviceWrapper} from '../../api';
import {selectDeviceById} from '../../state/devices.selectors';

@Component({
  selector: 'app-device',
  templateUrl: './device.component.html',
  styleUrls: ['./device.component.scss']
})
export class DeviceComponent implements OnInit {

  deviceId: string;
  activatedRoute: ActivatedRoute;

  device$: Observable<DeviceWrapper>;
  device: DeviceWrapper;
  store: Store;

  constructor(store: Store, activatedRoute: ActivatedRoute) {
    this.activatedRoute = activatedRoute;
    this.store = store;
  }

  ngOnInit(): void {
    this.deviceId = this.activatedRoute.snapshot.paramMap.get('id');
    this.device$ = this.store.pipe(select(selectDeviceById(this.deviceId)));

    this.device$.subscribe(device => this.device = device);
    console.log('got device');
  }


}

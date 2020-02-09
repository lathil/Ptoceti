import {Injectable} from '@angular/core';
import {Actions, createEffect, Effect, ofType} from '@ngrx/effects';

import {
  loadDevice,
  loadDevicesList,
  loadDevicesListForbidden,
  loadDevicesListSuccess,
  loadDeviceSuccess
} from './devices.actions';

import {DevicesService} from '../api/api/devices.service';
import {catchError, map, switchMap, exhaustMap, mergeMap} from 'rxjs/operators';
import {of} from 'rxjs';
import {genericErrorAction} from './index';

@Injectable()
export class DevicesEffects {

  constructor(private action$: Actions, private devicesService: DevicesService) {
  }

  loadDevicesEffect = createEffect(() => this.action$.pipe(
    ofType(loadDevicesList),
    exhaustMap(() => {
      return this.devicesService.getDevices().pipe(
        map(data => loadDevicesListSuccess({loadedDevices: data})),
        catchError((error: any) => {
          if (error.status === 403) {
            return of(loadDevicesListForbidden());
          }
          return of(genericErrorAction(error));
        })
      );
    })
  ));

  loadDeviceEffect = createEffect(() => this.action$.pipe(
    ofType(loadDevice),
    mergeMap(action => {
      return this.devicesService.getDevice(action.devicePid).pipe(
        map(data => loadDeviceSuccess({loadedDevice: data})),
        catchError((error: any) => of(genericErrorAction(error)))
      );
    })
  ));
}

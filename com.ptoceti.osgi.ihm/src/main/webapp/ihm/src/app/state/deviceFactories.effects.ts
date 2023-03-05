import {Injectable} from '@angular/core';
import {Actions, createEffect, Effect, ofType} from '@ngrx/effects';

import {
  loadDeviceFactoriesList,
  loadDeviceFactoriesListForbidden,
  loadDeviceFactoriesListSuccess
} from './deviceFactories.actions';
import {catchError, map, switchMap} from 'rxjs/operators';
import {FactoriesService} from '../api';
import {of} from 'rxjs';
import {genericErrorAction} from './index';


@Injectable()
export class DeviceFactoriesEffects {

  constructor(private action$: Actions, private factoryService: FactoriesService) {
  }

  loadDevices = createEffect(() => this.action$.pipe(
    ofType(loadDeviceFactoriesList),
    switchMap(() => {
      return this.factoryService.getDeviceFactoryInfos().pipe(
        map(data => loadDeviceFactoriesListSuccess({loadedDeviceFactories: data})),
        catchError((error: any) => {
          if (error.status === 403) {
            return of(loadDeviceFactoriesListForbidden());
          }
          return of(genericErrorAction(error));
        })
      );
    })
  ));
}

import {Injectable} from '@angular/core';
import {Actions, createEffect, Effect, ofType} from '@ngrx/effects';

import {
  loadDriver,
  loadDriversList,
  loadDriversListForbidden,
  loadDriversListSuccess,
  loadDriverSuccess
} from './drivers.actions';

import {DriverService} from '../api/api/driver.service';
import {catchError, exhaustMap, map, mergeMap, switchMap} from 'rxjs/operators';
import {of} from 'rxjs';
import {genericErrorAction} from './index';

@Injectable()
export class DriversEffects {

  constructor(private action$: Actions, private driverService: DriverService) {
  }

  loadDriversEffect = createEffect(() => this.action$.pipe(
    ofType(loadDriversList),
    exhaustMap(() => {
      return this.driverService.getDrivers().pipe(
        map(data => loadDriversListSuccess({loadedDrivers: data})),
        catchError((error: any) => {
          if (error.status === 403) {
            return of(loadDriversListForbidden());
          }
          return of(genericErrorAction(error));
        })
      );
    })
  ));

  loadDriverEffect = createEffect(() => this.action$.pipe(
    ofType(loadDriver),
    mergeMap(action => {
      return this.driverService.getDriver(action.driverPid).pipe(
        map(data => loadDriverSuccess({loadedDriver: data})),
        catchError((error: any) => of(genericErrorAction(error)))
      );
    })
  ));
}

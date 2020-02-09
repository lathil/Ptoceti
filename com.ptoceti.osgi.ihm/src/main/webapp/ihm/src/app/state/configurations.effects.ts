import {Injectable} from '@angular/core';
import {Actions, createEffect, Effect, ofType} from '@ngrx/effects';

import {
  createConfiguration,
  createConfigurationSuccess, loadConfigurationListForbidden,
  loadConfigurationsList,
  loadConfigurationsListSuccess, updateConfiguration, updateConfigurationSuccess
} from './configurations.actions';

import {catchError, exhaustMap, map, switchMap} from 'rxjs/operators';
import {ConfigurationService} from '../api';
import {genericErrorAction} from './index';
import {of} from 'rxjs';

@Injectable()
export class ConfigurationsEffects {

  constructor(private action$: Actions, private  configurationsService: ConfigurationService) {
  }

  loadDevicesDriversConfigurationsEffect = createEffect(() => this.action$.pipe(
    ofType(loadConfigurationsList),
    switchMap(() => {
      return this.configurationsService.getDevicesDriversConfigurations().pipe(
        map(data => loadConfigurationsListSuccess({loadedConfigurations: data})),
        catchError((error: any) => {
          if (error.status === 403) {
            return of(loadConfigurationListForbidden());
          }
          return of(genericErrorAction(error));
        })
      );
    })
  ));

  updateConfigurationEffect = createEffect(() => this.action$.pipe(
    ofType(updateConfiguration),
    exhaustMap(action => {
      return this.configurationsService.getConfiguration(action.configurationPid).pipe(
        map(data => updateConfigurationSuccess({updatedConfiguration: data})),
        catchError((error: any) =>
          of(genericErrorAction(error)))
      );
    })
  ));
}

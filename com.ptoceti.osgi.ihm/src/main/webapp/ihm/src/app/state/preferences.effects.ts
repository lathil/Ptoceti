import {Injectable} from '@angular/core';
import {Actions, createEffect, Effect, ofType} from '@ngrx/effects';


import {catchError, exhaustMap, map, switchMap} from 'rxjs/operators';
import {of} from "rxjs";
import {genericErrorAction} from './index';

import {PrefsService} from '../auth';
import {lloadPreferencesListForbidden, loadPreferencesList, loadPreferencesListSuccess} from './preferences.actions';

@Injectable()
export class PreferencesEffects {

  constructor(private action$: Actions, private preferencesService: PrefsService) {
  }

  loadPreferencesEffect = createEffect(() => this.action$.pipe(
    ofType(loadPreferencesList),
    switchMap(() => {
      return this.preferencesService.getPreferences('user/preferences').pipe(
        map(data => loadPreferencesListSuccess({loadedPreferences: [data]})),
        catchError((error: any) => {
          if (error.status === 403) {
            return of(lloadPreferencesListForbidden());
          }
          return of(genericErrorAction(error));
        })
      );
    })
  ));
}

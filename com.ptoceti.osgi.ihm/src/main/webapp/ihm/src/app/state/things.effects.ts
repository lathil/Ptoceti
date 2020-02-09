import {Injectable} from '@angular/core';
import {Actions, createEffect, Effect, ofType} from '@ngrx/effects';

import {
  loadThing, loadThingsForbidden,
  loadThingsList,
  loadThingsListSuccess,
  loadThingSuccess,
  thingUpdated,
  updateThing
} from './things.actions';

import {ThingsService} from '../api/api/things.service';
import {catchError, map, switchMap} from 'rxjs/operators';
import {of} from "rxjs";
import {genericErrorAction} from "./index";

@Injectable()
export class ThingsEffects {

  constructor(private action$: Actions, private thingService: ThingsService) {
  }

  loadThingsEffect = createEffect(() => this.action$.pipe(
    ofType(loadThingsList),
    switchMap(() => {
      return this.thingService.getThings().pipe(
        map(data => loadThingsListSuccess({loadedThings: data})),
        catchError((error: any) => {
          if (error.status === 403) {
            return of(loadThingsForbidden());
          }
          return of(genericErrorAction(error));
        })
      );
    })
  ));

  loadThingEffect = createEffect(() => this.action$.pipe(
    ofType(loadThing),
    switchMap(action => {
      return this.thingService.getThing(action.thingPid).pipe(
        map(data => loadThingSuccess({loadedThing: data})),
        catchError((error: any) => of(genericErrorAction(error)))
      );
    })
  ));

  updateThingEffect = createEffect(() => this.action$.pipe(
    ofType(thingUpdated),
    switchMap(action => {
      return this.thingService.getThing(action.thingPid).pipe(
        map(data => updateThing({updatedThing: data})),
        catchError((error: any) => of(genericErrorAction(error)))
      );
    })
  ));
}

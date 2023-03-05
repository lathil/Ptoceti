import {Injectable} from '@angular/core';
import {Actions, createEffect, Effect, ofType} from '@ngrx/effects';

import {loadWiresList, loadWiresListForbidden, loadWiresListSuccess} from './wires.actions';

import {WiresService} from '../api/api/wires.service';
import {catchError, map, switchMap} from 'rxjs/operators';
import {of} from 'rxjs';
import {genericErrorAction} from './index';

@Injectable()
export class WiresEffects {

  constructor(private action$: Actions, private wireService: WiresService) {
  }

  loadWires$ = createEffect(() => this.action$.pipe(
    ofType(loadWiresList),
    switchMap(() => {
      return this.wireService.getWires().pipe(
        map(data => loadWiresListSuccess({loadedWires: data})),
        catchError((error: any) => {
          if (error.status === 403) {
            return of(loadWiresListForbidden());
          }
          return of(genericErrorAction(error));
        })
      );
    })
  ));
}

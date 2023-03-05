import {Injectable} from '@angular/core';
import {Actions, createEffect, Effect, ofType} from '@ngrx/effects';

import {loadMetatypesList, loadMetatypesListSuccess} from './metatypes.actions';

import {filter, map, switchMap, withLatestFrom} from 'rxjs/operators';
import {MetatypeService} from '../api';
import {Store} from '@ngrx/store';
import {getMetatypesLoaded, selectAllMetatypes} from './metatypes.selectors';

@Injectable()
export class MetatypesEffects {

  constructor(private action$: Actions, private metatypeService: MetatypeService, private store: Store) {
  }

  loadMetatypes = createEffect(() => this.action$.pipe(
    ofType(loadMetatypesList),
    withLatestFrom(this.store.select(getMetatypesLoaded)),
    filter(([action, loaded]) => !loaded),
    switchMap(() => {
      return this.metatypeService.getMetaTypes().pipe(
        map(data => loadMetatypesListSuccess({loadedMetatypes: data}))
      );
    })
  ));
}

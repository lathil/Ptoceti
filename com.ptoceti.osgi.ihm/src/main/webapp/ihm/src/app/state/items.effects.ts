import {Injectable} from '@angular/core';
import {Actions, createEffect, Effect, ofType} from '@ngrx/effects';

import {
  loadItem,
  loadItemsList, loadItemsListForbidden,
  loadItemsListSuccess, loadItemSuccess,
} from './items.actions';


import {catchError, map, switchMap} from 'rxjs/operators';
import {of} from "rxjs";
import {genericErrorAction} from './index';
import {ItemsService} from '../api';
import {loadItemFunctionDatas} from './functiondata.actions';
import {Action} from '@ngrx/store';

@Injectable()
export class ItemsEffects {

  constructor(private action$: Actions, private itemsService: ItemsService) {
  }

  loadItemsEffect = createEffect(() => this.action$.pipe(
    ofType(loadItemsList),
    switchMap(() => {
      return this.itemsService.getItems().pipe(
        switchMap(data => {
          const actions: Action[] = [loadItemsListSuccess({loadedItems: data}), ...data.map(item => loadItemFunctionDatas({itemUid: item.uid}))];
          return actions;
        }),
        catchError((error: any) => {
          if (error.status === 403) {
            return of(loadItemsListForbidden());
          }
          return of(genericErrorAction(error));
        })
      );
    })
  ));

  loadItemEffect = createEffect(() => this.action$.pipe(
    ofType(loadItem),
    switchMap(action => {
      return this.itemsService.getItem(action.uid).pipe(
        switchMap(data => [loadItemSuccess({loadedItem: data}), loadItemFunctionDatas({itemUid: data.uid})]),
        catchError((error: any) => of(genericErrorAction(error)))
      );
    })
  ));

}

import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {ItemsService} from '../api';
import {catchError, map, switchMap} from 'rxjs/operators';
import {loadItemFunctionDatas, loadItemFunctionDatasSuccess} from './functiondata.actions';
import {of} from 'rxjs';
import {genericErrorAction} from './index';


@Injectable()
export class FunctionDataEffects {

  constructor(private action$: Actions, private itemsService: ItemsService) {
  }

  loadItemFunctionDataEffect = createEffect(() => this.action$.pipe(
    ofType(loadItemFunctionDatas),
    switchMap(action => {
      return this.itemsService.getPropertiesValues(action.itemUid).pipe(
        map(data => loadItemFunctionDatasSuccess({
          loadedItemFunctionData: {
            uid: action.itemUid,
            propertyFunctionData: data
          }
        })),
        catchError((error: any) => of(genericErrorAction(error)))
      );
    })
  ));

}

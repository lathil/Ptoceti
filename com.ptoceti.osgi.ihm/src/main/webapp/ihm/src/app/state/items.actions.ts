import {createAction, props} from '@ngrx/store';
import {ItemWrapper, ThingWrapper} from '../api';

export const loadItemsList = createAction('[Items List] Load Items List');
export const loadItemsListSuccess = createAction('[Items List] Load Items List Success', props<{ loadedItems: ItemWrapper[] }>());
export const loadItemsListForbidden = createAction('[Items List Forbidden] Load Items List Forbidden');
export const loadItem = createAction('[Item] Load Item', props<{ uid: string }>());
export const loadItemSuccess = createAction('[Item] Load Item Success', props<{ loadedItem: ItemWrapper }>());

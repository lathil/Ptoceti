import {createSelector} from '@ngrx/store' ;
import {AppState} from './index';
import {ItemsState} from './items.reducer';
import {ItemWrapper} from '../api';


export const selectItemState = (state: AppState) => state.items;
export const selectAllItems = createSelector(selectItemState, (state: ItemsState) => state.itemsInfos);
export const selectItemByUid = (uid: string) => createSelector(
  selectAllItems, (items: ItemWrapper[]) => items.find(item => item.uid === uid))
;
export const selectItemsByDeviceUid = (uid: string) => createSelector(
  selectAllItems, (items: ItemWrapper[]) => items.filter(item => item.deviceUid === uid))
;
export const getItemsLoaded = createSelector(selectItemState, (state: ItemsState) => state.loaded);

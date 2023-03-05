import {createReducer, on, Action, ActionReducer} from '@ngrx/store';
import {ItemWrapper} from '../api';
import {loadItem, loadItemsList, loadItemsListForbidden, loadItemsListSuccess, loadItemSuccess} from './items.actions';

export interface ItemsState {
  itemsInfos: ItemWrapper[];
  loaded: boolean;
}

export const initialState: ItemsState = {
  itemsInfos: [],
  loaded: false
};

export const itemsReducer = createReducer(
  initialState,
  on(loadItemsList, state => ({...state})),
  on(loadItemsListSuccess, (state, {loadedItems}) => {
    return {...state, itemsInfos: loadedItems, loaded: true};
  }),
  on(loadItemsListForbidden, (state) => ({
    ...state,
    itemsInfos: state.itemsInfos,
    loaded: true
  })),
  on(loadItemSuccess, (state, {loadedItem}) => {
    return {...state, itemsInfos: state.itemsInfos.concat(loadedItem), loaded: true};
  })
);


export function reducer(state: ItemsState | undefined, action: Action) {
  return itemsReducer(state, action);
};

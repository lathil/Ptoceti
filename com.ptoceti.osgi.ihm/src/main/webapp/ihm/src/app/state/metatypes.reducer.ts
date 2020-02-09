import {createReducer, on, Action, ActionReducer} from '@ngrx/store';

import {loadMetatypesList, loadMetatypesListSuccess} from './metatypes.actions';
import {MetatypeWrapper} from '../api';

export interface MetatypesState {
  metatypes: MetatypeWrapper[];
  loaded: boolean;
}

export const initialState: MetatypesState = {
  metatypes: [],
  loaded: false
};

export const metatypesReducer = createReducer(
  initialState,
  on(loadMetatypesList, state => ({...state})),
  on(loadMetatypesListSuccess, (state, {loadedMetatypes}) => ({...state, metatypes: loadedMetatypes, loaded: true})),
);

export function reducer(state: MetatypesState | undefined, action: Action) {
  return metatypesReducer(state, action);
};

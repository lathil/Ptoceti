import {createReducer, on, Action, ActionReducer} from '@ngrx/store';
import {WireWrapper} from '../api';
import {loadWiresList, loadWiresListForbidden, loadWiresListSuccess} from './wires.actions';

export interface WiresState {
  wireInfos: WireWrapper[];
  loaded: boolean;
}

export const initialState: WiresState = {
  wireInfos: [],
  loaded: false
};

export const wiresReducer = createReducer(
  initialState,
  on(loadWiresList, state => ({...state})),
  on(loadWiresListSuccess, (state, {loadedWires}) => ({...state, wireInfos: loadedWires, loaded: true})),
  on(loadWiresListForbidden, state => ({
    ...state,
    wireInfos: state.wireInfos,
    loaded: true
  }))
);

export function reducer(state: WiresState | undefined, action: Action) {
  return wiresReducer(state, action);
};

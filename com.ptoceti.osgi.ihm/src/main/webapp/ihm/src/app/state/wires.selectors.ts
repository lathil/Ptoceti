import {createSelector} from '@ngrx/store' ;
import {AppState} from './index';
import {WiresState} from './wires.reducer';


export const selectWireState = (state: AppState) => state.wires;
export const selectAllWires = createSelector(selectWireState, (state: WiresState) => state.wireInfos);
export const getWiresLoaded = createSelector(selectWireState, (state: WiresState) => state.loaded);

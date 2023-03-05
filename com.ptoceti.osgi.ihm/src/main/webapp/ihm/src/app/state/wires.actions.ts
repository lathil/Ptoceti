import {createAction, props} from '@ngrx/store';
import {WireWrapper} from '../api';

export const loadWiresList = createAction('[Wires List/API] Load Wires List');
export const loadWiresListSuccess = createAction('[Wires List/API] Load Wires List Success', props<{ loadedWires: WireWrapper[] }>());
export const loadWiresListForbidden = createAction('[Wires List/API Forbidden] Load Wires List Forbidden');

import {createAction, props} from '@ngrx/store';
import {MetatypeWrapper} from '../api';

export const loadMetatypesList = createAction('[Metatypes List] Load Metatypes List');
export const loadMetatypesListSuccess = createAction('[Metatypes List] Load Items Metatypes Success', props<{ loadedMetatypes: MetatypeWrapper[] }>());

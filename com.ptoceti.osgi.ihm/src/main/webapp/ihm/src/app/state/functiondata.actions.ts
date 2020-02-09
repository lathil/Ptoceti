import {createAction, props} from '@ngrx/store';
import {ItemFunctionData} from './functiondata.reducer';
import {FunctionDataWrapper} from '../api';

export const loadItemFunctionDatas = createAction('[FunctionData] Load Item FunctionDatas', props<{ itemUid: string }>());
export const loadItemFunctionDatasSuccess = createAction('[FunctionData] Load Item FunctionDatas Success', props<{ loadedItemFunctionData: ItemFunctionData }>());
export const itemFunctionDatapropertyUpdated = createAction('[FunctionData] Item FunctionDatas property updated', props<{ functionUid: string, propertyName: string, functionData: FunctionDataWrapper }>());

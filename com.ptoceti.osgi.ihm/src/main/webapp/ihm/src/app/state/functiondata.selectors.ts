import {AppState} from './index';
import {createSelector} from '@ngrx/store';
import {FunctionDatasState, ItemFunctionData} from './functiondata.reducer';


export const selectFunctionDataState = (state: AppState) => state.functiondatas;
export const selectAllFunctionData = createSelector(selectFunctionDataState, (state: FunctionDatasState) => state.functionDatas);
export const selectFunctionDataByUid = (uid: string) => createSelector(
  selectAllFunctionData, (functionDatas: ItemFunctionData[]) => functionDatas.find(functionData => functionData.uid === uid))
;

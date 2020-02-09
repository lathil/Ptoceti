import {FunctionDataWrapper, FunctionPropertyDataWrapper} from '../api';
import {Action, createReducer, on} from '@ngrx/store';
import {
  itemFunctionDatapropertyUpdated,
  loadItemFunctionDatas,
  loadItemFunctionDatasSuccess
} from './functiondata.actions';


export interface ItemFunctionData {
  uid: string;
  propertyFunctionData: FunctionPropertyDataWrapper[];
}

export interface FunctionDatasState {
  functionDatas: ItemFunctionData[];
}

export const initialState: FunctionDatasState = {
  functionDatas: []
};

export const functionDataReducers = createReducer(
  initialState,
  on(loadItemFunctionDatas, state => ({...state})),
  on(loadItemFunctionDatasSuccess, (state, {loadedItemFunctionData}) => {
    const index = state.functionDatas.findIndex(functionData => functionData.uid === loadedItemFunctionData.uid);
    if (index >= 0) {
      return {
        ...state,
        functionDatas: state.functionDatas.map(functionData => functionData.uid === loadedItemFunctionData.uid ? loadedItemFunctionData : functionData)
      };
    }
    ;
    return {...state, functionDatas: state.functionDatas.concat(loadedItemFunctionData)};
  }),
  on(itemFunctionDatapropertyUpdated, (state, {functionUid, propertyName, functionData}) => {
    const itemFunctionDataIndex = state.functionDatas.findIndex(itemFunctionData => itemFunctionData.uid === functionUid);
    if (itemFunctionDataIndex >= 0) {
      const propertyIndex = state.functionDatas[itemFunctionDataIndex].propertyFunctionData.findIndex(propertyFunctionData => propertyFunctionData.propertyName === propertyName);
      if (propertyIndex >= 0) {
        return {
          ...state,
          functionDatas: {
            ...state.functionDatas.map(itemFunctionData => {
              return itemFunctionData.uid !== functionUid ? itemFunctionData : {
                ...itemFunctionData,
                propertyFunctionData: itemFunctionData.propertyFunctionData.map(propertyDataWrapper => {
                  return propertyDataWrapper.propertyName !== propertyName ? propertyDataWrapper : {
                    ...propertyDataWrapper,
                    functionData
                  };
                })
              };
            })
          }
        };
      }
      ;
    }
    ;
    return {...state, functionDatas: state.functionDatas};
  })
);

export function reducer(state: FunctionDatasState | undefined, action: Action) {
  return functionDataReducers(state, action);
};

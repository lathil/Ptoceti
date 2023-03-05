import {createReducer, on, Action, ActionReducer} from '@ngrx/store';
import {DeviceFactoryInfoWrapper} from '../api';
import {
  loadDeviceFactoriesList,
  loadDeviceFactoriesListForbidden,
  loadDeviceFactoriesListSuccess
} from './deviceFactories.actions';

export interface DeviceFactoriesState {
  factoriesInfo: DeviceFactoryInfoWrapper[];
  loaded: boolean;
}

export const initialState: DeviceFactoriesState = {
  factoriesInfo: [],
  loaded: false
};

export const devicesFactoriesReducer = createReducer(
  initialState,
  on(loadDeviceFactoriesList, state => ({...state})),
  on(loadDeviceFactoriesListSuccess, (state, {loadedDeviceFactories}) => ({
    ...state,
    factoriesInfo: loadedDeviceFactories,
    loaded: true
  })),
  on(loadDeviceFactoriesListForbidden, (state) => ({
    ...state,
    factoriesInfo: state.factoriesInfo,
    loaded: true
  }))
);

export function reducer(state: DeviceFactoriesState | undefined, action: Action) {
  return devicesFactoriesReducer(state, action);
};

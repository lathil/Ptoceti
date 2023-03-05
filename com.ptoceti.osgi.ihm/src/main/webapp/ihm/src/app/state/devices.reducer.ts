import {createReducer, on, Action, ActionReducer} from '@ngrx/store';
import {DeviceWrapper} from '../api';
import {
  deviceRemoved,
  loadDevicesList,
  loadDevicesListForbidden,
  loadDevicesListSuccess,
  loadDeviceSuccess
} from './devices.actions';

export interface DevicesState {
  deviceInfos: DeviceWrapper[];
  loaded: boolean;
}

export const initialState: DevicesState = {
  deviceInfos: [],
  loaded: false
};

export const devicesReducer = createReducer(
  initialState,
  on(loadDevicesList, state => ({...state})),
  on(loadDevicesListSuccess, (state, {loadedDevices}) => ({...state, deviceInfos: loadedDevices, loaded: true})),
  on(loadDevicesListForbidden, (state) => ({
    ...state,
    deviceInfos: state.deviceInfos,
    loaded: true
  })),
  on(loadDeviceSuccess, (state, {loadedDevice}) => ({
    ...state,
    deviceInfos: state.deviceInfos.concat(loadedDevice),
    loaded: true
  })),
  on(deviceRemoved, (state, {devicePid}) => ({
    ...state,
    deviceInfos: state.deviceInfos.filter(value => value.properties['service.pid'] !== devicePid),
    loaded: true
  })),
);

export function reducer(state: DevicesState | undefined, action: Action) {
  return devicesReducer(state, action);
};

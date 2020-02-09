import {createSelector} from '@ngrx/store' ;
import {AppState} from './index';
import {DevicesState} from './devices.reducer';
import {DeviceWrapper} from '../api';

export const selectDeviceState = (state: AppState) => state.devices;
export const selectAllDevices = createSelector(selectDeviceState, (state: DevicesState) => state.deviceInfos);
export const selectDeviceById = (deviceId: string) => createSelector(
  selectAllDevices, (devices: DeviceWrapper[]) => devices.find(device => device.properties['service.id'] === deviceId))
;
export const getDevicesLoaded = createSelector(selectDeviceState, (state: DevicesState) => state.loaded);

import {createAction, props} from '@ngrx/store';
import {DeviceWrapper} from '../api';

export const loadDevicesList = createAction('[Device List] Load Devices List');
export const loadDevicesListSuccess = createAction('[Devices List] Load Devices List Success', props<{ loadedDevices: DeviceWrapper[] }>());
export const loadDevicesListForbidden = createAction('[Device List Forbidden] Load Devices List forbidden');
export const loadDevice = createAction('[Device] Load Device', props<{ devicePid: string }>());
export const loadDeviceSuccess = createAction('[Device] Load Device Success', props<{ loadedDevice: DeviceWrapper }>());
export const deviceRemoved = createAction('[Device] Device removed', props<{ devicePid: string }>());

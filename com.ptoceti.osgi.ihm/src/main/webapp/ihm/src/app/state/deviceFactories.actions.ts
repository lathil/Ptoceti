import {createAction, props} from '@ngrx/store';
import {DeviceFactoryInfoWrapper} from '../api';

export const loadDeviceFactoriesList = createAction('[Device Factories List] Load Devices Factories List');
export const loadDeviceFactoriesListSuccess = createAction('[Devices Factories List Success] Load Devices Factories List Success', props<{ loadedDeviceFactories: DeviceFactoryInfoWrapper[] }>());
export const loadDeviceFactoriesListForbidden = createAction('[Device Factories List Forbidden] Load Devices Factories List Forbidden');

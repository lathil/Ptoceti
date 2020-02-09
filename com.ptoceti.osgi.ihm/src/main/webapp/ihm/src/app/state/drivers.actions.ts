import {createAction, props} from '@ngrx/store';
import {DeviceWrapper, DriverWrapper} from '../api';

export const loadDriversList = createAction('[Drivers List] Load Drivers List');
export const loadDriversListSuccess = createAction('[Drivers List] Load Drivers List Success', props<{ loadedDrivers: DriverWrapper[] }>());
export const loadDriversListForbidden = createAction('[Drivers List Forbidden] Load Drivers List Forbidden');
export const loadDriver = createAction('[Driver] Load Driver', props<{ driverPid: string }>());
export const loadDriverSuccess = createAction('[Driver] Load Driver Success', props<{ loadedDriver: DriverWrapper }>());
export const driverRemoved = createAction('[Driver] Driver removed', props<{ driverPid: string }>());
export const driverAttached = createAction('[Driver] Driver attached', props<{ driverId: string, deviceSerial: string }>());


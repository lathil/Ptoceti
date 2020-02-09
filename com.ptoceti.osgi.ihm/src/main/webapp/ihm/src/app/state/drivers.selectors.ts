import {createSelector} from '@ngrx/store' ;
import {AppState} from './index';
import {DriversState} from './drivers.reducer';
import {DriverWrapper} from '../api';
import {selectAllDevices} from './devices.selectors';


export const selectDriverState = (state: AppState) => state.drivers;
export const selectAllDrivers = createSelector(selectDriverState, (state: DriversState) => state.driverInfos);
export const selectDriverById = (driverId: string) => createSelector(
  selectAllDrivers, (drivers: DriverWrapper[]) => drivers.find(driver => driver.properties['service.id'] === driverId))
;
export const getDriversLoaded = createSelector(selectDriverState, (state: DriversState) => state.loaded);

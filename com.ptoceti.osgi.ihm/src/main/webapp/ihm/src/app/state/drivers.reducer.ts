import {createReducer, on, Action, ActionReducer} from '@ngrx/store';
import {DriverWrapper} from '../api';
import {
  driverAttached,
  driverRemoved,
  loadDriversList, loadDriversListForbidden,
  loadDriversListSuccess,
  loadDriverSuccess
} from './drivers.actions';

export interface DriversState {
  driverInfos: DriverWrapper[];
  loaded: boolean;
}

export const initialState: DriversState = {
  driverInfos: [],
  loaded: false
};

export const driversReducer = createReducer(
  initialState,
  on(loadDriversList, state => ({...state})),
  on(loadDriversListSuccess, (state, {loadedDrivers}) => ({...state, driverInfos: loadedDrivers, loaded: true})),
  on(loadDriversListForbidden, (state) => ({
    ...state,
    driverInfos: state.driverInfos,
    loaded: true
  })),
  on(loadDriverSuccess, (state, {loadedDriver}) => ({
    ...state,
    driverInfos: state.driverInfos.concat(loadedDriver),
    loaded: true
  })),
  on(driverRemoved, (state, {driverPid}) => ({
    ...state,
    driverInfos: state.driverInfos.filter(value => value.properties['service.pid'] !== driverPid),
    loaded: true
  })),
  on(driverAttached, (state, {driverId, deviceSerial}) => ({
    ...state,
    driverInfos: state.driverInfos.map(driverWrapper => {
      if (driverWrapper.driverId === driverId) {
        const matchedDriverWrapper: DriverWrapper = {
          driverId: driverId,
          deviceSerial: deviceSerial,
          properties: driverWrapper.properties
        };
        return matchedDriverWrapper;
      }
      return driverWrapper;
    }),
    loaded: true
  }))
);

export function reducer(state: DriversState | undefined, action: Action) {
  return driversReducer(state, action);
};

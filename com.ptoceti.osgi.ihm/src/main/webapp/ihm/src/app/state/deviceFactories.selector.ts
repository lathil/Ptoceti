import {createSelector} from '@ngrx/store' ;
import {AppState} from './index';
import {DeviceFactoryInfoWrapper, DeviceFactoryInfoWrapperTypeEnum, DeviceWrapper} from '../api';
import {DeviceFactoriesState} from './deviceFactories.reducer';

export const selectDeviceFactoriesState = (state: AppState) => state.factories;
export const selectAllDeviceFactories = createSelector(selectDeviceFactoriesState, (state: DeviceFactoriesState) => state.factoriesInfo);
export const selectDeviceFactoriesByType = (factoryType: DeviceFactoryInfoWrapperTypeEnum) => createSelector(
  selectAllDeviceFactories, (factories: DeviceFactoryInfoWrapper[]) => factories.filter(factory => factory.type === factoryType));
export const selectDeviceFactorYByPid = (factoryPid: string) => createSelector(
  selectAllDeviceFactories, (factories: DeviceFactoryInfoWrapper[]) => factories.find(factory => factory.pid === factoryPid));
export const getDevicefactoriesLoaded = createSelector(selectDeviceFactoriesState, (state: DeviceFactoriesState) => state.loaded);

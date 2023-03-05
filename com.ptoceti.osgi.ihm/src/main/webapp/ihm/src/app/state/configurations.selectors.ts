import {createSelector} from '@ngrx/store' ;
import {AppState} from './index';
import {ConfigurationWrapper} from '../api';
import {ConfigurationsState} from './configurations.reducer';

export const selectConfigurationState = (state: AppState) => state.configurations;
export const selectAllConfigurations = createSelector(selectConfigurationState, (state: ConfigurationsState) => state.configurations);
export const selectConfigurationByPid = (configurationPid: string) => createSelector(
  selectAllConfigurations, (configurations: ConfigurationWrapper[]) => configurations.find(configuration => configuration.pid === configurationPid)
);
export const getConfigurationsLoaded = createSelector(selectConfigurationState, (state: ConfigurationsState) => state.loaded);

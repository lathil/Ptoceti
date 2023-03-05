import {createReducer, on, Action, ActionReducer} from '@ngrx/store';
import {ConfigurationWrapper} from '../api';
import {
  createConfigurationSuccess, deletedConfigurationSuccess, loadConfigurationListForbidden,
  loadConfigurationsList,
  loadConfigurationsListSuccess, updateConfigurationSuccess
} from './configurations.actions';

export interface ConfigurationsState {
  configurations: ConfigurationWrapper[];
  loaded: boolean;
}

export const initialState: ConfigurationsState = {
  configurations: [],
  loaded: false
};

export const configurationsReducer = createReducer(
  initialState,
  on(loadConfigurationsList, state => ({...state})),
  on(loadConfigurationsListSuccess, (state, {loadedConfigurations}) => ({
    ...state,
    configurations: loadedConfigurations,
    loaded: true
  })),
  on(loadConfigurationListForbidden, (state) => ({
    ...state,
    configurations: state.configurations,
    loaded: true
  })),
  on(createConfigurationSuccess, (state, {newConfiguration}) => ({
    ...state,
    configurations: state.configurations.concat(newConfiguration),
    loaded: true
  })),
  on(updateConfigurationSuccess, (state, {updatedConfiguration}) => {
    const index = state.configurations.findIndex(config => config.pid === updatedConfiguration.pid);
    if (index >= 0) {
      return {
        ...state,
        configurations: state.configurations.map(config => config.pid === updatedConfiguration.pid ? updatedConfiguration : config)
      };
    }
    ;
    return {...state, configurations: state.configurations.concat(updatedConfiguration), loaded: true};
  }),
  on(deletedConfigurationSuccess, (state, {configurationPid}) => ({
    ...state,
    configurations: state.configurations.filter(config => config.pid !== configurationPid),
    loaded: true
  }))
);

export function reducer(state: ConfigurationsState | undefined, action: Action) {
  return configurationsReducer(state, action);
};

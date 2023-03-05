import {createAction, props} from '@ngrx/store';
import {ConfigurationWrapper} from '../api';

export const loadConfigurationsList = createAction('[LOAD_CONFIGURATION_LIST]');
export const loadConfigurationsListSuccess = createAction('[LOAD_CONFIGURATION_LIST_SUCCESS]', props<{ loadedConfigurations: ConfigurationWrapper[] }>());
export const loadConfigurationListForbidden = createAction('[LOAD_CONFIGURATION_LIST_FORBIDDEN]');
export const createConfiguration = createAction('[CREATE_CONFIGURATION]', props<{ newConfiguration: ConfigurationWrapper }>());
export const createConfigurationSuccess = createAction('[CREATE_CONFIGURATION_SUCCESS]', props<{ newConfiguration: ConfigurationWrapper }>());
export const updateConfiguration = createAction('[UPDATE_CONFIGURATION]', props<{ configurationPid: string }>());
export const updateConfigurationSuccess = createAction('[UPDATE_CONFIGURATION_SUCCESS]', props<{ updatedConfiguration: ConfigurationWrapper }>());
export const deletedConfigurationSuccess = createAction('[DELETE_CONFIGURATION_SUCCESS]', props<{ configurationPid: string }>());

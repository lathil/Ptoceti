import {AppState} from './index';
import {createSelector} from '@ngrx/store';
import {PreferencesState} from './preferences.reducer';
import {PreferencesWrapper} from '../auth';


export const selectPreferencesState = (state: AppState) => state.preferences;
export const selectAllPreferences = createSelector(selectPreferencesState, (state: PreferencesState) => state.preferences);
export const selectConfigurationByPid = (preferenceName: string) => createSelector(
  selectAllPreferences, (preferences: PreferencesWrapper[]) => preferences.find(preference => preference.name === preferenceName)
);

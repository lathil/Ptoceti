import {createReducer, on, Action, ActionReducer} from '@ngrx/store';
import {PreferencesWrapper} from '../auth';

import {lloadPreferencesListForbidden, loadPreferencesList, loadPreferencesListSuccess} from './preferences.actions';

export interface PreferencesState {
  preferences: PreferencesWrapper[];
  loaded: boolean;
}

export const initialState: PreferencesState = {
  preferences: [],
  loaded: false
};

export const preferencesReducer = createReducer(
  initialState,
  on(loadPreferencesList, state => ({...state})),
  on(loadPreferencesListSuccess, (state, {loadedPreferences}) => ({
    ...state,
    preferences: loadedPreferences,
    loaded: true
  })),
  on(lloadPreferencesListForbidden, (state) => ({
    ...state,
    preferences: state.preferences,
    loaded: true
  })),
);

export function reducer(state: PreferencesState | undefined, action: Action) {
  return preferencesReducer(state, action);
};

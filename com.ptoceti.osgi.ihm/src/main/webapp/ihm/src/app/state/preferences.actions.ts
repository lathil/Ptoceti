import {createAction, props} from '@ngrx/store';
import {PreferencesWrapper} from '../auth';

export const loadPreferencesList = createAction('[LOAD_PREFERENCES_LIST]');
export const loadPreferencesListSuccess = createAction('[LOAD_PREFERENCES_LIST_SUCCESS]', props<{ loadedPreferences: PreferencesWrapper[] }>());
export const lloadPreferencesListForbidden = createAction('[LOAD_PREFERENCES_LIST_FORBIDDEN]');

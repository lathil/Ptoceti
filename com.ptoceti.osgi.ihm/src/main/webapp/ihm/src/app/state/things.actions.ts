import {createAction, props} from '@ngrx/store';
import {ThingWrapper} from '../api';

export const loadThingsList = createAction('[Things List] Load Things List');
export const loadThingsListSuccess = createAction('[Things List] Load Things List Success', props<{ loadedThings: ThingWrapper[] }>());
export const loadThingsForbidden = createAction('[Things List Forbidden] Load Things List Forbidden');
export const loadThing = createAction('[Thing] Load Thing', props<{ thingPid: string }>());
export const loadThingSuccess = createAction('[Thing] Load Thing Success', props<{ loadedThing: ThingWrapper }>());
export const thingRemoved = createAction('[Thing] Thing removed', props<{ thingPid: string }>());
export const thingUpdated = createAction('[Thing] Thing updated', props<{ thingPid: string }>());
export const updateThing = createAction('[Thing] update Thing', props<{ updatedThing: ThingWrapper }>());

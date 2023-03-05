import {createSelector} from '@ngrx/store' ;
import {AppState} from './index';
import {ThingsState} from './things.reducer';
import {ThingWrapper} from '../api';


export const selectThingState = (state: AppState) => state.things;
export const selectAllThings = createSelector(selectThingState, (state: ThingsState) => state.thingInfos);
export const selectThingById = (deviceId: string) => createSelector(
  selectAllThings, (items: ThingWrapper[]) => items.find(item => item.properties['service.id'] === deviceId))
;
export const getThingsLoaded = createSelector(selectThingState, (state: ThingsState) => state.loaded);

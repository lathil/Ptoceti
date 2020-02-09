import {createReducer, on, Action, ActionReducer} from '@ngrx/store';
import {ThingWrapper} from '../api';
import {
  loadThingsForbidden,
  loadThingsList,
  loadThingsListSuccess,
  loadThingSuccess,
  updateThing
} from './things.actions';

export interface ThingsState {
  thingInfos: ThingWrapper[];
  loaded: boolean;
}

export const initialState: ThingsState = {
  thingInfos: [],
  loaded: false
};

export const thingsReducer = createReducer(
  initialState,
  on(loadThingsList, state => ({...state})),
  on(loadThingsListSuccess, (state, {loadedThings}) => ({...state, thingInfos: loadedThings, loaded: true})),
  on(loadThingsForbidden, (state) => ({
    ...state,
    thingInfos: state.thingInfos,
    loaded: true
  })),
  on(loadThingSuccess, (state, {loadedThing}) => ({
    ...state,
    thingInfos: state.thingInfos.concat(loadedThing),
    loaded: true
  })),
  on(updateThing, (state, {updatedThing}) => ({
    ...state,
    thingInfos: state.thingInfos.map(thingWrapper => {
      if (thingWrapper.properties['service.pid'] === updatedThing.properties['service.pid']) {
        return updatedThing;
      }
      return thingWrapper;
    })
  }))
);

export function reducer(state: ThingsState | undefined, action: Action) {
  return thingsReducer(state, action);
};

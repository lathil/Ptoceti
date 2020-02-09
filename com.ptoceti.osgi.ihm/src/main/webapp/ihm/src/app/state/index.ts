import {
  Action,
  ActionReducer,
  ActionReducerMap, createAction,
  createFeatureSelector,
  createSelector,
  MetaReducer, props, State
} from '@ngrx/store';
import {environment} from '../../environments/environment';
import * as wiresReducers from './wires.reducer';
import * as thingsReducers from './things.reducer';
import * as itemsReducers from './items.reducer';
import * as driversReducers from './drivers.reducer';
import * as deviceReducers from './devices.reducer';
import * as metatypesReducers from './metatypes.reducer';
import * as configurationsReducers from './configurations.reducer';
import * as factoriesReducers from './deviceFactories.reducer';
import * as functiondataReducers from './functiondata.reducer';
import {WiresEffects} from './wires.effects';
import {WiresState} from './wires.reducer';
import {ThingsState} from './things.reducer';
import {ThingsEffects} from './things.effects';
import {DevicesState} from './devices.reducer';
import {DevicesEffects} from './devices.effects';
import {DriversState} from './drivers.reducer';
import {DriversEffects} from './drivers.effects';
import {MetatypesState} from './metatypes.reducer';
import {MetatypesEffects} from './metatypes.effects';
import {ConfigurationsState} from './configurations.reducer';
import {ConfigurationsEffects} from './configurations.effects';
import {DeviceFactoriesState} from './deviceFactories.reducer';
import {DeviceFactoriesEffects} from './deviceFactories.effects';
import {ItemsState} from './items.reducer';
import {ItemsEffects} from './items.effects';
import {FunctionDatasState} from './functiondata.reducer';
import {FunctionDataEffects} from "./functiondata.effects";


export interface AppState {
  wires: WiresState;
  things: ThingsState;
  items: ItemsState;
  drivers: DriversState;
  devices: DevicesState;
  metatypes: MetatypesState;
  configurations: ConfigurationsState;
  factories: DeviceFactoriesState;
  functiondatas: FunctionDatasState;
}


export const reducers: ActionReducerMap<AppState> = {
  wires: wiresReducers.wiresReducer,
  things: thingsReducers.thingsReducer,
  items: itemsReducers.itemsReducer,
  drivers: driversReducers.driversReducer,
  devices: deviceReducers.devicesReducer,
  metatypes: metatypesReducers.metatypesReducer,
  configurations: configurationsReducers.configurationsReducer,
  factories: factoriesReducers.devicesFactoriesReducer,
  functiondatas: functiondataReducers.functionDataReducers
};

// console.log all actions
export function logger(reducer: ActionReducer<any>): ActionReducer<any> {
  return function (state, action) {
    console.log('state', state);
    console.log('action', action);

    return reducer(state, action);
  };
}

export function logger2(reducer: ActionReducer<any>): ActionReducer<any> {
  return (state, action) => {
    const result = reducer(state, action);
    console.groupCollapsed(action.type);
    console.log('prev state', state);
    console.log('action', action);
    console.log('next state', result);
    console.groupEnd();

    return result;
  };
}

export const genericErrorAction = createAction('[GENERIC_ERROR_ACTION]', props<{ any }>());
export const genericForbiddenErrorAction = createAction('[FORBIDDEN_ERROR_ACTION]', props<{ any }>());

export const effects: any[] = [WiresEffects, ThingsEffects, ItemsEffects, DriversEffects, DevicesEffects, MetatypesEffects, ConfigurationsEffects, DeviceFactoriesEffects, FunctionDataEffects];

export const metaReducers: MetaReducer<AppState>[] = !environment.production ? [logger2] : [];

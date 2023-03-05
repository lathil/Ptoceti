import {createSelector} from '@ngrx/store' ;
import {AppState} from './index';
import {MetatypesState} from './metatypes.reducer';
import {MetatypeWrapper} from '../api';


export const selectMetatypeState = (state: AppState) => state.metatypes;
export const selectAllMetatypes = createSelector(selectMetatypeState, (state: MetatypesState) => state.metatypes);
export const getMetatypesLoaded = createSelector(selectMetatypeState, (state: MetatypesState) => state.loaded);
export const selectMetatypesByPid = (servicePid: string, factoryPid: string) => createSelector(
  selectAllMetatypes, (metatypes: MetatypeWrapper[]) => metatypes.find(metatype => {
      let found = false;
      if (metatype.pid) {
        found = (metatype.pid === servicePid);
      } else {
        found = (metatype.factoryPid === factoryPid);
      }
      return found;
    }
  ));

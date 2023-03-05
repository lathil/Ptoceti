import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot, Resolve} from '@angular/router';
import {Observable} from 'rxjs';
import {select, Store} from '@ngrx/store';
import {getMetatypesLoaded} from '../../state/metatypes.selectors';
import {filter, first, tap} from 'rxjs/operators';
import {loadMetatypesList} from '../../state/metatypes.actions';

@Injectable({
  providedIn: 'root'
})
@Injectable()
export class MetatypeResolver implements Resolve<boolean> {

  constructor(private store: Store) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.store.pipe(
      select(getMetatypesLoaded),
      tap(loaded => {
        if (!loaded) {
          this.store.dispatch(loadMetatypesList());
        }
      }),
      filter(loaded => loaded),
      first(),
    );
  }

}

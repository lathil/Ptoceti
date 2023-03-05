import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot, Resolve} from '@angular/router';
import {Observable} from 'rxjs';
import {select, Store} from '@ngrx/store';
import {filter, first, tap} from 'rxjs/operators';
import {getThingsLoaded} from '../../state/things.selectors';
import {loadThingsList} from '../../state/things.actions';

@Injectable({
  providedIn: 'root'
})
@Injectable()
export class ThingsResolver implements Resolve<boolean> {

  constructor(private store: Store) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.store.pipe(
      select(getThingsLoaded),
      tap(loaded => {
        if (!loaded) {
          this.store.dispatch(loadThingsList());
        }
      }),
      filter(loaded => loaded),
      first(),
    );
  }

}

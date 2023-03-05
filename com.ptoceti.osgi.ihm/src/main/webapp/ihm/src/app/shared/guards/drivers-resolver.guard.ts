import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot, Resolve} from '@angular/router';
import {Observable} from 'rxjs';
import {select, Store} from '@ngrx/store';
import {filter, first, tap} from 'rxjs/operators';
import {getDriversLoaded} from '../../state/drivers.selectors';
import {loadDriversList} from '../../state/drivers.actions';

@Injectable({
  providedIn: 'root'
})
@Injectable()
export class DriversResolver implements Resolve<boolean> {

  constructor(private store: Store) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.store.pipe(
      select(getDriversLoaded),
      tap(loaded => {
        if (!loaded) {
          this.store.dispatch(loadDriversList());
        }
      }),
      filter(loaded => loaded),
      first(),
    );
  }

}

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot, Resolve} from '@angular/router';
import {Observable} from 'rxjs';
import {select, Store} from '@ngrx/store';
import {filter, first, tap} from 'rxjs/operators';
import {loadDeviceFactoriesList} from '../../state/deviceFactories.actions';
import {getDevicefactoriesLoaded} from '../../state/deviceFactories.selector';


@Injectable({
  providedIn: 'root'
})
@Injectable()
export class FactoriesResolver implements Resolve<boolean> {

  constructor(private store: Store) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.store.pipe(
      select(getDevicefactoriesLoaded),
      tap(loaded => {
        if (!loaded) {
          this.store.dispatch(loadDeviceFactoriesList());
        }
      }),
      filter(loaded => loaded),
      first(),
    );
  }

}

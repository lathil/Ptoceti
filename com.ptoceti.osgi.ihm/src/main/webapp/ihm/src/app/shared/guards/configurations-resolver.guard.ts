import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot, Resolve} from '@angular/router';
import {Observable} from 'rxjs';
import {select, Store} from '@ngrx/store';
import {filter, first, tap} from 'rxjs/operators';
import {getConfigurationsLoaded} from '../../state/configurations.selectors';
import {loadConfigurationsList} from '../../state/configurations.actions';

@Injectable({
  providedIn: 'root'
})
@Injectable()
export class ConfigurationResolver implements Resolve<boolean> {

  constructor(private store: Store) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.store.pipe(
      select(getConfigurationsLoaded),
      tap(loaded => {
        if (!loaded) {
          this.store.dispatch(loadConfigurationsList());
        }
      }),
      filter(loaded => loaded),
      first(),
    );
  }

}

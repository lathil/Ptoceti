import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot, Resolve} from '@angular/router';
import {Observable} from 'rxjs';
import {select, Store} from '@ngrx/store';
import {filter, first, tap} from 'rxjs/operators';
import {getDevicesLoaded} from '../../state/devices.selectors';
import {loadDevicesList} from '../../state/devices.actions';

@Injectable({
  providedIn: 'root'
})
@Injectable()
export class DevicesResolver implements Resolve<boolean> {

  constructor(private store: Store) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.store.pipe(
      select(getDevicesLoaded),
      tap(loaded => {
        if (!loaded) {
          this.store.dispatch(loadDevicesList());
        }
      }),
      filter(loaded => loaded),
      first(),
    );
  }

}

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot, Resolve} from '@angular/router';
import {Observable} from 'rxjs';
import {select, Store} from '@ngrx/store';
import {filter, first, tap} from 'rxjs/operators';
import {getWiresLoaded} from '../../state/wires.selectors';
import {loadWiresList} from '../../state/wires.actions';

@Injectable({
  providedIn: 'root'
})
@Injectable()
export class WiresResolver implements Resolve<boolean> {

  constructor(private store: Store) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.store.pipe(
      select(getWiresLoaded),
      tap(loaded => {
        if (!loaded) {
          this.store.dispatch(loadWiresList());
        }
      }),
      filter(loaded => loaded),
      first(),
    );
  }

}

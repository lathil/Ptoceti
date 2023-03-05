import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot, Resolve} from '@angular/router';
import {Observable} from 'rxjs';
import {select, Store} from '@ngrx/store';
import {filter, first, tap} from 'rxjs/operators';
import {loadItemsList} from '../../state/items.actions';
import {getItemsLoaded} from '../../state/items.selectors';

@Injectable({
  providedIn: 'root'
})
@Injectable()
export class ItemsResolver implements Resolve<boolean> {

  constructor(private store: Store) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.store.pipe(
      select(getItemsLoaded),
      tap(loaded => {
        if (!loaded) {
          this.store.dispatch(loadItemsList());
        }
      }),
      filter(loaded => loaded),
      first(),
    );
  }

}

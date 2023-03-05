import {ChangeDetectionStrategy, Component, OnInit, ViewEncapsulation} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {Observable} from 'rxjs';
import {
  ConfigurationService,
  ConfigurationWrapper,
  DeviceFactoryInfoWrapper,
  DeviceFactoryInfoWrapperTypeEnum,
  ThingWrapper
} from '../../api';
import {selectAllThings} from '../../state/things.selectors';
import {selectAllConfigurations} from '../../state/configurations.selectors';
import {first, map} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {selectDeviceFactoriesByType} from '../../state/deviceFactories.selector';
import {AuthenticationService} from '../../services/authentication.service';

@Component({
  selector: 'app-things',
  templateUrl: './things.component.html',
  styleUrls: ['./things.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ThingsComponent implements OnInit {

  store: Store;
  router: Router;
  route: ActivatedRoute;
  configurationsService: ConfigurationService;
  things$: Observable<ThingWrapper[]>;
  configurations$: Observable<ConfigurationWrapper[]>;
  thingsFactories$: Observable<DeviceFactoryInfoWrapper[]>;
  itemsFactories$: Observable<DeviceFactoryInfoWrapper[]>;

  constructor(store: Store, router: Router, route: ActivatedRoute, configurationsService: ConfigurationService, private authenticationService: AuthenticationService) {
    this.store = store;
    this.router = router;
    this.route = route;
    this.configurationsService = configurationsService;
    this.things$ = this.store.pipe(select(selectAllThings));
    this.configurations$ = this.store.pipe(select(selectAllConfigurations));
    this.thingsFactories$ = this.store.pipe(select(selectDeviceFactoriesByType(DeviceFactoryInfoWrapperTypeEnum.Dal)));
    this.itemsFactories$ = this.store.pipe(select(selectDeviceFactoriesByType(DeviceFactoryInfoWrapperTypeEnum.Function)));
  }

  ngOnInit(): void {
  }

  hasConfiguration(servicePid: string): Observable<boolean> {

    return this.configurations$.pipe(first(),
      map((configurations: ConfigurationWrapper[]) => {
        const configuration = configurations.find(entry => entry.pid === servicePid);
        if (configuration) {
          return true;
        }
        return false;
      })
    );
  }


  addThing(): void {
    this.router.navigate(['../factory'], {
      relativeTo: this.route,
      queryParams: {type: DeviceFactoryInfoWrapperTypeEnum.Dal}
    });
  }

  addItem(): void {
    this.router.navigate(['../factory'], {
      relativeTo: this.route,
      queryParams: {type: DeviceFactoryInfoWrapperTypeEnum.Function}
    });
  }

  deleteThing(thing: ThingWrapper): void {
    this.configurationsService.deleteConfiguration(thing.properties['service.pid']).subscribe((value: any) => console.log('deleteConfiguration called'));
  }

  hasThingsFactories(): Observable<boolean> {
    return this.thingsFactories$.pipe(first(),
      map((factories: DeviceFactoryInfoWrapper[]) => {
        return factories.length > 0;
      }));
  }

  hasItemsFactories(): Observable<boolean> {
    return this.itemsFactories$.pipe(first(),
      map((factories: DeviceFactoryInfoWrapper[]) => {
        return factories.length > 0;
      }));
  }

  hasRole(role: string): boolean {
    return this.authenticationService.hasRole(role);
  }
}

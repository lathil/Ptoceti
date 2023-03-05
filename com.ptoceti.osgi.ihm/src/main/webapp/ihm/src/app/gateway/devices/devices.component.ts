import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {Observable} from 'rxjs';
import {
  ConfigurationService,
  ConfigurationWrapper,
  DeviceFactoryInfoWrapper, DeviceFactoryInfoWrapperTypeEnum,
  DeviceWrapper,
  DriverWrapper
} from '../../api';
import {selectAllDrivers} from '../../state/drivers.selectors';
import {selectAllDevices} from '../../state/devices.selectors';
import {selectAllConfigurations} from '../../state/configurations.selectors';
import {last, first, map, tap} from 'rxjs/operators';
import {selectAllDeviceFactories, selectDeviceFactoriesByType} from '../../state/deviceFactories.selector';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthenticationService} from '../../services/authentication.service';

@Component({
  selector: 'app-drivers',
  templateUrl: './devices.component.html',
  styleUrls: ['./devices.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DevicesComponent implements OnInit {

  store: Store;
  router: Router;
  route: ActivatedRoute;
  drivers$: Observable<DriverWrapper[]>;
  devices$: Observable<DeviceWrapper[]>;
  configurations$: Observable<ConfigurationWrapper[]>;

  deviceFactories$: Observable<DeviceFactoryInfoWrapper[]>;
  driverFactories$: Observable<DeviceFactoryInfoWrapper[]>;

  configurationsService: ConfigurationService;

  constructor(store: Store, router: Router, route: ActivatedRoute, configurationsService: ConfigurationService, private authenticationService: AuthenticationService) {
    this.store = store;
    this.router = router;
    this.route = route;
    this.configurationsService = configurationsService;
    this.drivers$ = this.store.pipe(select(selectAllDrivers)).pipe(tap(value => console.log('DevicesComponent drivers observable : next , nb:' + value.length)));
    this.devices$ = this.store.pipe(select(selectAllDevices)).pipe(tap(value => console.log('DevicesComponent devices observable : next, nb: ' + value.length)));
    this.configurations$ = this.store.pipe(select(selectAllConfigurations));
    this.deviceFactories$ = this.store.pipe(select(selectDeviceFactoriesByType(DeviceFactoryInfoWrapperTypeEnum.Device)));
    this.driverFactories$ = this.store.pipe(select(selectDeviceFactoriesByType(DeviceFactoryInfoWrapperTypeEnum.Driver)));
  }

  ngOnInit(): void {
  }

  hasDriverFactories(): Observable<boolean> {
    return this.driverFactories$.pipe(first(),
      map((factories: DeviceFactoryInfoWrapper[]) => {
        return factories.length > 0;
      }));
  }

  hasDeviceFactories(): Observable<boolean> {
    return this.deviceFactories$.pipe(first(),
      map((factories: DeviceFactoryInfoWrapper[]) => {
        return factories.length > 0;
      }));
  }

  addDevice(): void {
    this.router.navigate(['../factory'], {
      relativeTo: this.route,
      queryParams: {type: DeviceFactoryInfoWrapperTypeEnum.Device}
    });
  }

  addDriver(): void {
    this.router.navigate(['../factory'], {
      relativeTo: this.route,
      queryParams: {type: DeviceFactoryInfoWrapperTypeEnum.Driver}
    });
  }

  deleteDevice(servicePid: string): void {
    this.configurationsService.deleteConfiguration(servicePid).subscribe((value: any) => console.log('deleteConfiguration called'));
  }

  deleteDriver(servicePid: string): void {
    this.configurationsService.deleteConfiguration(servicePid).subscribe((value: any) => console.log('deleteConfiguration called'));
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

  hasRole(role: string): boolean {
    return this.authenticationService.hasRole(role);
  }
}

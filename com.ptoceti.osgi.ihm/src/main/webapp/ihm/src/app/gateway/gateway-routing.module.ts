import {Injectable, NgModule} from '@angular/core';
import {Routes, RouterModule, Resolve, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';

import {GatewayComponent} from './gateway.component';

import {DeviceComponent} from './device/device.component';
import {DevicesComponent} from './devices/devices.component';
import {ConfigurationComponent} from './configuration/configuration.component';
import {MetatypeResolver} from '../shared/guards/metatype-resolver.guard';
import {DevicesResolver} from '../shared/guards/devices-resolver.guard';
import {DriversResolver} from '../shared/guards/drivers-resolver.guard';
import {ThingsResolver} from '../shared/guards/things-resolver.guard';
import {ConfigurationResolver} from '../shared/guards/configurations-resolver.guard';
import {DriverComponent} from './driver/driver.component';
import {ThingsComponent} from './things/things.component';
import {ThingComponent} from './thing/thing.component';
import {FactoriesResolver} from '../shared/guards/factories-resolver.guards';
import {FactoryComponent} from './factory/factory.component';
import {ItemsResolver} from '../shared/guards/items-resolver.guard';
import {AuthGuard} from '../shared/guards/auth.guard';


const routes: Routes = [
  {
    path: '', component: GatewayComponent,
    resolve: {
      metaLoaded: MetatypeResolver
    },
    children: [
      {
        path: 'things', component: ThingsComponent,
        data: {
          breadcrumbItem: {key: 'things', labelName: 'Things'},
          roles: ['things']
        },
        resolve: {
          thingsLoaded: ThingsResolver,
          itemsLoaded: ItemsResolver,
          factoryLoaded: FactoriesResolver,
          configurationLoaded: ConfigurationResolver
        },
        canActivate: [AuthGuard]
      },
      {
        path: 'thing/:id', component: ThingComponent,
        data: {
          breadcrumbItem: {key: 'thing', labelName: 'Thing'},
          roles: ['things']
        },
        canActivate: [AuthGuard]
      },
      {
        path: 'devices', component: DevicesComponent,
        data: {
          breadcrumbItem: {key: 'devices', labelName: 'Devices'},
          roles: ['devices']
        },
        resolve: {
          devicesLoaded: DevicesResolver,
          driversLoaded: DriversResolver,
          factoryLoaded: FactoriesResolver,
          configurationLoaded: ConfigurationResolver
        },
        canActivate: [AuthGuard]
      },
      {
        path: 'device/:id', component: DeviceComponent,
        data: {
          breadcrumbItem: {key: 'device', labelName: 'Device'},
          roles: ['devices']
        },
        canActivate: [AuthGuard]
      },
      {
        path: 'driver/:id', component: DriverComponent,
        data: {
          breadcrumbItem: {key: 'driver', labelName: 'Driver'},
          roles: ['devices']
        },
        canActivate: [AuthGuard]
      },
      {
        path: 'conf/:pid', component: ConfigurationComponent,
        data: {
          breadcrumbItem: {key: 'conf', labelName: 'Configuration'}
        },
        canActivate: [AuthGuard]
      },
      {
        path: 'factory', component: FactoryComponent,
        data: {
          breadcrumbItem: {key: 'factory', labelName: 'Factory'}
        },
        canActivate: [AuthGuard]
      },
      {
        path: '',
        redirectTo: 'things',
        pathMatch: 'full',
      }
    ]
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GatewayRoutingModule {
}



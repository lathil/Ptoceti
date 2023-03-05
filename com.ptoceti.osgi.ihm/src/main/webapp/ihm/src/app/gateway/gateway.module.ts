import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {GatewayComponent} from './gateway.component';
import {GatewayRoutingModule} from './gateway-routing.module';
import {DevicesComponent} from './devices/devices.component';
import {SharedModule} from '../shared/shared.module';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NgbDropdownModule} from '@ng-bootstrap/ng-bootstrap';
import {DeviceComponent} from './device/device.component';
import {ConfigurationComponent} from './configuration/configuration.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {DriverComponent} from './driver/driver.component';
import {ThingsComponent} from './things/things.component';
import {ThingComponent} from './thing/thing.component';
import {FactoryComponent} from './factory/factory.component';
import {ThingItemsComponent} from './thing-items/thing-items.component';

@NgModule({
  declarations: [GatewayComponent, ThingsComponent, DevicesComponent, ThingComponent, DeviceComponent, ConfigurationComponent, DriverComponent, FactoryComponent, ThingItemsComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    GatewayRoutingModule,
    NgbModule,
    NgbDropdownModule,
    FormsModule,
  ]
})
export class GatewayModule {
}

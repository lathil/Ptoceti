import {NgModule, ModuleWithProviders, SkipSelf, Optional} from '@angular/core';
import {ApiConfiguration} from './configuration';
import {HttpClient} from '@angular/common/http';

import {ConfigurationService} from './api/configuration.service';
import {DevicesService} from './api/devices.service';
import {DriverService} from './api/driver.service';
import {EventsService} from './api/events.service';
import {FactoriesService} from './api/factories.service';
import {ItemsService} from './api/items.service';
import {LobbyService} from './api/lobby.service';
import {MetatypeService} from './api/metatype.service';
import {MqttService} from './api/mqtt.service';
import {ThingsService} from './api/things.service';
import {WiresService} from './api/wires.service';

@NgModule({
  imports: [],
  declarations: [],
  exports: [],
  providers: []
})
export class ApiApiModule {
  public static forRoot(configurationFactory: () => ApiConfiguration): ModuleWithProviders<ApiApiModule> {
    return {
      ngModule: ApiApiModule,
      providers: [{provide: ApiConfiguration, useFactory: configurationFactory}]
    };
  }

  constructor(@Optional() @SkipSelf() parentModule: ApiApiModule,
              @Optional() http: HttpClient) {
    if (parentModule) {
      throw new Error('ApiApiModule is already loaded. Import in your base AppModule only.');
    }
    if (!http) {
      throw new Error('You need to import the HttpClientModule in your AppModule! \n' +
        'See also https://github.com/angular/angular/issues/20575');
    }
  }
}

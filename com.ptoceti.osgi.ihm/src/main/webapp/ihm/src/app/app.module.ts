import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, NgModule} from '@angular/core';

import {HttpClientModule} from '@angular/common/http';
import {APP_BASE_HREF} from '@angular/common';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';


import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';


import {ApiModule} from './api/api.module';
import {Configuration, ConfigurationParameters} from './api/configuration';
import {LayoutModule} from './layout/layout.module';
import {AppSettings} from './app.settings';
import {AppConfig} from './app.config';


const appInitializerFn = (appConfig: AppConfig) => {
  return () => appConfig.load();
};

const apiConfigFn = (appSetting: AppSettings): Configuration => {

  const params: ConfigurationParameters = {
    // set configuration parameters here.
    basePath: appSetting.restUrl
  };

  return new Configuration(params);
};

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    NgbModule,
    AppRoutingModule,
    LayoutModule,
    ApiModule,
    HttpClientModule
  ],
  providers: [AppSettings,
    {provide: APP_BASE_HREF, useValue: window['base-href']},
    AppConfig,
    {provide: APP_INITIALIZER, useFactory: appInitializerFn, deps: [AppConfig], multi: true},
    {provide: Configuration, useFactory: apiConfigFn, deps: [AppSettings], multi: false}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}

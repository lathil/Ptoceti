import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, Inject, Injectable, InjectionToken, NgModule} from '@angular/core';

import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {APP_BASE_HREF, Location} from '@angular/common';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import {environment} from './../environments/environment';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';


import {ApiApiModule} from './api/api.module';
import {ApiConfiguration, ApiConfigurationParameters} from './api/configuration';
import {LayoutModule} from './layout/layout.module';
import {StoreModule} from '@ngrx/store';
import {reducers, metaReducers, effects} from './state';
import {EffectsModule} from '@ngrx/effects';
import {ServerConfig} from './server.config';
import {SseConfiguration, SseParameters, SseService} from './services/sse.service';
import {AuthApiModule, AuthConfiguration, AuthConfigurationParameters} from './auth';
import {JwtInterceptor} from './shared/jwt.interceptor';
import {AuthenticationErrorInterceptor} from './shared/authentication-error.interceptor';
import {JwtLocalStorage, JwtSessionStorage, JwtStorage} from './services/jwt-storage.service';


const apiConfigFn = (serverConfig: ServerConfig): ApiConfiguration => {

  let restUrl = serverConfig.restUrl;
  if (restUrl.startsWith('/')) {
    restUrl = location.origin + restUrl;
  }

  const params: ApiConfigurationParameters = {
    // set configuration parameters here.
    basePath: restUrl
  };

  return new ApiConfiguration(params);
};

const authConfigFn = (serverConfig: ServerConfig): AuthConfiguration => {

  let authUrl = serverConfig.authUrl;
  if (authUrl.startsWith('/')) {
    authUrl = location.origin + authUrl;
  }

  const params: AuthConfigurationParameters = {
    // set configuration parameters here.
    basePath: authUrl
  };

  return new AuthConfiguration(params);
};
const sseConfigFn = (serverConfig: ServerConfig): SseConfiguration => {

  let restUrl = serverConfig.restUrl;
  if (restUrl.startsWith('/')) {
    restUrl = location.origin + restUrl;
  }

  const params: SseParameters = {
    // set configuration parameters here.
    basePath: restUrl
  };

  return new SseConfiguration(params);
};

export interface Environment {
  production: boolean;
  configEndpoint: string;
}

export const ENVIRONMENT = new InjectionToken<Environment>('environment');

@Injectable({
  providedIn: 'root',
})
export class EnvironmentService {
  private readonly environment: Environment;

  // We need @Optional to be able start app without providing environment file
  constructor(@Inject(ENVIRONMENT) environment: Environment) {
    this.environment = environment;

    // deactivate console.log in production mode
    if (this.environment.production === true) {
      console.log = () => {
      };
    }
  }

  getValue(key: string, defaultValue?: any): any {
    return this.environment[key] !== undefined ? this.environment[key] : defaultValue;
  }
}

const jwtStorageFn = (environementService: EnvironmentService): JwtStorage => {
  if (environementService.getValue('production') === true) {
    return new JwtSessionStorage();
  }
  return new JwtLocalStorage();
};

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    NgbModule,
    AppRoutingModule,
    LayoutModule,
    ApiApiModule,
    AuthApiModule,
    HttpClientModule,
    StoreModule.forRoot(reducers, {
      metaReducers
    }),
    EffectsModule.forRoot(effects)
  ],
  providers: [
    {provide: APP_BASE_HREF, useValue: window['base-href']},
    {provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: AuthenticationErrorInterceptor, multi: true},
    {provide: ENVIRONMENT, useValue: environment},
    // {provide: APP_INITIALIZER, useFactory: appInitializerFn, deps: [AppConfig], multi: true},
    {provide: ApiConfiguration, useFactory: apiConfigFn, deps: [ServerConfig], multi: false},
    {provide: AuthConfiguration, useFactory: authConfigFn, deps: [ServerConfig], multi: false},
    {provide: SseConfiguration, useFactory: sseConfigFn, deps: [ServerConfig], multi: false},
    {provide: JwtStorage, useFactory: jwtStorageFn, deps: [EnvironmentService], multi: false},
    SseService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}

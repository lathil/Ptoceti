import {NgModule, ModuleWithProviders, SkipSelf, Optional} from '@angular/core';
import {AuthConfiguration} from './configuration';
import {HttpClient} from '@angular/common/http';

import {LoginService} from './api/login.service';
import {PrefsService} from './api/prefs.service';

@NgModule({
  imports: [],
  declarations: [],
  exports: [],
  providers: []
})
export class AuthApiModule {
  public static forRoot(configurationFactory: () => AuthConfiguration): ModuleWithProviders<AuthApiModule> {
    return {
      ngModule: AuthApiModule,
      providers: [{provide: AuthConfiguration, useFactory: configurationFactory}]
    };
  }

  constructor(@Optional() @SkipSelf() parentModule: AuthApiModule,
              @Optional() http: HttpClient) {
    if (parentModule) {
      throw new Error('AuthApiModule is already loaded. Import in your base AppModule only.');
    }
    if (!http) {
      throw new Error('You need to import the HttpClientModule in your AppModule! \n' +
        'See also https://github.com/angular/angular/issues/20575');
    }
  }
}

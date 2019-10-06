import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { LocationStrategy, HashLocationStrategy } from '@angular/common';
import { APP_INITIALIZER } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

import { AppComponent } from './app.component';
import { AppConfig }       from './app.config';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { NAV_DROPDOWN_DIRECTIVES } from './shared/nav-dropdown.directive';

import { SIDEBAR_TOGGLE_DIRECTIVES } from './shared/sidebar.directive';
import { AsideToggleDirective } from './shared/aside.directive';
import { BreadcrumbsComponent } from './shared/breadcrumb.component';



// Routing Module
import { AppRoutingModule } from './app-routing.module';

import { HttpClientModule } from '@angular/common/http';


//Oauth2
import { OAuthModule } from 'angular-oauth2-oidc';

import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { TokenInterceptor } from './auth/token.interceptor';

// Cookie
import { CookieModule} from './cookie/cookie.module';

// Feature Modules


// Layouts
import { FullLayoutComponent } from './layouts/full-layout.component';
import { SimpleLayoutComponent } from './layouts/simple-layout.component';


import { ObixModule } from './obix/obix.module';

@NgModule({
  imports: [
    BrowserModule,
    AppRoutingModule,
    OAuthModule.forRoot(),
    BsDropdownModule.forRoot(),
    TabsModule.forRoot(),
    BrowserAnimationsModule,
    CookieModule,
    ObixModule,
      HttpClientModule
  ],
  declarations: [
    AppComponent,
    FullLayoutComponent,
    SimpleLayoutComponent,
    NAV_DROPDOWN_DIRECTIVES,
    BreadcrumbsComponent,
    SIDEBAR_TOGGLE_DIRECTIVES,
    AsideToggleDirective,
  ],
  providers: [{ provide: LocationStrategy, useClass: HashLocationStrategy}, AppConfig,
              { provide: APP_INITIALIZER, useFactory: (config: AppConfig) => () => config.load(), deps: [AppConfig], multi: true },
              { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true }],
  bootstrap: [ AppComponent ]
})
export class AppModule { }

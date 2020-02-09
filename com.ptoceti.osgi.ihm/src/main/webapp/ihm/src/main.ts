import {enableProdMode} from '@angular/core';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';

import {AppModule} from './app/app.module';
import {environment} from './environments/environment';
import {ServerConfig} from './app/server.config';

if (environment.production) {
  enableProdMode();
}

// Load configuration that is external from build
fetch(environment.configEndpoint).then(async res => {
  const configuration = await res.json();

  platformBrowserDynamic([
    {provide: ServerConfig, useValue: configuration},
  ])
    .bootstrapModule(AppModule)
    .catch(err => console.error(err));
});



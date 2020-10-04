import {throwError as observableThrowError, Observable} from 'rxjs';

import {catchError, map} from 'rxjs/operators';
import {Inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {AppSettings} from './app.settings';

export interface Config {
  restUrl: string;
}

@Injectable()
export class AppConfig {

  constructor(private http: HttpClient, private appSettings: AppSettings) {
  }


  load(): Promise<void> {
    const configUrl = environment.configEndpoint;
    console.log('getting configuration values from ' + configUrl);

    return this.http.get<Config>(configUrl)
      .toPromise()
      .then((data: Config) => {
        this.appSettings.restUrl = data.restUrl;
        console.log('AppConfigService loaded() ' + JSON.stringify(data));
      });
  }
}

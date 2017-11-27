
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/first';




import { AsyncLocalStorage } from 'angular-async-local-storage';

import { Obj, About } from './obix';

@Injectable()
export class AboutService {

    http: HttpClient;
    serviceUrl : string;
    rootUrl : string;
    storage: AsyncLocalStorage;
    cache : ReplaySubject<About>;

    constructor( http: HttpClient, storage: AsyncLocalStorage) {
        this.http = http;
        this.storage = storage;
        // keep a cache of the lobby of only last value
        this.cache = new ReplaySubject<About>(1);
    }
    
    getAbout(): Observable<About>{
        return this.cache.asObservable();
    }
    
    initialize( rootUrl : string, serviceUrl : string) {
        
        this.rootUrl = rootUrl;
        this.serviceUrl = serviceUrl;
        
         // check first if we already have the lobby in local storage
        this.storage.getItem(serviceUrl)
            .map( response => {if(response != null ){ let about : About = new About;about.parse(response);return about;}})
            .subscribe((about) => {
            if (about != null) {
              this.cache.next(about);
              //observer.complete();
            } else{
                // not available, get it from backend server
                this.http.get( serviceUrl ).map( this.handleAboutResponse ).subscribe(about => {
                    // and save it in local storage
                    this.storage.setItem(serviceUrl, about).subscribe(() => { this.cache.next(about);}, () => {});
                });
            }
          }, (error) => {
              console.error( 'error reading from localstorage' + error );
          });
    }

    private handleAboutResponse(response: any ): About {
        let about : About = new About;
        about.parse(response);
        return about;
    }
}

import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';

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

    http: Http;
    serviceUrl : string;
    rootUrl : string;
    storage: AsyncLocalStorage;
    cache : ReplaySubject<About>;

    constructor( http: Http, storage: AsyncLocalStorage) {
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
                this.http.get( serviceUrl ).map( this.handleAboutResponse ).catch( this.handleError ).subscribe(about => {
                    // and save it in local storage
                    this.storage.setItem(serviceUrl, about).subscribe(() => { this.cache.next(about);}, () => {});
                });
            }
          }, (error) => {
              console.error( 'error reading from localstorage' + error );
          });
    }

    private handleAboutResponse(response: Response ): About {
        let about : About = new About;
        about.parse(response.json());
        return about;
    }

    private handleError( error: Response | any ) {
        // In a real world app, you might use a remote logging infrastructure
        let errMsg: string;
        if ( error instanceof Response ) {
            const body = error.json() || '';
            const err = body.error || JSON.stringify( body );
            errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
        } else {
            errMsg = error.message ? error.message : error.toString();
        }
        console.error( errMsg );
        return Observable.throw( errMsg );
    }
}
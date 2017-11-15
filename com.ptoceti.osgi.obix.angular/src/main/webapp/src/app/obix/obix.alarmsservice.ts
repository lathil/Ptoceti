
import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';

import { Observable } from 'rxjs/Observable';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/first';

import { AsyncLocalStorage } from 'angular-async-local-storage';

import { Obj,  AlarmService } from './obix';

@Injectable()
export class AlarmsService {
    http: Http;
    rootUrl : string;
    serviceUrl : string;
    storage: AsyncLocalStorage;
    cache : ReplaySubject<AlarmService>;
   
    constructor( http: Http, storage: AsyncLocalStorage ) {
        this.http = http;
        this.storage = storage;
        this.cache = new ReplaySubject<AlarmService>();
    }
    
    private handleAlarmServiceResponse(response: Response ): AlarmService {
        let result : AlarmService = new AlarmService;
        result.parse(response.json());
        return result;
    }
    
    getAlarmService(): Observable<AlarmService>{
        return this.cache.asObservable();
    }
    
    initialize( rootUrl : string, serviceUrl : string) {
       
       this.rootUrl = rootUrl;
        this.serviceUrl = serviceUrl;
            
        // check first if we already have the lobby in local storage
       this.storage.getItem(serviceUrl)
           .map( response => {if(response != null ){ let alarmservice : AlarmService = new AlarmService; alarmservice.parse(response);return alarmservice;}})
           .subscribe((alarmservice) => {
           if (alarmservice != null) {
             this.cache.next(alarmservice);
           } else{
               // not available, get it from backend server
               this.http.get( serviceUrl ).map( this.handleAlarmServiceResponse ).catch( this.handleError ).subscribe(alarmservice => {
                   // and save it in local storage
                   this.storage.setItem(serviceUrl, alarmservice).subscribe(() => { this.cache.next(alarmservice); }, () => {});
               });
           }
         }, () => {
             console.error( 'error reading from localstorage' );
         });
          
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
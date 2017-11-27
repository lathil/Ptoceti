
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

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
    http: HttpClient;
    rootUrl : string;
    serviceUrl : string;
    storage: AsyncLocalStorage;
    cache : ReplaySubject<AlarmService>;
   
    constructor( http: HttpClient, storage: AsyncLocalStorage ) {
        this.http = http;
        this.storage = storage;
        this.cache = new ReplaySubject<AlarmService>();
    }
    
    private handleAlarmServiceResponse(response: any ): AlarmService {
        let result : AlarmService = new AlarmService;
        result.parse(response);
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
               this.http.get( serviceUrl ).map( this.handleAlarmServiceResponse ).subscribe(alarmservice => {
                   // and save it in local storage
                   this.storage.setItem(serviceUrl, alarmservice).subscribe(() => { this.cache.next(alarmservice); }, () => {});
               });
           }
         }, () => {
             console.error( 'error reading from localstorage' );
         });
          
    }
    
}
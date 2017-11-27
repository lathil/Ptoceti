
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/first';

import { AsyncLocalStorage } from 'angular-async-local-storage';

import { Obj, HistoryService } from './obix';

@Injectable()
export class HistoriesService {
    http: HttpClient;
    serviceUrl : string;
    rootUrl : string;
    storage: AsyncLocalStorage;
    cache : ReplaySubject<HistoryService>;
 
    constructor( http: HttpClient, storage: AsyncLocalStorage ) {
        this.http = http;
        this.storage = storage;
        this.cache = new ReplaySubject<HistoryService>();
    }
    
    private handleHistoryServiceResponse(response: any ): HistoryService {
        let result : HistoryService = new HistoryService;
        result.parse(response);
        return result;
    }
    
    getHistoryService(): Observable<HistoryService>{
        return this.cache.asObservable();
    }
    
    initialize( rootUrl : string, serviceUrl : string) {
       
       this.rootUrl = rootUrl;
       this.serviceUrl = serviceUrl;
       
        // check first if we already have the lobby in local storage
       this.storage.getItem(serviceUrl)
           .map( response => {if(response != null ){ let historyservice : HistoryService = new HistoryService; historyservice.parse(response);return historyservice;}})
           .subscribe((historyservice) => {
           if (historyservice != null) {
             this.cache.next(historyservice);
           } else{
               // not available, get it from backend server
               this.http.get( serviceUrl ).map( this.handleHistoryServiceResponse ).subscribe(historyservice => {
                   // and save it in local storage
                   this.storage.setItem(serviceUrl, historyservice).subscribe(() => { this.cache.next(historyservice); }, () => {});
               });
           }
         }, () => {
             console.error( 'error reading from localstorage' );
         });
           
    }
}
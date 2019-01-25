import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/first';

import { AsyncLocalStorage } from 'angular-async-local-storage';

import { Obj, Lobby } from './obix';

@Injectable()
export class LobbyService {

    http: HttpClient;
    rootUrl : string;
    serviceUrl : string;
    storage: AsyncLocalStorage;
    cache : ReplaySubject<Lobby>;

    constructor( http: HttpClient, storage: AsyncLocalStorage) {
        this.http = http;
        this.storage = storage;
        // keep a cache of the lobby of only last value
        this.cache = new ReplaySubject<Lobby>(1);
    }
    
    getLobby(): Observable<Lobby>{
        return this.cache.asObservable();
    }
    
    initialize( rootUrl : string, serviceUrl : string) {
        
        this.rootUrl = rootUrl;
        this.serviceUrl = serviceUrl;
         // check first if we already have the lobby in local storage
        this.storage.getItem(serviceUrl)
            .map( response => {if(response != null ){ let lobby : Lobby = new Lobby;lobby.parse(response);return lobby;}})
            .subscribe((lobby) => {
            if (lobby != null) {
              this.cache.next(lobby);
              //observer.complete();
            } else{
                // not available, get it from backend server
                this.http.get( serviceUrl).map( this.handleLobbyResponse ).subscribe(lobby => {
                    // and save it in local storage
                    this.storage.setItem(serviceUrl, lobby).subscribe(() => { this.cache.next(lobby);}, () => {});
                });
            }
          }, () => {
              console.error( 'error reading from localstorage' );
          });
    }

    private handleLobbyResponse(response: any ): Lobby {
        let lobby : Lobby = new Lobby;
        lobby.parse(response);
        return lobby;
    }

}
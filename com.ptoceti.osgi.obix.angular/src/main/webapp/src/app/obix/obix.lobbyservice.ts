import {map} from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import {Observable, ReplaySubject, BehaviorSubject} from 'rxjs';


import {LocalStorage} from '@ngx-pwa/local-storage';

import { Obj, Lobby } from './obix';

@Injectable()
export class LobbyService {

    http: HttpClient;
    rootUrl : string;
    serviceUrl : string;
    storage: LocalStorage;
    cache : ReplaySubject<Lobby>;

    constructor(http: HttpClient, storage: LocalStorage) {
        this.http = http;
        this.storage = storage;
        // keep a cache of the lobby of only last value
        this.cache = new ReplaySubject<Lobby>(1);
    }

    getLobby(): Observable<Lobby>{
        return this.cache.asObservable();
    }

    initialize(rootUrl : string, serviceUrl : string) {

        this.rootUrl = rootUrl;
        this.serviceUrl = serviceUrl;
        // check first if we already have the lobby in local storage
        this.storage.getItem(serviceUrl)
            .subscribe((result) => {
                if (result) {
                    let lobby: Lobby = new Lobby;
                    lobby.parse(result);
                    this.cache.next(lobby);
                    //observer.complete();
                } else {
                    // not available, get it from backend server
                    this.http.get(serviceUrl).pipe(map(this.handleLobbyResponse)).subscribe(lobby => {
                        // and save it in local storage
                        this.storage.setItem(serviceUrl, lobby).subscribe(() => {
                            this.cache.next(lobby);
                        }, () => {
                        });
                    }, err => {
                        console.log("Error getting lobby from rest: " + err.message);
                    });
                }
            }, (error) => {
                console.error('error reading from localstorage' + error);
            });
    }

    private handleLobbyResponse(response: any ): Lobby {
        let lobby : Lobby = new Lobby;
        lobby.parse(response);
        return lobby;
    }

}
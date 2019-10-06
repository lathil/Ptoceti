import {map} from 'rxjs/operators';

import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import {Observable, ReplaySubject, BehaviorSubject} from 'rxjs';


import {LocalStorage} from '@ngx-pwa/local-storage';

import { Obj, About } from './obix';

@Injectable()
export class AboutService {

    http: HttpClient;
    serviceUrl : string;
    rootUrl : string;
    storage: LocalStorage;
    cache : ReplaySubject<About>;

    constructor(http: HttpClient, storage: LocalStorage) {
        this.http = http;
        this.storage = storage;
        // keep a cache of the lobby of only last value
        this.cache = new ReplaySubject<About>(1);
    }

    getAbout(): Observable<About>{
        return this.cache.asObservable();
    }

    initialize(rootUrl : string, serviceUrl : string) {

        this.rootUrl = rootUrl;
        this.serviceUrl = serviceUrl;

        // check first if we already have the lobby in local storage
        this.storage.getItem(serviceUrl).subscribe((response) => {
            if (response) {
                let about: About = new About;
                about.parse(response)
                this.cache.next(about);
                //observer.complete();
            } else{
                // not available, get it from backend server
                this.http.get(serviceUrl).pipe(map(this.handleAboutResponse)).subscribe(about => {
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
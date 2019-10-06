import {map} from 'rxjs/operators';

import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import {Observable, ReplaySubject, BehaviorSubject} from 'rxjs';


import { Obj, Ref,  SearchOut } from './obix';

@Injectable()
export class SearchService {

    http: HttpClient;
    rootUrl : string;
    serviceUrl : string;

    constructor(http: HttpClient ) {
        this.http = http;
    }

    initialize(rootUrl : string, serviceUrl : string) {
        this.rootUrl = rootUrl;
        this.serviceUrl = serviceUrl;
    }

    search(searchIn : Ref ) : Observable<SearchOut> {
        return this.http.post(this.serviceUrl, searchIn).pipe(map(this.handleSearchOutResponse));
    }

    private handleSearchOutResponse(response: any ): SearchOut {
        let searchOut : SearchOut = new SearchOut;
        searchOut.parse(response);
        return searchOut;
    }
}
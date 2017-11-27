
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/first';

import { Obj, Ref,  SearchOut } from './obix';

@Injectable()
export class SearchService {

    http: HttpClient;
    rootUrl : string;
    serviceUrl : string;

    constructor( http: HttpClient ) {
        this.http = http;
    }
    
    initialize( rootUrl : string, serviceUrl : string) {
        this.rootUrl = rootUrl;
        this.serviceUrl = serviceUrl;
    }
    
    search(searchIn : Ref ) : Observable<SearchOut> {
        return this.http.post( this.serviceUrl, searchIn ).map( this.handleSearchOutResponse );
    }

    private handleSearchOutResponse(response: any ): SearchOut {
        let searchOut : SearchOut = new SearchOut;
        searchOut.parse(response);
        return searchOut;
    }
}
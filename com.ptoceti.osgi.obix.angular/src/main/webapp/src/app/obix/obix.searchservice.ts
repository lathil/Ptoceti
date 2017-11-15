
import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';

import { Observable } from 'rxjs/Observable';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/first';

import { Obj, Ref,  SearchOut } from './obix';

@Injectable()
export class SearchService {

    http: Http;
    rootUrl : string;
    serviceUrl : string;

    constructor( http: Http ) {
        this.http = http;
    }
    
    initialize( rootUrl : string, serviceUrl : string) {
        this.rootUrl = rootUrl;
        this.serviceUrl = serviceUrl;
    }
    
    search(searchIn : Ref ) : Observable<SearchOut> {
        return this.http.post( this.serviceUrl, searchIn ).map( this.handleSearchOutResponse ).catch( this.handleError );
    }

    private handleSearchOutResponse(response: Response ): SearchOut {
        let searchOut : SearchOut = new SearchOut;
        searchOut.parse(response.json());
        return searchOut;
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
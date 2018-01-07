
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpHeaders } from '@angular/common/http';


import { Observable } from 'rxjs/Observable';
import { Observer } from 'rxjs/Observer';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { Subject } from 'rxjs/Subject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/operator/concat';
import 'rxjs/add/operator/first';

import { AsyncLocalStorage } from 'angular-async-local-storage';

import { Obj, Contract, Ref, List, Uri, SearchOut, WatchService, Watch, WatchIn, WatchInItem, WatchOut, Nil } from './obix';
import { SearchService } from '../obix/obix.searchservice';

import { Action } from '../obix/obix.services-commons';


export class WatchAction {
    action: Action;
    watch: Watch;
    constructor( action: Action, watch: Watch ) {
        this.action = action;
        this.watch = watch;
    }
}

export class WatchContentAction {
    action: Action;
    obj: Obj;
    constructor( action: Action, obj: Obj ) {
        this.action = action;
        this.obj = obj;
    }
}


@Injectable()
export class WatchesService {

    static watchesListKey: string = 'watchesList';

    http: HttpClient;
    rootUrl: string;
    serviceUrl: string;
    storage: AsyncLocalStorage;
    cache: ReplaySubject<WatchService>;
    watchStream: Subject<WatchAction>;
    watchContentStream: Subject<WatchContentAction>;
    searchService: SearchService;

    constructor( http: HttpClient, storage: AsyncLocalStorage, searchService: SearchService, ) {
        this.http = http;
        this.storage = storage;
        this.cache = new ReplaySubject<WatchService>();
        this.watchStream = new Subject<WatchAction>();
        this.watchContentStream = new Subject<WatchContentAction>();
        this.searchService = searchService;
    }

    private handleWatchServiceResponse( response: any ): WatchService {
        let result: WatchService = new WatchService;
        result.parse( response);
        return result;
    }

    getWatchService(): Observable<WatchService> {
        return this.cache.asObservable();
    }

    // items from add, update and delete whatch item
    getWatchStream(): Observable<WatchAction> {
        return this.watchStream.asObservable();
    }

    // return items from watch poolchanges and poolrefresh
    getWatchContentStream(): Observable<WatchContentAction> {
        return this.watchContentStream.asObservable();
    }

    // Initialize the watch service with it url. Retrieve watch service object, this one will tell us of its create watch operation.
    initialize( rootUrl: string, serviceUrl: string ) {

        this.rootUrl = rootUrl;
        this.serviceUrl = serviceUrl;

        // check first if we already have the watches service in local storage
        this.storage.getItem( serviceUrl )
            .map( response => { if ( response != null ) { let watchservice: WatchService = new WatchService; watchservice.parse( response ); return watchservice; } } )
            .subscribe(( watchservice ) => {
                if ( watchservice != null ) {
                    this.cache.next( watchservice );
                } else {
                    // not available, get it from backend server
                    this.http.get( serviceUrl ).map( this.handleWatchServiceResponse ).subscribe( watchservice => {
                        // and save it in local storage
                        this.storage.setItem( serviceUrl, watchservice ).subscribe(() => { this.cache.next( watchservice ); }, () => { } );
                    } );
                }
            }, () => {
                console.error( 'error reading from localstorage' );
            } );



    }

    /**
     * Retrieve a series of watches as observables.
     * The list of watches to be returned is fetched from local storage. If not found there a full list is retrieved from backend rest server.
     * Emit each watch obtained to observers
     * 
     */
    getWatchesList() {

        this.storage.getItem( WatchesService.watchesListKey )
            .map( item => { if ( item != null ) { let list: List = new List(); list.parse( item ); return list; } } )
            .subscribe(( list ) => {
                if ( list != null ) {
                    let observables: Array<Observable<Watch>> = [];
                    for ( let listItem of list.childrens ) {
                        observables.push( this.getWatch( listItem.getUrl( this.rootUrl ) ) );
                    }
                    Observable.concat<Watch>( ...observables ).subscribe(( watch ) => { this.watchStream.next( new WatchAction( Action.Add, watch ) ) } );

                } else {
                    let searchRef: Ref = new Ref();
                    searchRef.is = new Contract( [new Uri( 'obix:Watch' )] );

                    this.searchService.search( searchRef )
                        .map( item => { if ( item != null ) { let searchOut: SearchOut = new SearchOut(); searchOut.parse( item ); return searchOut; } } )
                        .subscribe(( searchOut ) => {
                            let searchList: List = searchOut.getValueList();
                            this.storage.setItem( WatchesService.watchesListKey, searchList ).subscribe(() => {

                                let observables: Array<Observable<Watch>> = [];
                                for ( let listItem of searchList.childrens ) {
                                    observables.push( this.getWatch( listItem.getUrl( this.rootUrl ) ) );
                                }
                                Observable.concat<Watch>( ...observables ).subscribe(( watch ) => { this.watchStream.next( new WatchAction( Action.Add, watch ) ) } );;
                            }, () => { } );
                        } )


                }

            }, ( error ) => {
                console.log( error );

            } );

    }

    /**
     * Reload the full of list of watches from from backend server and update with it the list contained in local storage.
     * Emit each watch obtained to observers
     * 
     */
    refreshWatchList() {

        let searchRef: Ref = new Ref();
        searchRef.is = new Contract( [new Uri( 'obix:Watch' )] );

        this.searchService.search( searchRef )
            .map( item => { if ( item != null ) { let searchOut: SearchOut = new SearchOut(); searchOut.parse( item ); return searchOut; } } )
            .subscribe(( searchOut ) => {
                let searchList: List = searchOut.getValueList();
                this.storage.setItem( WatchesService.watchesListKey, searchList ).subscribe(() => {

                    this.watchStream.next( new WatchAction( Action.Reset, null ) );

                    let observables: Array<Observable<Watch>> = [];
                    for ( let listItem of searchList.childrens ) {
                        let observer: ReplaySubject<Watch> = new ReplaySubject<Watch>();
                        this.refreshWatch( listItem.getUrl( this.rootUrl ), observer );
                        observables.push( observer.asObservable() );
                    }
                    Observable.concat<Watch>( ...observables ).subscribe(( watch ) => { this.watchStream.next( new WatchAction( Action.Add, watch ) ) } );;
                }, () => { } );
            } )
    }

    /**
     * Retrieve an observable on a Watch. The watch is first search in local storage, then from the backend rest server
     * The observable will emmit just once and then return complete.
     * 
     * @param watchUrl the  url of the watch to retrieve.
     * @returns Observable<Watch>
     */
    getWatch( watchUrl ): Observable<Watch> {

        let result: ReplaySubject<Watch> = new ReplaySubject<Watch>();

        this.storage.getItem( watchUrl )
            .map( item => { if ( item != null ) { let watchItem: Watch = new Watch(); watchItem.parse( item ); return watchItem; } } )
            .subscribe(( watchItem ) => {
                if ( watchItem != null ) {
                    result.next( watchItem );
                    result.complete();
                } else {
                    this.refreshWatch( watchUrl, result );
                }

            }, ( error ) => {
                console.log( error );
                result.error( error );
            } );

        return result.asObservable();
    }

    /**
     * Obtain a watch from backend rest server, saves it to localstorage and informs observer
     * 
     * @param watchUrl
     */
    refreshWatch( watchUrl: string, observer: Observer<Watch> ) {
        this.http.get( watchUrl ).map( response => {
            let watchItem: Watch = new Watch();
            watchItem.parse( response ); return watchItem;
        } )
            .subscribe( watch => {
                // and save it in local storage
                this.storage.setItem( watchUrl, watch ).subscribe(() => { observer.next( watch ); observer.complete(); }, () => { } );
            } )
    }


    /**
     * Create a new watch on backend rest service. Once created, store watch on local storage, and emit Action.Ass message to observers.
     * Emit the new watch to observers.
     * 
     */
    createWatch() {

        this.getWatchService().subscribe( watchService => {
            let makeUrl: string = watchService.getMakeOp().getUrl( this.rootUrl );
            // create watchn on backen server
            this.http.post( this.serviceUrl, null ).map( response => { let watchItem: Watch = new Watch(); watchItem.parse( response ); return watchItem; } )
                .subscribe(( watch: Watch ) => {
                    // once created, save it in local storage
                    this.storage.setItem( watch.getUrl( this.rootUrl ), watch )
                        .subscribe(() => {
                            // then update watch list on local storage. First get watch list ..
                            this.storage.getItem( WatchesService.watchesListKey )
                                .map( item => { if ( item != null ) { let list: List = new List(); list.parse( item ); return list; } } )
                                .subscribe(( list ) => {
                                    let ref: Ref = new Ref(); ref.href = new Uri( watch.href.val );
                                    list.childrens.push( ref );
                                    // ... then update watch list
                                    this.storage.setItem( WatchesService.watchesListKey, list )
                                        // and finally send watch to stream.
                                        .subscribe(() => { this.watchStream.next( new WatchAction( Action.Add, watch ) ); } )
                                } )
                        }, () => { console.error( 'error reading from localstorage' ) } );
                } )
        } )
    }

    /**
     * Save watch to backend
     * 
     * @param watch
     */
    saveWatch( watch: Watch ) {
        let watchUrl = watch.getUrl( this.rootUrl );

        let headers: HttpHeaders = new HttpHeaders( {
            'Content-Type': 'application/json',
            'Accept': 'q=0.8;application/json;q=0.9'
        } );

        this.http.put( watchUrl, JSON.stringify( watch, function( name, value ) { if ( name === "parent" ) return undefined; else return value; } ), {
            headers: headers,
        } )
            .subscribe(() => {
                this.storage.setItem( watchUrl, watch ).subscribe(() => {
                    this.watchStream.next( new WatchAction( Action.Update, watch ) );
                }, () => { console.error( 'error reading from localstorage' ) } )
            } )
    }


    /**
     * Delete a watch. Delete first on backend rest server. Then delete from local storage. On success emit Action.Delete message to observers.
     * 
     * @param watchUrl path  of watch
     */
    deleteWatch( url ) {

        let ref: Ref = new Ref(); ref.href = new Uri( url );
        let watchUrl = ref.getUrl( this.rootUrl );

        // retrieve full watch form local storage
        this.storage.getItem( watchUrl )
            .map( item => { if ( item != null ) { let watch: Watch = new Watch(); watch.parse( item ); return watch; } } )
            .subscribe(( watchItem ) => {
                if ( watchItem != null ) {
                    // the watch expose it delete operation url
                    let deleteUrl: string = watchItem.getDeleteOp().getUrl( this.rootUrl );
                    // do the delete on backend server
                    this.http.post( deleteUrl, null ).map( response => { let nil: Nil = new Nil(); nil.parse( response ); return nil; } )
                        .subscribe(() => {
                            // then on success remove from local storage
                            this.storage.removeItem( watchUrl ).subscribe(() => {
                                this.storage.getItem( WatchesService.watchesListKey )
                                    .map( item => { if ( item != null ) { let list: List = new List(); list.parse( item ); return list; } } )
                                    .subscribe(( list ) => {

                                        let elemIndex = list.childrens.findIndex( elem => elem.href.val == ref.href.val );
                                        if ( elemIndex > -1 ) {
                                            list.childrens.splice( elemIndex, 1 );
                                            // ... then update watch list
                                            this.storage.setItem( WatchesService.watchesListKey, list )
                                                // and finally send watch to stream.
                                                .subscribe(() => { this.watchStream.next( new WatchAction( Action.Delete, watchItem ) ); } )
                                        }
                                    }, ( error ) => { console.error( 'error reading from localstorage' + error ) } )
                            }, ( error ) => { console.error( 'error reading from localstorage' + error ) } )
                        } )
                }
            }, () => {
                console.error( 'error reading from localstorage' );
            } )
    }
    
    
    /**
     * Remove watch from client side only not from backend 
     * 
     * 
     * @param url
     */
    removeWatch( url ) {

        let ref: Ref = new Ref(); ref.href = new Uri( url );
        let watchUrl = ref.getUrl( this.rootUrl );

        // retrieve full watch form local storage
        this.storage.getItem( watchUrl )
            .subscribe(( watchItem ) => {
                if ( watchItem != null ) {
                   
                    // then on success remove from local storage
                    this.storage.removeItem( watchUrl ).subscribe(() => {
                        this.storage.getItem( WatchesService.watchesListKey )
                            .map( item => { if ( item != null ) { let list: List = new List(); list.parse( item ); return list; } } )
                            .subscribe(( list ) => {

                                let elemIndex = list.childrens.findIndex( elem => elem.href.val == ref.href.val );
                                if ( elemIndex > -1 ) {
                                    list.childrens.splice( elemIndex, 1 );
                                    // ... then update watch list
                                    this.storage.setItem( WatchesService.watchesListKey, list )
                                        // and finally send watch to stream.
                                        .subscribe(() => { this.watchStream.next( new WatchAction( Action.Delete, watchItem ) ); } )
                                }
                            }, ( error ) => { console.error( 'error reading from localstorage' + error ) } )
                    }, ( error ) => { console.error( 'error reading from localstorage' + error ) } )
                        
                }
            }, () => {
                console.error( 'error reading from localstorage' );
            } )
    }

    /**
     * Get the count of item included in a watch. Find from what is stored in local storage.
     * 
     * @param watchUrl
     */
    getWatchCount( watchUrl ): Observable<Number> {

        let result: ReplaySubject<Number> = new ReplaySubject<Number>();

        let ref: Ref = new Ref(); ref.href = new Uri( watchUrl );
        let watchFullUrl = ref.getUrl( this.rootUrl );

        // retrieve full watch from storage
        this.storage.getItem( watchFullUrl )
            .map( item => { if ( item != null ) { let watch: Watch = new Watch(); watch.parse( item ); return watch; } } )
            .subscribe(( watchItem ) => {
                if ( watchItem != null ) {
                    // the watch expose it delete operation url
                    let poolRefreshUrl: string = watchItem.getPoolRefreshOp().getUrl( this.rootUrl );
                    // try to get last poolRefresh from storage
                    this.storage.getItem( poolRefreshUrl ).map( item => { if ( item != null ) { let watchOut: WatchOut = new WatchOut(); watchOut.parse( item ); return watchOut; } } )
                        .subscribe(( watchOut ) => {
                            if ( watchOut != null ) {
                                // we have one, ...
                                let valueList: List = watchOut.getValueList();
                                result.next(valueList.childrens.length);
                                
                            } else {
                                // don't have one yet, load from backend ...
                                result.next(0);
                            }
                        }, () => { console.error( 'error reading from localstorage' ); } )

                }
            }, () => { console.error( 'error reading from localstorage' ); }
            )

        return result.asObservable();
    }

    /**
     * Load full content from a watch. Load first from storage, if not present load from back end. Content is emited to observers.
     * 
     * @param watchUrl the url from the watch.
     */
    getPoolRefresh( watchUrl ) {

        let ref: Ref = new Ref(); ref.href = new Uri( watchUrl );
        let watchFullUrl = ref.getUrl( this.rootUrl );

        // retrieve full watch from storage
        this.storage.getItem( watchFullUrl )
            .map( item => { if ( item != null ) { let watch: Watch = new Watch(); watch.parse( item ); return watch; } } )
            .subscribe(( watchItem ) => {
                if ( watchItem != null ) {
                    // the watch expose it delete operation url
                    let poolRefreshUrl: string = watchItem.getPoolRefreshOp().getUrl( this.rootUrl );
                    // try to get last poolRefresh from storage
                    this.storage.getItem( poolRefreshUrl ).map( item => { if ( item != null ) { let watchOut: WatchOut = new WatchOut(); watchOut.parse( item ); return watchOut; } } )
                        .subscribe(( watchOut ) => {
                            if ( watchOut != null ) {
                                // we have one, ...
                                let valueList: List = watchOut.getValueList();
                                this.watchContentStream.next( new WatchContentAction( Action.Reset, null ) );
                                for ( let value of valueList.childrens ) {
                                    // .. get latest value from storage as well
                                    this.storage.getItem( value.getUrl( this.rootUrl ) ).map( item => { if ( item != null ) { let obj: Obj = Obj.obixParse( item ); return obj; } } )
                                        .subscribe(( obj ) => { this.watchContentStream.next( new WatchContentAction( Action.Add, obj ) ); } )
                                }
                            } else {
                                // don't have one yet, load from backend ...
                                this.refreshPoolRefresh( poolRefreshUrl );
                            }
                        }, () => { console.error( 'error reading from localstorage' ); } )


                }
            }, () => { console.error( 'error reading from localstorage' ); }
            )
    }

    /**
     * Reload content of a watch from back end server and update records in local storage
     * 
     * @param watchUrl
     */
    forcePoolRefresh( watchUrl ) {
        let ref: Ref = new Ref(); ref.href = new Uri( watchUrl );
        let watchFullUrl = ref.getUrl( this.rootUrl );

        // retrieve full watch from storage
        this.storage.getItem( watchFullUrl )
            .map( item => { if ( item != null ) { let watch: Watch = new Watch(); watch.parse( item ); return watch; } } )
            .subscribe(( watchItem ) => {
                if ( watchItem != null ) {
                    // the watch expose it delete operation url
                    let poolRefreshUrl: string = watchItem.getPoolRefreshOp().getUrl( this.rootUrl );
                    this.refreshPoolRefresh( poolRefreshUrl );

                }
            }, () => { console.error( 'error reading from localstorage' ); }
            )
    }

    refreshPoolRefresh( poolRefreshUrl ) {
        // do the delete on backend server
        this.http.post( poolRefreshUrl, null ).map( response => { let watchOut: WatchOut = new WatchOut(); watchOut.parse( response ); return watchOut; } )
            .subscribe(( watchOut ) => {

                let values: List = watchOut.getValueList();
                for ( let item of values.childrens ) {
                    item.timestamp = new Date();
                }

                this.storage.setItem( poolRefreshUrl, watchOut ).subscribe(() => {
                    let valueList: List = watchOut.getValueList();
                    this.watchContentStream.next( new WatchContentAction( Action.Reset, null ) );
                    for ( let value of valueList.childrens ) {
                        this.storage.setItem( value.getUrl( this.rootUrl ), value ).subscribe(() => {
                            this.watchContentStream.next( new WatchContentAction( Action.Add, value ) );
                        } )
                    }
                } )
            } )
    }

    /**
     * Collect the changes on items recorded on a watch
     * 
     * @param watchUrl
     */
    getPoolChanges( watchUrl ) {
        let ref: Ref = new Ref(); ref.href = new Uri( watchUrl );
        let watchFullUrl = ref.getUrl( this.rootUrl );

        // retrieve full watch from storage
        this.storage.getItem( watchFullUrl )
            .map( item => { if ( item != null ) { let watch: Watch = new Watch(); watch.parse( item ); return watch; } } )
            .subscribe(( watchItem ) => {
                if ( watchItem != null ) {
                    // the watch expose it delete operation url
                    let poolChangesUrl: string = watchItem.getPoolChangesOp().getUrl( this.rootUrl );
                    this.http.post( poolChangesUrl, null ).map( response => { let watchOut: WatchOut = new WatchOut(); watchOut.parse( response ); return watchOut; } )
                        .subscribe(( watchOut ) => {
                            let valueList: List = watchOut.getValueList();
                            for ( let value of valueList.childrens ) {
                                value.timestamp = new Date();
                                this.storage.setItem( value.getUrl( this.rootUrl ), value ).subscribe(() => {
                                    this.watchContentStream.next( new WatchContentAction( Action.Update, value ) );
                                } )
                            }

                        } )

                }
            }, () => { console.error( 'error reading from localstorage' ); }
            )
    }

    addWatchItem( watchUrl: string, itemUrl: string ) {

        let watchIn: WatchIn = new WatchIn();
        let watchInItem: WatchInItem = new WatchInItem( itemUrl );
        watchIn.getHrefList().childrens.push( watchInItem );

        let ref: Ref = new Ref(); ref.href = new Uri( watchUrl );
        let watchFullUrl = ref.getUrl( this.rootUrl );

        // retrieve full watch from storage
        this.storage.getItem( watchFullUrl )
            .map( item => { if ( item != null ) { let watch: Watch = new Watch(); watch.parse( item ); return watch; } } )
            .subscribe(( watchItem ) => {
                if ( watchItem != null ) {
                    // the watch expose its add and poolrefresh operations url
                    let addUrl: string = watchItem.getAddOp().getUrl( this.rootUrl );
                    let poolRefreshUrl: string = watchItem.getPoolRefreshOp().getUrl( this.rootUrl );

                    this.http.post( addUrl, watchIn ).map( response => { let watchOutAdd: WatchOut = new WatchOut(); watchOutAdd.parse( response ); return watchOutAdd; } )
                        .subscribe(( watchOutAdd ) => {
                            this.storage.getItem( poolRefreshUrl ).map( item => { if ( item != null ) { let watchOutStorage: WatchOut = new WatchOut(); watchOutStorage.parse( item ); return watchOutStorage; } } )
                                .subscribe(( watchOutStorage ) => {


                                    let valueList: List = watchOutAdd.getValueList();
                                    //this.watchContentStream.next( new WatchContentAction( Action.Delete, null ) );
                                    for ( let value of valueList.childrens ) {
                                        value.timestamp = new Date();
                                        this.storage.setItem( value.getUrl( this.rootUrl ), value ).subscribe(() => {
                                            this.watchContentStream.next( new WatchContentAction( Action.Add, value ) );
                                        } )

                                        // add also item to locally stored list
                                        watchOutStorage.getValueList().childrens.push( value );
                                    }
                                    // update watch list in local storage
                                    this.storage.setItem( poolRefreshUrl, watchOutStorage ).subscribe(() => { } )

                                } )
                        } )


                }
            }, () => { console.error( 'error reading from localstorage' ); }
            )

    }

    removeWatchItem( watchUrl: string, itemUrl: string ) {

        let watchIn: WatchIn = new WatchIn();
        let watchInItem: WatchInItem = new WatchInItem( itemUrl );
        watchIn.getHrefList().childrens.push( watchInItem );

        let ref: Ref = new Ref(); ref.href = new Uri( watchUrl );
        let watchFullUrl = ref.getUrl( this.rootUrl );

        // retrieve full watch from storage
        this.storage.getItem( watchFullUrl )
            .map( item => { if ( item != null ) { let watch: Watch = new Watch(); watch.parse( item ); return watch; } } )
            .subscribe(( watchItem ) => {
                if ( watchItem != null ) {
                    // the watch expose its add and poolrefresh operations url
                    let removeUrl: string = watchItem.getRemoveOp().getUrl( this.rootUrl );
                    let poolRefreshUrl: string = watchItem.getPoolRefreshOp().getUrl( this.rootUrl );

                    this.http.post( removeUrl, watchIn )
                        .subscribe(() => {
                            this.storage.getItem( poolRefreshUrl ).map( item => { if ( item != null ) { let watchOutStorage: WatchOut = new WatchOut(); watchOutStorage.parse( item ); return watchOutStorage; } } )
                                .subscribe(( watchOutStorage ) => {

                                    // remove item from poll list
                                    let elemIndex = watchOutStorage.getValueList().childrens.findIndex( elem => elem.href.val == itemUrl );
                                    watchOutStorage.getValueList().childrens.splice( elemIndex, 1 );
                                    // update watch list in local storage
                                    this.storage.setItem( poolRefreshUrl, watchOutStorage ).subscribe(() => {

                                        this.storage.removeItem( removeUrl ).subscribe(() => {
                                            let ref: Ref = new Ref(); ref.href = new Uri( itemUrl );
                                            this.watchContentStream.next( new WatchContentAction( Action.Delete, ref ) );
                                        } )
                                    } )

                                } )
                        } )


                }
            }, () => { console.error( 'error reading from localstorage' ); }
            )

    }
    
    /**
     * Save an updated item to backend then in localstorage, and feedback to observers
     * 
     * @param obj
     */
    saveWatchItem( obj: Obj ) {
        let objUrl = obj.getUrl( this.rootUrl );

        let headers: HttpHeaders = new HttpHeaders( {
            'Content-Type': 'application/json',
            'Accept': 'q=0.8;application/json;q=0.9'
        } );

        this.http.put( objUrl, JSON.stringify( obj, function( name, value ) { if ( name === "parent" ) return undefined; else return value; } ), {
            headers: headers,
        }).subscribe(() => {
            obj.timestamp = new Date();
            this.storage.setItem( objUrl, obj ).subscribe(() => {
                this.watchContentStream.next( new WatchContentAction( Action.Update, obj ) );
            })
                            
        })
    }
    
    /**
     * Reload a watch item (an obj) from backend and saves it in local storage
     * 
     * @param obj
     */
    updateWatchItem(obj: Obj){
        
        let objUrl = obj.getUrl( this.rootUrl );
        
        this.http.get( objUrl ).map( response => {
            let objItem: Obj = Obj.obixParse( response ); return objItem;
        } )
            .subscribe( obj => {
                // and save it in local storage
                obj.timestamp = new Date();
                this.storage.setItem( objUrl, obj ).subscribe(() => {
                    this.watchContentStream.next( new WatchContentAction( Action.Update, obj ) );
                })
        } )
    }
    
    saveCurrentWatchId(id: string ){
        this.storage.setItem("currentWatchID", id).subscribe(() => {});
    }

    getCurrentWatchID() : Observable<any>{
        return this.storage.getItem("currentWatchID");
    }
}
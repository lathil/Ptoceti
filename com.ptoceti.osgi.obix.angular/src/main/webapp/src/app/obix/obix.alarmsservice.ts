import {concat as observableConcat, Observable, Observer, ReplaySubject, Subject, BehaviorSubject} from 'rxjs';

import {map} from 'rxjs/operators';

import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';


import { Action } from '../obix/obix.services-commons';

import {LocalStorage} from '@ngx-pwa/local-storage';

import { SearchService } from '../obix/obix.searchservice';

import { Obj, Ref, Contract, List, Uri, Nil, SearchOut, AlarmService, Alarm } from './obix';

export class AlarmAction {
    action: Action;
    alarm: Alarm;

    constructor(action: Action, alarm: Alarm ) {
        this.action = action;
        this.alarm = alarm;
    }
}

@Injectable()
export class AlarmsService {

    static alarmsListKey: string = 'alarmsList';

    http: HttpClient;
    rootUrl : string;
    serviceUrl : string;
    storage: LocalStorage;
    cache : ReplaySubject<AlarmService>;

    searchService: SearchService;

    alarmStream: Subject<AlarmAction>;

    constructor(http: HttpClient, storage: LocalStorage, searchService: SearchService) {
        this.http = http;
        this.storage = storage;
        this.cache = new ReplaySubject<AlarmService>(1);
        this.alarmStream = new Subject<AlarmAction>();
        this.searchService = searchService;
    }

    private handleAlarmServiceResponse(response: any ): AlarmService {
        let result : AlarmService = new AlarmService;
        result.parse(response);
        return result;
    }

    getAlarmService(): Observable<AlarmService>{
        return this.cache.asObservable();
    }

    // items from add, update and delete alarm item
    getAlarmStream(): Observable<AlarmAction> {
        return this.alarmStream.asObservable();
    }

    initialize(rootUrl : string, serviceUrl : string) {

        this.rootUrl = rootUrl;
        this.serviceUrl = serviceUrl;

        // check first if we already have the lobby in local storage
        this.storage.getItem(serviceUrl).subscribe((response) => {
            if (response) {
                let alarmservice: AlarmService = new AlarmService;
                alarmservice.parse(response)
                this.cache.next(alarmservice);
            } else{
                // not available, get it from backend server
                this.http.get(serviceUrl).pipe(map(this.handleAlarmServiceResponse)).subscribe(alarmservice => {
                    // and save it in local storage
                    this.storage.setItem(serviceUrl, alarmservice).subscribe(() => { this.cache.next(alarmservice); }, () => {});
                });
            }
        }, (error) => {
            console.error('error reading from localstorage' + error);
        });

    }

    /**
     * Retrieve a series of alarms as observables.
     * The list of alarms to be returned is fetched from local storage. If not found there a full list is retrieved from backend rest server.
     * Emit each alarm obtained to observers
     *
     */
    getAlarmsList() {

        this.storage.getItem(AlarmsService.alarmsListKey).pipe(
            map(item => {
                if (item != null) {
                    let list: List = new List();
                    list.parse(item);
                    return list;
                }
            }))
            .subscribe((list ) => {
                if ( list != null ) {
                    let observables: Array<Observable<Alarm>> = [];
                    for ( let listItem of list.childrens ) {
                        observables.push( this.getAlarm( listItem.getUrl( this.rootUrl ) ) );
                    }
                    observableConcat<Alarm>(...observables).subscribe((alarm) => {
                        this.alarmStream.next(new AlarmAction(Action.Add, alarm))
                    });

                } else {
                    let searchRef: Ref = new Ref();
                    searchRef.is = new Contract( [new Uri( 'obix:Alarm' )] );

                    this.searchService.search(searchRef).pipe(
                        map(item => {
                            if (item != null) {
                                let searchOut: SearchOut = new SearchOut();
                                searchOut.parse(item);
                                return searchOut;
                            }
                        }))
                        .subscribe((searchOut ) => {
                            let searchList: List = searchOut.getValueList();
                            this.storage.setItem( AlarmsService.alarmsListKey, searchList ).subscribe(() => {

                                let observables: Array<Observable<Alarm>> = [];
                                for ( let listItem of searchList.childrens ) {
                                    observables.push( this.getAlarm( listItem.getUrl( this.rootUrl ) ) );
                                }
                                observableConcat<Alarm>(...observables).subscribe((alarm) => {
                                    this.alarmStream.next(new AlarmAction(Action.Add, alarm))
                                });
                            }, () => { } );
                        } )


                }

            }, (error ) => {
                console.log( error );

            } );

    }

    /**
     * Reload the full of list of alarms from from backend server and update with it the list contained in local storage.
     * Emit each alarm obtained to observers
     *
     */
    refreshAlarmList() {

        let searchRef: Ref = new Ref();
        searchRef.is = new Contract( [new Uri( 'obix:Alarm' )] );

        this.searchService.search(searchRef).pipe(
            map(item => {
                if (item != null) {
                    let searchOut: SearchOut = new SearchOut();
                    searchOut.parse(item);
                    return searchOut;
                }
            }))
            .subscribe((searchOut ) => {
                let searchList: List = searchOut.getValueList();
                this.storage.setItem( AlarmsService.alarmsListKey, searchList ).subscribe(() => {

                    this.alarmStream.next( new AlarmAction( Action.Reset, null ) );

                    let observables: Array<Observable<Alarm>> = [];
                    for ( let listItem of searchList.childrens ) {
                        let observer: ReplaySubject<Alarm> = new ReplaySubject<Alarm>();
                        this.refreshAlarm( listItem.getUrl( this.rootUrl ), observer );
                        observables.push( observer.asObservable() );
                    }
                    observableConcat<Alarm>(...observables).subscribe((alarm) => {
                        this.alarmStream.next(new AlarmAction(Action.Add, alarm))
                    });
                    ;
                }, () => { } );
            } )
    }

    /**
     * Create an alarm object in backend service for a given object
     *
     * @param obj : the object for which the alarm is created
     */
    createAlarm(obj: Obj) : Observable<AlarmAction> {

        let result: ReplaySubject<AlarmAction> = new ReplaySubject<AlarmAction>();

        this.getAlarmService().subscribe(alarmService => {
            let makeUrl: string = alarmService.getMakeOp().getUrl( this.rootUrl );

            let objectRef: Ref = new Ref();
            objectRef.href = obj.href;

            // create history on backend server
            this.http.post(this.serviceUrl, objectRef).pipe(map(response => {
                let alarmItem: Alarm = new Alarm();
                alarmItem.parse(response);
                return alarmItem;
            }))
                .subscribe((alarm: Alarm ) => {
                        // once created, save it in local storage
                        this.storage.setItem( alarm.getUrl( this.rootUrl ), alarm )
                            .subscribe(() => {
                                // then update watch list on local storage. First get watch list ..
                                this.storage.getItem(AlarmsService.alarmsListKey).pipe(
                                    map(item => {
                                        if (item != null) {
                                            let list: List = new List();
                                            list.parse(item);
                                            return list;
                                        }
                                    }))
                                    .subscribe((list ) => {
                                        let ref: Ref = new Ref(); ref.href = new Uri( alarm.href.val );
                                        list.childrens.push( ref );
                                        // ... then update watch list
                                        this.storage.setItem( AlarmsService.alarmsListKey, list )
                                        // and finally send watch to stream.
                                            .subscribe(() => {
                                                let alarmAction : AlarmAction = new AlarmAction( Action.Add, alarm );
                                                this.alarmStream.next( alarmAction );
                                                result.next(alarmAction);
                                                result.complete();
                                            } )
                                    } )
                            }, () => { console.error( 'error reading from localstorage' ) } );
                    },
                    (error) => {
                        result.error (error);
                    })
        } )

        return result.asObservable();
    }

    /**
     * Delete a alarm. Delete first on backend rest server. Then delete from local storage. On success emit Action.Delete message to observers.
     *
     * @param alarmUrl path  of alarm
     */
    deleteAlarm(url ) : Observable<AlarmAction> {

        let ref: Ref = new Ref(); ref.href = new Uri( url );
        let alarmUrl = ref.getUrl( this.rootUrl );

        let result: ReplaySubject<AlarmAction> = new ReplaySubject<AlarmAction>();

        // retrieve full alarm form local storage
        this.storage.getItem(alarmUrl).pipe(
            map(item => {
                if (item != null) {
                    let alarm: Alarm = new Alarm();
                    alarm.parse(item);
                    return alarm;
                }
            }))
            .subscribe((alarmItem ) => {
                if ( alarmItem != null ) {
                    // do the delete on backend server
                    this.http.delete(alarmUrl).pipe(map(response => {
                        let nil: Nil = new Nil();
                        nil.parse(response);
                        return nil;
                    }))
                        .subscribe(() => {
                            // then on success remove from local storage
                            this.storage.removeItem( alarmUrl ).subscribe(() => {
                                this.storage.getItem(AlarmsService.alarmsListKey).pipe(
                                    map(item => {
                                        let list: List = new List();
                                        if (item != null) {
                                            list.parse(item);
                                        }
                                        return list;
                                    }))
                                    .subscribe((list ) => {

                                        let elemIndex = list.childrens.findIndex(elem => elem.href.val == ref.href.val );
                                        if ( elemIndex > -1 ) {
                                            list.childrens.splice( elemIndex, 1 );
                                            // ... then update watch list
                                            this.storage.setItem( AlarmsService.alarmsListKey , list )
                                            // and finally send watch to stream.
                                                .subscribe(() => {

                                                    let alarmAction : AlarmAction = new AlarmAction( Action.Delete, alarmItem);
                                                    this.alarmStream.next( alarmAction );
                                                    result.next(alarmAction);
                                                    result.complete();
                                                } )
                                        }
                                    }, ( error ) => { console.error( 'error reading from localstorage' + error ) } )
                            }, ( error ) => { console.error( 'error reading from localstorage' + error ) } )
                        } )
                }
            }, (error) => {
                console.error( 'error reading from localstorage' );
                result.error(error);
            })

        return result.asObservable();
    }

    /**
     * Retrieve an observable on a Alarm. The watch is first search in local storage, then from the backend rest server
     * The observable will emit just once and then return complete.
     *
     * @param alarmUrl the  url of the alarm to retrieve.
     * @returns Observable<Alarm>
     */
    getAlarm(alarmUrl ): Observable<Alarm> {

        let result: ReplaySubject<Alarm> = new ReplaySubject<Alarm>();

        this.storage.getItem(alarmUrl).pipe(
            map(item => {
                if (item != null) {
                    let alarmItem: Alarm = new Alarm();
                    alarmItem.parse(item);
                    return alarmItem;
                }
            }))
            .subscribe((alarmItem ) => {
                if ( alarmItem != null ) {
                    result.next( alarmItem );
                    result.complete();
                } else {
                    this.refreshAlarm( alarmUrl, result );
                }

            }, (error ) => {
                console.log( error );
                result.error( error );
            } );

        return result.asObservable();
    }

    /**
     * Obtain a alarm from backend rest server, saves it to localstorage and informs observer
     *
     * @param alarmUrl
     */
    refreshAlarm(alarmUrl: string, observer: Observer<Alarm> ) {
        this.http.get(alarmUrl).pipe(map(response => {
            let alarmItem: Alarm = new Alarm();
            alarmItem.parse( response ); return alarmItem;
        }))
            .subscribe(alarm => {
                // and save it in local storage
                this.storage.setItem( alarmUrl, alarm ).subscribe(() => { observer.next( alarm ); observer.complete(); }, () => { } );
            } )
    }
}
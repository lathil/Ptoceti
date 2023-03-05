import {Injectable, NgZone} from '@angular/core';
import {EventsService, EventWrapper, EventWrapperEventEnum} from '../api';
import {switchMap} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {
  CustomEventDataType,
  CustomEventErrorType,
  CustomEventReadyStateChangeType,
  CustomEventType,
  SSE,
  SSEOptions,
  SSEOptionsMethod
} from 'sse-ts';
import {JwtStorage} from './jwt-storage.service';


export interface SseParameters {
  basePath?: string;
}

export class SseConfiguration {
  basePath?: string;

  constructor(sseParameters: SseParameters = {}) {
    this.basePath = sseParameters.basePath;
  }
}


@Injectable({
  providedIn: 'root'
})
export class SseService {

  protected basePath = 'http://localhost';
  public sseConfiguration = new SseConfiguration();
  eventService: EventsService;

  zone: NgZone;

  constructor(private jwtstorage: JwtStorage, configuration: SseConfiguration, eventService: EventsService, zone: NgZone) {
    if (configuration) {
      this.sseConfiguration = configuration;
    }
    this.eventService = eventService;
    this.zone = zone;
  }

  subscribeBroadcast(): Observable<EventWrapper> {
    return this.eventService.broadcastEndPoint().pipe(switchMap(broadcastEndpoint => new Observable<EventWrapper>(observer => {

      const sseOptions: SSEOptions = {
        method: SSEOptionsMethod.GET
      };

      if (this.jwtstorage.get() !== null) {
        sseOptions.headers = {
          Authorization: `Bearer ${this.jwtstorage.get()}`
        };
      }
      const eventSource = new SSE(this.sseConfiguration.basePath + '/' + broadcastEndpoint, sseOptions);
      eventSource.addEventListener('message', (messageEvent: CustomEventType) => {
        const dataEvent = messageEvent as CustomEventDataType;
        const data: any = JSON.parse(dataEvent.data);
        const event: EventWrapperEventEnum = EventWrapperEventEnum[Object.keys(EventWrapperEventEnum)[Object.values(EventWrapperEventEnum).indexOf(data.event as unknown as EventWrapperEventEnum)]];
        const eventWrapper: EventWrapper = {};
        eventWrapper.event = event;
        eventWrapper.properties = data.properties;
        this.zone.run(() => observer.next(eventWrapper));
      });

      eventSource.addEventListener('readystatechange', (readyStateEvent: CustomEventReadyStateChangeType) => {
        // readyState === 0 (closed) means the remote source closed the connection,
        // so we can safely treat it as a normal situation. Another way
        // of detecting the end of the stream is to insert a special element
        // in the stream of events, which the client can identify as the last one.
        if (readyStateEvent.readyState === 0) {
          console.log('The stream has been closed by the server.');
          //eventSource.close();
          //observer.complete();
        }
      });


      eventSource.addEventListener('error', (errorEvent: CustomEventErrorType) => {
        observer.error('EventSource error: ' + errorEvent);
      });

      eventSource.stream();

    })));
  }
}

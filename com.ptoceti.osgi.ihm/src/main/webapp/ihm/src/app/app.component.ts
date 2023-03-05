import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Router} from '@angular/router';
import {Store} from '@ngrx/store';
import {loadThing, loadThingsList, thingRemoved, thingUpdated} from './state/things.actions';
import {SseService} from './services/sse.service';
import {EventsService, EventWrapper, EventWrapperEventEnum, FunctionDataWrapper} from './api';
import {deviceRemoved, loadDevice} from './state/devices.actions';
import {driverAttached, driverRemoved, loadDriver} from './state/drivers.actions';
import {deletedConfigurationSuccess, updateConfiguration} from './state/configurations.actions';
import {loadItem} from './state/items.actions';
import {itemFunctionDatapropertyUpdated} from './state/functiondata.actions';
import {AuthenticationService} from './services/authentication.service';
import {Subscription} from 'rxjs';


@Component({
  selector: 'app-root',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'ihm';

  router: Router;
  store: Store;
  sseService: SseService;
  eventService: EventsService;
  authentificationService: AuthenticationService;
  sseEventSubscription: Subscription;

  constructor(router: Router, store: Store, sseService: SseService, eventService: EventsService, authentificationService: AuthenticationService) {
    this.router = router;
    this.store = store;
    this.sseService = sseService;
    this.eventService = eventService;
    this.authentificationService = authentificationService;
  }

  ngOnInit(): void {

    this.authentificationService.isAuthenticated().subscribe({
      next: isAuthentified => {
        if (isAuthentified) {
          if (this.sseEventSubscription != null) {
            this.sseEventSubscription.unsubscribe();
          }
          this.sseEventSubscription = this.subscribeSseEvent();
        } else {
          if (this.sseEventSubscription != null) {
            this.sseEventSubscription.unsubscribe();
            this.sseEventSubscription = null;
          }
        }
      }
    });
  }

  subscribeSseEvent(): Subscription {
    return this.sseService.subscribeBroadcast().subscribe(event => {

      let propertiesList = '';
      Object.entries(event.properties).forEach(([key, value]) => {
        propertiesList += ', ' + key + ': ' + value;
      });
      console.log('received event: ' + event.event + ' with data:' + propertiesList);


      switch (event.event) {
        case EventWrapperEventEnum.DeviceAddedEvent:
          this.store.dispatch(loadDevice({devicePid: event.properties['service.pid']}));
          break;
        case EventWrapperEventEnum.DeviceRemovedEvent:
          this.store.dispatch(deviceRemoved({devicePid: event.properties['service.pid']}));
          break;
        case EventWrapperEventEnum.DalAddedEvent:
          this.store.dispatch(loadThing({thingPid: event.properties['service.pid']}));
          break;
        case EventWrapperEventEnum.DalRemovedEvent:
          this.store.dispatch(thingRemoved({thingPid: event.properties['service.pid']}));
          break;
        case EventWrapperEventEnum.DalDeviceModified:
          this.store.dispatch(thingUpdated({thingPid: event.properties['service.pid']}));
          break;
        case EventWrapperEventEnum.DalFunctionAdded:
          this.store.dispatch(loadItem({uid: event.properties['dal.function.UID']}));
          break;
        case EventWrapperEventEnum.DalFunctionRemoved:
          break;
        case EventWrapperEventEnum.DriverAddedEvent:
          this.store.dispatch(loadDriver({driverPid: event.properties['service.pid']}));
          break;
        case EventWrapperEventEnum.DriverRemovedEvent:
          this.store.dispatch(driverRemoved({driverPid: event.properties['service.pid']}));
          break;
        case EventWrapperEventEnum.DriverAttachedEvent:
          this.store.dispatch(driverAttached({
            driverId: event.properties['DRIVER_ID'],
            deviceSerial: event.properties['DEVICE_SERIAL']
          }));
          break;
        case EventWrapperEventEnum.CmUpdatedEvent:
          this.store.dispatch(updateConfiguration({configurationPid: event.properties['cm.pid']}));
          break;
        case EventWrapperEventEnum.CmDeletedEvent:
          this.store.dispatch(deletedConfigurationSuccess({configurationPid: event.properties['cm.pid']}));
          break;
        case EventWrapperEventEnum.DalFunctionPropertyChanged:
          this.store.dispatch(itemFunctionDatapropertyUpdated({
            functionUid: event.properties['dal.function.UID'],
            propertyName: event.properties['dal.function.property.name'],
            functionData: JSON.parse(event.properties['dal.function.property.value']) as FunctionDataWrapper
          }));
          break;
      }
    });
  }


}

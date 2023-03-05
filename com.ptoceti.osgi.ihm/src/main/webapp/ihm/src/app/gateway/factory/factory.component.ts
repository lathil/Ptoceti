import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Observable, Observer} from 'rxjs';
import {select, Store} from '@ngrx/store';
import {
  ConfigurationPropertyEntry,
  ConfigurationService,
  ConfigurationWrapper,
  DeviceFactoryInfoWrapper, DeviceFactoryInfoWrapperTypeEnum,
  MetatypeWrapper
} from '../../api';
import {selectDeviceFactoriesByType} from '../../state/deviceFactories.selector';
import {selectMetatypesByPid} from '../../state/metatypes.selectors';
import {ConfigurationEditorWrapper} from '../../shared/configuration-editor/configuration-editor.component';
import {take} from 'rxjs/operators';


export enum FactoryState {
  Chose,
  Create,
}

@Component({
  selector: 'app-factory',
  templateUrl: './factory.component.html',
  styleUrls: ['./factory.component.scss']
})
export class FactoryComponent implements OnInit {

  activatedRoute: ActivatedRoute;
  store: Store;

  type: DeviceFactoryInfoWrapperTypeEnum;
  factories$: Observable<DeviceFactoryInfoWrapper[]>;

  selectedFactory: DeviceFactoryInfoWrapper;
  factoryState: FactoryState = FactoryState.Chose;

  metatype$: Observable<MetatypeWrapper>;

  configurationsService: ConfigurationService;
  configurationEditorWrapper: ConfigurationEditorWrapper;


  constructor(store: Store, activatedRoute: ActivatedRoute, configurationsService: ConfigurationService) {
    this.activatedRoute = activatedRoute;
    this.store = store;
    this.configurationsService = configurationsService;

    this.type = DeviceFactoryInfoWrapperTypeEnum.Dal;
  }

  ngOnInit(): void {

    const typeParam = 'type';
    this.activatedRoute.queryParams.subscribe(params => {
      this.type = DeviceFactoryInfoWrapperTypeEnum[Object.keys(DeviceFactoryInfoWrapperTypeEnum)[Object.values(DeviceFactoryInfoWrapperTypeEnum).indexOf(params[typeParam] as unknown as DeviceFactoryInfoWrapperTypeEnum)]];
    });

    this.factories$ = this.store.pipe(select(selectDeviceFactoriesByType(this.type)));
  }

  selectFactory(): void {
    this.metatype$ = this.store.select(selectMetatypesByPid(this.selectedFactory.factory ? null : this.selectedFactory.pid, this.selectedFactory.factory ? this.selectedFactory.pid : null));
    this.metatype$.pipe(take(1)).subscribe(metatype => {
      const configuration: ConfigurationWrapper = ((): ConfigurationWrapper => {
        if (this.selectedFactory.factory) {
          return {factoryPid: this.selectedFactory.pid, properties: []} as ConfigurationWrapper;
        }
        return {pid: this.selectedFactory.pid, properties: []} as ConfigurationWrapper;
      })();

      for (const attributeDefinitinon of metatype.objectClassDefinition.attributeDefinitions) {
        configuration.properties.push({key: attributeDefinitinon.id, value: null} as ConfigurationPropertyEntry);
      }

      this.configurationEditorWrapper = new ConfigurationEditorWrapper(configuration, metatype);
      this.factoryState = FactoryState.Create;
    });
  }

  onChangeFactory(): void {
  }

  isFactoryStateCreate(): boolean {
    return this.factoryState === FactoryState.Create;
  }

  onCreateConfiguration(configuration: ConfigurationWrapper): void {
    console.log('onCreateConfiguration called.');
    this.configurationsService.createConfiguration(configuration).subscribe((value: any) => console.log('onCreateConfiguration return'));
  }
}

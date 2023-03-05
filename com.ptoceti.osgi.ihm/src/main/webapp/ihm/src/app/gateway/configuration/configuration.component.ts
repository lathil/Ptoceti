import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {select, Store} from '@ngrx/store';
import {ConfigurationPropertyEntry, ConfigurationService, ConfigurationWrapper, MetatypeWrapper} from '../../api';
import {combineLatest, forkJoin, Observable} from 'rxjs';
import {selectConfigurationByPid} from '../../state/configurations.selectors';
import {map, mapTo, switchMap, take, tap} from 'rxjs/operators';
import {selectMetatypesByPid, selectMetatypeState} from '../../state/metatypes.selectors';
import {FormControl, FormGroup, Validator, Validators} from '@angular/forms';
import {updateConfiguration} from '../../state/configurations.actions';
import {ConfigurationEditorWrapper} from '../../shared/configuration-editor/configuration-editor.component';

@Component({
  selector: 'app-configuration',
  templateUrl: './configuration.component.html',
  styleUrls: ['./configuration.component.scss']
})
export class ConfigurationComponent implements OnInit {


  activatedRoute: ActivatedRoute;
  servicePid: string;

  store: Store;
  configuration$: Observable<ConfigurationWrapper>;
  metatype$: Observable<MetatypeWrapper>;

  configurationEditorWrapper: ConfigurationEditorWrapper;

  configurationForm: FormGroup;

  configurationsService: ConfigurationService;

  constructor(store: Store, activatedRoute: ActivatedRoute, configurationsService: ConfigurationService) {
    this.activatedRoute = activatedRoute;
    this.store = store;
    this.configurationsService = configurationsService;

    const paramPid = this.activatedRoute.snapshot.paramMap.get('pid');
    this.servicePid = this.activatedRoute.snapshot.paramMap.get('pid');

    this.configuration$ = this.store.select(selectConfigurationByPid(this.servicePid));
    this.metatype$ = this.configuration$.pipe(
      switchMap((configuration: ConfigurationWrapper) => this.store.select(selectMetatypesByPid(configuration.pid, configuration.factoryPid))),
    );

    combineLatest([this.configuration$, this.metatype$]).pipe(take(1)).subscribe(([config, meta]) => {
      this.configurationEditorWrapper = new ConfigurationEditorWrapper(config, meta);
    });
  }

  onSaveConfiguration(configuration: ConfigurationWrapper): void {
    console.log('onSaveConfiguration called.');
    this.configurationsService.updateConfiguration(configuration.pid, configuration).subscribe((value: any) => console.log('onSaveConfiguration return'));
    ;
  }

  ngOnInit(): void {
  }
}

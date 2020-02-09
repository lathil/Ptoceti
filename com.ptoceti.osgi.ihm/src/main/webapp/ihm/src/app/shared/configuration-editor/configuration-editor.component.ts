import {Component, Input, OnInit, OnChanges, SimpleChanges, Output, EventEmitter, AfterViewInit} from '@angular/core';

import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ConfigurationPropertyEntry, ConfigurationWrapper, MetatypeWrapper} from '../../api';

export class ConfigurationEditorWrapper {
  configuration: ConfigurationWrapper;
  metatype: MetatypeWrapper;

  constructor(configuration: ConfigurationWrapper, metatype: MetatypeWrapper) {
    this.configuration = configuration;
    this.metatype = metatype;
  }
}

@Component({
  selector: 'app-configuration-editor',
  templateUrl: './configuration-editor.component.html',
  styleUrls: ['./configuration-editor.component.scss']
})
export class ConfigurationEditorComponent implements OnInit, OnChanges {

  @Input()
  configurationEditorWrapper: ConfigurationEditorWrapper;
  @Output()
  saveConfigEvent = new EventEmitter<ConfigurationWrapper>();

  configurationForm: FormGroup;

  constructor() {

  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log('configuration-editor component ngOnChanges');

    this.configurationForm = new FormGroup({});
    this.configurationEditorWrapper.metatype.objectClassDefinition.attributeDefinitions.forEach(attributeDef => {
      const entry: ConfigurationPropertyEntry = this.configurationEditorWrapper.configuration.properties.find(p => p.key === attributeDef.id);

      const validators = [];
      let defaultValue: any = null;
      let value: any = null;
      switch (attributeDef.type) {
        case 'LONG':
          validators.push(Validators.max(9223372036854775807), Validators.min(-9223372036854775808), Validators.pattern('[+-]?([0-9]+)$'));
          defaultValue = '0';
          if (entry && entry.value) {
            value = entry.value;
          } else {
            value = defaultValue;
          }
          break;
        case 'INTEGER':
          validators.push(Validators.max(2147483647), Validators.min(-2147483648), Validators.pattern('^[+-]?([0-9]+)$'));
          defaultValue = '0';
          if (entry && entry.value) {
            value = entry.value;
          } else {
            value = defaultValue;
          }
          break;
        case 'SHORT':
          validators.push(Validators.max(32767), Validators.min(-32768), Validators.pattern('^[+-]?([0-9]+)$'));
          defaultValue = '0';
          if (entry && entry.value) {
            value = entry.value;
          } else {
            value = defaultValue;
          }
          break;
        case 'BYTE':
          validators.push(Validators.max(127), Validators.min(-128), Validators.pattern('[+-]?([0-9]+)$'));
          defaultValue = '0';
          if (entry && entry.value) {
            value = entry.value;
          } else {
            value = defaultValue;
          }
          break;
        case 'DOUBLE':
          validators.push(Validators.max(1.7976931348623157E308), Validators.min(4.9E-324), Validators.pattern('^[+-]?([0-9]+\.?[0-9]+)$'));
          defaultValue = '0';
          if (entry && entry.value) {
            value = entry.value;
          } else {
            value = defaultValue;
          }
          break;
        case 'FLOAT':
          validators.push(Validators.max(3.4028235E38), Validators.min(1.4E-45), Validators.pattern('^[+-]?([0-9]+\.?[0-9]+)$'));
          defaultValue = '0';
          if (entry && entry.value) {
            value = entry.value;
          } else {
            value = defaultValue;
          }
          break;
        case 'BOOLEAN':
          defaultValue = null;
          if (entry && entry.value) {
            if (entry.value === 'true') {
              value = 'true';
            } else {
              value = null;
            }
          } else {
            value = defaultValue;
          }
          break;
        default:
          defaultValue = '';
          if (entry && entry.value) {
            value = entry.value;
          } else {
            value = defaultValue;
          }
          validators.push(Validators.required);
          break;
      }


      const formControl = new FormControl(value, validators);
      this.configurationForm.addControl(attributeDef.id, formControl);
    });
  }

  ngOnInit(): void {
  }


  onFormSubmit(): void {
    console.log('onFormSubmit called.');

    const updatedConfiguration: ConfigurationWrapper = {
      factoryPid: this.configurationEditorWrapper.configuration.factoryPid,
      pid: this.configurationEditorWrapper.configuration.pid,
      properties: []
    } as ConfigurationWrapper;
    this.configurationEditorWrapper.metatype.objectClassDefinition.attributeDefinitions.forEach(attributeDef => {
      let updatedValue = this.configurationForm.controls[attributeDef.id].value;
      if (attributeDef.type === 'BOOLEAN') {
        if (updatedValue == null) {
          updatedValue = 'false';
        }
      }
      updatedConfiguration.properties.push({key: attributeDef.id, value: updatedValue} as ConfigurationPropertyEntry);
    });
    this.saveConfigEvent.emit(updatedConfiguration);
  }

}

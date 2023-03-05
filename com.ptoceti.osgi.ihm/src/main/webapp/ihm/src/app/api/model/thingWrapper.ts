/**
 * Ptoceti Rest Api
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


export interface ThingWrapper {
  description?: string;
  driver?: string;
  firmwareVendor?: string;
  firmwareVersion?: string;
  hardwareVendor?: string;
  hardwareVersion?: string;
  model?: string;
  name?: string;
  properties?: { [key: string]: string; };
  serialNumber?: string;
  status?: ThingWrapperStatusEnum;
  statusDetail?: ThingWrapperStatusDetailEnum;
  types?: Array<string>;
  uid?: string;
}

export enum ThingWrapperStatusEnum {
  Removed = 'STATUS_REMOVED',
  Offline = 'STATUS_OFFLINE',
  Online = 'STATUS_ONLINE',
  Processing = 'STATUS_PROCESSING',
  NotInitialized = 'STATUS_NOT_INITIALIZED',
  NotConfigured = 'STATUS_NOT_CONFIGURED'
};

export enum ThingWrapperStatusDetailEnum {
  Connecting = 'STATUS_DETAIL_CONNECTING',
  Initializing = 'STATUS_DETAIL_INITIALIZING',
  Removing = 'STATUS_DETAIL_REMOVING',
  FirmwareUpdating = 'STATUS_DETAIL_FIRMWARE_UPDATING',
  ConfigurationUnapplied = 'STATUS_DETAIL_CONFIGURATION_UNAPPLIED',
  Broken = 'STATUS_DETAIL_BROKEN',
  CommunicationError = 'STATUS_DETAIL_COMMUNICATION_ERROR',
  DataInsufficient = 'STATUS_DETAIL_DATA_INSUFFICIENT',
  Inaccessible = 'STATUS_DETAIL_INACCESSIBLE',
  ConfigurationError = 'STATUS_DETAIL_CONFIGURATION_ERROR',
  DutyCycle = 'STATUS_DETAIL_DUTY_CYCLE'
};




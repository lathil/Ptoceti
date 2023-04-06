/**
 * Ptoceti Auth Api
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
/* tslint:disable:no-unused-variable member-ordering */

import {Inject, Injectable, Optional} from '@angular/core';
import {
  HttpClient, HttpHeaders, HttpParams,
  HttpResponse, HttpEvent, HttpParameterCodec
} from '@angular/common/http';
import {CustomHttpParameterCodec} from '../encoder';
import {Observable} from 'rxjs';

import {PreferencePropertyEntry} from '../model/models';
import {PreferencesWrapper} from '../model/models';

import {BASE_PATH, COLLECTION_FORMATS} from '../variables';
import {AuthConfiguration} from '../configuration';


@Injectable({
  providedIn: 'root'
})
export class PrefsService {

  protected basePath = 'http://localhost';
  public defaultHeaders = new HttpHeaders();
  public configuration = new AuthConfiguration();
  public encoder: HttpParameterCodec;

  constructor(protected httpClient: HttpClient, @Optional() @Inject(BASE_PATH) basePath: string, @Optional() configuration: AuthConfiguration) {
    if (configuration) {
      this.configuration = configuration;
    }
    if (typeof this.configuration.basePath !== 'string') {
      if (typeof basePath !== 'string') {
        basePath = this.basePath;
      }
      this.configuration.basePath = basePath;
    }
    this.encoder = this.configuration.encoder || new CustomHttpParameterCodec();
  }


  private addToHttpParams(httpParams: HttpParams, value: any, key?: string): HttpParams {
    if (typeof value === "object" && value instanceof Date === false) {
      httpParams = this.addToHttpParamsRecursive(httpParams, value);
    } else {
      httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
    }
    return httpParams;
  }

  private addToHttpParamsRecursive(httpParams: HttpParams, value?: any, key?: string): HttpParams {
    if (value == null) {
      return httpParams;
    }

    if (typeof value === "object") {
      if (Array.isArray(value)) {
        (value as any[]).forEach(elem => httpParams = this.addToHttpParamsRecursive(httpParams, elem, key));
      } else if (value instanceof Date) {
        if (key != null) {
          httpParams = httpParams.append(key,
            (value as Date).toISOString().substr(0, 10));
        } else {
          throw Error("key may not be null if value is Date");
        }
      } else {
        Object.keys(value).forEach(k => httpParams = this.addToHttpParamsRecursive(
          httpParams, value[k], key != null ? `${key}.${k}` : k));
      }
    } else if (key != null) {
      httpParams = httpParams.append(key, value);
    } else {
      throw Error("key may not be null if value is not object or array");
    }
    return httpParams;
  }

  /**
   * @param nodePath
   * @param preferencePropertyEntry
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public addPreferencesProperty(nodePath: string, preferencePropertyEntry?: PreferencePropertyEntry, observe?: 'body', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*' }): Observable<any>;
  public addPreferencesProperty(nodePath: string, preferencePropertyEntry?: PreferencePropertyEntry, observe?: 'response', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*' }): Observable<HttpResponse<any>>;
  public addPreferencesProperty(nodePath: string, preferencePropertyEntry?: PreferencePropertyEntry, observe?: 'events', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*' }): Observable<HttpEvent<any>>;
  public addPreferencesProperty(nodePath: string, preferencePropertyEntry?: PreferencePropertyEntry, observe: any = 'body', reportProgress: boolean = false, options?: { httpHeaderAccept?: '*/*' }): Observable<any> {
    if (nodePath === null || nodePath === undefined) {
      throw new Error('Required parameter nodePath was null or undefined when calling addPreferencesProperty.');
    }

    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [
        '*/*'
      ];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }


    // to determine the Content-Type header
    const consumes: string[] = [];
    const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
    if (httpContentTypeSelected !== undefined) {
      headers = headers.set('Content-Type', httpContentTypeSelected);
    }

    let responseType: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType = 'text';
    }

    return this.httpClient.post<any>(`${this.configuration.basePath}/prefs/props/${encodeURIComponent(String(nodePath))}`,
      preferencePropertyEntry,
      {
        responseType: <any>responseType,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress
      }
    );
  }

  /**
   * @param nodePath
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public deletePreferences(nodePath: string, observe?: 'body', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*' }): Observable<any>;
  public deletePreferences(nodePath: string, observe?: 'response', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*' }): Observable<HttpResponse<any>>;
  public deletePreferences(nodePath: string, observe?: 'events', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*' }): Observable<HttpEvent<any>>;
  public deletePreferences(nodePath: string, observe: any = 'body', reportProgress: boolean = false, options?: { httpHeaderAccept?: '*/*' }): Observable<any> {
    if (nodePath === null || nodePath === undefined) {
      throw new Error('Required parameter nodePath was null or undefined when calling deletePreferences.');
    }

    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [
        '*/*'
      ];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }


    let responseType: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType = 'text';
    }

    return this.httpClient.delete<any>(`${this.configuration.basePath}/prefs/node/${encodeURIComponent(String(nodePath))}`,
      {
        responseType: <any>responseType,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress
      }
    );
  }

  /**
   * @param nodePath
   * @param name
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public deletePreferencesProperty(nodePath: string, name?: string, observe?: 'body', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*' }): Observable<any>;
  public deletePreferencesProperty(nodePath: string, name?: string, observe?: 'response', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*' }): Observable<HttpResponse<any>>;
  public deletePreferencesProperty(nodePath: string, name?: string, observe?: 'events', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*' }): Observable<HttpEvent<any>>;
  public deletePreferencesProperty(nodePath: string, name?: string, observe: any = 'body', reportProgress: boolean = false, options?: { httpHeaderAccept?: '*/*' }): Observable<any> {
    if (nodePath === null || nodePath === undefined) {
      throw new Error('Required parameter nodePath was null or undefined when calling deletePreferencesProperty.');
    }

    let queryParameters = new HttpParams({encoder: this.encoder});
    if (name !== undefined && name !== null) {
      queryParameters = this.addToHttpParams(queryParameters,
        <any>name, 'name');
    }

    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [
        '*/*'
      ];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }


    let responseType: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType = 'text';
    }

    return this.httpClient.delete<any>(`${this.configuration.basePath}/prefs/props/${encodeURIComponent(String(nodePath))}`,
      {
        params: queryParameters,
        responseType: <any>responseType,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress
      }
    );
  }

  /**
   * @param nodePath
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getPreferences(nodePath: string, observe?: 'body', reportProgress?: boolean, options?: { httpHeaderAccept?: 'application/xml' | 'application/json' }): Observable<PreferencesWrapper>;
  public getPreferences(nodePath: string, observe?: 'response', reportProgress?: boolean, options?: { httpHeaderAccept?: 'application/xml' | 'application/json' }): Observable<HttpResponse<PreferencesWrapper>>;
  public getPreferences(nodePath: string, observe?: 'events', reportProgress?: boolean, options?: { httpHeaderAccept?: 'application/xml' | 'application/json' }): Observable<HttpEvent<PreferencesWrapper>>;
  public getPreferences(nodePath: string, observe: any = 'body', reportProgress: boolean = false, options?: { httpHeaderAccept?: 'application/xml' | 'application/json' }): Observable<any> {
    if (nodePath === null || nodePath === undefined) {
      throw new Error('Required parameter nodePath was null or undefined when calling getPreferences.');
    }

    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [
        'application/xml',
        'application/json'
      ];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }


    let responseType: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType = 'text';
    }

    return this.httpClient.get<PreferencesWrapper>(`${this.configuration.basePath}/prefs/node/${encodeURIComponent(String(nodePath))}`,
      {
        responseType: <any>responseType,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress
      }
    );
  }

  /**
   * @param nodePath
   * @param name
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getPreferencesProperty(nodePath: string, name?: string, observe?: 'body', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*' }): Observable<PreferencePropertyEntry>;
  public getPreferencesProperty(nodePath: string, name?: string, observe?: 'response', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*' }): Observable<HttpResponse<PreferencePropertyEntry>>;
  public getPreferencesProperty(nodePath: string, name?: string, observe?: 'events', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*' }): Observable<HttpEvent<PreferencePropertyEntry>>;
  public getPreferencesProperty(nodePath: string, name?: string, observe: any = 'body', reportProgress: boolean = false, options?: { httpHeaderAccept?: '*/*' }): Observable<any> {
    if (nodePath === null || nodePath === undefined) {
      throw new Error('Required parameter nodePath was null or undefined when calling getPreferencesProperty.');
    }

    let queryParameters = new HttpParams({encoder: this.encoder});
    if (name !== undefined && name !== null) {
      queryParameters = this.addToHttpParams(queryParameters,
        <any>name, 'name');
    }

    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [
        '*/*'
      ];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }


    let responseType: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType = 'text';
    }

    return this.httpClient.get<PreferencePropertyEntry>(`${this.configuration.basePath}/prefs/props/${encodeURIComponent(String(nodePath))}`,
      {
        params: queryParameters,
        responseType: <any>responseType,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress
      }
    );
  }

}

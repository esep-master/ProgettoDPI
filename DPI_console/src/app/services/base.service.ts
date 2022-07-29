import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Observable, throwError } from 'rxjs'
import { map, timeout, catchError } from 'rxjs/operators'
import { ResponseReader } from '../utility/response-reader';
import { ErrorCodeEnum } from '../utility/enums';
import { Router } from '@angular/router';
import { AppInjector } from '../utility/app-injector';
import { REQUEST_TIMEOUT, WS_BASE_URL } from '../utility/constants';

@Injectable({
  providedIn: 'root'
})
export class BaseService {

  router: Router

  constructor(protected http: HttpClient) {
    // Dependency Injection senza passare dal Costruttore
    // altrimenti dovrei passarli da tutti i servizi che estendono sender-service

    this.router = AppInjector.get(Router)
  }

  /**
   * Get Mock file
   * @param filePath Esempio: 'assets/mock/listaCommesse.json'
   */
  // doMock(filePath: string): Observable<ResponseReader> {
  //   let httpRequest = this.http.get(filePath).pipe(
  //     map(this.handleResponse.bind(this)),
  //     catchError(this.handleError)
  //   )
  //   return httpRequest
  // }

  /**
  * HTTP GET
  * @param url 
  * @param payload 
  */
  doGet(url: string, timeOut?: number): Observable<any> {
    let customHeaders = this.buildHeaders()

    let serviceURL = WS_BASE_URL + url

    let httpRequest = this.http.get(serviceURL, { headers: customHeaders }).pipe(
      timeout(timeOut ? timeOut : REQUEST_TIMEOUT),
      map(this.handleResponse.bind(this)),
      catchError(this.handleError.bind(this))
    )

    return httpRequest
  }

  /**
  * HTTP DELETE
  * @param url 
  * @param id 
  */
  doDelete(url: string): Observable<any> {
    let customHeaders = this.buildHeaders()

    let serviceURL = WS_BASE_URL + url

    // Crea la richiesta HTTP
    let httpRequest = this.http.delete(serviceURL, { headers: customHeaders }).pipe(
      timeout(REQUEST_TIMEOUT),
      //retry(Constants.REQUEST_RETRIES),
      map(this.handleResponse.bind(this)),
      catchError(this.handleError.bind(this))
    )

    return httpRequest
  }



  /**
   * POST
   * @param url HTTP POST
   * @param payload 
   */
  doPost(url: string, payload: any): Observable<ResponseReader> {
    let customHeaders = this.buildHeaders()

    // Costruisci l'URL
    let completeURL = WS_BASE_URL + url

    // Crea la richiesta HTTP
    let httpRequest = this.http.post(completeURL, JSON.stringify(payload), { headers: customHeaders }).pipe(
      timeout(REQUEST_TIMEOUT),
      //retry(Constants.REQUEST_RETRIES),
      map(this.handleResponse.bind(this)),
      catchError(this.handleError.bind(this))
    )

    return httpRequest
  }

  /**
   * PUT
   * @param url HTTP PUT
   * @param payload 
   */
  doPut(url: string, payload: any): Observable<ResponseReader> {
    let customHeaders = this.buildHeaders()

    // Costruisci l'URL
    let completeURL = WS_BASE_URL + url

    // Crea la richiesta HTTP
    let httpRequest = this.http.put(completeURL, JSON.stringify(payload), { headers: customHeaders }).pipe(
      timeout(REQUEST_TIMEOUT),
      //retry(Constants.REQUEST_RETRIES),
      map(this.handleResponse.bind(this)),
      catchError(this.handleError.bind(this))
    )

    return httpRequest
  }


  private handleResponse(response: any): ResponseReader {

    let reader = new ResponseReader(response, null)

    if (reader.isError()) {
      throw response
    }

    return reader
  }

  /**
  * Handle error
  */
  private handleError(response: any): Observable<ResponseReader> {

    let reader: ResponseReader

    if (response.error && response.status) {

      reader = new ResponseReader(null, response)

      if (reader.getError().code == ErrorCodeEnum.TOKEN_EXPIRED) {
        console.log("Token Scaduto, utente riportato al login")
        this.forceLogout()
      }

    } else {
      reader = new ResponseReader(response, null)
    }

    return throwError(reader)
  }

  private buildHeaders(): HttpHeaders {
    let token: string = localStorage.getItem("token")

    let headers
    if (token) {
      headers = new HttpHeaders().set('Content-Type', "application/json").append('Accept', "application/json").append('Token', token)
    } else {
      headers = new HttpHeaders().set('Content-Type', "application/json").append('Accept', "application/json")
    }

    // Restituisci Headers

    return headers

  }

  private forceLogout() {
    localStorage.clear()
    this.router.navigate(["login"])
  }
}
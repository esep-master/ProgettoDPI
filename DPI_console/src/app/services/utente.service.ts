import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseServiceEnum, UtenteServiceEnum } from '../utility/enums';
import { ResponseReader } from '../utility/response-reader';
import { Util } from '../utility/util';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class UtenteService extends BaseService {

  constructor(protected http: HttpClient) {
    super(http)
  }

  getListaUtenti(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.UTENTE, UtenteServiceEnum.LISTA))
  }

  delUtente(idUtente: number): Observable<ResponseReader> {
    return this.doDelete(Util.buildEnpoint(BaseServiceEnum.UTENTE, idUtente))
  }

  insUtente(payload: any): Observable<ResponseReader> {
    return this.doPost(BaseServiceEnum.UTENTE, payload)
  }

  updUtente(payload: any): Observable<ResponseReader> {
    return this.doPut(BaseServiceEnum.UTENTE, payload)
  }

  associaUtenteCommessa(payload: any): Observable<ResponseReader> {
    return this.doPost(Util.buildEnpoint(BaseServiceEnum.UTENTE, UtenteServiceEnum.ASS_COMMESSA), payload)
  }

  disassociaUtenteCommessa(payload: any): Observable<ResponseReader> {
    return this.doPost(Util.buildEnpoint(BaseServiceEnum.UTENTE, UtenteServiceEnum.DISASS_COMMESSA), payload)
  }

  cambiaPassword(payload: any): Observable<ResponseReader> {
    return this.doPost(Util.buildEnpoint(BaseServiceEnum.UTENTE, UtenteServiceEnum.CAMBIO_PSW), payload)
  }

  getListaCommesse(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.UTENTE, UtenteServiceEnum.LISTA_COMMESSE))
  }
}

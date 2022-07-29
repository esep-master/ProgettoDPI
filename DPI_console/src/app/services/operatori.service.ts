import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseServiceEnum, OperatoreServiceEnum } from '../utility/enums';
import { ResponseReader } from '../utility/response-reader';
import { Util } from '../utility/util';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class OperatoriService extends BaseService {

  constructor(protected http: HttpClient) {
    super(http)
  }

  getListaUtenti(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.OPERATORE, OperatoreServiceEnum.LISTA))
  }

  delOperatore(idOperatore: number): Observable<ResponseReader> {
    return this.doDelete(Util.buildEnpoint(BaseServiceEnum.OPERATORE, idOperatore))
  }

  insOperatore(payload: any): Observable<ResponseReader> {
    return this.doPost(BaseServiceEnum.OPERATORE, payload)
  }

  updOperatore(payload: any): Observable<ResponseReader> {
    return this.doPut(BaseServiceEnum.OPERATORE, payload)
  }

  resetPassword(idOperatore: number): Observable<ResponseReader> {
    let payload = {}
    return this.doPost(Util.buildEnpoint(BaseServiceEnum.OPERATORE, OperatoreServiceEnum.RESETTA_PSW, idOperatore), payload)
  }

  associaOperatoreCommessa(payload: any): Observable<ResponseReader> {
    return this.doPost(Util.buildEnpoint(BaseServiceEnum.OPERATORE, OperatoreServiceEnum.ASS_COMMESSA), payload)
  }

  disassociaOperatoreCommessa(payload: any): Observable<ResponseReader> {
    return this.doPost(Util.buildEnpoint(BaseServiceEnum.OPERATORE, OperatoreServiceEnum.DISASS_COMMESSA), payload)
  }

  associaOperatoreKit(payload: any): Observable<ResponseReader> {
    return this.doPost(Util.buildEnpoint(BaseServiceEnum.OPERATORE, OperatoreServiceEnum.ASS_KIT), payload)
  }

  disassociaOperatoreKit(payload: any): Observable<ResponseReader> {
    return this.doPost(Util.buildEnpoint(BaseServiceEnum.OPERATORE, OperatoreServiceEnum.DISASS_KIT), payload)
  }

}

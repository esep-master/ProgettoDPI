import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ICommessa } from '../interfaces/ICommessa';
import { BaseServiceEnum, CommessaServiceEnum, OperatoreServiceEnum } from '../utility/enums';
import { ResponseReader } from '../utility/response-reader';
import { Util } from '../utility/util';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class CommessaService extends BaseService {

  constructor(protected http: HttpClient) {
    super(http)
  }

  getOperatoriCommessa(idCommessa: number): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.COMMESSA, CommessaServiceEnum.LISTA_OPERATORI, idCommessa))
  }

  deleteCommessa(idCommessa: number): Observable<ResponseReader> {
    return this.doDelete(Util.buildEnpoint(BaseServiceEnum.COMMESSA, idCommessa))
  }

  saveCommessa(payload: any): Observable<ResponseReader> {
    return this.doPost(BaseServiceEnum.COMMESSA, payload)
  }

  getListaCommessa(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.COMMESSA, CommessaServiceEnum.LISTA))
  }

}

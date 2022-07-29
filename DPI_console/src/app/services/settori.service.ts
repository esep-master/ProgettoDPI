import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseServiceEnum, SettoreServiceEnum } from '../utility/enums';
import { ResponseReader } from '../utility/response-reader';
import { Util } from '../utility/util';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class SettoriService extends BaseService {

  constructor(protected http: HttpClient) {
    super(http)
  }

  getListaSettori(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.SETTORE, SettoreServiceEnum.LISTA))
  }

  saveSettore(payload: any): Observable<ResponseReader> {
    return this.doPost(BaseServiceEnum.SETTORE, payload)
  }

  updateSettore(payload: any): Observable<ResponseReader> {
    return this.doPut(BaseServiceEnum.SETTORE, payload)
  }

  deleteSettore(id: number): Observable<ResponseReader> {
    return this.doDelete(Util.buildEnpoint(BaseServiceEnum.SETTORE, id))
  }
}

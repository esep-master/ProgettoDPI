import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseServiceEnum, DpiServiceEnum } from '../utility/enums';
import { ResponseReader } from '../utility/response-reader';
import { Util } from '../utility/util';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class DpiService extends BaseService {

  constructor(protected http: HttpClient) {
    super(http)
  }

  getListaDpi(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.DPI, DpiServiceEnum.LISTA))
  }

  getListaBeaconDisponibili(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.DPI, DpiServiceEnum.BEACON_DISP))
  }

  insUpdDpi(payload: any): Observable<ResponseReader> { //if new idDpi & idBeacon = 0
    return this.doPost(BaseServiceEnum.DPI, payload)
  }

  deleteDpi(payload: any): Observable<ResponseReader> {
    return this.doDelete(Util.buildEnpoint(BaseServiceEnum.DPI, payload.idDpi, payload.idBeacon))
  }
}

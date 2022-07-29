import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseServiceEnum, LookupServiceEnum } from '../utility/enums';
import { ResponseReader } from '../utility/response-reader';
import { Util } from '../utility/util';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class LookupService extends BaseService {

  constructor(protected http: HttpClient) {
    super(http)
  }

  getRuoli(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.LOOKUP, LookupServiceEnum.RUOLI))
  }

  getTipoOperatore(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.LOOKUP, LookupServiceEnum.TIPO_OPERATORI))
  }

  getTipoDpi(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.LOOKUP, LookupServiceEnum.TIPO_DPI))
  }

  getTipoBeacon(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.LOOKUP, LookupServiceEnum.TIPO_BEACON))
  }

  getTipoAllarme(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.LOOKUP, LookupServiceEnum.TIPO_ALLARMI))
  }

  getStatiAllarme(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.LOOKUP, LookupServiceEnum.STATI_ALLARMI))
  }
}

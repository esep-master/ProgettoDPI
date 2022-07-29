import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseServiceEnum, KitServiceEnum } from '../utility/enums';
import { ResponseReader } from '../utility/response-reader';
import { Util } from '../utility/util';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class KitService extends BaseService {

  constructor(protected http: HttpClient) {
    super(http)
  }

  getListaKit(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.KIT, KitServiceEnum.LISTA))
  }

  saveKit(payload: any): Observable<ResponseReader> {
    return this.doPost(BaseServiceEnum.KIT, payload)
  }

  deleteKit(id: number): Observable<ResponseReader> {
    return this.doDelete(Util.buildEnpoint(BaseServiceEnum.KIT, id))
  }

  getInfoKit(payload: any): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.KIT, KitServiceEnum.INFO, payload.idSettore, payload.idOperatore))
  }
}
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AlarmServiceEnum, BaseServiceEnum } from '../utility/enums';
import { ResponseReader } from '../utility/response-reader';
import { Util } from '../utility/util';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class AllarmiService extends BaseService {

  constructor(protected http: HttpClient) {
    super(http)
  }

  getAllarmi(storico?: boolean, datetime?: Date): Observable<ResponseReader> {
    if (storico) {
      return this.doGet(Util.buildEnpoint(BaseServiceEnum.ALLARME, AlarmServiceEnum.STORICO, datetime ? datetime.getTime() : ""))
    } else {
      return this.doGet(Util.buildEnpoint(BaseServiceEnum.ALLARME, AlarmServiceEnum.LISTA, datetime ? datetime.getTime() : ""), 8000)
    }
    
  }

  lavorazioneAllarme(payload: any): Observable<ResponseReader> {
    return this.doPost(Util.buildEnpoint(BaseServiceEnum.ALLARME, AlarmServiceEnum.LAVORAZIONE), payload)
  }

  chiusuraAllarme(payload: any): Observable<ResponseReader> {
    return this.doPost(Util.buildEnpoint(BaseServiceEnum.ALLARME, AlarmServiceEnum.CHIUSURA), payload)
  }
}

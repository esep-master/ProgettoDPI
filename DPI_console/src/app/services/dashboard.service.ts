import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseServiceEnum, DashboardServiceEnum } from '../utility/enums';
import { ResponseReader } from '../utility/response-reader';
import { Util } from '../utility/util';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class DashboardService extends BaseService {

  constructor(protected http: HttpClient) {
    super(http)
  }

  getInfo(): Observable<ResponseReader> {
    return this.doGet(Util.buildEnpoint(BaseServiceEnum.DASHBOARD, DashboardServiceEnum.INFO))
  }
}

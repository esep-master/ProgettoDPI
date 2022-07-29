import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseServiceEnum } from '../utility/enums';
import { ResponseReader } from '../utility/response-reader';
import { Util } from '../utility/util';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService extends BaseService {

  constructor(protected http: HttpClient) {
    super(http)
  }

  doLogIn(payload:any): Observable<ResponseReader> {
    return this.doPost(BaseServiceEnum.LOGIN, payload)
  }
}

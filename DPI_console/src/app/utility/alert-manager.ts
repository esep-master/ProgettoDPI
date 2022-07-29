import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { MessageService } from "primeng/api";
import { IResponseError } from "../interfaces/IResponseError";
import { ErrorCodeEnum } from "./enums";
import { ResponseReader } from "./response-reader";

@Injectable({
  providedIn: 'root'
})

export class AlertManager {

  constructor(private router: Router,
    private messageService: MessageService) {

  }

  public manageError(error: ResponseReader, msg: any) {
    let errorResponseReader: IResponseError = error.getError()
    let isLogged = !!localStorage.getItem("token")
    if(isLogged){
      this.messageService.add({
        severity: msg.severity,
        summary: msg.summary,
        detail: msg.detail
      });
    }
    if (errorResponseReader.code == ErrorCodeEnum.NOT_AUTHORIZED && isLogged) {
      this.logOut()
    }
  }

  logOut() {
    localStorage.clear()
    this.router.navigate(["login"])
  }

}

import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { finalize } from 'rxjs/operators';
import { ILoginResponse } from 'src/app/interfaces/ILoginResponse';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { LoginService } from 'src/app/services/login.service';
import { StateTreeService } from 'src/app/services/state-tree.service';
import { ResponseReader } from 'src/app/utility/response-reader';
import { Util } from 'src/app/utility/util';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  isWaitingLogInService: boolean = false

  credentialFormGroup = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', [Validators.required])
  })

  constructor(private messageService: MessageService,
    private loginService: LoginService,
    private router: Router,
    private stateTree: StateTreeService) { }

  ngOnInit(): void {
    let isLogged = !!localStorage.getItem("token")
    if (isLogged) {
      this.router.navigate(["dashboard"])
    }
  }

  onLogInProvvisorio() {
    let token = "provvisorio"
    localStorage.setItem("token", token)

    this.router.navigate(["home"])

  }

  onKeyPress(e: any) {
    if (e.key == "Enter") {
      this.onLogIn()
    }
  }

  onLogIn() {
    this.credentialFormGroup.disable()
    this.isWaitingLogInService = true

    let payload = this.credentialFormGroup.value

    this.loginService.doLogIn(payload)
      .pipe(
        finalize(() => {
          this.isWaitingLogInService = false
          this.credentialFormGroup.enable()
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<ILoginResponse>()
          if (data.token) {
            let token = data.token
            let configurazioni = JSON.stringify(data.configurazioni)
            let utente = JSON.stringify({
              id: data.user.id,
              username: data.user.username
            })
            let elencoCommesse = JSON.stringify(data.user.utenteSediCommesse)
            let ruolo = JSON.stringify(data.user.ruolo)
            localStorage.setItem("token", token)
            localStorage.setItem("configurazioni", configurazioni)
            localStorage.setItem("elencoCommesse", elencoCommesse)
            localStorage.setItem("utente", utente)
            localStorage.setItem("ruolo", ruolo)

            this.stateTree.write("permessi", Util.getPermessi(), true)

            this.router.navigate(["dashboard"])
          }
        },
        error: (error: ResponseReader) => {
          let errorResponseReader: IResponseError = error.getError()
          this.messageService.add({
            severity: "error",
            summary: "" + errorResponseReader.code,
            detail: errorResponseReader.message
          });
        }
      })
  }
}

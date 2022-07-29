import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { finalize } from 'rxjs/operators';
import { IAdmin } from 'src/app/interfaces/IAdmin';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { UtenteService } from 'src/app/services/utente.service';
import { ResponseReader } from 'src/app/utility/response-reader';

@Component({
  selector: 'app-aggiungi-admin-modal',
  templateUrl: './aggiungi-admin-modal.component.html',
  styleUrls: ['./aggiungi-admin-modal.component.css']
})
export class AggiungiAdminModalComponent implements OnInit {

  isUpdate: boolean = false
  admin: IAdmin

  form: FormGroup = new FormGroup(
    {
      nome: new FormControl('', Validators.required),
      cognome: new FormControl('', Validators.required),
      email: new FormControl('', Validators.required),
      numeroTelefono: new FormControl(''),
      username: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required),
      passwordConfirm: new FormControl(''),
    });

  constructor(public config: DynamicDialogConfig,
    public utenteService: UtenteService,
    public messageService: MessageService,
    public ref: DynamicDialogRef) {
    this.isUpdate = this.config.data.update
    if (this.isUpdate) {
      this.admin = this.config.data.admin
    }
  }

  ngOnInit(): void {
    if (this.isUpdate) {
      this.form.controls['nome'].setValue(this.admin.nome)
      this.form.controls['cognome'].setValue(this.admin.cognome)
      this.form.controls['email'].setValue(this.admin.email)
      this.form.controls['numeroTelefono'].setValue(this.admin.numeroTelefono)

      this.form.controls['username'].setValidators(null)
      this.form.controls['password'].setValidators(null)
      this.form.controls['username'].updateValueAndValidity()
      this.form.controls['password'].updateValueAndValidity()
    }
  }

  onSubmit() {
    if (this.isUpdate) {
      let modAdmin = {
        //username: this.form.controls['username'].value,
        email: this.form.controls['email'].value,
        nome: this.form.controls['nome'].value,
        cognome: this.form.controls['cognome'].value,
        numeroTelefono: this.form.controls['numeroTelefono'].value,
        idUtente: this.admin.id
      }
      this.updAdmin(modAdmin)
    } else {
      let nuovoAdmin = {
        idRuolo: 2,
        username: this.form.controls['username'].value,
        password: this.form.controls['password'].value,
        email: this.form.controls['email'].value,
        numeroTelefono: this.form.controls['numeroTelefono'].value,
        nome: this.form.controls['nome'].value,
        cognome: this.form.controls['cognome'].value
      }
      this.insAdmin(nuovoAdmin)
    }
  }

  insAdmin(nuovoAdmin: any) {
    this.utenteService.insUtente(nuovoAdmin).pipe(
      finalize(() => {
      }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          if (data) {
            this.ref.close(true)
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

  updAdmin(modAdmin: any) {
    this.utenteService.updUtente(modAdmin).pipe(
      finalize(() => {
      }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          if (data) {
            this.ref.close(true)
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

  isFormValid() {
    return this.form.valid && this.form.controls['password'].value == this.form.controls['passwordConfirm'].value
  }

}

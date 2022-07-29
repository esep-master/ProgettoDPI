import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { finalize } from 'rxjs/operators';
import { IOperatore } from 'src/app/interfaces/IOperatore';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { LookupService } from 'src/app/services/lookup.service';
import { OperatoriService } from 'src/app/services/operatori.service';
import { ResponseReader } from 'src/app/utility/response-reader';

@Component({
  selector: 'app-aggiungi-operatore-modal',
  templateUrl: './aggiungi-operatore-modal.component.html',
  styleUrls: ['./aggiungi-operatore-modal.component.css']
})
export class AggiungiOperatoreModalComponent implements OnInit {

  isUpdate: boolean = false
  // risoluzioneAutomatica: boolean = false
  operatore: IOperatore
  tipiOperatore: any[] = []

  form: FormGroup = new FormGroup(
    {
      matricola: new FormControl('', Validators.required),
      idDispositivo: new FormControl('', Validators.required),
      nominativo: new FormControl('', Validators.required),
      telefono: new FormControl(''),
      email: new FormControl(''),
      password: new FormControl('', Validators.required),
      passwordConfirm: new FormControl(''),
      // risoluzioneAutomatica: new FormControl('', Validators.required),//boolean
      // idTipoOperatore: new FormControl('', Validators.required),//number
    });

  constructor(public config: DynamicDialogConfig,
    public operatoreService: OperatoriService,
    public lookUpService: LookupService,
    public messageService: MessageService,
    public ref: DynamicDialogRef) {
    this.isUpdate = this.config.data.update
    if (this.isUpdate) {
      this.operatore = this.config.data.operatore
    }
  }

  ngOnInit(): void {
    // this.getTipiOperatori()
    if (this.isUpdate) {
      this.form.controls['idDispositivo'].setValue(this.operatore.idDispositivo)
      this.form.controls['matricola'].setValue(this.operatore.matricola)
      this.form.controls['nominativo'].setValue(this.operatore.nominativo)
      this.form.controls['telefono'].setValue(this.operatore.numeroTelefono)
      this.form.controls['email'].setValue(this.operatore.email)
      // this.form.controls['risoluzioneAutomatica'].setValue(this.operatore.risoluzioneAutomatica)
      // this.form.controls['idTipoOperatore'].setValue(this.operatore.email)

      this.form.controls['password'].setValidators(null)
      this.form.controls['password'].updateValueAndValidity()
    }
  }

  onSubmit() {
    if (this.isUpdate) {
      let modOperatore = {
        matricola: this.form.controls['matricola'].value,
        idDispositivo: this.form.controls['idDispositivo'].value,
        nominativo: this.form.controls['nominativo'].value,
        email: this.form.controls['email'].value,
        numeroTelefono: this.form.controls['telefono'].value,
        // risoluzioneAutomatica: this.risoluzioneAutomatica,
        idOperatore: this.operatore.id
      }
      this.updOperatore(modOperatore)
    } else {
      let nuovoOperatore = {
        matricola: this.form.controls['matricola'].value,
        idDispositivo: this.form.controls['idDispositivo'].value,
        nominativo: this.form.controls['nominativo'].value,
        numeroTelefono: this.form.controls['telefono'].value,
        email: this.form.controls['email'].value,
        password: this.form.controls['password'].value,
        risoluzioneAutomatica: false,
        idTipoOperatore: 1,//non CMR
      }
      this.insOperatore(nuovoOperatore)
    }
  }

  updOperatore(payload: any) {
    this.operatoreService.updOperatore(payload).pipe(
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

  insOperatore(payload: any) {
    this.operatoreService.insOperatore(payload).pipe(
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

  // getTipiOperatori() {
  //   this.lookUpService.getTipoOperatore().pipe(
  //     finalize(() => {
  //     }))
  //     .subscribe({
  //       next: (response: ResponseReader) => {
  //         let data = response.getData<any>()
  //         if (data) {
  //           this.tipiOperatore = data
  //         }
  //       },
  //       error: (error: ResponseReader) => {
  //         let errorResponseReader: IResponseError = error.getError()
  //         this.messageService.add({
  //           severity: "error",
  //           summary: "" + errorResponseReader.code,
  //           detail: errorResponseReader.message
  //         });
  //       }
  //     })
  // }
}

import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { finalize } from 'rxjs/operators';
import { ICommessa } from 'src/app/interfaces/ICommessa';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { ISettore } from 'src/app/interfaces/ISettore';
import { CommessaService } from 'src/app/services/commessa.service';
import { SettoriService } from 'src/app/services/settori.service';
import { ResponseReader } from 'src/app/utility/response-reader';

@Component({
  selector: 'app-aggiungi-commessa-modal',
  templateUrl: './aggiungi-commessa-modal.component.html',
  styleUrls: ['./aggiungi-commessa-modal.component.css']
})
export class AggiungiCommessaModalComponent implements OnInit {

  isUpdate: boolean = false
  commessa: any

  settoriList: ISettore[] = []

  form: FormGroup = new FormGroup(
    {
      nome: new FormControl('', Validators.required),
      settore: new FormControl('', Validators.required)
    });

  constructor(public config: DynamicDialogConfig,
    public commessaService: CommessaService,
    public settoriService: SettoriService,
    public messageService: MessageService,
    public ref: DynamicDialogRef) {
    this.isUpdate = this.config.data.update
    if (this.isUpdate) {
      this.commessa = this.config.data.commessa
    }
  }

  ngOnInit(): void {
    if (this.isUpdate) {
      this.form.controls['nome'].setValue(this.commessa.nome)
      this.form.controls['settore'].setValue(this.commessa.idSettore)
    }
    this.loadSettori()
  }

  loadSettori() {
    this.settoriList = []
    this.settoriService.getListaSettori()
      .pipe(
        finalize(() => {
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<ISettore[]>()
          if (data) {
            this.settoriList = data
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
    return this.form.valid
  }

  onSubmit() {
    let payload
    if (this.isUpdate) {
      payload = {
        idCommessa: this.commessa.id,
        nome: this.form.controls['nome'].value,
        idSettore: this.form.controls['settore'].value
      }
    } else {
      payload = {
        nome: this.form.controls['nome'].value,
        idSettore: this.form.controls['settore'].value
      }
    }
    this.commessaService.saveCommessa(payload)
      .pipe(
        finalize(() => {
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          this.ref.close(true)
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

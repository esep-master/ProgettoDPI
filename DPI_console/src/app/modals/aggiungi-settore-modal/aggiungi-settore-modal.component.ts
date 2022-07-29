import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { finalize } from 'rxjs/operators';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { ISettore } from 'src/app/interfaces/ISettore';
import { SettoriService } from 'src/app/services/settori.service';
import { ResponseReader } from 'src/app/utility/response-reader';

@Component({
  selector: 'app-aggiungi-settore-modal',
  templateUrl: './aggiungi-settore-modal.component.html',
  styleUrls: ['./aggiungi-settore-modal.component.css']
})
export class AggiungiSettoreModalComponent implements OnInit {

  settore: ISettore
  nome: string
  isUpdate: boolean = false

  constructor(public config: DynamicDialogConfig,
    public settoreService: SettoriService,
    public messageService: MessageService,
    public ref: DynamicDialogRef) {
    this.isUpdate = this.config.data.update
    if (this.isUpdate) {
      this.settore = this.config.data.settore
      this.nome = this.settore.nome
    }
  }

  ngOnInit(): void {
  }

  onSubmit() {
    if (this.isUpdate) {
      let payload = {
        id: this.settore.id,
        nome: this.nome
      }
      this.updSettore(payload)
    } else {
      let payload = {
        nome: this.nome
      }
      this.insSettore(payload)
    }
  }

  insSettore(payload: any) {
    this.settoreService.saveSettore(payload).pipe(
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

  updSettore(payload: any) {
    this.settoreService.updateSettore(payload).pipe(
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

}

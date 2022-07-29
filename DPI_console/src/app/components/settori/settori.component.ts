import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DialogService } from 'primeng/dynamicdialog';
import { Table } from 'primeng/table';
import { finalize } from 'rxjs/operators';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { ISettore } from 'src/app/interfaces/ISettore';
import { AggiungiSettoreModalComponent } from 'src/app/modals/aggiungi-settore-modal/aggiungi-settore-modal.component';
import { SettoriService } from 'src/app/services/settori.service';
import { ResponseReader } from 'src/app/utility/response-reader';

@Component({
  selector: 'app-settori',
  templateUrl: './settori.component.html',
  styleUrls: ['./settori.component.css']
})
export class SettoriComponent implements OnInit {

  settoriList: ISettore[]
  loading: boolean = false

  constructor(public settoriService: SettoriService,
    public messageService: MessageService,
    public confirmationService: ConfirmationService,
    public dialogService: DialogService,
    public translate: TranslateService) { }

  ngOnInit(): void {
    this.loadListaSettori()
  }

  clear(table: Table) {
    table.clear();
    table.el.nativeElement.querySelector('.p-inputtext').value = ''
  }

  loadListaSettori() {
    this.settoriList = []
    this.loading = true
    this.settoriService.getListaSettori()
      .pipe(
        finalize(() => {
          this.loading = false
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

  onInsSettore() {
    let header
    this.translate.get("OPERATORI_CMP.AGGIUNGI_MODAL").subscribe((res: any) => {
      header = res
    })
    const ref = this.dialogService.open(AggiungiSettoreModalComponent, {
      data: {
        update: false
      },
      header: header,
      width: '40%'
    });
    ref.onClose.subscribe((data: any) => {
      if (data) {
        this.loadListaSettori()
        this.messageService.add({
          severity: "success",
          summary: "Settore inserito con successo",
          detail: ""
        });
      }
    });
  }

  onUpdateSettore(settore: ISettore) {
    let header
    this.translate.get("OPERATORI_CMP.MODIFICA_MODAL").subscribe((res: any) => {
      header = res
    })
    const ref = this.dialogService.open(AggiungiSettoreModalComponent, {
      data: {
        update: true,
        operatore: settore
      },
      header: header,
      width: '40%'
    });
    ref.onClose.subscribe((data: any) => {
      if (data) {
        this.loadListaSettori()
        this.messageService.add({
          severity: "success",
          summary: "Settore modificato con successo",
          detail: ""
        });
      }
    });
  }

  onDeleteSettore(idSettore: number) {
    this.translate.get('OPERATORI_CMP.CONFERMA_DEL').subscribe((res: any) => {
      this.confirmationService.confirm({
        message: res,
        acceptLabel: "Si",
        accept: () => {
          this.settoriService.deleteSettore(idSettore)
            .pipe(
              finalize(() => {
                this.loading = false
              }))
            .subscribe({
              next: (response: ResponseReader) => {
                this.loadListaSettori()
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
      })
    })
  }

}

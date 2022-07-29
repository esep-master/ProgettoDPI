import { Component, HostListener, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DialogService } from 'primeng/dynamicdialog';
import { Table } from 'primeng/table';
import { finalize } from 'rxjs/operators';
import { IOperatore } from 'src/app/interfaces/IOperatore';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { AggiungiOperatoreModalComponent } from 'src/app/modals/aggiungi-operatore-modal/aggiungi-operatore-modal.component';
import { AssociaCommesseModalComponent } from 'src/app/modals/associa-commesse-modal/associa-commesse-modal.component';
import { OperatoriService } from 'src/app/services/operatori.service';
import { ResponseReader } from 'src/app/utility/response-reader';
import * as FileSaver from 'file-saver';
import { Util } from 'src/app/utility/util';
import { LookupService } from 'src/app/services/lookup.service';

@Component({
  selector: 'app-operatori',
  templateUrl: './operatori.component.html',
  styleUrls: ['./operatori.component.css']
})
export class OperatoriComponent implements OnInit {

  operatoriList: IOperatore[] = []
  loading: boolean = false
  display: boolean = false

  idOperatoreSelected: number
  password: string
  passwordConfrim: string

  tipiOperatori: any[] = []
  tipiOperatoriSelected: any[] = [];

  /* Modifica grafica */
  showTextButton: boolean = true;

  constructor(public messageService: MessageService,
    public confirmationService: ConfirmationService,
    public lookupService: LookupService,
    public dialogService: DialogService,
    public operatoriService: OperatoriService,
    public translate: TranslateService) { }

  ngOnInit(): void {
    this.getTipiOperatori()
    this.loadLista()
  }

  clear(table: Table) {
    table.clear();
    table.el.nativeElement.querySelector('.p-inputtext').value = ''
  }

  getTipiOperatori() {
    this.lookupService.getTipoOperatore().pipe(
      finalize(() => {
      }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          if (data) {
            this.tipiOperatori = data
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

  onAddOperatore() {
    let header
    this.translate.get("OPERATORI_CMP.AGGIUNGI_MODAL").subscribe((res: any) => {
      header = res
    })
    const ref = this.dialogService.open(AggiungiOperatoreModalComponent, {
      data: {
        update: false
      },
      styleClass: "minWidth800",
      header: header,
      width: '60%'
    });
    ref.onClose.subscribe((data: any) => {
      if (data) {
        this.loadLista()
        this.messageService.add({
          severity: "success",
          summary: "Operaio inserito con successo",
          detail: ""
        });
      }
    });
  }

  onAssociaCommesse(operatore: IOperatore) {
    let header
    this.translate.get("ADMIN_CMP.ASS_COMMESSE_MODAL").subscribe((res: any) => {
      header = res
    })
    const ref = this.dialogService.open(AssociaCommesseModalComponent, {
      data: {
        isAdmin: false,
        operatore: operatore
      },
      header: header,
      width: '40%'
    });
    ref.onClose.subscribe((data: any) => {
      this.loadLista()
    });
  }

  onUpdateOperatore(operatore: IOperatore) {
    let header
    this.translate.get("OPERATORI_CMP.MODIFICA_MODAL").subscribe((res: any) => {
      header = res
    })
    const ref = this.dialogService.open(AggiungiOperatoreModalComponent, {
      data: {
        update: true,
        operatore: operatore
      },
      header: header,
      styleClass: "minWidth800",
      width: '45%'
    });
    ref.onClose.subscribe((data: any) => {
      if (data) {
        this.loadLista()
        this.messageService.add({
          severity: "success",
          summary: "Operaio modificato con successo",
          detail: ""
        });
      }
    });
  }

  onResetPassword(idOperatore: number) {
    this.translate.get('OPERATORI_CMP.CONFERMA_RES').subscribe((res: any) => {
      this.confirmationService.confirm({
        message: res,
        acceptLabel: "Si",
        accept: () => {
          this.operatoriService.resetPassword(idOperatore).pipe(
            finalize(() => {
            }))
            .subscribe({
              next: (response: ResponseReader) => {
                let data = response.getData<any>()
                if (data) {
                }
                this.messageService.add({
                  severity: "success",
                  summary: "Password resettata con successo",
                  detail: ""
                });
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

  onUpdateOperatorePsw(idOperatore: number) {
    this.idOperatoreSelected = idOperatore
    this.display = true
  }

  onConfirm() {
    let modPswAdmin = {
      password: this.password,
      idOperatore: this.idOperatoreSelected
    }
    this.operatoriService.updOperatore(modPswAdmin).pipe(
      finalize(() => {
      }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          if (data) {
            this.display = false
            this.idOperatoreSelected = null
            this.password = null
            this.passwordConfrim = null
          }
          this.messageService.add({
            severity: "success",
            summary: "Password resettata con successo",
            detail: ""
          });
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

  onDeleteOperatore(idOperatore: number) {
    this.translate.get('OPERATORI_CMP.CONFERMA_DEL').subscribe((res: any) => {
      this.confirmationService.confirm({
        message: res,
        acceptLabel: "Si",
        accept: () => {
          this.operatoriService.delOperatore(idOperatore)
            .pipe(
              finalize(() => {
                this.loading = false
              }))
            .subscribe({
              next: (response: ResponseReader) => {
                this.loadLista()
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

  loadLista() {
    this.operatoriList = []
    this.loading = true
    this.operatoriService.getListaUtenti()
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<IOperatore[]>()
          if (data) {
            this.operatoriList = data
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

  disableChangePsw() {
    return this.password == this.passwordConfrim
  }

  getCommessa(operatore: IOperatore) {
    let elencoCommesse: string = ''
    if (operatore.operatoreSediCommesse) {
      operatore.operatoreSediCommesse.forEach(opSedComm => {
        if (opSedComm.sedeCommessa && opSedComm.sedeCommessa.commessa) {
          elencoCommesse += opSedComm.sedeCommessa.commessa.nome + ', '
        }
      })
    }
    operatore.elencoCommesse = elencoCommesse.slice(0, -2)
    return elencoCommesse.slice(0, -2)
  }

  exportExcel(table: any) {
    let selected = table.filteredValue ? table.filteredValue : table.value
    let formatted = this.formatValue(selected)
    Util.exportExcel(formatted, "Elenco_Operai")
  }

  formatValue(selected: any) {
    let formatted: any[] = []
    selected.forEach(elem => {
      let form = {
        nominativo: elem.nominativo,
        telefono: elem.numeroTelefono,
        email: elem.email,
        commessa: this.getCommessa(elem),
        tipo: elem.tipoOperatore.nome,
        idDispositivo: elem.idDispositivo,
        matricola: elem.matricola,
      }
      formatted.push(form)
    })
    return formatted
  }

  /* Modifiche grafica */
  @HostListener('window:resize', ['$event'])
  getScreenSize(event?) {
    if (window.innerWidth < 1290) {
      this.showTextButton = false;
    } else {
      this.showTextButton = true;
    }
  }

}

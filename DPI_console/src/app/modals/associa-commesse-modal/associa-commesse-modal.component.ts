import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { finalize } from 'rxjs/operators';
import { IUtenteSediCommesse } from 'src/app/interfaces/ILoginResponse';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { OperatoriService } from 'src/app/services/operatori.service';
import { UtenteService } from 'src/app/services/utente.service';
import { ResponseReader } from 'src/app/utility/response-reader';

@Component({
  selector: 'app-associa-commesse-modal',
  templateUrl: './associa-commesse-modal.component.html',
  styleUrls: ['./associa-commesse-modal.component.css']
})
export class AssociaCommesseModalComponent implements OnInit {

  // commesseSelected: any[]
  isAdminSection: boolean = false
  listaCommesse: any[] = []
  idAdmin: number
  idOperatore: number
  loading: boolean = false
  commesseAssociate: any[] = []

  constructor(public config: DynamicDialogConfig,
    public utenteService: UtenteService,
    public operatoreService: OperatoriService,
    public messageService: MessageService,
    public ref: DynamicDialogRef) {
    this.isAdminSection = this.config.data.isAdmin
    if (this.isAdminSection) {
      this.idAdmin = this.config.data.admin.id
      this.commesseAssociate = this.config.data.admin.utenteSediCommesse
    } else {
      this.idOperatore = this.config.data.operatore.id
      this.commesseAssociate = this.config.data.operatore.operatoreSediCommesse
    }
  }

  ngOnInit(): void {
    this.loadCommesseFromService()
  }

  loadCommesseFromService() {

    this.loading = true

    this.utenteService.getListaCommesse().subscribe({
      next: (response: ResponseReader) => {

        let commesse: IUtenteSediCommesse[] = response.getData<IUtenteSediCommesse[]>()
        // localStorage.setItem("elencoCommesse", JSON.stringify(commesse))
        this.addCommesseToTable(commesse)


        this.loading = false

      }
    })

  }

  addCommesseToTable(rawList:IUtenteSediCommesse[]) {
    let listaAppoggio: any[] = []
    rawList.forEach(utenteSedeCommessa => {
      let item = {
        id: utenteSedeCommessa.sedeCommessa.id,
        nome: utenteSedeCommessa.sedeCommessa.commessa.nome,
        associata: false
      }
      if (this.commesseAssociate.length > 0) {
        this.commesseAssociate.forEach(commessaAss => {
          if (commessaAss.sedeCommessa.id == utenteSedeCommessa.sedeCommessa.id) {
            item.associata = true
          }
        })
      }
      listaAppoggio.push(item)
    });
    this.listaCommesse = listaAppoggio
  }

  onDisAssocia(commessa: any) {
    if (commessa.associata) {
      if (this.isAdminSection) {
        this.disassociaCommessaAdmin(commessa.id)
      } else {
        this.disassociaCommessaOperatore(commessa.id)
      }
    } else {
      if (this.isAdminSection) {
        this.associaCommessaAdmin(commessa.id)
      } else {
        this.associaCommessaOperatore(commessa.id)
      }
    }
  }

  disassociaCommessaAdmin(idSedeCommessa: number) {
    this.loading = true
    let payload = {
      idSedeCommessa: idSedeCommessa,
      idUtente: this.idAdmin
    }
    this.utenteService.disassociaUtenteCommessa(payload)
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          this.listaCommesse.forEach(commessa => {
            if (commessa.id == idSedeCommessa) {
              commessa.associata = false
            }
          })
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

  associaCommessaAdmin(idSedeCommessa: number) {
    this.loading = true
    let payload = {
      idSedeCommessa: idSedeCommessa,
      idUtente: this.idAdmin
    }
    this.utenteService.associaUtenteCommessa(payload)
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          this.listaCommesse.forEach(commessa => {
            if (commessa.id == idSedeCommessa) {
              commessa.associata = true
            }
          })
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

  disassociaCommessaOperatore(idSedeCommessa: number) {
    this.loading = true
    let payload = {
      idSedeCommessa: idSedeCommessa,
      idOperatore: this.idOperatore
    }
    this.operatoreService.disassociaOperatoreCommessa(payload)
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          this.listaCommesse.forEach(commessa => {
            if (commessa.id == idSedeCommessa) {
              commessa.associata = false
            }
          })
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

  associaCommessaOperatore(idSedeCommessa: number) {
    this.loading = true
    let payload = {
      idSedeCommessa: idSedeCommessa,
      idOperatore: this.idOperatore
    }
    this.operatoreService.associaOperatoreCommessa(payload)
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          this.listaCommesse.forEach(commessa => {
            if (commessa.id == idSedeCommessa) {
              commessa.associata = true
            }
          })
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

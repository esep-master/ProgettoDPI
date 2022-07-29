import { Component, HostListener, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { DialogService } from 'primeng/dynamicdialog';
import { Table } from 'primeng/table';
import { AggiungiAdminModalComponent } from 'src/app/modals/aggiungi-admin-modal/aggiungi-admin-modal.component';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AssociaCommesseModalComponent } from 'src/app/modals/associa-commesse-modal/associa-commesse-modal.component';
import { UtenteService } from 'src/app/services/utente.service';
import { finalize } from 'rxjs/operators';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { ResponseReader } from 'src/app/utility/response-reader';
import { IAdmin } from 'src/app/interfaces/IAdmin';
import * as FileSaver from 'file-saver';
import { Util } from 'src/app/utility/util';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

  display: boolean = false
  idAdminSelected: number
  password: string
  passwordConfrim: string
  loading: boolean = false;
  adminList: IAdmin[] = [];

    /* Modifiche grafica */
    showTextButton: boolean = true;

  constructor(public dialogService: DialogService,
    public confirmationService: ConfirmationService,
    public messageService: MessageService,
    public utenteService: UtenteService,
    public translate: TranslateService) { }

  ngOnInit(): void {
    this.loadLista()
  }

  clear(table: Table) {
    table.clear();
    table.el.nativeElement.querySelector('.p-inputtext').value = ''
  }

  onAddAdmin() {
    let header
    this.translate.get("ADMIN_CMP.AGGIUNGI_MODAL").subscribe((res: any) => {
      header = res
    })
    const ref = this.dialogService.open(AggiungiAdminModalComponent, {
      data: {
        update: false
      },
      header: header,
      styleClass:"minWidth800",
      width: '50%'
    });
    ref.onClose.subscribe((data: any) => {
      if (data) {
        this.loadLista()
        this.messageService.add({
          severity: "success",
          summary: "Admin inserito con successo",
          detail: ""
        });
      }
    });
  }

  onUpdateAdmin(admin: IAdmin) {
    let header
    this.translate.get("ADMIN_CMP.MODIFICA_MODAL").subscribe((res: any) => {
      header = res
    })
    const ref = this.dialogService.open(AggiungiAdminModalComponent, {
      data: {
        update: true,
        admin: admin
      },
      header: header,
      styleClass:"minWidth800",
      width: '45%'
    });
    ref.onClose.subscribe((data: any) => {
      if (data) {
        this.loadLista()
        this.messageService.add({
          severity: "success",
          summary: "Admin modificato con successo",
          detail: ""
        });
      }
    });
  }

  onAssociaCommesse(admin: IAdmin) {
    let header
    this.translate.get("ADMIN_CMP.ASS_COMMESSE_MODAL").subscribe((res: any) => {
      header = res
    })
    const ref = this.dialogService.open(AssociaCommesseModalComponent, {
      data: {
        isAdmin: true,
        admin: admin
      },
      header: header,
      width: '40%'
    });
    ref.onClose.subscribe((data: any) => {
      this.loadLista()
    });
  }

  onUpdateAdminPsw(idAdmin: number) {
    this.idAdminSelected = idAdmin
    this.display = true
  }

  onConfirm() {
    let modPswAdmin = {
      password: this.password,
      idUtente: this.idAdminSelected
    }
    this.utenteService.updUtente(modPswAdmin).pipe(
      finalize(() => {
      }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          if (data) {
            this.display = false
            this.idAdminSelected = null
            this.passwordConfrim = null
            this.password = null
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

  onDeleteAdmin(idAdmin: number) {
    this.translate.get('ADMIN_CMP.CONFERMA_DEL').subscribe((res: any) => {
      this.confirmationService.confirm({
        message: res,
        acceptLabel: "Si",
        accept: () => {
          this.utenteService.delUtente(idAdmin)
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
    this.adminList = []
    this.loading = true
    this.utenteService.getListaUtenti()
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<IAdmin[]>()
          if (data) {
            this.adminList = data
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

  exportExcel(table: any) {
    let selected = table.filteredValue ? table.filteredValue : table.value
    let formatted = this.formatValue(selected)
    Util.exportExcel(formatted,"Elenco_Admin")
  }

  formatValue(selected: any) {
    let formatted: any[] = []
    selected.forEach(elem => {
      let form = {
        username: elem.username,
        email: elem.email,
        nome: elem.nome,
        cognome: elem.cognome,
        telefono: elem.numeroTelefono
      }
      formatted.push(form)
    })
    return formatted
  }
  
  /* Modifiche grafica */
  @HostListener('window:resize', ['$event'])
  getScreenSize(event?) {
    if (window.innerWidth < 1040) {
      this.showTextButton = false;
    } else {
      this.showTextButton = true;
    }
  }
}

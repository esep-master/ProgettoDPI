import { Component, HostListener, OnInit } from '@angular/core';
import { NavigationExtras, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { finalize } from 'rxjs/operators';
import { IKit } from 'src/app/interfaces/IKit';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { ISettore } from 'src/app/interfaces/ISettore';
import { KitService } from 'src/app/services/kit.service';
import { OperatoriService } from 'src/app/services/operatori.service';
import { SettoriService } from 'src/app/services/settori.service';
import { ResponseReader } from 'src/app/utility/response-reader';
import * as FileSaver from 'file-saver';
import { Util } from 'src/app/utility/util';

@Component({
  selector: 'app-kit-dpi',
  templateUrl: './kit-dpi.component.html',
  styleUrls: ['./kit-dpi.component.css']
})
export class KitDpiComponent implements OnInit {

  loading: boolean = false
  kitList: IKit[] = []
  settoriList: ISettore[] = []
  selectedSettori: number[] = []

  /* Modifiche grafica */
  showTextButton: boolean = true;

  constructor(private router: Router,
    public settoriService: SettoriService,
    public messageService: MessageService,
    public translate: TranslateService,
    public confirmationService: ConfirmationService,
    public operatoriService: OperatoriService,
    public kitService: KitService) { }

  ngOnInit(): void {
    this.loadLista()
    this.loadListaSettori()
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
            if (localStorage.getItem("fromDashboard")) {
              this.selectedSettori = [JSON.parse(localStorage.getItem("fromDashboard"))]
              localStorage.removeItem("fromDashboard")
            }
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

  clear(table: Table) {
    table.clear();
    table.el.nativeElement.querySelector('.p-inputtext').value = ''
  }

  onCreaKit() {
    let kit = localStorage.getItem("selectedKit")
    if (kit) { localStorage.removeItem("selectedKit") }
    this.router.navigate(["dpi", "crea-kit"])
  }

  onUpdateKit(kit: IKit) {
    kit.dpiKit.sort((a, b) => a.dpi.tipoDPI.id - b.dpi.tipoDPI.id)
    localStorage.setItem("selectedKit", JSON.stringify(kit))
    this.router.navigate(["dpi", "crea-kit"])
  }

  confirmDelete(kit: IKit) {
    this.translate.get('KIT_COMPONENT.CONFERMA_DEL').subscribe((res: any) => {
      this.confirmationService.confirm({
        message: res,
        acceptLabel: "Si",
        accept: () => {
          this.onDeleteKit(kit)
        }
      })
    })
  }

  onDeleteKit(kit: IKit) {
    this.loading = true
    this.kitService.deleteKit(kit.id)
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
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

  loadLista() {
    this.kitList = []
    this.loading = true
    this.kitService.getListaKit()
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<IKit[]>()
          if (data) {
            this.kitList = data
            // this.listaAdapter(data)
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

  disassociaKitAdOperatore(Kit: IKit) {
    let payload = {
      idOperatore: Kit.operatore.id,
      idKit: Kit.id
    }
    this.operatoriService.disassociaOperatoreKit(payload)
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
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

  exportExcel(table: any) {
    let selected = table.filteredValue ? table.filteredValue : table.value
    let formatted = this.formatValue(selected)
    Util.exportExcel(formatted, "Elenco_kit")
  }

  formatValue(selected: any) {
    let formatted: any[] = []
    selected.forEach(elem => {
      let form = {
        modello: elem.modello,
        operaio: elem.operatore ? elem.operatore.nominativo : '',
        settore: elem.settore.nome,
        note: elem.note
      }
      formatted.push(form)
    })
    return formatted
  }

  /* Modifiche grafica */
  @HostListener('window:resize', ['$event'])
  getScreenSize(event?) {
    if (window.innerWidth < 1200) {
      this.showTextButton = false;
    } else {
      this.showTextButton = true;
    }
  }

}

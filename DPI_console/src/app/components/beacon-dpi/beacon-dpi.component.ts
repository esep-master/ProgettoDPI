import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';
import { DialogService } from 'primeng/dynamicdialog';
import { Table } from 'primeng/table';
import { finalize } from 'rxjs/operators';
import { IDPI } from 'src/app/interfaces/IDPI';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { ISettore } from 'src/app/interfaces/ISettore';
import { AggiungiDPIModalComponent } from 'src/app/modals/aggiungi-dpimodal/aggiungi-dpimodal.component';
import { ModificaDpiModalComponent } from 'src/app/modals/modifica-dpi-modal/modifica-dpi-modal.component';
import { DpiService } from 'src/app/services/dpi.service';
import { SettoriService } from 'src/app/services/settori.service';
import { ResponseReader } from 'src/app/utility/response-reader';
import * as FileSaver from 'file-saver';
import { Util } from 'src/app/utility/util';
import { LookupService } from 'src/app/services/lookup.service';

@Component({
  selector: 'app-beacon-dpi',
  templateUrl: './beacon-dpi.component.html',
  styleUrls: ['./beacon-dpi.component.css']
})
export class BeaconDPIComponent implements OnInit {

  dpiList: IDPI[] = []
  dpiListProvvisorio: IDPI[] = []
  display: boolean = false
  loading: boolean = false
  tipoEliminazione: number = 2
  dpiSelected: IDPI

  settoriList: ISettore[] = []
  selectedSettori: number[] = []

  tipiDpi: any[] = []
  selectedTipiDpi: number[] = []

  constructor(public dialogService: DialogService,
    public dpiService: DpiService,
    public settoriService: SettoriService,
    public lookupService: LookupService,
    public messageService: MessageService,
    public translate: TranslateService) { }

  ngOnInit(): void {
    this.loadLista()
    this.loadListaSettori()
    this.getTipoDpi()
  }

  getTipoDpi() {
    this.lookupService.getTipoDpi()
      .pipe(
        finalize(() => {
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any[]>()
          if (data) {
            this.tipiDpi = data
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

  clear(table: Table) {
    table.clear();
    table.el.nativeElement.querySelector('.p-inputtext').value = ''
  }

  onCreaDpi() {
    let header
    this.translate.get("BEACON_DPI_CMP.AGGIUNGI_MODAL").subscribe((res: any) => {
      header = res
    })
    const ref = this.dialogService.open(AggiungiDPIModalComponent, {
      data: {
        update: false,
        elencoDpi: this.dpiList
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
          summary: "DPI inserito con successo",
          detail: ""
        });
      }
    });
  }

  onUpdateDpi(dpi: IDPI) {
    let header
    this.translate.get(["BEACON_DPI_CMP.UPDATE_MODAL", "BEACON_DPI_CMP.UPDATE_MODAL_BEACON"]).subscribe((res: any) => {
      header = res
    })
    const ref = this.dialogService.open(ModificaDpiModalComponent, {
      data: {
        dpi: dpi,
        elencoDpi: this.dpiList
      },
      header: header["BEACON_DPI_CMP.UPDATE_MODAL"] + ": " + dpi.modello + " | " + header["BEACON_DPI_CMP.UPDATE_MODAL_BEACON"] + ": " + dpi.beacon.seriale,
      styleClass: "minWidth800",
      width: '60%'
    });
    ref.onClose.subscribe((data: any) => {
      if (data) {
        this.loadLista()
        this.messageService.add({
          severity: "success",
          summary: "DPI modificato con successo",
          detail: ""
        });
      }
    });
  }

  loadLista() {
    this.dpiList = []
    this.loading = true
    this.dpiService.getListaDpi()
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<IDPI[]>()
          if (data) {
            //this.dpiList = data
            this.listaAdapter(data)
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

  listaAdapter(data: IDPI[]) {
    data.forEach(dpi => {
      dpi.marcaModello = dpi.marca + ' ' + dpi.modello

      dpi.idSettori = []
      dpi.settoriDPI.forEach(setDpi => {
        dpi.idSettori.push(setDpi.settore.id)
      })

      let scadenza = new Date(dpi.dataScadenza)
      let oggi = new Date()
      var diff = scadenza.getTime() - oggi.getTime();
      var diffDays = Math.ceil(diff / (1000 * 3600 * 24));
      dpi.scadenzaCalcolata = diffDays
      if (dpi.beacon) {
        if (dpi.beacon.livelloBatteria >= 75) {
          dpi.iconaBatt = "battFull"
        } else if (dpi.beacon.livelloBatteria < 75 && dpi.beacon.livelloBatteria >= 50) {
          dpi.iconaBatt = "battHigh"
        } else if (dpi.beacon.livelloBatteria < 50 && dpi.beacon.livelloBatteria >= 25) {
          dpi.iconaBatt = "battMed"
        } else {
          dpi.iconaBatt = "battLow"
        }
      }
    })
    data.sort(function (a: IDPI, b: IDPI) { return (a.tipoDPI.id >= b.tipoDPI.id) ? 1 : -1 })
    this.dpiList = data
    this.dpiListProvvisorio = data
  }

  onDeleteDpi(dpi: IDPI) {
    this.dpiSelected = dpi
    this.display = true
  }

  onConfirm() {
    let payload
    if (this.tipoEliminazione == 1) {
      payload = {
        idDpi: this.dpiSelected.id,
        idBeacon: 0
      }
    } else if (this.tipoEliminazione == 2) {
      payload = {
        idDpi: this.dpiSelected.id,
        idBeacon: this.dpiSelected.beacon.id
      }
    }
    this.dpiService.deleteDpi(payload)
      .pipe(
        finalize(() => {
          this.loading = false
          this.tipoEliminazione = 2
          this.dpiSelected = null
          this.display = false
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

  getElencoSettori(dpi: IDPI) {
    let elenco = dpi.settoriDPI.map(x => x.settore.nome).join(', ')
    return elenco
  }

  exportExcel(table: any) {
    let selected = table.filteredValue ? table.filteredValue : table.value
    let formatted = this.formatValue(selected)
    Util.exportExcel(formatted, "Elenco_DPI")
  }

  formatValue(selected: any) {
    let formatted: any[] = []
    selected.forEach(elem => {
      let form = {
        tipo: elem.tipoDPI.nome,
        codice: elem.codice,
        marcaModello: elem.marca ? elem.marca + ',' : '' + elem.modello,
        settori: this.getElencoSettori(elem),
        scadenza: elem.scadenzaCalcolata,
        seriale: elem.beacon.seriale,
        batteria: elem.beacon.livelloBatteria
      }
      formatted.push(form)
    })
    return formatted
  }

  filtroPerSettori(settoriSelezionati: number[]) {
    let elenco = []
    if (this.selectedSettori.length == 0) {
      this.dpiList = this.dpiListProvvisorio
    } else {
      this.dpiListProvvisorio.forEach(dpi => {
        let dpiFiltrato: boolean = true
        settoriSelezionati.forEach(settoreSel => {
          if (!dpi.idSettori.includes(settoreSel)) {
            dpiFiltrato = false
          }
        })
        if (dpiFiltrato) {
          elenco.push(dpi)
        }
      })
      this.dpiList = elenco
    }
  }

}

import { Component, HostListener, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { finalize } from 'rxjs/operators';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { ISettore } from 'src/app/interfaces/ISettore';
import { AllarmiService } from 'src/app/services/allarmi.service';
import { SettoriService } from 'src/app/services/settori.service';
import { StateTreeService } from 'src/app/services/state-tree.service';
import { StatiKitReportEnum, TipoAllarmeEnum } from 'src/app/utility/enums';
import { ResponseReader } from 'src/app/utility/response-reader';
import { Util } from 'src/app/utility/util';
import { FormatDatePipe } from 'src/app/pipes/format-date.pipe';
import { IUomoATerra } from 'src/app/interfaces/IUomoATerra';
import { IKitAllarmato } from 'src/app/interfaces/IKitAllarmato';
import { IDPIAllarmato } from 'src/app/interfaces/IDPIAllarmato';
import { LookupService } from 'src/app/services/lookup.service';
import { DialogService } from 'primeng/dynamicdialog';
import { DettaglioAlertComponent } from '../dettaglio-alert/dettaglio-alert.component';

@Component({
  selector: 'app-notifiche-alert',
  templateUrl: './notifiche-alert.component.html',
  styleUrls: ['./notifiche-alert.component.css']
})
export class NotificheAlertComponent implements OnInit {

  isWaitingService: boolean = false

  alertList: any[] = []
  alertStoricoList: any[] = []
  totalRecords: number = 0
  loading: boolean = false
  loadingStorico: boolean = false

  kits: IKitAllarmato[] = []
  kitsUomoATerra: IUomoATerra[] = []

  settoriList: ISettore[] = []
  selectedSettori: number[] = []

  headerGestioneAlert: string = ''
  display: boolean = false
  note: string = ''
  kitAllarmatoSelected: any

  tabIndex: number = 0

  sort: any

  dataStoricoSelected: Date
  maxDateValue: Date = new Date()

  tipiAllarmi: any[] = []
  selectedTipoAllarme: string[]
  elencoCommesse: any[] = []
  selectedCommesse: string[]
  statiAllarmeList: any[] = []
  selectedStatiAllarme: number[]
  tipiDpi: any[] = []
  selectedTipiDpi: any[]

  /* Modifiche grafica */
  showTextButton: boolean = true;

  constructor(
    public settoriService: SettoriService,
    public lookupService: LookupService,
    private dialogService: DialogService,
    public allarmiService: AllarmiService,
    private formatDatePipe: FormatDatePipe,
    public messageService: MessageService,
    private stateTree: StateTreeService) {
  }

  ngOnInit(): void {

    this.tabIndex = Number(localStorage.getItem("indexOfAlert"))

    this.loading = true
    this.listenAlarms()
    this.loadListaSettori()
    this.getStatiAllarme()
    this.getTipoAllarme()
    this.getTipoDpi()
  }

  listenAlarms() {

    // Sembra che uscendo dal componente, venga eliminata la subscription automaticamente
    // Su Prelios non succedeva così, bisognava fare l'unsubscribe a mano nell'ngOnDestroy, perché senno si sommavano i subscribe
    // Probabilmente perché era una versione precedente di Angular
    // Comunque tenere sotto controllo la cosa, se succede roba strana prova a fare l'unsubscribe

    this.stateTree.read("allarmi").subscribe({
      next: (allarmi: any[]) => {

        this.loading = false

        // Ordino di default tutti gli allarmi per data

        this.alertList = allarmi
        this.alertList.sort(function (a: any, b: any) { return (a.dataAllarme > b.dataAllarme) ? -1 : 1 })

        this.kits = []
        this.kitsUomoATerra = []

        // Popolo gli array di Uomo a terra, Kit e Dpi

        allarmi.forEach(allarme => {
          if (allarme.kit && allarme.tipoAllarme.id == TipoAllarmeEnum.UOMO_TERRA) {
            this.pushUomoATerra(allarme)
          } else if (allarme.kit && allarme.dpi) {
            this.pushKitDpiAllarmati(allarme)
          }
        })

        // Imposto default sorting della tabella dei DPI

        this.sort = JSON.parse(localStorage.getItem("alarmSorting")) || { field: "data", order: "-1" }

      }
    })
  }

  private pushUomoATerra(allarme): any {

    let uomoATerra: IUomoATerra = {
      idAllarme: allarme.id,
      modello: allarme.kit.modello,
      nominativo: allarme.kit.operatore.nominativo,
      settore: allarme.kit.settore.nome,
      settoreId: allarme.kit.settore.id,
      dataIntervento: allarme.intervento.dataInizio,
      commessa: allarme.intervento.sedeCommessa.commessa.nome,
      data: allarme.dataAllarme,
      latitudine: allarme.latitudine,
      longitudine: allarme.longitudine,
      latLong: allarme.latitudine + ' ' + allarme.longitudine,
      lavorazione: allarme.dataPresaInCarico != null
    }

    this.kitsUomoATerra.push(uomoATerra)

  }

  private pushKitDpiAllarmati(allarme): any {

    // Controllo se il Kit non esiste, se non esiste lo creo

    if (!this.kits.map(x => x.idKit).includes(allarme.kit.id)) {

      let kitAllarmato: IKitAllarmato = {
        idAllarme: allarme.id,
        idKit: allarme.kit.id,
        modello: allarme.kit.modello,
        //commessa: allarme.intervento ? allarme.intervento.sedeCommessa.commessa.nome : "",
        //dataInizioIntervento: allarme.intervento ? allarme.intervento.dataInizio : "",
        nominativo: allarme.kit.operatore.nominativo,
        settore: allarme.kit.settore.nome,
        settoreId: allarme.kit.settore.id,
        DPI: []
      }

      this.kits.push(kitAllarmato)

    }

    // Aggiungo sempre il DPI al Kit,
    // tanto se il Kit era nuovo l'ho creato per forza prima

    let DpiAllarmato: IDPIAllarmato = {
      idAllarme: allarme.id,
      idDpi: allarme.dpi.id,
      tipoAllarme: allarme.tipoAllarme.nome,
      codice: allarme.dpi.codice,
      data: allarme.dataAllarme,
      commessa: allarme.intervento ? allarme.intervento.sedeCommessa.commessa.nome : "",
      dataInizioIntervento: allarme.intervento ? allarme.intervento.dataInizio : "",
      tipoDpi: allarme.dpi.tipoDPI.nome,
      latitudine: allarme.latitudine,
      longitudine: allarme.longitudine,
      lavorazione: allarme.dataPresaInCarico != null
    }

    this.kits.find(x => x.idKit == allarme.kit.id).DPI.push(DpiAllarmato)

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

  onConfirm() {
    let payload = {
      idAllarme: this.kitAllarmatoSelected.idAllarme,
      note: this.note
    }
    if (this.kitAllarmatoSelected.lavorazione) { //chiudere
      this.chiusuraAllarme(payload)
    } else {//prendere in carico
      this.prendiInCarico(payload)
    }
    this.display = false
    this.note = ''
  }

  onGestisciAllarme(kitOdpi: any) {
    this.display = true
    this.kitAllarmatoSelected = kitOdpi
    if (kitOdpi.lavorazione) { //chiudere
      this.headerGestioneAlert = "Chiusura dell'allarme"
    } else {//prendere in carico
      this.headerGestioneAlert = "Presa in carico dell'allarme"
    }
  }

  chiusuraAllarme(payload: any) {
    this.isWaitingService = true
    this.allarmiService.chiusuraAllarme(payload).pipe(
      finalize(() => {
        this.isWaitingService = false
      }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          this.listenAlarms()
          if (data) {
            // this.loading = true
            this.ricaricaAllarmi()
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

  prendiInCarico(payload: any) {
    this.isWaitingService = true
    this.allarmiService.lavorazioneAllarme(payload).pipe(
      finalize(() => {
        this.isWaitingService = false
      }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          this.listenAlarms()
          if (data) {
            // this.loading = true
            this.ricaricaAllarmi()
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

  caricaStoricoAllarmi(forStorico?: boolean, dataFiltro?: Date) {
    if (forStorico) {
      this.loadingStorico = true
    } else {
      this.loading = true
    }
    let today = new Date();
    let lastWeek = dataFiltro ? dataFiltro : new Date(today.getFullYear(), today.getMonth(), today.getDate() - 7);
    this.dataStoricoSelected = lastWeek
    this.allarmiService.getAllarmi(forStorico, lastWeek).subscribe({
      next: (response: ResponseReader) => {

        let data: any[] = response.getData()
        let datiFiltrati = data.filter(x => !x.statoAllarme.statoFinale)

        this.stateTree.write("allarmi", datiFiltrati, true)
        if (forStorico) {
          this.loadingStorico = false
          this.alertStoricoList = data
          this.alertStoricoList.sort(function (a: any, b: any) { return (a.dataAllarme > b.dataAllarme) ? -1 : 1 })

          this.alertStoricoList.forEach(allarme => {
            if (allarme.intervento) {
              if (this.elencoCommesse.indexOf(allarme.intervento.sedeCommessa.commessa.nome) == -1) {
                this.elencoCommesse.push(allarme.intervento.sedeCommessa.commessa.nome)
              }
            }
          })
        }

      },
      error: (error: ResponseReader) => {

        console.log(error.getError())

      }
    })
  }

  ricaricaAllarmi() {
    this.loading = true
    this.allarmiService.getAllarmi().subscribe({
      next: (response: ResponseReader) => {

        let data: any[] = response.getData()
        let datiFiltrati = data.filter(x => !x.statoAllarme.statoFinale)

        this.stateTree.write("allarmi", datiFiltrati, true)

      },
      error: (error: ResponseReader) => {

        console.log(error.getError())

      }
    })
  }

  exportExcel(table: any) {
    let selected = table.filteredValue ? table.filteredValue : table.value
    let formatted = this.formatValue(selected)
    Util.exportExcel(formatted, "Storico_Allarmi")
  }

  formatValue(selected: any) {
    let formatted: any[] = []
    selected.forEach(elem => {
      let form = {
        tipoAllarme: elem.tipoAllarme.nome,
        dpi: elem.dpi ? elem.dpi.codice : '',
        kit: elem.kit ? elem.kit.modello : '',
        commessa: elem.intervento ? elem.intervento.sedeCommessa.commessa.nome : "",
        stato: elem.statoAllarme.nome,
        dataAllarme: this.formatDatePipe.transform(elem.dataAllarme),
        matricola: elem.intervento ? elem.intervento.operatore.matricola : "",
        operaio: elem.intervento ? elem.intervento.operatore.nominativo : "",
        latitudine: elem.latitudine,
        longitudine: elem.longitudine,
        presaInCaricoDa: elem.utentePresaInCarico ? elem.utentePresaInCarico.username : '',
        ChiusoDa: elem.utenteRisoluzione ? elem.utenteRisoluzione.username : '',
        dataRisoluzione: this.formatDatePipe.transform(elem.dataRisoluzione),
        dataInizioIntervento: this.formatDatePipe.transform(elem.intervento ? elem.intervento.dataInizio : ""),
        note: elem.note,
      }
      formatted.push(form)
    })
    return formatted
  }

  coordFormat(lat: any, long: any) {
    if ((!lat && !long) || (lat == 0 && long == 0)) {
      return "NA"
    } else {
      return parseFloat(lat).toFixed(5) + " " + parseFloat(long).toFixed(5)
    }
  }

  onSort(e) {
    localStorage.setItem("alarmSorting", JSON.stringify(e))
  }

  handleChange(e) {
    var index = e.index;
    if (index == 2) {
      this.caricaStoricoAllarmi(true, this.dataStoricoSelected ? this.dataStoricoSelected : null)
    }
  }

  getTipoAllarme() {
    this.lookupService.getTipoAllarme()
      .pipe(
        finalize(() => {
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any[]>()
          if (data) {
            this.tipiAllarmi = data
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

  getStatiAllarme() {
    this.lookupService.getStatiAllarme()
      .pipe(
        finalize(() => {
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any[]>()
          if (data) {
            this.statiAllarmeList = data
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

  dettaglioAllarme(allarme: any) {
    const ref = this.dialogService.open(DettaglioAlertComponent, {
      data: {
        allarme: allarme
      },
      header: 'Dettaglio Allarme',
      width: '75%'
    });
    ref.onClose.subscribe((data: any) => {
    });
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
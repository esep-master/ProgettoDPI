import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Table } from 'primeng/table';
import { finalize } from 'rxjs/operators';
import { ICommessa } from 'src/app/interfaces/ICommessa';
import { IDPI } from 'src/app/interfaces/IDPI';
import { IKit } from 'src/app/interfaces/IKit';
import { IUtenteSediCommesse } from 'src/app/interfaces/ILoginResponse';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { ISettore } from 'src/app/interfaces/ISettore';
import { ITipoDPI } from 'src/app/interfaces/ITipoDPI';
import { CommessaService } from 'src/app/services/commessa.service';
import { DpiService } from 'src/app/services/dpi.service';
import { KitService } from 'src/app/services/kit.service';
import { LookupService } from 'src/app/services/lookup.service';
import { OperatoriService } from 'src/app/services/operatori.service';
import { SedeCommessaService } from 'src/app/services/sede-commessa.service';
import { SettoriService } from 'src/app/services/settori.service';
import { StateTreeService } from 'src/app/services/state-tree.service';
import { UtenteService } from 'src/app/services/utente.service';
import { StatiKitReportEnum, StatoAllarmiEnum, TipoAllarmeEnum } from 'src/app/utility/enums';
import { ResponseReader } from 'src/app/utility/response-reader';
import * as FileSaver from 'file-saver';
import { DialogService } from 'primeng/dynamicdialog';
import { StoricoAllarmiKitComponent } from 'src/app/modals/storico-allarmi-kit/storico-allarmi-kit.component';
import { DettaglioKitComponent } from 'src/app/modals/dettaglio-kit/dettaglio-kit.component';
import { Util } from 'src/app/utility/util';
import { ValueConverter } from '@angular/compiler/src/render3/view/template';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css']
})
export class ReportComponent implements OnInit {

  loading: boolean = false
  kitList: IKit[] = []
  settoriList: ISettore[] = []
  selectedSettori: number[] = []
  selectedSettoriDpi: number[] = []
  alertList: any[] = []

  dpiList: IDPI[] = []
  dpiListProvvisorio: IDPI[] = []
  display: boolean = false
  loadingDPI: boolean = false

  elencoCommesse: any[] = []
  selectedCommesse: any[]
  idKitSelected: any

  alertOptions: any[] = [
    { id: true, nome: 'Si' },
    { id: false, nome: 'No' }
  ]
  selectedAlert: any[]
  tipiDpi: ITipoDPI[] = []
  selectedTipiDpi: number[]
  tipiAllarmi: any[] = []
  selectedTipoAllarme: string[]

  statiKitList: any[] = [
    { id: 1, nome: StatiKitReportEnum.KIT_OK },
    { id: 2, nome: StatiKitReportEnum.KIT_DISATTIVATO },
    { id: 3, nome: StatiKitReportEnum.PIU_DPI_ALLARME },
    { id: 4, nome: StatiKitReportEnum.UN_DPI_ALLARME }
  ]
  selectedStatiKit: number[]

  sediCommesse: any[]

  constructor(private router: Router,
    public settoriService: SettoriService,
    public messageService: MessageService,
    public translate: TranslateService,
    private sedeCommesseService: SedeCommessaService,
    private dialogService: DialogService,
    public confirmationService: ConfirmationService,
    public lookupService: LookupService,
    public dpiService: DpiService,
    public operatoriService: OperatoriService,
    public kitService: KitService,
    private stateTree: StateTreeService) { }

  ngOnInit(): void {
    this.getElencoCommesse(() => {
      this.loadLista()
    })
    this.loadListaDpi()
    this.loadListaSettori()
    this.getTipoDpi()
    this.getTipoAllarme()
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
            data.forEach(kit => {
              if (kit.operatore) {
                kit.associato = true
              } else {
                kit.associato = false
              }
            })
            this.kitList = data
            // this.getResponsabileKitFromCommessa()
            this.listenAlarms(true)
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

  // getResponsabileKitFromCommessa() {
  //   this.kitList.forEach(kit => {
  //     this.sediCommesse.forEach(sedeComm => {
  //       if (kit.operatore) {
  //         kit.operatore.operatoreSediCommesse.forEach(kitSedeComm => {
  //           if (kitSedeComm.sedeCommessa && kitSedeComm.sedeCommessa.id == sedeComm.id) {
  //             kit.responsabile = sedeComm.utenteSediCommesse
  //           }
  //         })
  //       }
  //     })
  //   })
  // }

  getResponsabili(responsabili: any[]) {
    let elenco = ''
    if (responsabili) {
      responsabili.forEach(resp => {
        if (resp.utente && resp.utente.ruolo.id == 2) {
          elenco += resp.utente.username + ', '
        }
      })
    }
    return elenco.slice(0, -2)
  }

  loadListaDpi() {
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
      if (dpi.dpiKit && dpi.dpiKit.length > 0) {
        dpi.associato = true
      }
      dpi.idSettori = []
      dpi.settoriDPI.forEach(setDpi => {
        dpi.idSettori.push(setDpi.settore.id)
      })

      let scadenza = new Date(dpi.dataScadenza)
      let oggi = new Date()
      var diff = Math.abs(scadenza.getTime() - oggi.getTime());
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
    this.listenAlarms(false)
  }

  getElencoSettori(dpi: IDPI) {
    let elenco = dpi.settoriDPI.map(x => x.settore.nome).join(', ')
    return elenco
  }

  getCommesse(kit: IKit) {
    let elencoCommesse: string = ''
    // kit.uomoATerraAlert = false
    kit.elencoCommesse = []
    if (kit.operatore) {
      kit.operatore.operatoreSediCommesse.forEach(utSedeCommessa => {
        elencoCommesse += utSedeCommessa.sedeCommessa.commessa.nome + ', '
        kit.elencoCommesse.push(utSedeCommessa.sedeCommessa.commessa.id)
      })
    }
    return elencoCommesse.slice(0, -2)
  }

  getSede(kit: IKit) {
    let sede: string = ''
    if (kit.operatore) {
      kit.operatore.operatoreSediCommesse.forEach(utSedeCommessa => {
        sede += utSedeCommessa.sedeCommessa.nome + ', '
      })
    }
    return sede.slice(0, -2)
  }



  listenAlarms(kit: boolean) {

    this.stateTree.read("allarmi").subscribe({
      next: (allarmi: any[]) => {

        this.alertList = allarmi
        // ciclo la lista degli alert e divido i kit nei due array in base al tipo(uomo a terra o non) escludento gli allarmi chiusi
        if (kit) {
          allarmi.forEach(allarme => {
            this.kitList.forEach(kit => {
              // kit.alertCount = 0
              if (allarme.kit && kit.id == allarme.kit.id) {
                if (allarme.tipoAllarme.id == TipoAllarmeEnum.UOMO_TERRA) {
                  kit.uomoATerraAlert = true
                } else {
                  if (!kit.alertCount) {
                    kit.alertCount = 1
                  } else {
                    kit.alertCount += 1
                  }
                }
              }
            })
          })
        } else {
          allarmi.forEach(allarme => {
            this.dpiList.forEach(dpi => {
              if (allarme.dpi && dpi.id == allarme.dpi.id) {
                dpi.tipoAllarme = allarme.tipoAllarme.nome
                dpi.ultimoStato = allarme.statoAllarme.nome
              }
            })
          })
        }

      }
    })

  }

  getStatoKit(kit: IKit) {
    let stato = StatiKitReportEnum.KIT_OK
    kit.statoKit = 1
    if (kit.alertCount && kit.alertCount == 1) {
      stato = StatiKitReportEnum.UN_DPI_ALLARME
      kit.statoKit = 4
    } else if (kit.alertCount && kit.alertCount > 1) {
      stato = StatiKitReportEnum.PIU_DPI_ALLARME
      kit.statoKit = 3
    }
    // if (kit.disattivato) {
    //   stato = 'red'
    //   kit.statoKit = 2
    // }
    return stato
  }

  getTipoDpi() {
    this.lookupService.getTipoDpi()
      .pipe(
        finalize(() => {
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<ITipoDPI[]>()
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

  getElencoCommesse(callback?: any) {
    this.sedeCommesseService.getListaSediCommessa()
      .pipe(
        finalize(() => {
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any[]>()
          if (data) {
            data.forEach(sedeCommessa => {
              this.elencoCommesse.push(sedeCommessa.commessa)
            })
            this.sediCommesse = data
            if (callback) {
              callback()
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

  exportExcel(table: any, kitTable?: boolean) {
    let selected = table.filteredValue ? table.filteredValue : table.value
    // let formatted = []
    if (kitTable) {
      Util.exportExcel(this.formatValueKit(selected), "Report_Kit")
    } else {
      Util.exportExcel(this.formatValueDpi(selected), "Report_DPI")
    }
  }

  formatValueKit(selected: any[]) {
    let formatted: any[] = []
    selected.forEach(elem => {
      let form = {
        idKit: elem.id,
        kit: elem.modello,
        commessa: this.getCommesse(elem),
        settore: elem.settore.nome,
        stato: this.getStatoKit(elem),
        uomoAterra: elem.uomoATerra ? 'Si' : 'No',
        sede: this.getSede(elem),
        // responsabile: this.getResponsabili(elem.responsabile),
        operatore: elem.operatore.nominativo
      }
      formatted.push(form)
    })
    return formatted
  }

  formatValueDpi(selected: any) {
    let formatted: any[] = []
    selected.forEach(elem => {
      let form = {
        idDpi: elem.id,
        codice: elem.codice,
        tipo: elem.tipoDPI.nome,
        settori: this.getElencoSettori(elem),
        batteria: elem.beacon.livelloBatteria + "%",
        giorniAllaScadenza: elem.scadenzaCalcolata + "giorni",
        allarme: elem.tipoAllarme,
        ultimoStato: elem.ultimoStato
      }
      formatted.push(form)
    })
    return formatted
  }

  modalStoricoAllarmi(idKit: number) {
    let elencoSelezionato: any[] = []
    this.alertList.forEach(alarm => {
      if (alarm.kit && alarm.kit.id == idKit) {
        elencoSelezionato.push(alarm)
      }
    })
    const ref = this.dialogService.open(StoricoAllarmiKitComponent, {
      data: {
        listaAllarmi: elencoSelezionato
      },
      header: 'Storico Allarmi',
      width: '40%'
    });
    ref.onClose.subscribe((data: any) => {
    });
  }

  modalStoricoAllarmiDpi(idDpi: number) {
    let elencoSelezionato: any[] = []
    this.alertList.forEach(alarm => {
      if (alarm.dpi && alarm.dpi.id == idDpi) {
        elencoSelezionato.push(alarm)
      }
    })
    const ref = this.dialogService.open(StoricoAllarmiKitComponent, {
      data: {
        listaAllarmi: elencoSelezionato
      },
      header: 'Storico Allarmi',
      width: '40%'
    });
    ref.onClose.subscribe((data: any) => {
    });
  }

  dettaglioKit(kit: IKit) {
    const ref = this.dialogService.open(DettaglioKitComponent, {
      data: {
        dpiKit: kit.dpiKit,
        kit: kit
      },
      header: 'Dettaglio Kit',
      width: '70%'
    });
    ref.onClose.subscribe((data: any) => {
    });
  }

  disableStoricoAllarmi(idKit) {
    let elencoSelezionato: any[] = []
    this.alertList.forEach(alarm => {
      if (alarm.kit && alarm.kit.id == idKit) {
        elencoSelezionato.push(alarm)
      }
    })
    return elencoSelezionato.length == 0
  }

  disableStoricoAllarmiDpi(idDpi) {
    let elencoSelezionato: any[] = []
    if (this.alertList) {
      this.alertList.forEach(alarm => {
        if (alarm.dpi && alarm.dpi.id == idDpi) {
          elencoSelezionato.push(alarm)
        }
      })
    }
    return elencoSelezionato.length == 0
  }

  filtroPerSettori(settoriSelezionati: number[]) {
    let elenco = []
    if (this.selectedSettoriDpi.length == 0) {
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

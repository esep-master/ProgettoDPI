import { Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmationService, LazyLoadEvent, MessageService } from 'primeng/api';
import { DialogService } from 'primeng/dynamicdialog';
import { Table } from 'primeng/table';
import { finalize } from 'rxjs/operators';
import { ICommessa } from 'src/app/interfaces/ICommessa';
import { IUtenteSediCommesse } from 'src/app/interfaces/ILoginResponse';
import { IOperatore } from 'src/app/interfaces/IOperatore';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { ISettore } from 'src/app/interfaces/ISettore';
import { AggiungiCommessaModalComponent } from 'src/app/modals/aggiungi-commessa-modal/aggiungi-commessa-modal.component';
import { DettaglioSquadraModalComponent } from 'src/app/modals/dettaglio-squadra-modal/dettaglio-squadra-modal.component';
import { CommessaService } from 'src/app/services/commessa.service';
import { SettoriService } from 'src/app/services/settori.service';
import { StateTreeService } from 'src/app/services/state-tree.service';
import { UtenteService } from 'src/app/services/utente.service';
import { AlertManager } from 'src/app/utility/alert-manager';
import { StatoAllarmiEnum, TipoAllarmeEnum } from 'src/app/utility/enums';
import { ResponseReader } from 'src/app/utility/response-reader';
import { Util } from 'src/app/utility/util';
import * as FileSaver from 'file-saver';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  commesseList: any[] = []
  loading: boolean = false
  totalRecords: number = 0;
  idUtente: number

  settoriList: ISettore[] = []
  selectedSettori: ISettore[] = []

  fromCRM: boolean = false

  alarmSub: Subscription

  responsabiliList: any[] = []
  selectedResponsabiliList: number[] = []

  /* Modifiche grafica */
  showTextButton: boolean = true;

  constructor(public dialogService: DialogService,
    public translate: TranslateService,
    public confirmationService: ConfirmationService,
    private router: Router,
    public settoriService: SettoriService,
    public messageService: MessageService,
    public alertManager: AlertManager,
    private utenteService: UtenteService,
    private commessaService: CommessaService,
    private stateTree: StateTreeService) {
    this.idUtente = JSON.parse(localStorage.getItem("utente")).id
    let configurazioni = JSON.parse(localStorage.getItem("configurazioni"))
    configurazioni.forEach(conf => {
      if (conf.id == 1 && conf.valore == "true") {
        this.fromCRM = true
      }
    });
  }

  ngOnInit(): void {
    //this.loadCommesseFromService()
    this.loadLista()
    this.loadListaSettori()
    //this.listenAlarms()
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

  goToAlert(commessa: any) {
    if (commessa.uomoATerra) {
      localStorage.setItem("indexOfAlert", "0")
    } else {
      localStorage.setItem("indexOfAlert", "1")
    }
    this.router.navigate(['alert', 'lista'])
  }

  goToSquadra(commessa: ICommessa) {//o id squadra
    let header
    this.getElencoOperatori(commessa.id, (elencoOperatori: any[]) => {
      this.translate.get("HOME_CMP.DETTAGLIO_SQUADRA").subscribe((res: any) => {
        header = res
      })
      const ref = this.dialogService.open(DettaglioSquadraModalComponent, {
        data: {
          elencoOperatori: elencoOperatori,
          commessa: commessa.nome
        },
        header: header,
        width: '90%'
      });
    })
  }

  getElencoOperatori(idCommessa: number, callback: any) {

    let elencoOperatori: IOperatore[] = []

    this.commessaService.getOperatoriCommessa(idCommessa).subscribe(
      (response: ResponseReader) => {
        elencoOperatori = response.getData<IOperatore[]>()
        callback(elencoOperatori)
      },
      (error: ResponseReader) => {
        console.log("Errore get operatori commessa: ", error)
        callback(elencoOperatori)
      }
    )
  }

  getListaAlertUtente(callback: any) {
    let alerts = []
    //TODO chiamata a servizio con idUtente
    // servizio ritorna gli alert delle commesse dell'utente loggato
    if (callback) {
      callback(alerts)
    }
  }

  onCreaCommessa() {
    let header
    this.translate.get("HOME_CMP.AGGIUNGI_MODAL").subscribe((res: any) => {
      header = res
    })
    const ref = this.dialogService.open(AggiungiCommessaModalComponent, {
      data: {
        update: false
      },
      header: header,
      styleClass: "minWidth800",
      width: '40%'
    });
    ref.onClose.subscribe((data: any) => {
      if (data) {
        this.loadLista()
        this.messageService.add({
          severity: "success",
          summary: "Commessa inserita con successo",
          detail: ""
        });
      }
    });
  }

  onUpdateCommessa(commessa: ICommessa) {
    let header
    this.translate.get("HOME_CMP.MODIFICA_MODAL").subscribe((res: any) => {
      header = res
    })
    const ref = this.dialogService.open(AggiungiCommessaModalComponent, {
      data: {
        update: true,
        commessa: commessa
      },
      styleClass: "minWidth800",
      header: header,
      width: '40%'
    });
    ref.onClose.subscribe((data: any) => {
      if (data) {
        this.loadLista()
        this.messageService.add({
          severity: "success",
          summary: "Commessa modificata con successo",
          detail: ""
        });
      }
    });
  }

  confirmDelete(commessa: ICommessa) {
    this.translate.get('HOME_CMP.CONFERMA_DEL').subscribe((res: any) => {
      this.confirmationService.confirm({
        message: res,
        acceptLabel: "Si",
        accept: () => {
          this.onDeleteCommessa(commessa)
        }
      })
    })
  }

  onDeleteCommessa(commessa: ICommessa) {
    this.loading = true
    this.commessaService.deleteCommessa(commessa.id)
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
    this.commesseList = []
    this.loading = true
    this.commessaService.getListaCommessa()
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any[]>()
          if (data) {
            //this.commesseList = data
            this.addCommesseToTableFromService(data)
            this.listenAlarms()
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

  addCommesseToTableFromService(commesse: any[]) {

    this.commesseList = []

    commesse.forEach(item => {

      // Recupero sedi e responsabili

      let sedi: string[] = []
      let responsabili: string[] = []
      let listaResponsabili: any[] = []

      item.sediCommessa.forEach(sede => {

        sedi.push(sede.nome)

        let USC = sede.utenteSediCommesse.filter(x => x.utente.ruolo.id == 2)

        USC.forEach(usc => {
          responsabili.push(usc.utente.nome + " " + usc.utente.cognome);
          listaResponsabili.push(usc.utente.cognome);
          if (this.responsabiliList.indexOf(usc.utente.cognome) == -1) {
            this.responsabiliList.push(usc.utente.cognome)
          }
        })
      })
      // Mappo l'oggetto

      let commessa = {
        id: item.id,
        nome: item.nome,
        sede: sedi.join(", "),
        settore: item.settore.nome,
        responsabili: responsabili.join(", "),
        listaResponsabili: listaResponsabili,
        idSettore: item.settore.id,
        alert: false
      }

      this.commesseList.push(commessa)

    })

    this.totalRecords = commesse.length
  }

  listenAlarms() {

    this.alarmSub = this.stateTree.read("allarmi").subscribe({
      next: (allarmi: any[]) => {

        allarmi.forEach(allarme => {
          this.commesseList.forEach(commessa => {
            if (allarme.intervento && allarme.intervento.sedeCommessa.commessa.id == commessa.id) {
              commessa.alert = true
              if (allarme.tipoAllarme.id == TipoAllarmeEnum.UOMO_TERRA) {
                commessa.uomoATerra = true
              }
            }

          })
        })

        this.commesseList.sort((a, b) => a.alert > b.alert ? -1 : 1)

      }
    })
  }

  clear(table: Table) {
    table.clear();
    table.el.nativeElement.querySelector('.p-inputtext').value = ''
  }


  exportExcel(table: any) {
    let selected = table.filteredValue ? table.filteredValue : table.value
    let formatted = this.formatValue(selected)
    Util.exportExcel(formatted, "Elenco_commesse")
  }

  formatValue(selected: any) {
    let formatted: any[] = []
    selected.forEach(elem => {
      let form = {
        nome: elem.nome,
        sede: elem.sede,
        responsabili: elem.responsabili,
        settore: elem.settore
      }
      formatted.push(form)
    })
    return formatted
  }

  ngOnDestroy() {
    if (this.alarmSub) this.alarmSub.unsubscribe()
  }

  // OBSOLETO
  // loadCommesseFromService() {

  //   this.loading = true

  //   this.utenteService.getListaCommesse().subscribe({
  //     next: (response: ResponseReader) => {

  //       let commesse: IUtenteSediCommesse[] = response.getData<IUtenteSediCommesse[]>()
  //       //localStorage.setItem("elencoCommesse", JSON.stringify(commesse))
  //       this.addCommesseToTable(commesse)

  //       this.listenAlarms()

  //       this.loading = false

  //     }
  //   })

  // }

  /* Modifiche grafica */
  @HostListener('window:resize', ['$event'])
  getScreenSize(event?) {
    if (window.innerWidth < 900) {
      this.showTextButton = false;
    } else {
      this.showTextButton = true;
    }
  }

}
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DialogService } from 'primeng/dynamicdialog';
import { Table } from 'primeng/table';
import { finalize } from 'rxjs/operators';
import { IDPI } from 'src/app/interfaces/IDPI';
import { IKit } from 'src/app/interfaces/IKit';
import { IOperatore } from 'src/app/interfaces/IOperatore';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { ISettore } from 'src/app/interfaces/ISettore';
import { KitService } from 'src/app/services/kit.service';
import { OperatoriService } from 'src/app/services/operatori.service';
import { SettoriService } from 'src/app/services/settori.service';
import { ResponseReader } from 'src/app/utility/response-reader';
import { AggiungiDPIModalComponent } from '../aggiungi-dpimodal/aggiungi-dpimodal.component';
import * as Enum from '../../utility/enums';
import { DpiService } from 'src/app/services/dpi.service';

@Component({
  selector: 'app-aggiungi-kit-modal',
  templateUrl: './aggiungi-kit-modal.component.html',
  styleUrls: ['./aggiungi-kit-modal.component.css']
})
export class AggiungiKitModalComponent implements OnInit {

  loading: boolean = false
  isUpdate: boolean = false
  kitUpdate: IKit = null

  display: boolean = false
  noteSbloccoTotale: string

  elencoOperai: IOperatore[] = []
  selectedOperaio: IOperatore
  elencoSettoriTotali: ISettore[] = []
  elencoSettori: ISettore[] = []
  selectedSettore: ISettore

  elencoTipiDPINecessari: any[] = []
  elencoDpi: IDPI[] = []
  selectedDpi: IDPI[] = []
  // selectedRisoluzioni: string[] = []
  nomeKit: string = ""
  note: string = ""

  check = false;
  listDpiNecessari = [];

  dpiList: IDPI[] = []

  constructor(private router: Router,
    public route: ActivatedRoute,
    public dialogService: DialogService,
    public messageService: MessageService,
    public confirmationService: ConfirmationService,
    public translate: TranslateService,
    public kitService: KitService,
    public settoriService: SettoriService,
    public dpiService: DpiService,
    public operatoriService: OperatoriService) { }

  ngOnInit(): void {

    this.kitUpdate = JSON.parse(localStorage.getItem("selectedKit"))

    if (this.kitUpdate) {

      this.isUpdate = true

      this.nomeKit = this.kitUpdate.modello
      this.note = this.kitUpdate.note
      //this.kitUpdate.dpiKit.sort((a, b) => a.dpi.tipoDPI.id - b.dpi.tipoDPI.id) // Fix 25-10-2021 AQ

    }
    this.loadListaOperatori()
    this.loadLista()
    // this.loadListaSettori()
  }

  onChangeOperaio(settoreUpdate?: ISettore) {

    if (this.selectedOperaio) {
      let arrayAppoggio = []
      this.elencoSettoriTotali.forEach(settore => {
        let isPresent = false
        this.selectedOperaio.kit.forEach(kit => {
          if (kit.settore && kit.settore.id == settore.id) {
            isPresent = true
          }
        })
        if (!isPresent) {
          arrayAppoggio.push(settore)
        }
      })
      if (settoreUpdate) {
        arrayAppoggio.push(settoreUpdate)
      }
      this.elencoSettori = arrayAppoggio
    }

    this.selectedSettore = null
  }

  loadListaOperatori() {
    this.elencoOperai = []
    this.loading = true
    this.operatoriService.getListaUtenti()
      .pipe(
        finalize(() => {
          // this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<IOperatore[]>()
          if (data) {
            this.elencoOperai = data
            if (this.kitUpdate && this.kitUpdate.operatore) this.selectedOperaio = this.elencoOperai.find(x => x.id == this.kitUpdate.operatore.id)
            this.loadListaSettori()
          }
        },
        error: (error: ResponseReader) => {
          this.loading = false
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
    this.elencoSettori = []
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
            this.elencoSettori = data
            this.elencoSettoriTotali = data
            if (this.kitUpdate) {
              this.selectedSettore = this.elencoSettori.find(x => x.id == this.kitUpdate.settore.id)
              this.onChangeSettore()
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

  onChangeSettore() {

    this.elencoTipiDPINecessari = []

    if (this.selectedOperaio && this.selectedSettore) {
      this.loadListaDpi()
    }

  }

  initFields(data: any) {

    if (this.isUpdate) {

      // MODALITA' UPDATE

      this.kitUpdate.dpiKit.forEach(dpiKit => {
        let change = false;
        this.elencoDpi.forEach((dpi: IDPI) => {
          // prima c'era 11 nella condiziona ma con l'aggiunta del dpi cuffie che ha id 11 la lista dei dpi
          // necessari non veniva valorizzata correttamente, inserendo 12 salto questo controllo come era prima
          // con 11
          if (dpiKit.dpi.tipoDPI.id == 12) {
            if (dpi.tipoDPI.id == dpiKit.dpi.tipoDPI.id) {
              change = true;
            } else {
              change = false;
            }
          } else {
            change = false;
          }
        })

        let tipo = {
          nomeIcona: dpiKit.dpi.tipoDPI.nomeIcona,
          id: dpiKit.dpi.tipoDPI.id,
          idDpi: change ? null : dpiKit.id,
          associato: change ? false : true,
          startDate: dpiKit.sbloccoAllarmeDa ? new Date(dpiKit.sbloccoAllarmeDa) : "",
          endDate: dpiKit.sbloccoAllarmeA ? new Date(dpiKit.sbloccoAllarmeA) : ""
        }

        // ELENCO DPI NECESSARI

        this.elencoTipiDPINecessari.push(tipo)
      })

      // PRE-SELEZIONE TABELLA

      let dpiToSelect = this.kitUpdate.dpiKit.map(x => x.dpi.id)
      this.selectedDpi = this.elencoDpi.filter(x => dpiToSelect.includes(x.id))

    } else {

      // MODALITA' CREAZIONE

      data.tipiDPISettore.forEach(tipoDPIset => {

        let tipo = {
          nomeIcona: tipoDPIset.nomeIcona,
          id: tipoDPIset.id,
          idDpi: null,
          associato: false,
          startDate: "",
          endDate: ""
        }

        this.elencoTipiDPINecessari.push(tipo)
      })

      this.selectedDpi = []
    }

  }

  clear(table: Table) {
    table.clear();
    table.el.nativeElement.querySelector('.p-inputtext').value = ''
  }

  loadListaDpi(callback?: any) {
    this.elencoDpi = []
    this.loading = true
    let payload = {
      idSettore: this.selectedSettore.id,
      idOperatore: this.selectedOperaio.id
    }
    this.kitService.getInfoKit(payload)
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {

          let data = response.getData<any>()

          if (data) {

            this.listaAdapter(data.dpiDisponibili)

            if (callback) {

              callback(data.tipiDPISettore)

            } else {

              this.initFields(data)

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

  listaAdapter(data: IDPI[]) {
    // se sono in inserimento l'utente vede tutti i dpi disponibili, anche cuffie e casci che non sono
    // associati tra di loro
    // se sono in modifica l'utente vede tutti i dpi disponibili e solo cuffie e caschi che sono associati
    // tra di loro
    if (this.kitUpdate) {
      // recupero la lista delle cuffie dai dpi disponibili
      let listaCuffieApp = []
      data.forEach(dpi => {
        if (dpi.tipoDPI.id == 11) {
          listaCuffieApp.push(dpi);
        }
      })
      // rimuovo tutte le cuffie dai dpi disponibili
      listaCuffieApp.forEach((dpi: IDPI) => {
        data.splice(data.findIndex(function (i) {
          return i.id === dpi.id;
        }), 1);
      })
      // controllo se ci sono delle cuffie che sono associate ad un casco e le recupero
      let app = []
      data.forEach((dpi: IDPI) => {
        listaCuffieApp.forEach((dpiCuffia: IDPI) => {
          let seriale = dpiCuffia.beacon.seriale.split('_C');
          if (dpi.beacon.seriale == seriale[0]) {
            app.push(dpiCuffia);
          }
        })
      })
      // inserirsco tutte le cuffie che sono disponibili e che sono associate ad un casco e le inserisco
      // tra i dispositivi disponibili
      app.forEach(el => {
        data.push(el);
      })
    }

    data.forEach(dpi => {

      dpi.giaUtilizzato = 0

      //evidenziazione dpi già utilizzato in un  altro kit
      this.selectedOperaio.kit.forEach(kit => {
        kit.dpiKit.forEach(dpiKit => {
          if (dpiKit.dpi.id == dpi.id) {
            dpi.giaUtilizzato = 1
          }
        })
      })

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

    data.sort((a, b) => b.giaUtilizzato - a.giaUtilizzato)

    this.elencoDpi = data
  }

  disableCheckTable(dpi: IDPI) {
    let tipoGiaPresente: boolean = false
    this.selectedDpi.forEach(dpiSel => {
      if ((dpi.tipoDPI.id == dpiSel.tipoDPI.id) && (dpi.id != dpiSel.id)) {
        tipoGiaPresente = true
      }
    })
    return tipoGiaPresente
  }

  /**
   * Metodo chiamato al click del dpi (selezione)
   * @param event dpi selezionato
   */
  onDpiSelect(event) {
    let newEntry = event.data
    // se il dpi selezionato è un casco
    if (newEntry.tipoDPI.id == 1) {
      this.associazioneCuffia(newEntry);
      // se il dpi selezionato è una cuffia
    } else if (newEntry.tipoDPI.id == 11) {
      this.associazioneCasco(newEntry);
    }
    // associo il dispositivo selezionato
    this.elencoTipiDPINecessari.forEach(dpiNecessario => {
      if ((newEntry.tipoDPI.id == dpiNecessario.id)) {
        dpiNecessario.idDpi = newEntry.id
        dpiNecessario.associato = true
      }
    })
    this.elencoTipiDPINecessari = this.elencoTipiDPINecessari
  }

  /**
   * Metodo che gestisce la selezione automatica delle cuffie quando viene selezionato un casco
   * @param newEntry casco selezionato
   */
  associazioneCuffia(newEntry) {
    // 1) recupero l'elenco delle cuffie
    let elencoDpiCuffie = []
    this.elencoDpi.forEach(el => {
      if (el.beacon.seriale.includes('_C')) {
        elencoDpiCuffie.push(el);
      }
    })
    // 2) controllo se c'è una cuffia che è associata al casco selezionata, se prensente la seleziono in automatico
    elencoDpiCuffie.forEach(el => {
      let seriale = el.beacon.seriale.split('_C');
      if (seriale[0] == newEntry.beacon.seriale) {
        let oldIndex = this.selectedDpi.findIndex((dpi: IDPI) => dpi.tipoDPI.nome === Enum.NomeDpiEnum.CUFFIE)
        if (oldIndex > -1) {
          this.selectedDpi.splice(oldIndex, 1);
        }
        // attivo il dpi cuffia con il suo relativo id
        this.elencoTipiDPINecessari.forEach(dpiNecessario => {
          if ((el.tipoDPI.id == dpiNecessario.id)) {
            dpiNecessario.idDpi = el.id
            dpiNecessario.associato = true
          }
        })
        this.selectedDpi.push(el)
        console.log(this.selectedDpi)
        this.messageService.add({
          severity: "success",
          summary: "Successo",
          detail: "Cuffia associata in automatico, se era già selezionata è stata cambiata con la cuffia associata al casco"
        });
      }
    })
    this.elencoTipiDPINecessari = this.elencoTipiDPINecessari
  }

  /**
   * Metodo che gestisce la selezione automatica del casco quando viene selezionato una cuffia
   * @param newEntry cuffia selezionato
   */
  associazioneCasco(newEntry) {
    // 1) recupero l'elenco dei caschi
    let elencoDpiCaschi = []
    this.elencoDpi.forEach(el => {
      if (el.tipoDPI.id == 1) {
        elencoDpiCaschi.push(el);
      }
    })
    // 2) controllo se c'è un casco che è associata alla cuffia selezionata, se prensente la seleziono in automatico
    let seriale = newEntry.beacon.seriale.split('_C');
    elencoDpiCaschi.forEach(el => {
      if (seriale[0] == el.beacon.seriale) {
        let oldIndex = this.selectedDpi.findIndex((dpi: IDPI) => dpi.tipoDPI.nome === Enum.NomeDpiEnum.CASCO)
        if (oldIndex > -1) {
          this.selectedDpi.splice(oldIndex, 1);
        }
        // attivo il dpi casco con il suo relativo id
        this.elencoTipiDPINecessari.forEach(dpiNecessario => {
          if ((el.tipoDPI.id == dpiNecessario.id)) {
            dpiNecessario.idDpi = el.id
            dpiNecessario.associato = true
          }
        })
        this.selectedDpi.push(el)
        this.messageService.add({
          severity: "success",
          summary: "Successo",
          detail: "Casco associato in automatico, se era già selezionato è stata cambiato con il casco associato alle cuffie"
        });
      }
    })
    this.elencoTipiDPINecessari = this.elencoTipiDPINecessari
  }

  /**
   * Metodo chiamato al click del dpi (deselezione)
   * @param event dpi selezionato
   */
  onDpiUnselect(event) {
    let rimosso = event.data
    if (rimosso.tipoDPI.id == 1) {
      this.elencoTipiDPINecessari.forEach(dpiNecessario => {
        if ((rimosso.tipoDPI.id == dpiNecessario.id)) {
          dpiNecessario.idDpi = null
          dpiNecessario.associato = false
        }
        if (dpiNecessario.id == 11) {
          dpiNecessario.idDpi = null
          dpiNecessario.associato = false
        }
      })
      this.messageService.add({
        severity: "info",
        summary: "Info",
        detail: "Deselezionando il casco vengono deselezionate anche le cuffie"
      });
    } else if (rimosso.tipoDPI.id == 11) {
      this.elencoTipiDPINecessari.forEach(dpiNecessario => {
        if ((rimosso.tipoDPI.id == dpiNecessario.id)) {
          dpiNecessario.idDpi = null
          dpiNecessario.associato = false
        }
        if (dpiNecessario.id == 1) {
          dpiNecessario.idDpi = null
          dpiNecessario.associato = false
        }
      })
      this.messageService.add({
        severity: "info",
        summary: "Info",
        detail: "Deselezionando le cuffie viene deselezionato anche il casco"
      });
    }
    this.elencoTipiDPINecessari.forEach(dpiNecessario => {
      if ((rimosso.tipoDPI.id == dpiNecessario.id)) {
        dpiNecessario.idDpi = null
        dpiNecessario.associato = false
      }
    })
    // chiamo il metodo per rimuovere i dpi dalla lista dei dpi selezionati
    this.removeScelectedDpi(rimosso.tipoDPI.id)
    this.elencoTipiDPINecessari = this.elencoTipiDPINecessari
  }

  /**
   * Metodo che rimuove il dpi selezionato dalla lista dei dpi selezionati, se è casco o cuffia rimuove ache
   * l'associazione
   * @param id dpi
   */
  removeScelectedDpi(id) {
    // id casco rimuovo l'associazione con le cuffie
    if (id == 1) {
      let index = this.selectedDpi.findIndex(x => x.tipoDPI.id === 11);
      if (index > -1) {
        this.selectedDpi.splice(index, 1);
      }
      // id cuffie rimuovo l'associazione con il casco
    } else if (id == 11) {
      let index = this.selectedDpi.findIndex(x => x.tipoDPI.id === 1);
      if (index > -1) {
        this.selectedDpi.splice(index, 1);
      }
    }
    // rimuovo il dpi selezionato della lista dei dpi selezionati
    let index = this.selectedDpi.findIndex(x => x.tipoDPI.id === id);
    if (index > -1) {
      this.selectedDpi.splice(index, 1);
    }
  }

  isValidForm() {
    let isValid: boolean = true

    if (!this.nomeKit) {
      isValid = false
    } else {
      this.elencoTipiDPINecessari.forEach(tipo => {
        if (!tipo.associato) {
          isValid = false
        }
      })
    }

    return isValid
  }

  copyCalendar(tipoId: number) {

    let startDate = this.elencoTipiDPINecessari.find(x => x.id == tipoId).startDate
    let endDate = this.elencoTipiDPINecessari.find(x => x.id == tipoId).endDate

    for (let i = 0; i < this.elencoTipiDPINecessari.length; i++) {
      this.elencoTipiDPINecessari[i].startDate = startDate
      this.elencoTipiDPINecessari[i].endDate = endDate
    }

  }

  resetCalendar(tipoId: number) {

    this.elencoTipiDPINecessari.find(x => x.id == tipoId).startDate = ""
    this.elencoTipiDPINecessari.find(x => x.id == tipoId).endDate = ""

  }

  /**
   * Click save button
   */
  onConferma() {
    // recupero il casco selezionato
    let dpiCascoSelected = this.selectedDpi.find((dpi: IDPI) => dpi.tipoDPI.nome === Enum.NomeDpiEnum.CASCO);
    // recupero le cuffie selezionate
    let dpiCuffiaSelected = this.selectedDpi.find((dpi: IDPI) => dpi.tipoDPI.nome === Enum.NomeDpiEnum.CUFFIE);
    if (dpiCascoSelected && dpiCuffiaSelected) {
      // se ci sono entrambi i dpi
      let serialeCuffia = dpiCuffiaSelected.beacon.seriale.split('_C');
      if (dpiCascoSelected.beacon.seriale == serialeCuffia[0]) {
        this.onSalva()
      } else {
        this.messageService.add({
          severity: "error",
          summary: "Errore",
          detail: "Casco e cuffie non coincidono"
        });
      }
    } else if (dpiCascoSelected && dpiCuffiaSelected == undefined) {
      // se c'è solo il casco
      this.confirmationService.confirm({
        message: 'Sei sicuro di voler inserire in kit senza le cuffie?',
        acceptLabel: "Si",
        accept: () => {
          this.onSalva()
        }
      })
    } else if (dpiCascoSelected == undefined && dpiCuffiaSelected) {
      // se ci sono solo le cuffie
      this.messageService.add({
        severity: "error",
        summary: "Errore",
        detail: "Non è possibile inserire una cuffia senza il casco"
      });
    } else {
      // controlli precedenti
      let equalStartDates = true
      let equalEndDates = true

      this.elencoTipiDPINecessari.map(x => x.startDate).forEach((v: Date, i: number, a: Date[]) => {
        if (!v || !a[0] || v.getTime() != a[0].getTime()) {
          equalStartDates = false
        }
      })

      if (equalStartDates && equalEndDates) {
        this.translate.get('ADD_KIT_COMPONENT.CONFIRM_MSG').subscribe((res: any) => {
          this.confirmationService.confirm({
            message: res,
            acceptLabel: "Si",
            accept: () => {
              this.onSalva()
            }
          })
        })
        this.display = true
      } else {
        this.onSalva()
      }
    }
  }

  onSalva() {

    this.loading = true
    this.display = false

    let listaDpi: any[] = []
    console.log(this.selectedDpi)
    this.selectedDpi.forEach((dpi: IDPI) => {

      let startDate: string = this.elencoTipiDPINecessari.find(x => x.id == dpi.tipoDPI.id).startDate
      let endDate: string = this.elencoTipiDPINecessari.find(x => x.id == dpi.tipoDPI.id).endDate

      listaDpi.push({
        idDPI: dpi.id,
        sbloccoAllarmeDa: startDate ? new Date(startDate).toISOString() : "",
        sbloccoAllarmeA: endDate ? new Date(endDate).toISOString() : ""
      })

    })

    let payload = {
      idKit: this.isUpdate ? this.kitUpdate.id : 0,
      idOperatore: this.selectedOperaio.id,
      idSettore: this.selectedSettore.id,
      modello: this.nomeKit,
      dataAssegnazione: new Date().toISOString(),
      note: this.note,
      noteSbloccoTotale: this.noteSbloccoTotale ? this.noteSbloccoTotale : "",
      listaDPI: listaDpi
    }

    this.kitService.saveKit(payload)
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          // if (data) {
          //   this.associaKitAdOperatore(data.id)
          // }
          if (this.isUpdate) {
            localStorage.removeItem("selectedKit")
          }
          this.router.navigate(['dpi/kit'])
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

  onCreaDpi() {
    let header
    this.translate.get("BEACON_DPI_CMP.AGGIUNGI_MODAL").subscribe((res: any) => {
      header = res
    })
    const ref = this.dialogService.open(AggiungiDPIModalComponent, {
      data: {
        update: false,
        settore: this.selectedSettore,
        elencoDpi: this.dpiList
      },
      styleClass: "minWidth800",
      header: header,
      width: '60%'
    });
    ref.onClose.subscribe((nuovoDpi: any) => {
      if (nuovoDpi) {

        let giaAssociato = false
        this.elencoTipiDPINecessari.forEach(tipo => {

          if (tipo.id == nuovoDpi.tipoDPI.id) {
            if (tipo.associato) {
              giaAssociato = true
            } else {
              tipo.idDpi = nuovoDpi.id
              tipo.associato = true
            }
          }
        })

        this.reloadDpiAndPreselect(nuovoDpi.id, giaAssociato)

        this.messageService.add({
          severity: "success",
          summary: "DPI inserito con successo",
          detail: ""
        });
      }
    });
  }

  reloadDpiAndPreselect(idNuovoDPI: number, giaAssociato: boolean) {
    this.loadListaDpi(() => {

      // PRE-SELEZIONE TABELLA

      let dpiToSelect = this.selectedDpi.map(x => x.id)
      if (!giaAssociato) dpiToSelect.push(idNuovoDPI)
      this.selectedDpi = this.elencoDpi.filter(x => dpiToSelect.includes(x.id))
    })
  }

  getSettori(dpi: IDPI) {
    let elencoSettori = ''
    dpi.settoriDPI.forEach(settore => {
      elencoSettori += settore.settore.nome + ', '
    })
    return elencoSettori.slice(0, -2)
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
            this.listaAdapterDPI(data)
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

  listaAdapterDPI(data: IDPI[]) {
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
    //this.dpiListProvvisorio = data
  }
}
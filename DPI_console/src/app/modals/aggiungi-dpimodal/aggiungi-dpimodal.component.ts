import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { finalize } from 'rxjs/operators';
import { IBeacon } from 'src/app/interfaces/IBeacon';
import { IDPI } from 'src/app/interfaces/IDPI';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { ISettore } from 'src/app/interfaces/ISettore';
import { ITipoBeacon } from 'src/app/interfaces/ITipoBeacon';
import { ITipoDPI } from 'src/app/interfaces/ITipoDPI';
import { DpiService } from 'src/app/services/dpi.service';
import { LookupService } from 'src/app/services/lookup.service';
import { SettoriService } from 'src/app/services/settori.service';
import { ResponseReader } from 'src/app/utility/response-reader';
import * as Enum from '../../utility/enums';

@Component({
  selector: 'app-aggiungi-dpimodal',
  templateUrl: './aggiungi-dpimodal.component.html',
  styleUrls: ['./aggiungi-dpimodal.component.css']
})
export class AggiungiDPIModalComponent implements OnInit {

  tipiDpi: ITipoDPI[] = []
  selectedTipoDpi: ITipoDPI
  tipiBeacon: ITipoBeacon[] = []
  selectedTipoBeacon: ITipoBeacon

  note: string = ""
  dataScadenza: Date
  mesiScadenza: number

  codice: string
  marca: string
  modello: string
  elencoBeaconDisponibili: IBeacon[] = []
  beacon: IBeacon
  serialeBeacon: string = ''

  elencoSettori: ISettore[] = []
  selectedSettore: number[] = []

  tabIndex: number = 0

  scanResult: any
  useQRCode: boolean = false

  checked: boolean = false

  preselectedSettore: ISettore

  disabledFirstPanel: boolean = false;
  loading = false;
  disabledSecondPanel: boolean = false;
  customElencoBeacon: IBeacon[] = []
  elencoDpiGenerale = []

  constructor(public dpiService: DpiService,
    public settoriService: SettoriService,
    public ref: DynamicDialogRef,
    public config: DynamicDialogConfig,
    public messageService: MessageService,
    public lookupService: LookupService) {
    this.preselectedSettore = this.config.data.settore
    this.elencoDpiGenerale = this.config.data.elencoDpi
  }

  ngOnInit(): void {
    this.loadTipiDpi()
    this.loadTipiBeacon()
    this.loadElencoSeriali()
    this.loadListaSettori()
  }

  onChangeMese() {
    let oggi = new Date()
    let dataScadenza = new Date()
    let giorno = oggi.getDate() //prendo il giorno del mese
    let mese = oggi.getMonth()
    let anno = oggi.getFullYear()
    let mesiScadenza = this.mesiScadenza
    if (giorno > 15) {
      mesiScadenza += 1
    } // se il giorno del mese è >15 imposto
    let annoCalcolato = anno + (mese + mesiScadenza) / 12
    let meseCalcolato = mese + mesiScadenza % 12
    dataScadenza.setDate(1)
    dataScadenza.setMonth(meseCalcolato)
    dataScadenza.setFullYear(annoCalcolato)
    this.dataScadenza = dataScadenza
  }

  loadListaSettori() {
    this.elencoSettori = []
    this.settoriService.getListaSettori()
      .pipe(
        finalize(() => {
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<ISettore[]>()
          if (data) {
            this.elencoSettori = data

            if (this.preselectedSettore) {
              this.selectedSettore.push(this.preselectedSettore.id)
              this.elencoSettori.forEach(settore => {
                settore.multiselectDisabled = settore.id == this.preselectedSettore.id
              })
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

  /**
   * Metodo che chiama l'API per recuperare l'elenco dei beacon disponibili
   * Tutti i seriali che sono censiti al DB(comprese cuffie) che non sono associati a dispositivo
   */
  loadElencoSeriali() {
    this.dpiService.getListaBeaconDisponibili()
      .pipe(
        finalize(() => {
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<IBeacon[]>()
          if (data) {
            this.elencoBeaconDisponibili = data
            // se la variabile selectedTipoDpi non è valorizzata significa che siamo appena entrati nel modale
            if (!this.selectedTipoDpi) {
              this.tabIndex = 0;
              this.disabledSecondPanel = true;
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

  loadTipiBeacon() {
    this.lookupService.getTipoBeacon()
      .pipe(
        finalize(() => {
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<ITipoBeacon[]>()
          if (data) {
            this.tipiBeacon = data
            this.selectedTipoBeacon = data[0]
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

  loadTipiDpi() {
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

  onSalva() {

    let payload = {
      idDPI: 0,
      codice: this.codice,
      dataScadenza: this.dataScadenza,
      marca: this.marca ? this.marca : '',
      modello: this.modello ? this.modello : '',
      note: this.note ? this.note : '',
      settoriDPI: this.selectedSettore,
      idTipoDPI: this.selectedTipoDpi.id
    }

    if (this.tabIndex == 0) {
      payload["beacon"] =
      {
        idBeacon: 0,
        seriale: this.serialeBeacon,
        idTipoBeacon: this.selectedTipoBeacon.id
      }
    } else if (this.tabIndex == 1) {
      payload["beacon"] =
      {
        idBeacon: this.beacon.id,
        seriale: this.beacon.seriale,
        idTipoBeacon: this.beacon.tipoBeacon.id
      }

    }


    this.dpiService.insUpdDpi(payload)
      .pipe(
        finalize(() => {
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          this.ref.close(data)
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

  isValidForm() {
    let isValid: boolean = false
    if (this.tabIndex == 1) {
      if (this.selectedTipoDpi && this.selectedSettore.length > 0 && this.codice && this.beacon && ((this.mesiScadenza != null && this.mesiScadenza >= 0) || this.checked)) {
        isValid = true
      }
    } else {
      if (this.selectedTipoDpi && this.selectedSettore.length > 0 && this.codice && this.serialeBeacon && ((this.mesiScadenza != null && this.mesiScadenza >= 0) || this.checked)) {
        isValid = true
      }
    }
    return isValid
  }

  useCamera() {
    this.useQRCode = !this.useQRCode
  }

  onCodeResult(event: any) {
    this.scanResult = event
    this.serialeBeacon = event
    // console.log("code result " + JSON.stringify(event))
    //event il risultato del qrCode
  }

  onCamerasFound(event: any) {
    // console.log("camera found " + JSON.stringify(event))
    // [{"deviceId":"438cd43837e5d3b572fd857bd3745df30eceefe9840c78b3b735d731a909e2a0",
    // "label":"Integrated Webcam (0c45:6481)","kind":"videoinput",
    // "groupId":"732e83fc5bdf94b940ffdb099db447669f74944b6c6923820cfe26cf9d0446e0"}]
  }

  onChangeBeacon(e) {

    let blankString: string = this.serialeBeacon.split(":").join("")

    this.serialeBeacon = blankString                                        // Lavoro sempre sulla stringa senza :
      .split("")                                                            // Divido la stringa in array di caratteri
      .reduce((acc, val, i) => acc + (i && !(i % 2) ? ":" : "") + val, "")  // Inserisco : ogni 2 caratteri
      .toUpperCase()                                                        // Trasformo in maiuscolo
      .substring(0, 17)                                                     // Limito sempre a 17 caratteri
  }

  onNonScade(value) {
    if (value.checked) {
      let scadenza = new Date("9999-12-31T12:00:00")
      this.dataScadenza = scadenza
    }
  }

  /* Metodo che intercetta il change della selezione del DPI */
  onChangeTipiDpi() {
    // gestisce il cambio dell'icona del dpi
    this.loading = true;
    setTimeout(() => {
      this.loading = false
    }, 0)

    // se viene selezionata una cuffia
    if (this.selectedTipoDpi && this.selectedTipoDpi.nome == Enum.NomeDpiEnum.CUFFIE) {
      this.checkTabs(1, true, false);
      this.filtraCustomElencoBeacon(true);
      // se viene selezionata un dispositivo che non è una cuffia
    } else if (this.selectedTipoDpi && this.selectedTipoDpi.nome != Enum.NomeDpiEnum.CUFFIE) {
      this.checkTabs(0, false, false);
      this.filtraCustomElencoBeacon(false);
      // se non viene selezionato nessun dispositivo
    } else {
      this.checkTabs(0, false, true);
    }
  }

  /**
   * Metodo che gestiosce i tabs
   * @param tabIndex 
   * @param disabledFirstPanel 
   * @param disabledSecondPanel 
   */
  private checkTabs(tabIndex: number, disabledFirstPanel: boolean, disabledSecondPanel: boolean) {
    this.tabIndex = tabIndex;
    this.disabledFirstPanel = disabledFirstPanel;
    this.disabledSecondPanel = disabledSecondPanel;
  }

  /**
   * Metodo che gestisce la visualizzazione dei seriali per l'associazione con le cuffie
   */
  adaptListBeacon() {
    let elencoCuffieCustomFE = [];
    let elencoCuffieCustomBE = [];
    // recuperlo la lista delle cuffie create lato FE dall'elenco dei caschi e la lista delle cuffie presenti
    // nel db
    if (this.elencoDpiGenerale && this.elencoDpiGenerale.length > 0) {
      elencoCuffieCustomFE = this.createElencoCuffieFE();
      elencoCuffieCustomBE = this.createElencoCuffieBE();
      // valorizzo la lista dei beacon con il merge tra le due liste e prendendo con priorità quelle provenienti 
      // dal db
      this.customElencoBeacon = this.mergeElencoCuffieFEBE(elencoCuffieCustomFE, elencoCuffieCustomBE);
    }
    let elencoCuffieDispDB = [];
    // controllo se mi tornano beacon dei beacon disponibili
    if (this.elencoBeaconDisponibili && this.elencoBeaconDisponibili.length > 0) {
      elencoCuffieDispDB = this.createElencoCuffieDB();
    }
    // se ho dei dei beacon disponibili delle cuffie faccio un merge con i beacon creati precedentemente
    if (elencoCuffieDispDB.length > 0) {
      this.mergeElencoCuffie(elencoCuffieDispDB);
    }
  }

  /**
   * Controllo se la cuffia disponibile è già presente nell'elenco dei beacon, se non lo è viene aggiunta
   * @param elencoCuffieDispDB 
   */
  private mergeElencoCuffie(elencoCuffieDispDB: any[]) {
    elencoCuffieDispDB.forEach(cuffiaDB => {
      let index = this.customElencoBeacon.findIndex(x => x.seriale === cuffiaDB.seriale);
      if (index < 0) {
        this.customElencoBeacon.push(cuffiaDB);
      }
    })
  }

  /**
   * Merge tra le due liste di cuffie
   * @param elencoCuffieCustomFE 
   * @param elencoCuffieCustomBE 
   * @returns 
   */
  private mergeElencoCuffieFEBE(elencoCuffieCustomFE: any[], elencoCuffieCustomBE: any[]) {
    let elencoCuffie = [];
    elencoCuffieCustomBE.forEach(cuffiaDB => {
      let index = elencoCuffieCustomFE.findIndex(x => x.seriale === cuffiaDB.beacon.seriale);
      if (index > -1) {
        let newBeacon: IBeacon = {
          id: cuffiaDB.beacon.id,
          livelloBatteria: cuffiaDB.beacon.livelloBatteria,
          seriale: cuffiaDB.beacon.seriale,
          tipoBeacon: cuffiaDB.beacon.tipoBeacon,
        };
        elencoCuffie.push(newBeacon);
        elencoCuffieCustomFE.splice(index, 1);
      }
    })
    elencoCuffieCustomFE.forEach(el => {
      elencoCuffie.push(el);
    })
    return elencoCuffie;
  }

  /**
   * Recupero le cuffie presenti che mi tornano dai dpi disponibili
   * @returns 
   */
  private createElencoCuffieDB() {
    let elencoCuffieDispDB = []
    this.elencoBeaconDisponibili.forEach((el) => {
      if (el.seriale.includes('_C')) {
        elencoCuffieDispDB.push(el);
      }
    });
    return elencoCuffieDispDB;
  }

  /**
   * Creo una lista di cuffie con id 0 e prendendo il seriale dai caschi per l'associazione aggiungendo _C
   * @returns 
   */
  private createElencoCuffieFE() {
    let elencoCuffieCustom = [];
    this.elencoDpiGenerale.forEach((dpi: IDPI) => {
      if (dpi && dpi.tipoDPI.nome == Enum.NomeDpiEnum.CASCO) {
        let newBeacon: IBeacon = {
          id: 0,
          livelloBatteria: dpi.beacon.livelloBatteria,
          seriale: dpi.beacon.seriale + '_C',
          tipoBeacon: dpi.beacon.tipoBeacon,
        };
        elencoCuffieCustom.push(newBeacon);
      }
    });
    return elencoCuffieCustom;
  }

  /**
   * Recupero tutte le cuffie dal db
   * @returns 
   */
  private createElencoCuffieBE() {
    let elencoCuffieCustom = [];
    this.elencoDpiGenerale.forEach((dpi: IDPI) => {
      if (dpi && dpi.tipoDPI.nome == Enum.NomeDpiEnum.CUFFIE) {
        elencoCuffieCustom.push(dpi);
      }
    });
    return elencoCuffieCustom;
  }

  /**
   * Metodo per la gestione della lista dei seriali
   * @param isCuffiaSelected 
   */
  filtraCustomElencoBeacon(isCuffiaSelected: boolean) {
    this.customElencoBeacon = [];
    if (isCuffiaSelected) {
      this.adaptListBeacon();
    } else {
      // recupero tutti i beacon liberi dal DB escludendo le cuffie
      this.elencoBeaconDisponibili.forEach((el) => {
        if (!el.seriale.includes('_C')) {
          this.customElencoBeacon.push(el);
        }
      })
    }
  }
}



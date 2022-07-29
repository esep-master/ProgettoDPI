import { Component, OnInit } from '@angular/core';
import { DynamicDialogConfig } from 'primeng/dynamicdialog';
import { IDPI } from 'src/app/interfaces/IDPI';
import { IKit } from 'src/app/interfaces/IKit';
import { IKitDPI } from 'src/app/interfaces/IKitDPI';

@Component({
  selector: 'app-dettaglio-kit',
  templateUrl: './dettaglio-kit.component.html',
  styleUrls: ['./dettaglio-kit.component.css']
})
export class DettaglioKitComponent implements OnInit {

  loading: boolean = false
  elencoDpi: IKitDPI[]
  kit: IKit

  constructor(public config: DynamicDialogConfig) {
    this.elencoDpi = config.data.dpiKit
    this.kit = config.data.kit
  }

  ngOnInit(): void {
    this.listaAdapter(this.elencoDpi)
  }

  listaAdapter(data: IKitDPI[]) {
    data.forEach(dpiKit => {
      let scadenza = new Date(dpiKit.dpi.dataScadenza)
      let oggi = new Date()
      var diff = Math.abs(scadenza.getTime() - oggi.getTime());
      var diffDays = Math.ceil(diff / (1000 * 3600 * 24));
      dpiKit.dpi.scadenzaCalcolata = diffDays
      if (dpiKit.dpi.beacon) {
        if (dpiKit.dpi.beacon.livelloBatteria >= 75) {
          dpiKit.dpi.iconaBatt = "battFull"
        } else if (dpiKit.dpi.beacon.livelloBatteria < 75 && dpiKit.dpi.beacon.livelloBatteria >= 50) {
          dpiKit.dpi.iconaBatt = "battHigh"
        } else if (dpiKit.dpi.beacon.livelloBatteria < 50 && dpiKit.dpi.beacon.livelloBatteria >= 25) {
          dpiKit.dpi.iconaBatt = "battMed"
        } else {
          dpiKit.dpi.iconaBatt = "battLow"
        }
      }
    })
  }

}

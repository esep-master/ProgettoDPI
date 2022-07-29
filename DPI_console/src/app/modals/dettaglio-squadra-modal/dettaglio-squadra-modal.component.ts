import { Component, OnInit } from '@angular/core';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { IOperatore } from 'src/app/interfaces/IOperatore';

@Component({
  selector: 'app-dettaglio-squadra-modal',
  templateUrl: './dettaglio-squadra-modal.component.html',
  styleUrls: ['./dettaglio-squadra-modal.component.css']
})
export class DettaglioSquadraModalComponent implements OnInit {

  operatori: IOperatore[] = []

  constructor(public config: DynamicDialogConfig, public ref: DynamicDialogRef) {

    this.operatori = this.config.data.elencoOperatori
    this.operatori.forEach(op => {
      op.elencoCommesse = this.config.data.commessa
    })

  }

  ngOnInit(): void {

  }
}
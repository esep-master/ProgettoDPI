import { Component, OnInit } from '@angular/core';
import { DynamicDialogConfig } from 'primeng/dynamicdialog';

@Component({
  selector: 'app-storico-allarmi-kit',
  templateUrl: './storico-allarmi-kit.component.html',
  styleUrls: ['./storico-allarmi-kit.component.css']
})
export class StoricoAllarmiKitComponent implements OnInit {

  loading: boolean = false
  alertList: any[]

  constructor(public config: DynamicDialogConfig) {
    this.alertList = config.data.listaAllarmi
  }

  ngOnInit(): void {
  }

}

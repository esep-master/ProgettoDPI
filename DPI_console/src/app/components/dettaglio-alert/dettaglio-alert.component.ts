import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DynamicDialogConfig } from 'primeng/dynamicdialog';

@Component({
  selector: 'app-dettaglio-alert',
  templateUrl: './dettaglio-alert.component.html',
  styleUrls: ['./dettaglio-alert.component.css']
})
export class DettaglioAlertComponent implements OnInit {

  loading: boolean = false
  allarme: any

  constructor(public config: DynamicDialogConfig) {
    this.allarme = config.data.allarme
  }

  ngOnInit(): void {
  }

  coordFormat(value){
    if (!value || value == 0) {
      return "NA"
    } else {
      return parseFloat(value).toFixed(5)
    }
  }
}

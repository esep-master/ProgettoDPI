import { Component, Input, OnInit } from '@angular/core';
import { Table } from 'primeng/table';
import { IOperatore } from 'src/app/interfaces/IOperatore';
import * as FileSaver from 'file-saver';
import { Util } from 'src/app/utility/util';
import { finalize } from 'rxjs/operators';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { LookupService } from 'src/app/services/lookup.service';
import { ResponseReader } from 'src/app/utility/response-reader';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'operatori-read',
  templateUrl: './operatori-read.component.html',
  styleUrls: ['./operatori-read.component.css']
})
export class OperatoriReadComponent implements OnInit {

  @Input() operatori: IOperatore[] = []

  tipiOperatori: any[] = []
  tipiOperatoriSelected: any[] = []

  constructor(public messageService: MessageService,
    public lookupService: LookupService,
  ) { }

  ngOnInit(): void {
    this.getTipiOperatori()
  }

  getTipiOperatori() {
    this.lookupService.getTipoOperatore().pipe(
      finalize(() => {
      }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          if (data) {
            this.tipiOperatori = data
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

  exportExcel(table: any) {
    let selected = table.filteredValue ? table.filteredValue : table.value
    let formatted = this.formatValue(selected)
    Util.exportExcel(formatted, "Elenco_operai")
  }

  formatValue(selected: any) {
    let formatted: any[] = []
    selected.forEach(elem => {
      let form = {
        nominativo: elem.nominativo,
        telefono: elem.numeroTelefono,
        email: elem.email,
        commessa: elem.elencoCommesse,
        tipo: elem.tipoOperatore.nome,
        idDispositivo: elem.idDispositivo,
        matricola: elem.matricola,
      }
      formatted.push(form)
    })
    return formatted
  }

}

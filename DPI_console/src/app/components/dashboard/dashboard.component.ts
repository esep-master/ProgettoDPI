import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { finalize } from 'rxjs/operators';
import { IDashboard } from 'src/app/interfaces/IDashboard';
import { IResponseError } from 'src/app/interfaces/IResponseError';
import { DashboardService } from 'src/app/services/dashboard.service';
import { ResponseReader } from 'src/app/utility/response-reader';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  dashdata: IDashboard
  loading: boolean = false

  constructor(public messageService: MessageService,
    private router: Router,
    public dashboardService: DashboardService) { }

  ngOnInit(): void {
    this.getDashboardInfo()
  }

  getDashboardInfo() {
    this.loading = true
    this.dashboardService.getInfo()
      .pipe(
        finalize(() => {
          this.loading = false
        }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<IDashboard>()
          if (data) {
            this.dashdata = data
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

//   listAdapter(data: IDashboard) {
//     let nCommesse = {
//       label: "Commesse Attive",
//       num: data.numeroCommesseAttive,
//       icona: '',
//       url: 'home'
//     }
//     let nDpiNonAssociati = {
//       label: "DPI non Associati",
//       num: data.numeroDPINonAssociati,
//       icona: '',
//       url: 'dpi/beacon'
//     }
//     let nKitNonAssociati = {
//       label: "KIT non Associati",
//       num: data.numeroKitNonAssociati,
//       icona: '',
//       url: 'dpi/kit'
//     }
//     this.infoList = [nCommesse, nDpiNonAssociati, nKitNonAssociati]

//     data.kitSettore.forEach(kitSett => {
//       let item = {
//         num: kitSett.numeroKit,
//         label: "Kit per il settore " + kitSett.settore.nome,
//         icona: kitSett.settore.nomeIcona,
//         idSettore: kitSett.settore.id,
//         url: 'dpi/kit'
//       }
//       this.infoList.push(item)
//     })
//   }

  goTo(url: any) {
    this.router.navigate([url])
  }
}
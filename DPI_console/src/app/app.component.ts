import { ChangeDetectorRef, Component, ElementRef, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { MenuItem, MessageService } from 'primeng/api';
import { PrimeNGConfig } from 'primeng/api';
import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { IResponseError } from './interfaces/IResponseError';
import { AllarmiService } from './services/allarmi.service';
import { StateTreeService } from './services/state-tree.service';
import { UtenteService } from './services/utente.service';
import { ALERT_JOB_TIMER } from './utility/constants';
import { TipoAllarmeEnum } from './utility/enums';
import { ResponseReader } from './utility/response-reader';
import { Util } from './utility/util';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'ConsoleDPI';

  isLogged: boolean = !!localStorage.getItem("token")
  items: MenuItem[] = []
  splitBtnItems: MenuItem[] = []

  breadcrumbItems: MenuItem[] = []
  homeItem: MenuItem = {
    url: "#/dashboard",
    icon: "pi pi-home"
  }

  utenteLogged: any
  display: boolean = false
  password: string
  confPassword: string
  passwordOld: string

  warnAlarms: number = 0
  dangerAlarms: number = 0

  subscription: Subscription

  constructor(public translate: TranslateService,
    private primengConfig: PrimeNGConfig,
    public messageService: MessageService,
    public utenteService: UtenteService,
    public allarmeService: AllarmiService,
    private router: Router,
    private stateTree: StateTreeService,
    private ref: ElementRef,
    private changeDetector: ChangeDetectorRef) {

    translate.addLangs(["it", "en"])
    translate.setDefaultLang('it')

    // Identifica lingua dal browser

    const browserLang = translate.getBrowserLang()
    let lang = browserLang.match(/it/) ? browserLang : 'en'
    this.translate.use(lang)

    // Traduci tutte le API PrimeNG globalmente

    this.subscription = this.translate.stream('primeng').subscribe(data => {
      this.primengConfig.setTranslation(data)
    })
  }

  ngOnInit() {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        // let currentRoute = this.router.config.find(x => x.path == event.urlAfterRedirects.substring(1))
        let currentUrl = event.urlAfterRedirects
        this.setBreacrumb(currentUrl)
      }
    })
    this.startAlertJob()
    this.primengConfig.ripple = true;
    this.isLogged = !!localStorage.getItem("token")
    this.utenteLogged = JSON.parse(localStorage.getItem("utente"))
    this.router.events.subscribe({
      next: (event) => {
        if (event instanceof NavigationEnd) {
          this.isLogged = !!localStorage.getItem("token")
          this.utenteLogged = JSON.parse(localStorage.getItem("utente"))
        }
      }
    })
    //this.loadMenu()
    this.stateTree.read("permessi").subscribe({
      next: async (visibleMenu: string[]) => {
        if (visibleMenu && visibleMenu.length) {
          this.items = await Util.loadMenu(visibleMenu, this.translate)
        }
      }
    })

    this.splitBtnItems = [
      {
        label: 'Log-out', icon: 'pi pi-sign-out', command: () => {
          this.onLogOut();
        }
      },
      {
        label: 'Cambia Password', icon: 'pi pi-key', command: () => {
          this.onModificaPsw();
        }
      }
    ];
  }

  ngAfterViewInit() {

    // Count allarmi per Badges

    this.stateTree.read("allarmi").subscribe({
      next: (allarmi: any[]) => {

        if (allarmi) {
          this.warnAlarms = 0
          this.dangerAlarms = 0

          allarmi.forEach(allarme => {
            if (allarme.statoAllarme && !allarme.statoAllarme.statoFinale && allarme.kit) {
              if (allarme.tipoAllarme.id == TipoAllarmeEnum.UOMO_TERRA) {
                this.dangerAlarms++
              } else {
                this.warnAlarms++
              }
              this.changeDetector.detectChanges()
            }
          })

          // Inserisci Alarm Badges nel menu

          setTimeout(() => {
            let badges: HTMLElement = document.getElementById("alarm-badges")
            let el: HTMLElement = this.ref.nativeElement.querySelector(".p-menubar p-menubarsub .p-menubar-root-list > .p-menuitem:last-child .p-menuitem-link")

            if (el && badges) {
              el.append(badges)
              badges.style.display = "flex"
            }
          })

        }
      }
    })

  }

  onLogOut() {
    localStorage.removeItem("token")
    localStorage.clear()
    this.router.navigate(["login"])
  }

  onModificaPsw() {
    this.utenteLogged.id//id del loggato
    // RESET MODALE
    this.passwordOld = ""
    this.password = ""
    this.confPassword = ""
    //
    this.display = true
  }

  onConfirm() {

    let modPswAdmin = {
      oldPassword: this.passwordOld,
      newPassword: this.password,
    }
    this.utenteService.cambiaPassword(modPswAdmin).pipe(
      finalize(() => {
      }))
      .subscribe({
        next: (response: ResponseReader) => {
          let data = response.getData<any>()
          this.display = false
          this.password = null
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

  onLogoClick() {
    this.router.navigate(['home'])
  }


  // JOB PROGRAMMATO PER RICEZIONE ALLARMI

  startAlertJob() {

    let job = setInterval(() => {

      if (this.isLogged && localStorage.getItem("token")) {

        this.allarmeService.getAllarmi().subscribe({
          next: (response: ResponseReader) => {
            let data: any[] = response.getData()
            let datiFiltrati = data ? data.filter(x => !x.statoAllarme.statoFinale) : []

            this.stateTree.write("allarmi", datiFiltrati, true)

          },
          error: (error: ResponseReader) => {

            console.log(error.getError())

          }
        })
      }
    }, ALERT_JOB_TIMER)
  }

  setBreacrumb(currentUrl: string) {
    let urlArray = currentUrl.split("/")
    let newArray: any[] = []
    let last = urlArray[urlArray.length - 1]
    let breadCrumbLabels = Util.getMenuLabels()
    breadCrumbLabels.push('CREA_KIT')
    this.translate.get(breadCrumbLabels).subscribe((res: any) => {
      switch (last) {
        case "home":
          newArray.push({ label: res['GESTIONE_COMMESSE'] }, { label: res['COMMESSE'] })
          break;
        case "admin":
          newArray.push({ label: res['GESTIONE_USERS'] }, { label: res['ADMIN'] })
          break;
        case "operatori":
          newArray.push({ label: res['GESTIONE_USERS'] }, { label: res['USER'] })
          break;
        case "beacon":
          newArray.push({ label: res['GESTIONE_DPI'] }, { label: res['BEACON'] })
          break;
        case "kit":
          newArray.push({ label: res['GESTIONE_DPI'] }, { label: res['KIT'] })
          break;
        case "crea-kit":
          newArray.push({ label: res['GESTIONE_DPI'] }, { label: res['CREA_KIT'] })
          break;
        case "lista":
          newArray.push({ label: res['NOTIFICHE'] }, { label: res['LISTA_ALERT'] })
          break;
        case "dashboard":
          newArray.push({ label: res['GESTIONE_DASHBOARD'] }, { label: res['DASHBOARD'] })
          break;
        case "report":
          newArray.push({ label: res['GESTIONE_DASHBOARD'] }, { label: res['REPORT'] })
          break;
        default:
          break;
      }
      this.breadcrumbItems = newArray
    })
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe()
    }
  }
}
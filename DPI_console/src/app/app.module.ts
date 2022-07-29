import { Injector, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

//primeng
import { AccordionModule } from 'primeng/accordion';
import { DialogModule } from 'primeng/dialog';
import { PanelModule } from 'primeng/panel';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { ConfirmationService, MessageService } from 'primeng/api';
import { MenubarModule } from 'primeng/menubar';
import { RippleModule } from 'primeng/ripple';
import { CascadeSelectModule } from 'primeng/cascadeselect';
import { TableModule } from 'primeng/table';
import { ToastModule } from 'primeng/toast';
import { DialogService, DynamicDialogModule } from 'primeng/dynamicdialog';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { CheckboxModule } from 'primeng/checkbox';
import { MultiSelectModule } from 'primeng/multiselect';
import { SplitButtonModule } from 'primeng/splitbutton';
import { DividerModule } from 'primeng/divider';
import { DropdownModule } from 'primeng/dropdown';
import { TabViewModule } from 'primeng/tabview';
import { RadioButtonModule } from 'primeng/radiobutton';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { CalendarModule } from 'primeng/calendar';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { DataViewModule } from 'primeng/dataview';
import { ZXingScannerModule } from '@zxing/ngx-scanner'
import { CardModule } from 'primeng/card';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { BadgeModule } from 'primeng/badge';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputSwitchModule } from 'primeng/inputswitch';
import { ScrollPanelModule } from 'primeng/scrollpanel';

//components
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { LoadingComponent } from './components/loading/loading.component';
import { LoginService } from './services/login.service';
import { BaseService } from './services/base.service';
import { AuthGuard } from './utility/authGuard';
import { NotificheAlertComponent } from './components/notifiche-alert/notifiche-alert.component';
import { DettaglioAlertComponent } from './components/dettaglio-alert/dettaglio-alert.component';
import { AdminComponent } from './components/admin/admin.component';
import { inject } from './utility/app-injector';
import { AggiungiAdminModalComponent } from './modals/aggiungi-admin-modal/aggiungi-admin-modal.component';
import { DettaglioSquadraModalComponent } from './modals/dettaglio-squadra-modal/dettaglio-squadra-modal.component';
import { AssociaCommesseModalComponent } from './modals/associa-commesse-modal/associa-commesse-modal.component';
import { OperatoriComponent } from './components/operatori/operatori.component';
import { AggiungiOperatoreModalComponent } from './modals/aggiungi-operatore-modal/aggiungi-operatore-modal.component';
import { BtnProfileDirective } from './directives/btn-profile.directive';
import { BeaconDPIComponent } from './components/beacon-dpi/beacon-dpi.component';
import { AggiungiDPIModalComponent } from './modals/aggiungi-dpimodal/aggiungi-dpimodal.component';
import { ModificaDpiModalComponent } from './modals/modifica-dpi-modal/modifica-dpi-modal.component';
import { KitDpiComponent } from './components/kit-dpi/kit-dpi.component';
import { AggiungiKitModalComponent } from './modals/aggiungi-kit-modal/aggiungi-kit-modal.component';
import { SettoriComponent } from './components/settori/settori.component';
import { AggiungiSettoreModalComponent } from './modals/aggiungi-settore-modal/aggiungi-settore-modal.component';
import { OperatoriReadComponent } from './components/operatori-read/operatori-read.component';
import { DaysToYearsPipe } from './pipes/days-to-years.pipe';
import { AggiungiCommessaModalComponent } from './modals/aggiungi-commessa-modal/aggiungi-commessa-modal.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { FormatDatePipe } from './pipes/format-date.pipe';
import { ReportComponent } from './components/report/report.component';
import { SmartIconDirective } from './directives/smart-icon.directive';
import { StoricoAllarmiKitComponent } from './modals/storico-allarmi-kit/storico-allarmi-kit.component';
import { DettaglioKitComponent } from './modals/dettaglio-kit/dettaglio-kit.component';
import { FormatDateShortPipe } from './pipes/format-date-short.pipe';

// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, "assets/i18n/", ".json");
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    HeaderComponent,
    FooterComponent,
    LoadingComponent,
    NotificheAlertComponent,
    DettaglioAlertComponent,
    AggiungiAdminModalComponent,
    AdminComponent,
    DettaglioSquadraModalComponent,
    AssociaCommesseModalComponent,
    OperatoriComponent,
    AggiungiOperatoreModalComponent,
    BtnProfileDirective,
    BeaconDPIComponent,
    AggiungiDPIModalComponent,
    ModificaDpiModalComponent,
    KitDpiComponent,
    AggiungiKitModalComponent,
    SettoriComponent,
    AggiungiSettoreModalComponent,
    OperatoriReadComponent,
    DaysToYearsPipe,
    AggiungiCommessaModalComponent,
    DashboardComponent,
    FormatDatePipe,
    ReportComponent,
    SmartIconDirective,
    StoricoAllarmiKitComponent,
    DettaglioKitComponent,
    FormatDateShortPipe
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    TranslateModule.forRoot({
      defaultLanguage: 'it',
      loader: {
        provide: TranslateLoader,
        useFactory: (HttpLoaderFactory),
        deps: [HttpClient]
      }
    }),
    DialogModule,
    PanelModule,
    InputTextModule,
    PasswordModule,
    AccordionModule,
    ButtonModule,
    MenubarModule,
    RippleModule,
    CascadeSelectModule,
    TableModule,
    ToastModule,
    DynamicDialogModule,
    ConfirmDialogModule,
    CheckboxModule,
    MultiSelectModule,
    SplitButtonModule,
    DividerModule,
    DropdownModule,
    TabViewModule,
    RadioButtonModule,
    InputTextareaModule,
    CalendarModule,
    BreadcrumbModule,
    DataViewModule,
    ZXingScannerModule,
    CardModule,
    ProgressSpinnerModule,
    BadgeModule,
    InputNumberModule,
    InputSwitchModule,
    ScrollPanelModule
  ],
  providers: [
    LoginService,
    MessageService,
    BaseService,
    AuthGuard,
    DialogService,
    ConfirmationService,
    FormatDatePipe,
    FormatDateShortPipe
  ],
  entryComponents: [
    AggiungiAdminModalComponent,
    AssociaCommesseModalComponent,
    AggiungiDPIModalComponent,
    AggiungiOperatoreModalComponent,
    ModificaDpiModalComponent,
    StoricoAllarmiKitComponent,
    DettaglioAlertComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  static injector: Injector

  constructor(injector: Injector) {
    inject(injector)

  }
}

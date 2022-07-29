import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './components/admin/admin.component';
import { BeaconDPIComponent } from './components/beacon-dpi/beacon-dpi.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { DettaglioAlertComponent } from './components/dettaglio-alert/dettaglio-alert.component';
import { HomeComponent } from './components/home/home.component';
import { KitDpiComponent } from './components/kit-dpi/kit-dpi.component';
import { LoginComponent } from './components/login/login.component';
import { NotificheAlertComponent } from './components/notifiche-alert/notifiche-alert.component';
import { OperatoriComponent } from './components/operatori/operatori.component';
import { ReportComponent } from './components/report/report.component';
import { SettoriComponent } from './components/settori/settori.component';
import { AggiungiKitModalComponent } from './modals/aggiungi-kit-modal/aggiungi-kit-modal.component';
import { AuthGuard } from './utility/authGuard';

const routes: Routes = [
  {
    path: "home",
    component: HomeComponent,
    canActivate: [AuthGuard]
  },
  {
    path: "login",
    component: LoginComponent
  },
  {
    path: "admin",
    component: AdminComponent,
    canActivate: [AuthGuard]
  },
  {
    path: "operatori",
    component: OperatoriComponent,
    canActivate: [AuthGuard]
  },
  {
    path: "settori",
    component: SettoriComponent,
    canActivate: [AuthGuard]
  },
  {
    path: "dashboard",
    component: DashboardComponent,
    canActivate: [AuthGuard]
  },
  {
    path: "report",
    component: ReportComponent,
    canActivate: [AuthGuard]
  },
  {
    path: "alert",
    children: [
      {
        path: 'lista', component: NotificheAlertComponent,
        canActivate: [AuthGuard]
      }
    ]
  },
  {
    path: "dpi",
    children: [
      {
        path: 'beacon', component: BeaconDPIComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'kit', component: KitDpiComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'crea-kit', component: AggiungiKitModalComponent,
        canActivate: [AuthGuard]
      }
    ]
  },
  {
    path: '',
    pathMatch: 'prefix', //default
    redirectTo: "login"
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }

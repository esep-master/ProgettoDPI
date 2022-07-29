import { MenuItem } from "primeng/api";

//export const WS_BASE_URL = "http://192.168.5.182:7580/api/"; //interno
//export const WS_BASE_URL = "http://77.39.130.80:6880/api/"; //NAT
export const WS_BASE_URL = "http://57.128.16.179:7580/api/"; //EASYSERVIZI 

export const VERSION = "3.1";
export const REQUEST_TIMEOUT = 20000;
export const REQUEST_RETRIES = 3;
export const ALERT_JOB_TIMER = 10000; // 10 secondi

// MENU

export const menuTemplate: MenuItem[] = [
    {
        label: 'GESTIONE_DASHBOARD',
        icon: 'pi pi-home',
        items: [
            {
                label: 'DASHBOARD',
                icon: 'pi pi-bars',
                url: '#/dashboard'
            },
            {
                label: 'REPORT',
                icon: 'pi pi-chart-bar',
                url: '#/report'
            }
        ]
    },
    {
        label: 'GESTIONE_COMMESSE',
        icon: 'pi pi-list',
        items: [
            {
                label: 'COMMESSE',
                icon: 'pi pi-book',
                url: '#/home'
                // ,
                // command: (event: any) => { console.log("funge")}
            }
            // ,
            // {
            //     label: 'SETTORI',
            //     icon: 'pi pi-list',
            //     url: '#/settori'
            // }
        ]
    },
    {
        label: 'GESTIONE_USERS',
        icon: 'pi pi-users',
        items: [
            {
                label: 'ADMIN',
                icon: 'pi pi-user-plus',
                url: '#/admin'
            },
            {
                label: 'USER',
                icon: 'pi pi-user',
                url: '#/operatori'
            },
            {
                label: 'MODIFICA_PASSWORD',
                icon: 'pi pi-pencil',
                url: '#/password'
            }
        ]
    },
    {
        label: 'GESTIONE_DPI',
        icon: 'pi pi-cog',
        items: [
            {
                label: 'BEACON',
                icon: 'pi pi-credit-card',
                url: '#/dpi/beacon'
            },
            {
                label: 'KIT',
                icon: 'pi pi-briefcase',
                url: '#/dpi/kit'
            }
        ]
    },
    {
        label: 'NOTIFICHE',
        icon: 'pi pi-bell',
        items: [
            {
                label: 'LISTA_ALERT',
                icon: 'pi pi-exclamation-triangle',
                url: '#/alert/lista'
            }
        ]
    }
]

export const menuKeyMap: any = [
    { key: "lista_sedi_commesse", label: "COMMESSE" },
    { key: "lista_sedi_commesse", label: "SETTORI" },
    { key: "lista_amministratori", label: "ADMIN" },
    { key: "lista_allarmi", label: "LISTA_ALERT" },
    { key: "lista_dpi", label: "BEACON" },
    { key: "lista_kit", label: "KIT" },
    { key: "lista_allarmi", label: "DASHBOARD" },
    { key: "lista_allarmi", label: "REPORT" },
    { key: "lista_operai", label: "USER" }
]
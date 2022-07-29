import { TranslateService } from "@ngx-translate/core"
import { MenuItem } from "primeng/api"
import { menuKeyMap, menuTemplate } from "./constants"
import * as FileSaver from 'file-saver';

export class Util {

    public static buildEnpoint(baseEndpoint: string, method: any, ...args: any) {
        return baseEndpoint + "/" + method + "/" + args.join("/")
    }

    public static getPermessi() {

        let ruolo: any = JSON.parse(localStorage.getItem("ruolo"))

        let menu: string[] = []

        if (ruolo) {
            menu = ruolo["funzioniRuolo"].map(x => x["funzione"]["nome"])
        }

        return menu

    }

    public static loadMenu(visibleMenu: string[], translate: TranslateService) {

        // Creo promise perché le traduzioni arrivano asincrone

        const promise = new Promise<MenuItem[]>((resolve, reject) => {

            // Carico la struttura gerarchica del menu

            let template: MenuItem[] = JSON.parse(JSON.stringify(menuTemplate)) // Tolgo riferimento dal template

            let resultMenu: MenuItem[] = []

            // Prendo le traduzioni di tutte le label necessarie per il menu

            translate.get(this.getMenuLabels()).subscribe((res: any) => {

                // Per ogni macro-menu:

                template.forEach((parentMenu: MenuItem) => {

                    let children: MenuItem[] = []

                    // Per ogni sotto-menu:

                    parentMenu["items"].forEach((childMenu: MenuItem) => {

                        // Fai vedere un sotto-menu se è incluso nei permessi

                        if (visibleMenu.includes(this.getMenuKeyByLabel(childMenu.label))) {

                            childMenu.label = res[childMenu.label]

                            children.push(childMenu)

                        }
                    })

                    // Fai vedere un macro-menu, solo se c'è almeno un sotto-menu

                    if (children.length) {

                        parentMenu.label = res[parentMenu.label]

                        parentMenu.items = children

                        resultMenu.push(parentMenu)

                    }
                })

                resolve(resultMenu)
                
            })
        })

        return promise
    }

    public static getMenuLabels() {

        let template: MenuItem[] = menuTemplate

        let flatLabels = template.map(x => x.label)

        template.forEach(parentMenu => {

            flatLabels = flatLabels.concat(parentMenu.items.map(x => x.label))

        })

        return flatLabels

    }

    public static getMenuKeyByLabel(label: string) {

        let menuMap: { key: string, label: string }[] = menuKeyMap

        let menuElement = menuMap.find(x => x.label == label)

        return menuElement ? menuElement.key : ""

    }

    public static distinct(array: any[]) {

        return [...new Set(array)]

    }

    public static exportExcel(selectedValue:any,fileName:string) {
        import("xlsx").then(xlsx => {
          const worksheet = xlsx.utils.json_to_sheet(selectedValue);
          const workbook = { Sheets: { 'data': worksheet }, SheetNames: ['data'] };
          const excelBuffer: any = xlsx.write(workbook, { bookType: 'xlsx', type: 'array' });
          this.saveAsExcelFile(excelBuffer, fileName);
        });
      }
    
    public static saveAsExcelFile(buffer: any, fileName: string): void {
        let EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
        let EXCEL_EXTENSION = '.xlsx';
        const data: Blob = new Blob([buffer], {
          type: EXCEL_TYPE
        });
        FileSaver.saveAs(data, fileName + '_export_' + new Date().getTime() + EXCEL_EXTENSION);
      }
}
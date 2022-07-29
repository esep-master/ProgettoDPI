import { ICommessa } from "./ICommessa";
import { IKitDPI } from "./IKitDPI";
import { IOperatore } from "./IOperatore";
import { ISettore } from "./ISettore";

export interface IKit {
    id: number,
    operatore: IOperatore,
    modello: string,
    note: string,
    settore: ISettore,
    dataAssegnazione: Date,
    lavorazione?: boolean,
    tipoAllarme?: string,
    idAllarme?: number,
    dpiKit: IKitDPI[],
    elencoCommesse?: number[],
    uomoATerraAlert?: boolean,
    alertCount?: number,
    statoKit?: number,
    associato?: boolean,
    responsabile: any[]
}
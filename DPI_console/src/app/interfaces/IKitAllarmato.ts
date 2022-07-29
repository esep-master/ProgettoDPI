import { IDPIAllarmato } from "./IDPIAllarmato";

export interface IKitAllarmato {
    idAllarme: number,
    idKit: number,
    modello: string,
    //commessa: string,
    nominativo: string,
    //dataInizioIntervento?: Date,
    settore: string,
    settoreId: number,
    DPI: IDPIAllarmato[]
}
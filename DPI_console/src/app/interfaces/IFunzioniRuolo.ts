import { IFunzione } from "./IFunzione";

export interface IFunzioniRuolo {
    id: number,
    utenteInserimento?: number,
    dataInserimento?: Date,
    utenteUltimaModifica?: number,
    dataUltimaModifica?: Date,
    funzione: IFunzione
}
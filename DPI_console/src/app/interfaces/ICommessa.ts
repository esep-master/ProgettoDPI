import { ISettore } from "./ISettore";

export interface ICommessa {
    id: number,
    utenteInserimento?: number,
    dataInserimento?: Date,
    utenteUltimaModifica?: number,
    dataUltimaModifica?: Date,
    nome: string,
    settore: ISettore
}

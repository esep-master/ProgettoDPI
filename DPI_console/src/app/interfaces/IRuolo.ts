import { IFunzioniRuolo } from "./IFunzioniRuolo";

export interface IRuolo {
    id: number,
    utenteInserimento?: number,
    dataInserimento?: Date,
    utenteUltimaModifica?: number,
    dataUltimaModifica?: Date,
    nome: string,
    superAdmin: boolean,
    funzioniRuolo: IFunzioniRuolo[]
}
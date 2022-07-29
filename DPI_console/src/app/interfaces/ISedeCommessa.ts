import { ICommessa } from "./ICommessa";

export interface ISedeCommessa {
    id: number,
    utenteInserimento?: number,
    dataInserimento?: Date,
    utenteUltimaModifica?: number,
    dataUltimaModifica?: Date,
    nome: string,
    commessa: ICommessa
}
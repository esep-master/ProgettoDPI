import { ISedeCommessa } from "./ISedeCommessa";

export interface IOperatoreSedeCommessa {
    id: number,
    utenteInserimento?: number,
    dataInserimento?: Date,
    utenteUltimaModifica?: number,
    dataUltimaModifica?: Date,
    sedeCommessa: ISedeCommessa
}
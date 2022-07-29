import { ISedeCommessa } from "./ISedeCommessa";

export interface IUtenteSedeCommessa {
    id: number,
    utenteInserimento: number,
    dataInserimento: Date,
    utenteUltimaModifica: number,
    dataUltimaModifica: Date,
    sedeCommessa: ISedeCommessa
}
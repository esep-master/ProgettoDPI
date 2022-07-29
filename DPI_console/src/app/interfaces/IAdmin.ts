import { IRuolo } from "./IRuolo";
import { IUtenteSedeCommessa } from "./IUtenteSedeCommessa";

export interface IAdmin {
    id: number,
    utenteInserimento?: number,
    dataInserimento?: Date,
    utenteUltimaModifica?: number,
    dataUltimaModifica?: Date,
    username: string,
    email: string,
    numeroTelefono: string,
    nome: string,
    cognome: string,
    ruolo: IRuolo,
    utenteSediCommesse: IUtenteSedeCommessa[]
}
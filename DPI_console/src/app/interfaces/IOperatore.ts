import { IKit } from "./IKit";
import { IOperatoreSedeCommessa } from "./IOperatoreSedeCommessa";
import { ITipoOperatore } from "./ITipoOperatore";

export interface IOperatore {
    id: number,
    utenteInserimento?: number,
    dataInserimento?: Date,
    utenteUltimaModifica?: number,
    numeroTelefono: number,
    email?: string,
    dataUltimaModifica?: Date,
    matricola: string,
    idDispositivo: string,
    password: null,
    nominativo: string,
    tipoOperatore: ITipoOperatore,
    operatoreSediCommesse: IOperatoreSedeCommessa[],
    kit: IKit[],
    elencoCommesse?: string
}
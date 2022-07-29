import { ITipiDpiSettori } from "./ITipiDpiSettori";

export interface ISettore {
    id: number,
    utenteInserimento?: number,
    dataInserimento?: Date,
    utenteUltimaModifica?: number,
    dataUltimaModifica?: Date,
    multiselectDisabled?: boolean,
    nome: string,
    nomeIcona: string,
    tipiDPISettori: ITipiDpiSettori[]
}
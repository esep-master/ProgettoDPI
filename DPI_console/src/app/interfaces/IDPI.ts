import { IBeacon } from "./IBeacon";
import { IKitDPI } from "./IKitDPI";
import { ISettore } from "./ISettore";
import { ISettoreDPI } from "./ISettoreDPI";
import { ITipoDPI } from "./ITipoDPI";

export interface IDPI {
    id: number,
    codice: string,
    modello: string,
    marca: string,
    tipoDPI: ITipoDPI,
    note: string,
    dataScadenza: Date,
    scadenzaCalcolata?: any,
    beacon: IBeacon,
    dpiKit: IKitDPI[],
    iconaBatt?: string
    risoluzioneAutomatica?: boolean,
    settoriDPI: ISettoreDPI[],
    alert?: boolean,
    lavorazione?: boolean,
    giaUtilizzato?: number,
    idAllarme?: number,
    tipoAllarme?: string,
    ultimoStato?: string,
    dataAllarme?: Date,
    latitudine?: string,
    longitudine?: string,
    idSettori?: number[],
    marcaModello?: string,
    associato?: boolean
}
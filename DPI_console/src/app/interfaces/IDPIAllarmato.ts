export interface IDPIAllarmato {
    idAllarme: number,
    idDpi: number,
    tipoAllarme: string,
    codice: string,
    data: string,
    tipoDpi: string,
    latitudine: string,
    longitudine: string,
    lavorazione: boolean,
    commessa?: string,
    dataInizioIntervento?: string
}
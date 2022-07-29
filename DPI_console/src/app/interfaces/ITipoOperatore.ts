export interface ITipoOperatore {
    id: number,
    utenteInserimento?: number,
    dataInserimento?: Date,
    utenteUltimaModifica?: number,
    dataUltimaModifica?: Date,
    nome: string,
    esterno: boolean
}
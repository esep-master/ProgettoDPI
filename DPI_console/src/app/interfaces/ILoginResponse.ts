export interface ILoginResponse {
    user: {
        id: number,
        username: string,
        ruolo: {
            id: number,
            nome: string,
            funzioniRuolo: IFunzioniRuolo[]
        },
        utenteSediCommesse: IUtenteSediCommesse[]
    },
    configurazioni: IConfigurazioni[]
    token: string
}

interface IFunzioniRuolo {
    id: number,
    funzione: {
        id: number,
        nome: string
    }
}

interface IConfigurazioni {
    id: number,
    nome: string,
    valore: string,
    loginApp: boolean
}

export interface IUtenteSediCommesse {
    id: number,
    sedeCommessa: {
        id: number,
        nome: string,
        commessa: {
            id: number,
            nome: string,
            settore: {
                id: number,
                nome: string
            }
        }
    }
}


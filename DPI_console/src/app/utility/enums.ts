export enum EXAMPLE_ENUM {
    KEY = "value",
}

// DA MAPPARE L'ERROR CODE
export enum ErrorCodeEnum {
    BAD_LOGIN = -100,

    SESSION_EXPIRED = 307,
    PROFILE_EXPIRED = 666,
    NOT_AUTHORIZED = 401,
    TOKEN_EXPIRED = 403,
    NOT_FOUND = 404,

    BAD_REQUEST = 0,
    INTERNAL_ERROR = 0,
    NOT_ALLOWED = 0,
    NOT_RECOGNIZED = 0,
    TIMEOUT = 0,
}

export enum BaseServiceEnum {
    LOGIN = "auth",
    UTENTE = "utente",
    COMMESSA = "commessa",
    SEDE_COMMESSA = "sede_commessa",
    OPERATORE = "operatore",
    DASHBOARD = "dashboard",
    LOOKUP = "lookup",
    SETTORE = "settore",
    KIT = "kit",
    DPI = "dpi",
    ALLARME = "allarme"
}

export enum DashboardServiceEnum {
    INFO = "info"
}

export enum UtenteServiceEnum {
    LISTA = "list",
    LISTA_COMMESSE = "commesse",
    ASS_COMMESSA = "associa_sede_commessa",
    DISASS_COMMESSA = "disassocia_sede_commessa",
    CAMBIO_PSW = "cambia_password",
}

export enum OperatoreServiceEnum {
    LISTA = "list",
    ASS_COMMESSA = "associa_sede_commessa",
    DISASS_COMMESSA = "disassocia_sede_commessa",
    RESETTA_PSW = "reset_password",
    ASS_KIT = "associa_kit",
    DISASS_KIT = "disassocia_kit",
}

export enum CommessaServiceEnum {
    LISTA_OPERATORI = "operatori",
    LISTA = "list"
}

export enum SedeCommessaServiceEnum {
    LISTA = "list"
}

export enum LookupServiceEnum {
    RUOLI = "ruoli",
    TIPO_OPERATORI = "tipi_operatore",
    TIPO_DPI = "tipi_dpi",
    TIPO_ALLARMI = "tipi_allarmi",
    STATI_ALLARMI = "stati_allarmi",
    TIPO_BEACON = "tipi_beacon"
}

export enum DpiServiceEnum {
    LISTA = "list",
    BEACON_DISP = "beacon_disponibili"
}

export enum KitServiceEnum {
    LISTA = "list",
    INFO = "info",
}

export enum SettoreServiceEnum {
    LISTA = "list",
}

export enum AlarmServiceEnum {
    LISTA = "list",
    STORICO = "storico/list",
    LAVORAZIONE = "lavorazione",
    CHIUSURA = "chiusura"
}

export enum StatoAllarmiEnum {
    APERTO = 1,
    IN_LAVORAZIONE = 2,
    CHIUSO = 3,
    SBLOCCO_AUTOMATICO = 4
}

export enum TipoAllarmeEnum {
    DPI_NON_INDOSSATO = 1,
    UOMO_TERRA = 2,
    BATTERIA_SCADENZA = 3,
    BATTERIA_SCARICA = 4,
    DPI_SCADENZA = 5
}

export enum StatiKitReportEnum {
    UN_DPI_ALLARME = '1 DPI in allarme',
    PIU_DPI_ALLARME = 'piu di un DPI in allarme',
    KIT_DISATTIVATO = 'Kit disattivato',
    KIT_OK = 'Kit ok'
}

export enum NomeDpiEnum {
    CUFFIE = 'Cuffie',
    CASCO = 'Casco'
}
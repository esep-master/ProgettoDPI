package it.bleb.dpi.utils;

public enum TipoAzioneOperatoreEnum {
    LOGIN(1),
    INIZIO_ATTIVITA(2),
    FINE_ATTIVITA(3),
    NUOVO_ALLARME(4),
    SBLOCCO_ALLARME(5),
    LOGOUT(6);


    private int value;

    TipoAzioneOperatoreEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

package it.bleb.dpi.utils;

public enum AlertType {

    ALERT(1),                           //Alert classico (DPI non indossato)
    ALERT_UAT(2),                       //Alert Uomo A Terra
    ALERT_BATTERIA_IN_SCADENZA(3),      //Alert batteria in scadenza
    ALERT_BATTERIA_SCARICA(4);          //Alert batteria scarica


    private int value;

    AlertType(int value) {
        this.value = value;
    }
}

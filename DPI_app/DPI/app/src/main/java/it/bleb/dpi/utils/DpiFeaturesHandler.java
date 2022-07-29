package it.bleb.dpi.utils;

import org.json.JSONException;

import it.bleb.dpi.database.entity.Alert;
import it.bleb.dpi.database.entity.AzioneOperatore;
import it.bleb.dpi.database.entity.Intervento;
import it.bleb.dpi.database.entity.Task;

public interface DpiFeaturesHandler {

    void stopMessage(boolean isReceived);

    void logout();

    void updateTask(Task task);

    void onScan();

    void stopScan();

    void setKit(boolean fromTest, String settore);

    void sendToPortale() throws JSONException;

    void saveAlertInDB(Alert alert);

    void saveInterventoInDB(Intervento intervento);

    void saveAzioneOperatore(AzioneOperatore azioneOperatore);

    String getTokenOperatore();

    void showToast(String string);
}

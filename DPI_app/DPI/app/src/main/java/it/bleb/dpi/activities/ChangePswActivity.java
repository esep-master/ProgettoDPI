package it.bleb.dpi.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import it.bleb.dpi.DpiAppApplication;
import it.bleb.dpi.R;
import it.bleb.dpi.apiservices.APIService;
import it.bleb.dpi.database.DpiDatabase;
import it.bleb.dpi.database.entity.Alert;
import it.bleb.dpi.database.entity.AzioneOperatore;
import it.bleb.dpi.database.entity.Intervento;
import it.bleb.dpi.database.entity.Operatore;
import it.bleb.dpi.database.entity.Task;
import it.bleb.dpi.utils.DpiFeaturesHandler;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePswActivity extends AppCompatActivity implements DpiFeaturesHandler {
    private Button buttonConfirm;
    private EditText oldPsw, newPsw, confirmPsw;
    private TextInputLayout inputLayoutOldPsw, inputLayoutNewPsw, inputLayoutConfirmPsw;
    private static final String TAG = "ChangePswActivity";
    private DpiDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psw);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        setLocalDB();
        oldPsw = findViewById(R.id.editTextOldPwd);
        newPsw = findViewById(R.id.editTextNewPwd);
        confirmPsw = findViewById(R.id.editTextConfirmPwd);

        //Messaggi errore
        inputLayoutOldPsw = findViewById(R.id.txt_layout_old_psw);
        inputLayoutNewPsw = findViewById(R.id.txt_layout_newPsw);
        inputLayoutConfirmPsw = findViewById(R.id.txt_layout_confirmPsw);

        //Button
        buttonConfirm = findViewById(R.id.buttonConfirmChangePsw);
        setButtonListener();
    }

    /**
     * set DB APP
     */
    private void setLocalDB() {
        db = Room.databaseBuilder(ChangePswActivity.this, DpiDatabase.class, "dpi_database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    /**
     * Listener del pulsante conferma cambio psw
     */
    private void setButtonListener() {
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doChangePsw();
            }
        });
    }

    /**
     * conferma btn event
     */
    private void doChangePsw() {
        boolean isValid = true;
        String oldPassword = oldPsw.getText().toString();
        String nuovaPassword = newPsw.getText().toString();
        String confermaPassword = confirmPsw.getText().toString();
        if (!oldPassword.isEmpty() && !nuovaPassword.isEmpty() && !confermaPassword.isEmpty()
                && nuovaPassword.equals(confermaPassword)) {
            inputLayoutNewPsw.setError(null);
            inputLayoutConfirmPsw.setError(null);
        }

        if (oldPassword.equals("")) {
            isValid = false;
            inputLayoutOldPsw.setError(getText(R.string.text_required));
        }
        if (nuovaPassword.equals("")) {
            isValid = false;
            inputLayoutNewPsw.setError(getText(R.string.text_required));
        }
        if (confermaPassword.equals("")) {
            isValid = false;
            inputLayoutConfirmPsw.setError(getText(R.string.text_required));
        }

        if (isValid) {
            if (!DpiAppApplication.DEBUG_MODE) {
                callChangePswService(oldPassword, nuovaPassword);
            }
        }
    }

    /**
     * call change psw service
     *
     * @param oldPsw, newPsw
     */
    private void callChangePswService(String oldPsw, String newPsw) {
        APIService APIService = new APIService(ChangePswActivity.this);
        APIService.setUrl();

        APIService.doChangePsw(oldPsw, newPsw, getTokenOperatore(), new APIService.IChangePsw() {
            @Override
            public void changePsw(boolean isError) {
                if (!isError) {
                    //Ok request
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePswActivity.this);
                    builder.setTitle(getText(R.string.app_name)).setMessage(getText(R.string.txt_change_psw_ok)).setCancelable(true);
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(ChangePswActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                    builder.create().show();

                } else {
                    //Errore request
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePswActivity.this);
                    builder.setTitle(getText(R.string.warning_title)).setMessage(getText(R.string.network_error)).setCancelable(true);
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            }
        });
    }

    @Override
    public void stopMessage(boolean isReceived) {

    }

    @Override
    public void logout() {

    }

    @Override
    public void updateTask(Task task) {

    }

    @Override
    public void onScan() {

    }


    @Override
    public void stopScan() {

    }

    @Override
    public void setKit(boolean fromTest, String settore) {

    }

    @Override
    public void sendToPortale() throws JSONException {

    }

    @Override
    public void saveAlertInDB(Alert alert) {

    }

    @Override
    public void saveInterventoInDB(Intervento intervento) {

    }

    @Override
    public void saveAzioneOperatore(AzioneOperatore azioneOperatore) {

    }

    @Override
    public String getTokenOperatore() {
        return db.operatoreDao().getAll().get(0).getToken();
    }

    @Override
    public void showToast(String string) {

    }
}
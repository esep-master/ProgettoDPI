package it.bleb.dpi.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import it.bleb.dpi.DpiAppApplication;
import it.bleb.dpi.R;
import it.bleb.dpi.apiservices.APIService;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class RecoveryPswActivity extends AppCompatActivity {
    private Button buttonConfirm;
    private EditText email;
    private TextInputLayout inputLayoutEmail;
    private static final String TAG = "RecoveryPswActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_psw);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        email = findViewById(R.id.editTextEmail);
        inputLayoutEmail = findViewById(R.id.txt_layout_email);

        //Button
        buttonConfirm = findViewById(R.id.buttonConfirmRecoveryPsw);
        setButtonListener();
    }

    /**
     * Listener del pulsante conferma recovery psw
     */
    private void setButtonListener() {
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRecoveryPsw();
            }
        });
    }

    /**
     * conferma btn event
     */
    private void doRecoveryPsw() {
        boolean isValid = true;
        String emailTxt = email.getText().toString();
        if (!emailTxt.isEmpty()) {
            inputLayoutEmail.setError(null);
            buttonConfirm.setEnabled(true);
        }

        if (emailTxt.equals("")) {
            buttonConfirm.setEnabled(true);
            isValid = false;
            inputLayoutEmail.setError(getText(R.string.text_required));
        }

        if (isValid) {
            callChangePswService(emailTxt);

        }
    }

    /**
     * call recovery psw service
     *
     * @param email
     */
    private void callChangePswService(String email) {
        APIService APIService = new APIService(RecoveryPswActivity.this);
        //change url service
        APIService.setUrl();

        APIService.doRecoveryPsw(email, new APIService.IRecoveryPsw() {
            @Override
            public void recoveryPsw(boolean isError) {
                if (!isError) {
                    //Ok request
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecoveryPswActivity.this);
                    builder.setTitle(getText(R.string.app_name)).setMessage(getText(R.string.txt_emai_recovery_ok)).setCancelable(true);
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(RecoveryPswActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                    builder.create().show();

                } else {
                    //Errore request
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecoveryPswActivity.this);
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
}
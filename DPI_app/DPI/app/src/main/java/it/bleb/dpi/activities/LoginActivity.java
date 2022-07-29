package it.bleb.dpi.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;
import it.bleb.blebandroid.Blebricks;
import it.bleb.dpi.DpiAppApplication;
import it.bleb.dpi.R;
import it.bleb.dpi.apiservices.APIService;
import it.bleb.dpi.database.DpiDatabase;
import it.bleb.dpi.database.entity.Admin;
import it.bleb.dpi.database.entity.Beacon;
import it.bleb.dpi.database.entity.Commessa;
import it.bleb.dpi.database.entity.Dpi;
import it.bleb.dpi.database.entity.DpiKit;
import it.bleb.dpi.database.entity.Kit;
import it.bleb.dpi.database.entity.Operatore;
import it.bleb.dpi.database.entity.OperatoreSediCommesse;
import it.bleb.dpi.database.entity.SedeCommessa;
import it.bleb.dpi.database.entity.Settore;
import it.bleb.dpi.database.entity.TipoAzioneOperatore;
import it.bleb.dpi.database.entity.TipoBeacon;
import it.bleb.dpi.database.entity.TipoDpi;
import it.bleb.dpi.database.entity.UtenteSediCommesse;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String booleanExtra = "loggedIn";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    //VIEW
    private Button buttonConfirm;
    private EditText username, password;
    private TextView recuperaPsw;
    public TextInputLayout inputLayoutUsr, inputLayoutPwd, inputLayout;
    private ConstraintLayout constraintLayout;
    private ImageSwitcher successfulLogin;

    //DB
    private DpiDatabase db;

    //OBJECT
    private Operatore operatore = null;

    //SHARED PREFERENCES
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    boolean fromAssenzaKit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        sharedPref = getBaseContext().getSharedPreferences(
                getString(R.string.preferences), Context.MODE_PRIVATE);
        //LAYOUT
        constraintLayout = findViewById(R.id.login_form);
        successfulLogin = findViewById(R.id.successful_login);
        username = findViewById(R.id.editTextUsr);
        password = findViewById(R.id.editTextPwd);
        recuperaPsw = findViewById(R.id.textViewRecuperaPsw);
        //Messaggi errore
        inputLayoutUsr = findViewById(R.id.textinput_layout_usr);
        inputLayoutPwd = findViewById(R.id.textinput_layout_pwd);
        inputLayout = findViewById(R.id.textinput_layout);
        //Button
        buttonConfirm = findViewById(R.id.buttonConfirm);

        recuperaPswBtnListener();
        setClearErrorMessagesListener();
        setButtonListener();

        //Su utente non è già loggato creo il DB
        if (!userIsLogged()) {
            //Set local DB
            setLocalDB();
        }

        // Check se utente è già loggato su Heratech
        boolean isLoggedIn = false;
        if(getIntent() != null){
            isLoggedIn = getIntent().getBooleanExtra(booleanExtra, false);
            fromAssenzaKit = getIntent().getBooleanExtra("assenzaKit", false);
        }

        //SE loggato
        if (isLoggedIn) {
            setLocalDB();
            //Valori da intent app lavoro
            String letturista = getIntent().getStringExtra("IDLetturista");
            String imei = getIntent().getStringExtra("IDImei");
            Log.i(TAG, "RECEIVER ricevuto: letturista [" + letturista + "]" + " imei [" + imei + "]");
            callLoginService(imei, null);
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            }
        }
    }

    /**
     * go to homeActivity
     *
     * @param fromAppLavoro se loggato con heratech
     */
    private void goToHomeActivity(boolean fromAppLavoro) {
        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        i.putExtra("operatore", operatore);
        i.putExtra("fromAppLavoro", fromAppLavoro);
        startActivity(i);
        finish();
    }

    /**
     * Controllo SP se operatore è presente e se è già loggato a sistema
     *
     * @return user logged true/false
     */
    private boolean userIsLogged() {
        boolean userLogged = false;
        if (sharedPref != null) {
            Gson gson = new Gson();
            String json = sharedPref.getString(getString(R.string.operatore), "");
            if (json != null) {
                Operatore operatoreLoggato = gson.fromJson(json, Operatore.class);
                if (operatoreLoggato != null && operatoreLoggato.isLoggedin()) {
                    userLogged = true;
                }
            }
        }
        return userLogged;
    }

    /**
     * set DB APP
     */
    private void setLocalDB() {
        if (getBaseContext().getDatabasePath("dpi_database") != null) {
            getBaseContext().deleteDatabase("dpi_database");
        }
        db = Room.databaseBuilder(LoginActivity.this, DpiDatabase.class, "dpi_database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    /**
     * animation Login
     */
    private void animationLogin() {
        constraintLayout.setVisibility(View.GONE);
        //Mostra spunta di login effettuata
        TextView title = findViewById(R.id.app_title);
        Animation anim = AnimationUtils.loadAnimation(LoginActivity.this, android.R.anim.fade_in);
        anim.setDuration(2000);
        title.setAnimation(anim);
        successfulLogin.setFactory(() -> {
            ImageView myView = new ImageView(getApplicationContext());
            myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            myView.setImageResource(R.drawable.ic_btn_done);
            return myView;
        });
        Animation in = AnimationUtils.loadAnimation(LoginActivity.this, android.R.anim.fade_in);
        in.setDuration(2000);
        successfulLogin.setAnimation(in);
    }


    /**
     * Edit text listener per eliminare i messaggi di errore durante digitazione
     */
    private void setClearErrorMessagesListener() {
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputLayoutUsr.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputLayoutPwd.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Listener del pulsante della login
     */
    private void setButtonListener() {
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
    }

    private void recuperaPswBtnListener() {
        recuperaPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRecoveryPsw();
            }
        });
    }

    /**
     * login btn event
     */
    private void doLogin() {
        boolean isValid = true;
        String user = username.getText().toString().trim();
        String pwd = password.getText().toString();
        inputLayoutUsr.setError(null);
        inputLayoutPwd.setError(null);
        inputLayout.setError(null);

        if (user.equals("")) {
            isValid = false;
            inputLayout.setError(null);
            inputLayoutUsr.setError(getText(R.string.text_username_obbligatorio));
        }
        if (pwd.equals("")) {
            isValid = false;
            inputLayout.setError(null);
            inputLayoutPwd.setError(getText(R.string.text_password_obbligatoria));
        }

        if (isValid) {
            if (DpiAppApplication.DEBUG_MODE) {
                //TODO TEST
                operatore = new Operatore(1, "eleonora", false, "gdsldgqi", null, null);
                saveOperatoreData(false, operatore);
                goToHomeActivity(false);
            } else {
                callLoginService(user, pwd);
            }
        }
    }

    /**
     * call login service
     *
     * @param username  username/imei
     * @param pwd==null se vengo da app lavoro
     */
    private void callLoginService(String username, String pwd) {
        APIService APIService = new APIService(LoginActivity.this);
        //change url service
        APIService.setUrl();

        APIService.doLogin(username, pwd, new APIService.ILogin() {
            @Override
            public void authenticate(Object responseData, boolean isError) {
                if (!isError) {
                    if (!responseData.toString().equals("null")) {
                        animationLogin();

                        getResponseInfoAndSetDB((JSONObject) responseData);

                        goToHomeActivity(pwd == null);
                    } else {
                        inputLayout.setError(getText(R.string.text_dati_non_corretti));
                    }
                } else {
                    //Errore request
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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

    /**
     * Gestione della response della Login
     *
     * @param responseData oggetto response
     */
    private void getResponseInfoAndSetDB(JSONObject responseData) {
        operatore = getOperatore(responseData);
        readAdminsAndSaveIntoDB(responseData);
        readTipiAzioneOperatore(responseData);
        //saveOperatoreData(false, operatore);
    }

    /**
     * Recupero informazioni dell'operatore
     *
     * @param data response della login
     * @return operatore loggato
     */
    private Operatore getOperatore(JSONObject data) {
        Operatore operatore = null;
        try {
            JSONObject operatoreJson = data.getJSONObject("operatore");
            int id = operatoreJson.getInt("id");
            String matricola = operatoreJson.getString("matricola");
            List<OperatoreSediCommesse> operatoreSediCommesseList = getOperatoreSediCommesse(operatoreJson);
            List<Kit> kitList = getKits(operatoreJson);
            String token = data.getString("token");
            operatore = new Operatore(id, matricola, true, token, operatoreSediCommesseList, kitList);
            saveOperatoreToSP(operatore);
            db.operatoreDao().insertAll(operatore);
            db.kitDao().insertAll(kitList);
            for (Kit kit : kitList) {
                db.dpiKitDao().insertAll(kit.getDpiKits());
            }
            db.operatoreSediCommesseDao().insertAll(operatoreSediCommesseList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return operatore;
    }

    /**
     * save in Sp operatore loggato
     *
     * @param operatore
     */
    private void saveOperatoreToSP(Operatore operatore) {
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(operatore);
        editor.putString(getString(R.string.operatore), json);
        editor.apply();
    }

    /**
     * Recupero Kit associati all'operatore
     *
     * @param operatoreJson
     * @return lista dei Kit
     */
    private List<Kit> getKits(JSONObject operatoreJson) {
        List<Kit> kitList = new ArrayList<>();
        try {
            JSONArray kitListJson = operatoreJson.getJSONArray("kit");
            for (int i = 0; i < kitListJson.length(); i++) {
                JSONObject kitJson = kitListJson.getJSONObject(i);
                JSONObject settoreJson = kitJson.getJSONObject("settore");
                Settore settore = db.settoreDao().getSettore(settoreJson.getInt("id"));
                if (settore == null) {
                    settore = getSettore(settoreJson);
                }
                int id = kitJson.getInt("id");
                String modello = kitJson.getString("modello");
                String note = kitJson.getString("note");
                String noteSbloccoTotale = kitJson.getString("noteSbloccoTotale");
                String dataAssegnazione = kitJson.getString("dataAssegnazione");
                List<DpiKit> dpiKitList = getDpiKit(kitJson, id);
                Kit kit = new Kit(id, settore.getId(), modello, note, noteSbloccoTotale, dataAssegnazione, dpiKitList, operatoreJson.getInt("id"));
                kitList.add(kit);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return kitList;
    }

    /**
     * Recupero DpiKit associati all'operatore
     *
     * @param kitJson
     * @param idKit
     * @return lista DpiKit
     */
    private List<DpiKit> getDpiKit(JSONObject kitJson, int idKit) {
        List<DpiKit> dpiKitList = new ArrayList<>();
        try {
            JSONArray dpiKitListJson = kitJson.getJSONArray("dpiKit");
            for (int i = 0; i < dpiKitListJson.length(); i++) {
                JSONObject dpiKitJson = dpiKitListJson.getJSONObject(i);
                JSONObject dpiJson = dpiKitJson.getJSONObject("dpi");
                Dpi dpi = db.dpiDao().getDpi(dpiJson.getInt("id"));
                if (dpi == null) {
                    dpi = getDpi(dpiJson);
                }
                int id = dpiKitJson.getInt("id");
                String sbloccoAllarmeDa = dpiKitJson.getString("sbloccoAllarmeDa");
                String sbloccoAllarmeA = dpiKitJson.getString("sbloccoAllarmeA");
                DpiKit dpiKit = new DpiKit(id, dpi.getId(), sbloccoAllarmeDa, sbloccoAllarmeA, idKit);
                dpiKitList.add(dpiKit);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dpiKitList;
    }

    /**
     * Recupero Dpi associati al Kit
     *
     * @param dpiJson
     * @return Dpi
     */
    private Dpi getDpi(JSONObject dpiJson) {
        Dpi dpi = null;
        try {
            int id = dpiJson.getInt("id");
            String modello = dpiJson.getString("modello");
            //String dataScadenza = dpiJson.getString("dataScadenza");
            String note = dpiJson.getString("note");
            TipoDpi tipoDpi = getTipoDpi(dpiJson.getJSONObject("tipoDPI"));
            JSONObject beaconJson = dpiJson.getJSONObject("beacon");
            Beacon beacon = db.beaconDao().getBeacon(beaconJson.getInt("id"));
            if (beacon == null) {
                beacon = getBeacon(beaconJson);
            }
            dpi = new Dpi(id, modello, note, tipoDpi.getId(), beacon.getId());
            db.dpiDao().insertAll(dpi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * Recupero della tipologia Dpi
     *
     * @param tipoDpiJson
     * @return TipoDpi
     */
    private TipoDpi getTipoDpi(JSONObject tipoDpiJson) {
        TipoDpi tipoDpi = null;
        try {
            int id = tipoDpiJson.getInt("id");
            String nome = tipoDpiJson.getString("nome");
            String nomeModelloTF = tipoDpiJson.getString("nomeModelloTF");
            String nomeIcona = tipoDpiJson.getString("nomeIcona");
            tipoDpi = new TipoDpi(id, nome, nomeModelloTF, nomeIcona);
            db.tipoDpiDao().insertAll(tipoDpi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tipoDpi;
    }


    /**
     * Recupero informazioni del Beacon associato al DPI
     *
     * @param beaconJson
     * @return Beacon
     */
    private Beacon getBeacon(JSONObject beaconJson) {
        Beacon beacon = null;
        try {
            int id = beaconJson.getInt("id");
            String seriale = beaconJson.getString("seriale");
            int livelloBatteria = beaconJson.getInt("livelloBatteria");
            JSONObject tipoBeaconJson = beaconJson.getJSONObject("tipoBeacon");
            TipoBeacon tipoBeacon = db.tipoBeaconDao().getTipoBeacon(tipoBeaconJson.getInt("id"));
            if (tipoBeacon == null) {
                tipoBeacon = getTipoBeacon(tipoBeaconJson);
            }
            beacon = new Beacon(id, seriale, livelloBatteria, tipoBeacon.getId());
            db.beaconDao().insertAll(beacon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return beacon;
    }

    /**
     * Recupero tipologia del beacon
     *
     * @param tipoBeaconJson
     * @return TipoBeacon
     */
    private TipoBeacon getTipoBeacon(JSONObject tipoBeaconJson) {
        TipoBeacon tipoBeacon = null;
        try {
            int id = tipoBeaconJson.getInt("id");
            String nome = tipoBeaconJson.getString("nome");
            boolean isBeaconDpi = tipoBeaconJson.getBoolean("beaconDPI");
            tipoBeacon = new TipoBeacon(id, nome, isBeaconDpi);
            db.tipoBeaconDao().insertAll(tipoBeacon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tipoBeacon;
    }

    /**
     * Recupero informazioni sedi e commesse dell'operatore
     *
     * @param operatoreJson
     * @return OperatoreSediCommesse
     */
    private List<OperatoreSediCommesse> getOperatoreSediCommesse(JSONObject operatoreJson) {
        List<OperatoreSediCommesse> operatoreSediCommesseList = new ArrayList<>();
        try {
            JSONArray operatoreSediCommesseListJson = operatoreJson.getJSONArray("operatoreSediCommesse");
            for (int i = 0; i < operatoreSediCommesseListJson.length(); i++) {
                JSONObject operatoreSediCommesseJson = operatoreSediCommesseListJson.getJSONObject(i);
                JSONObject sedeCommessaJson = operatoreSediCommesseJson.getJSONObject("sedeCommessa");
                SedeCommessa sedeCommessa = db.sedeCommessaDao().getSedeCommessa(sedeCommessaJson.getInt("id"));
                if (sedeCommessa == null) {
                    sedeCommessa = getSedeCommessa(operatoreSediCommesseJson);
                }
                int id = operatoreSediCommesseJson.getInt("id");
                OperatoreSediCommesse operatoreSediCommesse = new OperatoreSediCommesse(id, sedeCommessa.getId(), operatoreJson.getInt("id"));
                operatoreSediCommesseList.add(operatoreSediCommesse);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return operatoreSediCommesseList;
    }


    /**
     * Recupero Admin dal Login
     * salvo dati su DB
     *
     * @param data response data
     */
    private void readAdminsAndSaveIntoDB(JSONObject data) {
        try {
            JSONArray adminArr = data.getJSONArray("admin");
            for (int i = 0; i < adminArr.length(); i++) {
                JSONObject adminJson = adminArr.getJSONObject(i);
                int id = adminJson.getInt("id");
                String username = adminJson.getString("username");
                String email = adminJson.getString("email");
                String numTelefono = adminJson.getString("numeroTelefono");
                String nome = adminJson.getString("nome");
                String cognome = adminJson.getString("cognome");
                int ruoloId = (int) adminJson.getJSONObject("ruolo").get("id");
                //UtenteSediCommesse
                List<UtenteSediCommesse> utenteSediCommesseList = getUtenteSediCommesse(adminJson);

                Admin admin = new Admin(id, username, email, numTelefono, nome, cognome, ruoloId, utenteSediCommesseList);
                db.adminDao().insertAll(admin);
                db.utenteSediCommesseDao().insertAll(utenteSediCommesseList);
                List<UtenteSediCommesse> list = db.utenteSediCommesseDao().getAll();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recupero sedi commesse associate all'admin
     *
     * @param adminJson
     * @return lista UtenteSediCommesse
     */
    private List<UtenteSediCommesse> getUtenteSediCommesse(JSONObject adminJson) {
        List<UtenteSediCommesse> utenteSediCommesseList = new ArrayList<>();
        try {
            JSONArray utentiSediCommesseJson = adminJson.getJSONArray("utenteSediCommesse");
            for (int j = 0; j < utentiSediCommesseJson.length(); j++) {
                JSONObject utenteSediCommesseJson = utentiSediCommesseJson.getJSONObject(j);
                JSONObject sedeCommessaJson = utenteSediCommesseJson.getJSONObject("sedeCommessa");
                SedeCommessa sedeCommessa = db.sedeCommessaDao().getSedeCommessa(sedeCommessaJson.getInt("id"));
                if (sedeCommessa == null) {
                    sedeCommessa = getSedeCommessa(utenteSediCommesseJson);
                }
                int id = utenteSediCommesseJson.getInt("id");
                UtenteSediCommesse utenteSediComm = new UtenteSediCommesse(id, sedeCommessa.getId(), adminJson.getInt("id"));
                utenteSediCommesseList.add(utenteSediComm);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return utenteSediCommesseList;
    }

    /**
     * Recupero sede commessa
     *
     * @param utenteSediCommesse
     * @return SedeCommessa
     */
    private SedeCommessa getSedeCommessa(JSONObject utenteSediCommesse) {
        SedeCommessa sedeCommessa = null;
        try {
            JSONObject sedeCommessaJson = utenteSediCommesse.getJSONObject("sedeCommessa");
            int id = sedeCommessaJson.getInt("id");
            String nome = sedeCommessaJson.getString("nome");
            Commessa commessa = getCommessa(sedeCommessaJson);
            sedeCommessa = new SedeCommessa(id, nome, commessa.getId());
            db.sedeCommessaDao().insertAll(sedeCommessa);
            db.commessaDao().insertAll(commessa);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sedeCommessa;
    }

    /**
     * Recupero commessa della sede
     *
     * @param sedeCommessaJson
     * @return Commessa
     */
    private Commessa getCommessa(JSONObject sedeCommessaJson) {
        Commessa commessa = null;
        try {
            JSONObject commessaJson = sedeCommessaJson.getJSONObject("commessa");
            int id = commessaJson.getInt("id");
            String nome = commessaJson.getString("nome");
            JSONObject settoreJson = commessaJson.getJSONObject("settore");
            Settore settore = db.settoreDao().getSettore(settoreJson.getInt("id"));
            if (settore == null) {
                settore = getSettore(settoreJson);
            }
            commessa = new Commessa(id, nome, settore.getId(), sedeCommessaJson.getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commessa;
    }

    /**
     * Recupero del settore della commessa
     *
     * @param settoreJson
     * @return Settore
     */
    private Settore getSettore(JSONObject settoreJson) {
        Settore settore = null;
        try {
            int id = settoreJson.getInt("id");
            String nome = settoreJson.getString("nome");
            String nomeIcona = settoreJson.getString("nomeIcona");
            settore = new Settore(id, nome, nomeIcona);
            db.settoreDao().insertAll(settore);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return settore;
    }

    /**
     * Recupero tipi azione disponibili per l'operatore
     *
     * @param data response della login
     */
    private void readTipiAzioneOperatore(JSONObject data) {
        List<TipoAzioneOperatore> tipoAzioneOperatoreList = new ArrayList<>();
        try {
            JSONArray tipoAzioneOperatoreListJson = data.getJSONArray("tipiAzioneOperatori");
            for (int i = 0; i < tipoAzioneOperatoreListJson.length(); i++) {
                JSONObject tipoAzioneOperatore = tipoAzioneOperatoreListJson.getJSONObject(i);
                int id = tipoAzioneOperatore.getInt("id");
                String nome = tipoAzioneOperatore.getString("nome");
                tipoAzioneOperatoreList.add(new TipoAzioneOperatore(id, nome));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.tipoAzioneOperatoreDao().insertAll(tipoAzioneOperatoreList);
    }


    /**
     * Save User data on DB for an automatic login
     */
    private void saveOperatoreData(boolean fromAppLavoro, Operatore operatoreLogged) {
        Operatore operatoreDb = db.operatoreDao().getOperatore(operatoreLogged.getId());
        if (operatoreDb != null && !operatoreDb.equals(operatoreLogged)) {
            db.operatoreDao().delete(operatoreDb);
        }
        db.operatoreDao().insertAll(operatoreLogged);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Disable auto turning on GPS if your coming back from other activities
        if (Blebricks.IsInit())
            Blebricks.DisableAutoEnableGpsWhenTurnedOff();
        if (userIsLogged() && !fromAssenzaKit) {
            if (operatore == null) {
                Gson gson = new Gson();
                String json = sharedPref.getString(getString(R.string.operatore), "");
                operatore = gson.fromJson(json, Operatore.class);
            }
            goToHomeActivity(false);
        }
    }


    /**
     * recupera psw click event
     */
    private void goToRecoveryPsw() {
        Intent i = new Intent(LoginActivity.this, RecoveryPswActivity.class);
        startActivity(i);
    }
}

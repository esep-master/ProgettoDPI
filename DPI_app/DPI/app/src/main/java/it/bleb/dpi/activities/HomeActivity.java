package it.bleb.dpi.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import it.bleb.blebandroid.Blebricks;
import it.bleb.blebandroid.Component;
import it.bleb.dpi.DpiAppApplication;
import it.bleb.dpi.R;
import it.bleb.dpi.apiservices.APIService;
import it.bleb.dpi.brickblocks.BrickBlock;
import it.bleb.dpi.database.DpiDatabase;
import it.bleb.dpi.database.entity.Admin;
import it.bleb.dpi.database.entity.Alert;
import it.bleb.dpi.database.entity.AlertRisolto;
import it.bleb.dpi.database.entity.AzioneOperatore;
import it.bleb.dpi.database.entity.Beacon;
import it.bleb.dpi.database.entity.Commessa;
import it.bleb.dpi.database.entity.Dpi;
import it.bleb.dpi.database.entity.DpiKit;
import it.bleb.dpi.database.entity.Intervento;
import it.bleb.dpi.database.entity.Kit;
import it.bleb.dpi.database.entity.Operatore;
import it.bleb.dpi.database.entity.OperatoreSediCommesse;
import it.bleb.dpi.database.entity.SedeCommessa;
import it.bleb.dpi.database.entity.Settore;
import it.bleb.dpi.database.entity.Task;
import it.bleb.dpi.database.entity.TipoDpi;
import it.bleb.dpi.database.entity.UtenteSediCommesse;
import it.bleb.dpi.fragments.DetailsFragment;
import it.bleb.dpi.fragments.HomeFragment;
import it.bleb.dpi.fragments.TaskFragment;
import it.bleb.dpi.receivers.DpiScanReceiver;
import it.bleb.dpi.services.BackgroundNetworkDataService;
import it.bleb.dpi.services.DpiDetectorService;
import it.bleb.dpi.utils.AlertType;
import it.bleb.dpi.utils.ArrayUtil;
import it.bleb.dpi.utils.BeaconData;
import it.bleb.dpi.utils.Common;
import it.bleb.dpi.utils.Constants;
import it.bleb.dpi.utils.DateUtil;
import it.bleb.dpi.utils.DpiData;
import it.bleb.dpi.utils.DpiDetails;
import it.bleb.dpi.utils.DpiErrorManager;
import it.bleb.dpi.utils.DpiFeaturesHandler;
import it.bleb.dpi.utils.MathUtil;
import it.bleb.dpi.utils.ModelManager;
import it.bleb.dpi.utils.ModelNormalize;
import it.bleb.dpi.utils.ModelUtil;
import it.bleb.dpi.utils.NetworkState;
import it.bleb.dpi.utils.Prefs;
import it.bleb.dpi.utils.TipoAzioneOperatoreEnum;

public class HomeActivity extends AppCompatActivity implements BrickBlock.OnHasToScrollToBlockListener, HomeFragment.SetDetailsDpi, DpiFeaturesHandler, LocationListener {

    public static final String RESTART_ACTION = "restartService";
    private final Handler mSkipHandler = new Handler();
    private final Handler mRefreshHandler = new Handler();
    private static final String TAG = "HomeActivity";

    private boolean mEnabling = false;
    private boolean errorMsgReceived;

    private HashMap<String, DpiData> beacon2dpi = new HashMap<>();
    private ArrayList<DpiDetails> dpiDetailsList;

    private DpiDetectorService dpiDetectorService;
    private DpiErrorManager dpiErrorManager;

    private final HomeFragment homeFragment = new HomeFragment();
    private final DetailsFragment detailsFragment = new DetailsFragment();
    private final TaskFragment taskFragment = new TaskFragment();
    private FragmentTransaction fragmentTran;
    //DB
    private DpiDatabase db;

    private ArrayList<String> dpiResponse;
    private BackgroundNetworkDataService backgroundNetworkDataService = new BackgroundNetworkDataService();
    private List<Task> taskList;

    private TextView caricamentoTxt;
    private ProgressBar progressBar;
    private FloatingActionButton uomoATerraFab;
    private String uatMsg;
    private boolean serviceStarted;
    private boolean fromAppLavoro;

    private double latitude;
    private double longitude;

    private APIService apiService;

    private TextView changePsw;

    private int idKit;
    private Operatore operatore = null;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    private final static boolean forceNetwork = false;

    private Task taskIniziato = null;
    private boolean checkUATJacket = false; //VARIABILE PER VERIFICARE SE IL DPI JACKET E' ANALIZZABILE PER UAT
    private boolean alarmUATSendend = false;
    private boolean shoesAlarmSended = false;
    private Date shoesAlarmSendedData = null;
    private int trousersCount = 0;

    Handler mHandler = new Handler();
    boolean isRunning = true;
    private ProgressDialog progressDialog;

    private boolean checkEarMuffs = false;
    private Float minZ = 0.0f;
    private Float maxY = 0.0f;
    private Float maxX = 0.0f;
    private int countFuoriScalaZ;
    private int posMinZ, posMaxY, posMaxX, counterY, counterX;
    public static final boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        apiService = new APIService(HomeActivity.this);
        //change url service
        apiService.setUrl();


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
        verifyPermissions();
        Intent i = getIntent();
        fromAppLavoro = i.getBooleanExtra("fromAppLavoro", false);
        operatore = (Operatore) i.getSerializableExtra("operatore");

        setLocalDB();

        if (db.operatoreDao().getAll().size() == 0) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }
        //Registrazione receiver
        registerReceiver();

        //LAYOUT
        progressBar = findViewById(R.id.progressBar);
        caricamentoTxt = findViewById(R.id.textViewCaricamento);
        uomoATerraFab = findViewById(R.id.sms_alert_button);
        changePsw = findViewById(R.id.textViewChangePsw);
        //Menù
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        uomoATerraFab.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        changePswBtnListener();

        caricamentoTxt.setText(R.string.text_beacon_inattivi);

        dpiErrorManager = new DpiErrorManager(this);
        dpiErrorManager.createNotificationChannel();

        //HOME
        setHomeFragment(fromAppLavoro, tabLayout);

        getListOperatoreSediCommesse();

        //TASK
        setTaskFragment();
        if (fromAppLavoro) {
            changePsw.setVisibility(View.GONE);
            tabLayout.removeTab(Objects.requireNonNull(tabLayout.getTabAt(2)));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    //Home
                    case 0:
                        fragmentTran = getSupportFragmentManager().beginTransaction();
                        fragmentTran.replace(R.id.frame_layout, homeFragment);
                        fragmentTran.commit();
                        if (interventoIsStarted()) {
                            showProgressDialog();
                        }
                        break;
                    //Info DPI
                    case 1: {
                        fragmentTran = getSupportFragmentManager().beginTransaction();
                        fragmentTran.replace(R.id.frame_layout, detailsFragment);
                        fragmentTran.commit();
                    }
                    break;
                    //Gestione attività
                    case 2: {
                        fragmentTran = getSupportFragmentManager().beginTransaction();
                        fragmentTran.replace(R.id.frame_layout, taskFragment);
                        fragmentTran.commit();
                    }
                    break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //Uomo a terra bottone
        uomoATerraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert(getText(R.string.text_emergenza_uat_title).toString(), uatMsg, HomeActivity.this);
                uomoATerraFab.setVisibility(View.INVISIBLE);
                sendSMSMessage(true);

            }
        });

        //Registrazione del Service
        registerService();

        //Localizzazione
        getLocation();
        if (fromAppLavoro) {
            Log.i(TAG, "--- FROM APP LAVORO ---");
            checkPermission();
        }

        syncDataToBE();
    }

    private boolean checkDBData() {
        if (db != null) {
            return db.alertDao().getAll().size() > 0 ||
                    db.interventoDao().getAll().size() > 0 ||
                    db.azioneOperatoreDao().getAll().size() > 0;
        }
        return false;
    }

    private void syncDataToBE() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(30000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                Log.i(TAG, "THREAD SYNC");
                                try {
                                    if (NetworkState.getConnectivityStatus(getBaseContext()) && checkDBData()) {
                                        sendToPortale();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "--- checkPermission  2 ---");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                Log.i(TAG, "--- checkPermission  3 ---");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    private void getListOperatoreSediCommesse() {
        taskList = new ArrayList<>();
        if (DpiAppApplication.DEBUG_MODE) {
            //TODO: da eliminare! per ora è solo per test
            taskList.add(new Task(56371, "LAVINT1012", "Gas", 1284));
            taskList.add(new Task(56371, "LAVINT1012", "Gas", 1284));
            taskList.add(new Task(64366, "PIMORC1012", "Acqua", 4315));
            taskList.add(new Task(65421, "LAVINT2412", "Gas", 9875));
            taskList.add(new Task(85473, "PIMORC4314", "Gas", 8462));
            for (Task task : taskList) {
                db.taskDao().insertAll(task);
            }
            taskList = db.taskDao().getAll();
        } else {
            List<OperatoreSediCommesse> operatoreSediCommesseList = db.operatoreSediCommesseDao().getAll();
            if (operatoreSediCommesseList != null && operatoreSediCommesseList.size() > 0) {
                //variabile di controllo su kit
                boolean isKitNull = false;
                for (OperatoreSediCommesse opSedeComm : operatoreSediCommesseList) {
                    SedeCommessa sedeCommessa = db.sedeCommessaDao().getSedeCommessa(opSedeComm.getSedeCommessaId());
                    Commessa commessa = db.commessaDao().getCommessa(sedeCommessa.getCommessaId());
                    Settore settore = db.settoreDao().getSettore(commessa.getSettoreId());
                    //Settore settore = new Settore(2, "Acqua", "intAcqua");
                    Kit kit = null;
                    if (settore != null) {
                        kit = db.kitDao().getKitFromSettore(settore.getId());
                    }
                    isKitNull = kit == null;
                    //se ci sono kit associati
                    if (!isKitNull) {
                        Task task = new Task(sedeCommessa.getId(), commessa.getNome(), settore.getNome(), kit.getId());
                        //check se task è già sul DB se non c'è lo aggiungo
                        if (!checkTaskInDB(task)) {
                            db.taskDao().insertAll(task);
                        }
                    } else {
                        break;
                    }
                }
                if (!isKitNull) {
                    taskList = db.taskDao().getAll();
                } else {
                    //lancio allarme di configurazione errata e rimando l'utente alla login
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setTitle(getText(R.string.warning_title)).setMessage(getText(R.string.kit_error)).setCancelable(true);
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getBaseContext().deleteDatabase("dpi_database");
                            clearSP();
                            if (!fromAppLavoro) {
                                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                                i.putExtra("assenzaKit", true);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                    builder.create().show();
                }
            }
        }
    }

    private boolean checkTaskInDB(Task taskDaInserire) {
        Task taskSulDb = db.taskDao().getTaskFromSedeCommessa(taskDaInserire.getIdSedeCommessa());
        return taskSulDb != null;
    }

    /**
     * test
     * valorizza lista custom seriale beacon
     */
    private void getKitTest() {
        dpiResponse = new ArrayList<>();
        //Beacon Ronci
        //dpiResponse.add("F8:BE:95:CE:43:A2");
        //Beacon Moschen
        //dpiResponse.add("F8:81:5E:05:44:7E");
        //Trousers
        dpiResponse.add("CF:62:B8:0F:D2:A6");
        //Jacket
        dpiResponse.add("DF:6B:87:07:39:83");
        //Helmet
        //dpiResponse.add("F2:FC:A1:8B:EB:C1");
        //Left shoe
        dpiResponse.add("E0:4C:75:B3:F0:D0");
        //Right shoe
        dpiResponse.add("D9:78:9D:FB:D0:20");
        //Left glove
        dpiResponse.add("D6:49:01:E3:2A:6A");
        //Right glove
        dpiResponse.add("F2:FC:A1:8B:EB:C1");

    }

    private void verifyPermissions() {
        if (Prefs.GetIsCTrace(getApplicationContext())) {
            Blebricks.AskPermissions(HomeActivity.this, new Blebricks.OnAskPermissionsListener() {
                @Override
                public void OnAskPermissionsDone() {
                    if (!Blebricks.IsInit())
                        Blebricks.Init(getApplicationContext());
                }
            });
        }
    }

    /**
     * Set messagio di pericolo Uomo a terra
     * informando se è già stato inviato un SMS o meno
     *
     * @param uatTxt
     */
    private void setUatMsg(String uatTxt) {
        uatMsg = uatTxt;
    }

    private void registerReceiver() {
        DpiScanReceiver serviceReceiver = new DpiScanReceiver();
        serviceReceiver.setCallbacks(HomeActivity.this);
        registerReceiver(serviceReceiver, new IntentFilter(DpiScanReceiver.ERROR_RECEIVED_ACTION));
        registerReceiver(serviceReceiver, new IntentFilter(DpiScanReceiver.RESTART_ACTION));
        registerReceiver(serviceReceiver, new IntentFilter(DpiScanReceiver.START_ACTIVITY_ACTION));
    }

    /**
     * set DB APP
     */
    private void setLocalDB() {
        db = Room.databaseBuilder(HomeActivity.this, DpiDatabase.class, "dpi_database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    /**
     * Aggiornamento task su db locale
     *
     * @param task
     */
    @Override
    public void updateTask(Task task) {
        taskIniziato = task;
        db.taskDao().update(task);
        refreshDpiAlarmSended();
    }

    private void refreshDpiAlarmSended() {
        if (dpiDetailsList != null && dpiDetailsList.size() > 0) {
            for (DpiDetails dpi : dpiDetailsList) {
                dpi.setAlarmSended(false);
            }
        }
    }

    /**
     * App Logout
     */
    @Override
    public void logout() {
        if (DpiAppApplication.DEBUG_MODE) {
            getBaseContext().deleteDatabase("dpi_database");
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        } else {
            apiService.doLogout(getTokenOperatore(), new APIService.ILogout() {
                @Override
                public void logout(boolean isError) {
                    if (!isError) {
                        clearSP();
                        getBaseContext().deleteDatabase("dpi_database");
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        //Errore request
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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

    private void clearSP() {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preferences), Context.MODE_PRIVATE);
        if (sharedPref != null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();
        }
    }

    /**
     * Gestione EMERGENZA: Uomo a terra
     *
     * @param title
     * @param msg
     * @param myActivity
     */
    private void showAlert(String title, String msg, Context myActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myActivity);
        builder.setTitle(title).setMessage(msg).setCancelable(true);
        builder.setNegativeButton(getString(R.string.annulla), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Stop scan?
            }
        });
        builder.setPositiveButton(getString(R.string.continua), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: gestione click continua
                uomoATerraFab.setVisibility(View.INVISIBLE);
                dpiErrorManager.sendError(false);
            }
        });
        builder.create().show();
    }


    private void setHomeFragment(boolean fromAppLavoro, TabLayout tabLayout) {
        if (!fromAppLavoro) {
            fragmentTran = getSupportFragmentManager().beginTransaction();
            fragmentTran.add(R.id.frame_layout, taskFragment);
            TabLayout.Tab tab = tabLayout.getTabAt(2);
            tab.select();
            fragmentTran.commit();
        }

    }


    private void setTaskFragment() {
        List<Operatore> operatoreList = db.operatoreDao().getAll();
        Bundle bundle = new Bundle();
        bundle.putSerializable("operatoreInfo", operatoreList != null && operatoreList.size() > 0 ? operatoreList.get(0) : operatore);
        bundle.putSerializable("taskList", (Serializable) taskList);
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        taskFragment.setArguments(bundle);
        taskFragment.setCallbacks(HomeActivity.this);
    }

    /**
     * Set info of beacon list updated
     *
     * @param dpiList
     */
    @Override
    public void setInfoDPI(HashMap<String, DpiData> dpiList) {
        beacon2dpi = dpiList;
        if (dpiDetailsList == null || dpiDetailsList.size() == 0) {
            dpiDetailsList = new ArrayList<>();
        }
        for (Map.Entry<String, DpiData> dpi : beacon2dpi.entrySet()) {
            String nome;
            switch (dpi.getValue().getModelName()) {
                case Constants.MODEL_HELMET:
                    nome = getString(R.string.label_helmet);
                    break;
                case Constants.MODEL_JACKET:
                    nome = getString(R.string.label_jacket);
                    break;
                case Constants.MODEL_LEFT_GLOVE:
                    nome = getString(R.string.label_left_glove);
                    break;
                case Constants.MODEL_LEFT_SHOE:
                    nome = getString(R.string.label_left_shoe);
                    break;
                case Constants.MODEL_RIGHT_GLOVE:
                    nome = getString(R.string.label_right_glove);
                    break;
                case Constants.MODEL_RIGHT_SHOE:
                    nome = getString(R.string.label_right_shoe);
                    break;
                case Constants.MODEL_TROUSERS:
                    nome = getString(R.string.label_trousers);
                    break;
                case Constants.MODEL_GOGGLES:
                    nome = getString(R.string.label_goggles);
                    break;
                case Constants.MODEL_BADGE:
                    nome = getString(R.string.label_badge);
                    break;
                case Constants.MODEL_HARNESS:
                    nome = getString(R.string.label_harness);
                    break;
                case Constants.MODEL_EARMUFFS:
                    nome = getString(R.string.label_ear_muffs);
                    break;
                default:
                    nome = getString(R.string.unknown);
                    break;
            }
            if (!checkArrayContainsBeacon(dpi.getKey())) {
                dpiDetailsList.add(new DpiDetails(dpi.getKey(), nome));
            }
        }
        //Set info to Details Fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("beaconList", dpiDetailsList);
        detailsFragment.setArguments(bundle);
    }

    private boolean checkArrayContainsBeacon(String beaconAddress) {
        for (DpiDetails item : dpiDetailsList) {
            if (item.getAddress().equals(beaconAddress)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (Component c : Blebricks.Components.Array) {
            c.setConnected(false);
        }

        mRefreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        hideProgressDialog();
        //Send Broadcast to Service in order to keep running in background
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(RESTART_ACTION);
        broadcastIntent.setClass(this, DpiScanReceiver.class);
        this.sendBroadcast(broadcastIntent);
        if (!serviceStarted) {
            startService(new Intent(HomeActivity.this, DpiDetectorService.class));
        }
        //unregisterService();
        super.onDestroy();
    }

    public void registerService() {
        //Bind to Service only when activity has started on EasyServizi
        Intent intent = new Intent(this, DpiDetectorService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, BackgroundNetworkDataService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                BackgroundNetworkDataService.LocalBinder binder = (BackgroundNetworkDataService.LocalBinder) service;
                backgroundNetworkDataService = binder.getService();
                backgroundNetworkDataService.setCallbacks(HomeActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                backgroundNetworkDataService.setCallbacks(HomeActivity.this);
            }
        }, Context.BIND_AUTO_CREATE);
    }

    /**
     * quando heratech si chiude
     */
    public void unregisterService() {
        unbindService(serviceConnection);
    }


    /**
     * Metodo di scan dei beacon richiamato dal Service
     */
    @Override
    public void onScan() {
        serviceStarted = true;
        progressBar.setVisibility(View.VISIBLE);
        caricamentoTxt.setText(R.string.text_caricamento);

        if (!mEnabling) {
            mEnabling = true;

            new Thread(new BeaconNoSignal()).start();
            Common.EnableAdaptersAndStartScan(null, HomeActivity.this, new Blebricks.OnScanListener() {
                @Override
                public void OnScanStarted() {
                    mEnabling = false;
                }

                @Override
                public void OnScanFailed(int errorCode) {
                    mEnabling = false;
                }

                @Override
                public void OnScanStopped() {
                    mEnabling = false;
                    serviceStarted = false;
                }
            }, null, new Blebricks.OnAdvertiseReceivedFromScanListener() {
                @Override
                public void OnAdvertiseReceived(String address, String name, int rssi, String manufacturerData, int battery, String rawData) {
                    //controllo se address è presente nella lista dei beacon
                    if (beaconExists(address)) {
                        //TODO: controllo su variabile da eliminare! per ora è solo per test
                        if (!DpiAppApplication.DEBUG_MODE) {
                            setBattery(address, battery);
                        }
                        Log.d(TAG, "OnAdvertiseReceived !!!!!!!!! Segnale Beacon !!!!!");
                        Log.d(TAG, "OnAdvertiseReceived " + address);
                        DpiData dpiData = beacon2dpi.get(address);
                        //nuovo indice movimento per dispositivi
                        int move = Integer.parseInt(getByteFromManufacturerData(manufacturerData, 1, 2));
                        //VERIFICA SE ADDRESS == A GIACCA O PANTALONI O IMBRACATURA PER SOSTITUIRE IL CAPACITIVO CON rssi
                        if (dpiData != null) {
                            setMovingBeacon(address, move);
                            if (dpiData.getModelName().equals(Constants.MODEL_JACKET)
                                    || dpiData.getModelName().equals(Constants.MODEL_TROUSERS)
                                    || dpiData.getModelName().equals(Constants.MODEL_HARNESS)) {
                                //nuovo indice movimento
                                //METODO PER CONVERITIRE I BYTE IN VALORI DOUBLE x,y,z per ACCELLEROMETRO E PER IL VALORE DI CAPACITIVO
                                String bytesX = getByteFromManufacturerData(manufacturerData, 2, 6);
                                double accX = completamentoADue(bytesX);
                                if (dpiData.getModelName().equals(Constants.MODEL_JACKET)
                                        || dpiData.getModelName().equals(Constants.MODEL_TROUSERS)) {
                                    jacketAndTrousersOutput(dpiData, address, accX, move);
                                } else if (dpiData.getModelName().equals(Constants.MODEL_HARNESS)) {
                                    String bytesM = getByteFromManufacturerData(manufacturerData, 14, 18);
                                    //Modifiche imbracatura
                                    double accM = completamentoADue(bytesM) * 100 / 16;
                                    harnessOutput(dpiData, address, accX, move, accM);
                                }

                            } else {
                                int capacitivo = Integer.parseInt(getByteFromManufacturerData(manufacturerData, 0, 1));

                                if (dpiData.getModelName().equalsIgnoreCase(Constants.MODEL_HELMET)) {
                                    //Modifiche cuffie legate al casco
                                    String bytesAccZ = getByteFromManufacturerData(manufacturerData, 2, 6);
                                    String bytesMagX = getByteFromManufacturerData(manufacturerData, 6, 10);
                                    String bytesMagY = getByteFromManufacturerData(manufacturerData, 10, 14);
                                    String bytesMagZ = getByteFromManufacturerData(manufacturerData, 14, 18);
                                    double accZ = completamentoADue(bytesAccZ);
                                    double magX = completamentoADue(bytesMagX) * 100 / 16;
                                    double magY = completamentoADue(bytesMagY) * 100 / 16;
                                    double magZ = completamentoADue(bytesMagZ) * 100 / 16;
                                    setAccelerationAndCapacitanceListener(address, magX, magY, magZ, capacitivo);
                                    if (DEBUG) {
                                        String s = "Accelerometro Z: " + Math.round(accZ)
                                                + "\nMagnetometro X: " + Math.round(magX)
                                                + "\nMagnetometro Y: " + Math.round(magY)
                                                + "\nMagnetometro Z: " + Math.round(magZ)
                                                + "\nMax X: " + Math.round(maxX) + "\tAlla posizione: " + posMaxX
                                                + "\nCounter X: " + counterX
                                                + "\nMax Y: " + Math.round(maxY) + "\tAlla posizione: " + posMaxY
                                                + "\nCounter Y: " + counterY
                                                + "\nMin Z: " + Math.round(minZ) + "\tAlla posizione: " + posMinZ
                                                + "\nZ fuori scala: " + countFuoriScalaZ
                                                + "\nCuffie indossate: " + checkEarMuffs;
                                        taskFragment.datiTest.setText(s);
                                    }

                                } else {
                                    //METODO PER CONVERTIRE I BYTE IN VALORI DOUBLE x,y,z per ACCELLEROMETRO E PER IL VALORE DI CAPACITIVO
                                    String bytesX = getByteFromManufacturerData(manufacturerData, 2, 6);
                                    String bytesY = getByteFromManufacturerData(manufacturerData, 6, 10);
                                    String bytesZ = getByteFromManufacturerData(manufacturerData, 10, 14);
                                    double accX = completamentoADue(bytesX);
                                    double accY = completamentoADue(bytesY);
                                    double accZ = completamentoADue(bytesZ);
                                    //GESTIONE DI ACCELEREMOTRO E CAPACITIVO
                                    setAccelerationAndCapacitanceListener(address, accX, accY, accZ, capacitivo);
                                }
                            }
                        }
                    }
                }
            });
        }
    }


    /**
     * return byte string from manufacturerData
     *
     * @param manufacturerData from beacon signal
     * @param startIndex       from substring
     * @param endIndex         to from substring
     * @return
     */
    private String getByteFromManufacturerData(String manufacturerData, int startIndex, int endIndex) {
        return manufacturerData.substring(startIndex, endIndex);
    }

    /**
     * convert hexoDecimal to decimal and return double value (x,y,z)
     *
     * @param byteString
     * @return
     */
    private Double completamentoADue(String byteString) {
        int decimal = Integer.valueOf(byteString, 16).shortValue();
        BigDecimal app = BigDecimal.valueOf(decimal).movePointRight(2).round(MathContext.UNLIMITED).movePointLeft(2);
        return app.doubleValue() / 100.00;
    }

    /**
     * Aggiornamento livello di batteria del beacon
     *
     * @param address identificativo del beacon
     * @param battery livello di batteria
     */
    private void setBattery(String address, int battery) {
        for (DpiDetails dpi : dpiDetailsList) {
            if (dpi.getAddress().equals(address) && dpi.getBatteryLvl() != battery) {
                dpi.setBatteryLvl(battery);
                int idBeacon = db.beaconDao().getBeaconIdBySeriale(address);
                Beacon beacon = db.beaconDao().getBeacon(idBeacon);
                beacon.setLivelloBatteria(battery);
                db.beaconDao().updateBeacon(beacon);
                //chiamata API aggiornamento livello batteria
                List<Beacon> beaconList = new ArrayList<>();
                beaconList.add(beacon);
                try {
                    apiService.aggiornaStatoBeacon(beaconList, getTokenOperatore(), new APIService.IBeacon() {
                        @Override
                        public void aggiornaStato(Object response, boolean isError) {
                            if (!isError) {
                                Log.i(TAG, "aggiornaStatoBeacon SUCCESS []");
                            } else {
                                Log.e(TAG, "errore aggiornaStato [ ]");
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_service), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Metodo di stop scan dei beacon richiamato dal Service
     */
    @Override
    public void stopScan() {
        Common.StopScan();
        new Thread(new BeaconNoSignal()).interrupt();
        serviceStarted = false;
        dpiErrorManager.setErrorSent(false);
        progressBar.setVisibility(View.INVISIBLE);
        caricamentoTxt.setText(R.string.text_beacon_inattivi);
        if (uomoATerraFab.getVisibility() == (View.VISIBLE))
            uomoATerraFab.setVisibility(View.INVISIBLE);
        alarmUATSendend = false;
        setKit(DpiAppApplication.DEBUG_MODE, null);
    }

    @Override
    public void setKit(boolean fromTest, String settore) {
        Bundle bundle = new Bundle();
        if (settore != null && !fromTest) {
            int idSettore = db.settoreDao().getIdFromDescrizione(settore);
            Kit kitUtilizzato = db.kitDao().getKitFromSettore(idSettore);
            if (kitUtilizzato != null) {
                idKit = kitUtilizzato.getId();
                kitUtilizzato.setDpiKits(db.dpiKitDao().getDpiKitByKitId(kitUtilizzato.getId()));
                List<DpiKit> listDpi = kitUtilizzato.getDpiKits();
                if (listDpi != null && listDpi.size() > 0) {
                    for (DpiKit dpi : listDpi) {
                        if (dpi.getSbloccoAllarmeDa().equals("null") && dpi.getSbloccoAllarmeA().equals("null")) {
                            Dpi dpiItem = db.dpiDao().getDpi(dpi.getDpiId());
                            Beacon beacon = db.beaconDao().getBeacon(dpiItem.getBeaconId());
                            TipoDpi tipoDpi = db.tipoDpiDao().getTipoDpi(dpiItem.getTipoDpiId());
                            //TODO Badge e Occhiali non hanno il modelloTFlite al momento --> nomeModello.tflite
                            String modelName;
                            try {
                                modelName = tipoDpi.getNomeModelloTF().substring(0, tipoDpi.getNomeModelloTF().indexOf("."));
                            } catch (Exception e) {
                                modelName = tipoDpi.getNomeModelloTF().substring(0, 1).toUpperCase() + tipoDpi.getNomeModelloTF().substring(1);
                            }

                            beacon2dpi.put(beacon.getSeriale(), new DpiData(modelName, null));
                        }
                    }
                }
            }
            bundle.putSerializable("kitUtilizzato", kitUtilizzato);
        } else if (settore == null) {
            //Ripristino kit DPI
            beacon2dpi.clear();
        } else {
            getKitTest();
            bundle.putSerializable("dpiResponse", dpiResponse);
        }

        bundle.putSerializable("beacon2dpi", beacon2dpi);
        setInfoDPI(beacon2dpi);
        bundle.putSerializable("fromTest", fromTest);
        bundle.putBoolean("isStarting", interventoIsStarted());
        homeFragment.setArguments(bundle);
    }

    /**
     * metodo di recupero dati accelerometro e capacitivo
     *
     * @param address beacon address
     * @param x
     * @param y
     * @param z
     */
    private void setAccelerationAndCapacitanceListener(String address, double x, double y, double z, int c) {
        if (beaconExists(address)) {
            List<BeaconData> beaconDataArray = Objects.requireNonNull(beacon2dpi.get(address)).getBeaconDataArray();
            if (beaconDataArray == null || beaconDataArray.size() == 0) {
                beaconDataArray = new ArrayList<>();
                BeaconData beaconData = new BeaconData(x, y, z, c);
//                beaconData.setC(1);
                beaconDataArray.add((beaconData));
            } else {
                boolean beaconDataFound = false;
                //altrimenti ciclo arrayList e valorizzo il primo elemento non completo
                for (BeaconData beaconData : beaconDataArray) {
                    if (beaconData.getX() == null || beaconData.getY() == null || beaconData.getZ() == null || beaconData.getC() == null) {
                        beaconData.setX(x);
                        beaconData.setY(y);
                        beaconData.setZ(z);
                        beaconData.setC(c);
                        beaconDataFound = true;
                        break;
                    }
                }
                if (!beaconDataFound && beaconDataArray.size() <= 40) {
                    if (beaconDataArray.size() == 40) {
                        beaconDataArray.subList(0, 10).clear();
                    }
                    BeaconData beaconData = new BeaconData(x, y, z, c);
//                    beaconData.setC((int) (Math.random() * 10));
                    beaconDataArray.add((beaconData));
                }
            }

            Objects.requireNonNull(beacon2dpi.get(address)).setBeaconDataArray(beaconDataArray);

            //Giro per cuffie/casco
            DpiData model = beacon2dpi.get(address);
            if (isHelmet(model)) {
                handleBeaconSignal(address, beaconDataArray);

            }
            //Giro generico
            else if (isArrayReady(beaconDataArray)) {
                handleBeaconSignal(address, beaconDataArray);
            }
        }
    }

    /**
     * check if dpi is helmet or shoe (left/right)
     *
     * @param dpiData
     * @return boolean
     */
    private boolean isHelmetShoesOrGloves(DpiData dpiData) {
        if (dpiData.getModelName().equals(Constants.MODEL_HELMET)
                || dpiData.getModelName().equals(Constants.MODEL_LEFT_SHOE)
                || dpiData.getModelName().equals(Constants.MODEL_RIGHT_SHOE)
                || dpiData.getModelName().equals(Constants.MODEL_LEFT_GLOVE)
                || dpiData.getModelName().equals(Constants.MODEL_RIGHT_GLOVE)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * check if model is Helmet
     *
     * @param dpiData
     * @return
     */
    private boolean isHelmet(DpiData dpiData) {
        return dpiData.getModelName().equals(Constants.MODEL_HELMET);
    }

    /**
     * controllo se almeno un capacitivo è == 1
     *
     * @param beaconDataArray
     * @return true/false
     */
    private boolean isCapacitivoOne(List<BeaconData> beaconDataArray) {
        boolean check = false;
        Map<String, List<Float>> value2array = ArrayUtil.getInstance().getArrayValues(beaconDataArray);
        List<Float> cArray = value2array != null && value2array.size() > 0 ? value2array.get("C") : null;
        if (cArray != null) {
            for (Float el : cArray) {
                if (el == 1) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    /**
     * minimo di magz deve essere inferiore -35
     * massimo di magy deve essere minore di 60
     *
     * @param beaconDataArray
     * @return true/false
     */
    private boolean checkEarMuffs(List<BeaconData> beaconDataArray) {
        boolean check = false;
        Map<String, List<Float>> value2array = ArrayUtil.getInstance().getArrayValues(beaconDataArray);
        List<Float> zArray = value2array != null && value2array.size() > 0 ? value2array.get("Z") : null;
        List<Float> yArray = value2array != null && value2array.size() > 0 ? value2array.get("Y") : null;
        List<Float> xArray = value2array != null && value2array.size() > 0 ? value2array.get("X") : null;
        Log.i("CUFFIEZ", zArray.size() + "\t" + zArray);
        Log.i("CUFFIEY", yArray.size() + "\t" + yArray);
        if (zArray != null && yArray != null && xArray != null) {
//            minZ = Collections.min(zArray);
//            posMinZ = zArray.indexOf(minZ);
//            maxY = Collections.max(yArray);
//            posMaxY = yArray.indexOf(maxY);
            maxY = yArray.get(0);
            posMaxY = 0;
            maxX = xArray.get(0);
            posMaxX = 0;
            //Modifica controllo Z magnetometro
            countFuoriScalaZ = 0;
            posMinZ = 0;
            int j = 0;
            minZ = zArray.get(0);
            for (Float n : zArray) {
                if (n > -35)
                    countFuoriScalaZ++;
                if (n < minZ) {
                    minZ = n;
                    posMinZ = j;
                }
                j++;
            }
            if (yArray.size() > 1)
                for (int i = 1; i < yArray.size(); i++)
                    if (yArray.get(i) > maxY) {
                        maxY = yArray.get(i);
                        posMaxY = i;
                    }
//            if(minZ >= -38) counterZ++;
//            else counterZ = 0;
            counterX = maxX >= 25 ? counterX + 1 : 0;
            counterY = maxY >= 30 ? counterY + 1 : 0;
            check = countFuoriScalaZ <= 3 && maxY < 30 && maxX < 25;
        }
        return check;
    }


    private boolean checkNormaAcc(List<BeaconData> beaconDataArray) {
        Map<String, List<Float>> value2array = ArrayUtil.getInstance().getArrayValues(beaconDataArray);
        List<Float> xArray = new ArrayList<>();
        List<Float> yArray = new ArrayList<>();
        List<Float> zArray = new ArrayList<>();
        if (value2array != null && value2array.size() > 0) {
            xArray = value2array.get("X");
            yArray = value2array.get("Y");
            zArray = value2array.get("Z");
        }
        return checkArrayAcc(xArray, yArray, zArray, beaconDataArray);
    }

    private boolean checkArrayAcc(List<Float> xArray, List<Float> yArray, List<Float> zArray, List<BeaconData> beaconDataArray) {
        boolean isReady = false;
        float x, y, z;
        double result = 0;
        for (int i = 0; i < beaconDataArray.size(); i++) {
            x = xArray.size() > 0 ? xArray.get(i) : 0;
            y = yArray.size() > 0 ? yArray.get(i) : 0;
            z = zArray.size() > 0 ? zArray.get(i) : 0;
            result = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
            if (result >= 10.1) {
                isReady = true;
                break;
            }
        }
        return isReady;
    }

    /**
     * definisce se l'array è completo nella dimensione e in tutti i suoi elementi (x,y,z,c)
     *
     * @param beaconDataArray
     * @return true/false
     */
    private boolean isArrayReady(List<BeaconData> beaconDataArray) {
        boolean isReady = true;
        if (beaconDataArray.size() >= 10 && beaconDataArray.size() % 10 == 0) {
            for (BeaconData beaconData : beaconDataArray) {
                isReady = beaconData.getX() != null && beaconData.getY() != null && beaconData.getZ() != null && beaconData.getC() != null;
                if (!isReady)
                    break;
            }
        } else {
            isReady = false;
        }
        return isReady;
    }

    /**
     * find beacon id in hashmap
     *
     * @param beaconId beacon find
     * @return true/false
     */
    public boolean beaconExists(String beaconId) {
        return beacon2dpi.containsKey(beaconId);
    }

    @Override
    protected void onPause() {
        hideProgressDialog();
        super.onPause();
        mRefreshHandler.removeCallbacksAndMessages(null);
        mSkipHandler.removeCallbacksAndMessages(null);
        //mSensorManager.unregisterListener(this);

        //Common.StopScan();
    }

    @Override
    public void onHasToScrollToBlock(View container) {
    }

    /**
     * process beacon value
     *
     * @param beaconId        id beacon
     * @param beaconDataArray beacon Data
     */
    private void handleBeaconSignal(String beaconId, List<BeaconData> beaconDataArray) {
        List<BeaconData> tail = beaconDataArray.subList(Math.max(beaconDataArray.size() - 10, 0), beaconDataArray.size());
        //1 - preprocessing array valido
        List<Float> preprocessingArray = getPreprocessingArray(tail);
        //2 - normalizzazione dato
        String modelName = Objects.requireNonNull(beacon2dpi.get(beaconId)).getModelName();
        ModelNormalize modelNormalize = ModelUtil.getInstance().getConfigObjectFromJson(modelName, getBaseContext());
        List<Float> listNormalize = ArrayUtil.getInstance().normalize(preprocessingArray, modelNormalize.getMin(), modelNormalize.getMax());

        //3 - svuotare arrayList mappa
        //Objects.requireNonNull(beacon2dpi.get(beaconId)).setBeaconDataArray(new ArrayList<>());

        DpiData model = beacon2dpi.get(beaconId);
        float modelOutput = runModel(model, listNormalize);
        Log.i("TNDPI", "******************************************************");
        Log.i("TNDPI", "BEACON *** " + beaconId + " ***");
        Log.i("TNDPI", "NOME MODELLO *** " + model.getModelName() + " ***");
        Log.i("TNDPI", "Output Modello *** " + modelOutput + " ***");
        handleModelOutput(model, modelOutput, beaconId, tail);
    }

    /**
     * Run modello
     *
     * @param model           obj
     * @param normalizedArray array dati normalizzati
     * @return model output
     */
    private float runModel(DpiData model, List<Float> normalizedArray) {
        float modelOutput = 0;
        ModelManager modelManager = new ModelManager(getBaseContext(), model.getModelName());
        modelOutput = modelManager.runModel(normalizedArray);
        return modelOutput;
    }

    @Override
    public void stopMessage(boolean isReceived) {
        errorMsgReceived = isReceived;
    }


    /**
     * check beacon set output and iconImage only jacket and trousers
     *
     * @param model    model
     * @param beaconId address
     * @param x        x param
     */
    private void jacketAndTrousersOutput(DpiData model, String beaconId, double x, int mode) {
        setSignalInterceptedBeacon(beaconId);

        if (mode == 1) {
            dpiStatusOK(model, beaconId);
            Log.i("jacketAndtrousersOutput", "dispositivo " + "[ " + model.getModelName() + " indossato, indice X== " + x);
            //UOMO A TERRA
            //checkForUAT(model, x);
        } else {
            //se il pantalone risulta non indossato partono una serie di controlli
            if (model.getModelName().equals(Constants.MODEL_TROUSERS)) {
                trousersCount++;
                Log.i("jacketAndtrousersOutput", "dispositivo [ PANTALONI ]");
                //se le scarpe non sono indossate contemporaneamente
                //oppure sono indossate ma hanno lanciato un allarme negli ultimi 30 secondi
                Log.i("jacketAndtrousersOutput", "shoesAlarmSended [ " + shoesAlarmSended + " ]");
                if (!shoesAreWorn() || (shoesAreWorn() && shoesAlarmSended && getDateDiff(shoesAlarmSendedData, new Date(), TimeUnit.SECONDS) <= 30)) {
                    shoesAlarmSended = false;
                    shoesAlarmSendedData = null;
                    Log.i("jacketAndtrousersOutput", "scarpe non indossate oppure sono indossate ma hanno lanciato un allarme negli ultimi 30 secondi");
                    //controllo movimento di tutti i dispositivi indossati
                    if (checkMoving()) {
                        //se sono in movimento lancio l'allarme
                        Log.i("jacketAndTrousersOutput", "ALLARME IF CHECKMOVING");
                        dpiStatusKO(model, beaconId);
                    } else {
                        //se sono fermi nei 30 secondi successivi non lancio l'allarme
                        Log.i("jacketAndTrousersOutput", "sono fermi nei 3 successivi");
                        if (trousersCount == 3) {
                            Log.i("jacketAndTrousersOutput", "3 TENTATIVI");
                            trousersCount = 0;
                            dpiStatusKO(model, beaconId);
                        }
                    }
                } else if (shoesArentWorn()) {
                    dpiStatusKO(model, beaconId);
                }
            } else {
                Log.i("jacketAndTrousersOutput", "ALLARME GIACCA");
                dpiStatusKO(model, beaconId);
            }
        }
    }


    /**
     * check output and set iconImage
     *
     * @param model       model
     * @param modelOutput output Modello
     */
    private void handleModelOutput(DpiData model, float modelOutput, String beaconId, List<BeaconData> tail) {
        setSignalInterceptedBeacon(beaconId);
        //VERIFICA SE ADDRESS == A HELMET O SHOES O GLOVES PER CONTROLLO CAPACITIVO
        boolean check;
        if (isHelmetShoesOrGloves(model)) {
            //solo per casco guanti e scarpe
            //variabile di controllo se almeno un capacitivo è == 1
            check = isCapacitivoOne(tail);
            Log.i("handleModelOutput", "check se capacitivo == 1 [" + check + "] su modello [ " + model.getModelName() + " ]");
            DpiData modelCuffie = new DpiData(Constants.MODEL_EARMUFFS, "imgEarMuffs"); //Modello cuffie
            if ((modelOutput <= 0.5 && !isHelmet(model)) || check) {
                //CASCO INDOSSATO, CONTROLLO SU DATI MAGNETOMETRO PER CUFFIE
                dpiStatusOK(model, beaconId);
                if (isHelmet(model)) {
                    //Controllo cuffie indossate
                    checkEarMuffs = checkEarMuffs(tail);
                    if (checkEarMuffs)
                        dpiStatusOK(modelCuffie, beaconId + "_C");
                    else
                        dpiStatusKO(modelCuffie, beaconId + "_C");
                }
            } else {
                if (isHelmet(model))
                    dpiStatusKO(modelCuffie, beaconId + "_C");
                dpiStatusKO(model, beaconId);
            }
        }
    }

    /**
     * verifica uomo a terra su giacca e pantaloni indossati
     *
     * @param model modello
     * @param x     indice x
     */
    private void checkForUAT(DpiData model, double x) {
        //controllo se giacca e pantaloni sono indossati
        if (jacketAndTrousersIsOk()) {
            //controllo giacca
            if (model.getModelName().equalsIgnoreCase(Constants.MODEL_JACKET)) {
                //verifica soglia giacca
                checkUATJacket = checkTailsUAT(x);
            } else {
                //controllo pantaloni
                if (checkUATJacket && model.getModelName().equalsIgnoreCase(Constants.MODEL_TROUSERS)) {
                    //verifica soglia pantaloni
                    if (checkTailsUAT(x)) {
                        //se allarme non è già stato inviato
                        if (!alarmUATSendend) {
                            Log.i(TAG, "Invio UAT !!!! ");
                            alarmUAT();
                        }
                    }
                }
            }
        }
    }

    /**
     * verifica su indice x per verifica se parametri indicano l'emergenza dell'uomo a terra
     *
     * @param x indice x
     * @return false se solo un elemento supera 2.6 true se sono tutti sotto la soglia
     * List<BeaconData> tail
     * for (int i = 0; i < tail.size(); i++) {
     * if (x > 2.6) {
     * return false;
     * }
     * }
     * return true;
     */
    private boolean checkTailsUAT(double x) {
        //for (int i = 0; i < tail.size(); i++) {
        if (x > 2.6) {
            return false;
        }
        //}
        return true;
    }

    /**
     * invio allarme UAT
     */
    private void alarmUAT() {
        alarmUATSendend = true;
        uomoATerraFab.setVisibility(View.VISIBLE);
        Alert alert = new Alert("Alert: Uomo a terra", -1, getIdInterventoFromDB(), getIdAppIntervento(), AlertType.ALERT_UAT, latitude, longitude);
        if (NetworkState.getConnectivityStatus(getBaseContext())) {
            if (!DpiAppApplication.DEBUG_MODE) {
                callNuovoAllarmeUATService(alert);
            }
            //invio SMS
            sendSMSMessage(false);
            setUatMsg(getString(R.string.text_emergenza_uat_sms_OK));
        } else {
            Intent intent = new Intent(HomeActivity.this, BackgroundNetworkDataService.class);
            intent.putExtra("alert", alert);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            setUatMsg(getString(R.string.text_emergenza_uat_sms_KO));
            resetUATData();
        }
    }

    /**
     * controllo se giacca e pantaloni sono indossati
     *
     * @return true se sono indossati contemporaneamente false se non lo sono
     */
    private boolean jacketAndTrousersIsOk() {
        if (dpiDetailsList.size() > 0) {
            for (DpiDetails itemJacket : dpiDetailsList) {
                if (itemJacket.getName().equalsIgnoreCase(getString(R.string.label_jacket)) && itemJacket.isStatus()) {
                    for (DpiDetails itemTrousers : dpiDetailsList) {
                        if (itemTrousers.getName().equalsIgnoreCase(getString(R.string.label_trousers)) && itemTrousers.isStatus()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * controllo se scarpe e pantaloni sono indossati
     *
     * @return true se sono indossati contemporaneamente false se non lo sono
     */
    private boolean trousersAndShoesIsOk() {
        if (dpiDetailsList.size() > 0) {
            for (DpiDetails itemTrousers : dpiDetailsList) {
                if (itemTrousers.getName().equalsIgnoreCase(getString(R.string.label_trousers)) && itemTrousers.isStatus()) {
                    for (DpiDetails itemRightShoes : dpiDetailsList) {
                        if (itemRightShoes.getName().equalsIgnoreCase(getString(R.string.label_right_shoe)) && itemRightShoes.isStatus()) {
                            for (DpiDetails itemLeftShoes : dpiDetailsList) {
                                if (itemLeftShoes.getName().equalsIgnoreCase(getString(R.string.label_left_shoe)) && itemLeftShoes.isStatus()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * controllo se almeno una scarpa è indossate
     *
     * @return true se una indossata
     */
    private boolean shoesAreWorn() {
        if (dpiDetailsList.size() > 0) {
            for (DpiDetails itemRightShoes : dpiDetailsList) {
                if (itemRightShoes.getName().equalsIgnoreCase(getString(R.string.label_right_shoe))) {
                    if (itemRightShoes.isStatus()) {
                        Log.i("jacketAndtrousersOutput", "[shoesAreWorn] almeno una scarpa indossata ");
                        return true;
                    } else {
                        for (DpiDetails itemLeftShoes : dpiDetailsList) {
                            if (itemLeftShoes.getName().equalsIgnoreCase(getString(R.string.label_left_shoe))) {
                                if (itemLeftShoes.isStatus()) {
                                    Log.i("jacketAndtrousersOutput", "[shoesAreWorn] almeno una scarpa indossata ");
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * controllo se entrambe le scarpe NON sono indossate
     *
     * @return true se lo sono
     */
    private boolean shoesArentWorn() {
        if (dpiDetailsList.size() > 0) {
            for (DpiDetails itemRightShoes : dpiDetailsList) {
                if (itemRightShoes.getName().equalsIgnoreCase(getString(R.string.label_right_shoe))) {
                    if (!itemRightShoes.isStatus()) {
                        for (DpiDetails itemLeftShoes : dpiDetailsList) {
                            if (itemLeftShoes.getName().equalsIgnoreCase(getString(R.string.label_left_shoe))) {
                                if (!itemLeftShoes.isStatus()) {
                                    return true;
                                }
                            }
                        }
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }


    /**
     * set green icon for status KO
     *
     * @param model    dpiData
     * @param beaconId address
     */
    private void dpiStatusKO(DpiData model, String beaconId) {
        Alert alert = null;
        //Set Icona
        setRedIcon(model.getImageView());
        //Set Status beacon KO
        setStatusBeacon(beaconId, false);
        //Notifica di errore
        if (checkAlarmForSend(beaconId)) {
            //Invio errore
            if (fromAppLavoro && !errorMsgReceived) {
                //Blocco app di lavoro
                dpiErrorManager.sendError(true);
            } else {
                dpiErrorManager.setErrorSent(true);
            }
            if (model.getModelName().equals(Constants.MODEL_RIGHT_SHOE) || model.getModelName().equals(Constants.MODEL_LEFT_SHOE)) {
                shoesAlarmSended = true;
                shoesAlarmSendedData = new Date();
            }
            dpiErrorManager.setNotification(model.getModelName(), false);
            //Nuovo alert
            getLocation();
            alert = new Alert("Alert: DPI non indossato", getDpiIdFromSerialeBeacon(beaconId), getIdInterventoFromDB(), getIdAppIntervento(), AlertType.ALERT, latitude, longitude);
            //Invio gli alert al server oppure li salvo sul DB
            if (NetworkState.getConnectivityStatus(getBaseContext())) {
                callNuovoAllarmeService(alert);
            } else {
                db.alertDao().insertAll(alert);
                AzioneOperatore azione = new AzioneOperatore(alert.getIdAppIntervento(), TipoAzioneOperatoreEnum.NUOVO_ALLARME.getValue(), getDataNow());
                db.azioneOperatoreDao().insert(azione);
            }
        }


    }

    /**
     * set green icon for status ok
     *
     * @param model    dpiData
     * @param beaconId address
     */
    private void dpiStatusOK(DpiData model, String beaconId) {
        /*if(isHelmet(model) && checkEarMuffs){

        }*/
        Alert alert = null;
        AlertRisolto alertRisolto = null;
        //Set Icona
        setGreenIcon(model.getImageView());
        setStatusBeacon(beaconId, true);
        if (getStatusBeaconForResolution(beaconId)) {
            Log.i(TAG, "chiamata servizio per risoluzione anomalia");
            alert = new Alert("Alert: DPI indossato", getDpiIdFromSerialeBeacon(beaconId), getIdInterventoFromDB(), getIdAppIntervento(), AlertType.ALERT, latitude, longitude);
            if (NetworkState.getConnectivityStatus(getBaseContext())) {
                //chiamata servizio per risoluzione anomalia
                callChiudiAllarme(alert, beaconId);
            } else {
                Log.i(TAG, "inserimento chiusura allarme in DB");
                alertRisolto = new AlertRisolto(getDpiIdFromSerialeBeacon(beaconId), getIdInterventoFromDB(), getIdAppIntervento());
                db.alertRisoltoDao().insertAll(alertRisolto);
                AzioneOperatore azione = new AzioneOperatore(alert.getIdAppIntervento(), TipoAzioneOperatoreEnum.SBLOCCO_ALLARME.getValue(), getDataNow());
                db.azioneOperatoreDao().insert(azione);
            }
        }

        //Check se tutti i beacon sono OK
        if (fromAppLavoro && checkStatusBeaconsOk() && errorMsgReceived) {
            dpiErrorManager.sendError(false);
            errorMsgReceived = false;
        } else if (!fromAppLavoro && checkStatusBeaconsOk()) {
            dpiErrorManager.setErrorSent(false);
        }
    }

    /**
     * Set segnale beacon intercettato
     *
     * @param beaconId MAC beacon
     */
    private void setSignalInterceptedBeacon(String beaconId) {
        for (DpiDetails dpi : dpiDetailsList) {
            if (!dpi.isSignalIntercepted() && dpi.getAddress().equals(beaconId)) {
                dpi.setSignalIntercepted(true);
            }
        }
    }


    /**
     * Recupero id dell'intervento iniziato e non concluso dal db
     *
     * @return id intervento
     */
    private int getIdInterventoFromDB() {
        int idIntervento = 0;
        for (Task task : taskList) {
            if (task != null && task.isStarted() && !task.isCompleted()) {
                idIntervento = task.getIdIntervento();
            }
        }
        return idIntervento;
    }

    /**
     * Recupero id dell'intervento app iniziato e non concluso dal db
     *
     * @return id intervento
     */
    private int getIdAppIntervento() {
        int idAppIntervento = -1;
        for (Task task : taskList) {
            if (task != null && task.isStarted() && !task.isCompleted()) {
                idAppIntervento = task.getIdApp();
            }
        }
        return idAppIntervento;
    }

    /**
     * Recupero id del dpi tramite il seriale beacon associato
     *
     * @param beaconId
     * @return id Dpi
     */
    private int getDpiIdFromSerialeBeacon(String beaconId) {
        int idBeacon = db.beaconDao().getBeaconIdBySeriale(beaconId);
        Dpi dpi = db.dpiDao().getDpiByBeaconId(idBeacon);
        return dpi != null ? dpi.getId() : -1;
    }

    /**
     * Controllo dello stato di tutti i beacon
     *
     * @return statoDpiOk
     */
    private boolean checkStatusBeaconsOk() {
        boolean statoDpiOk = false;
        if (dpiDetailsList.size() > 0) {
            for (int i = 0; i < dpiDetailsList.size(); i++) {
                if (dpiDetailsList.get(i).isStatus()) {
                    statoDpiOk = true;
                } else {
                    statoDpiOk = false;
                    break;
                }
            }
        }
        return statoDpiOk;
    }

    /**
     * Set stato beacon
     *
     * @param beaconId MAC beacon
     * @param isOk     boolean status beacon
     */
    private void setStatusBeacon(String beaconId, boolean isOk) {
        for (DpiDetails dpi : dpiDetailsList) {
            if (dpi.isStatus() != isOk && dpi.getAddress().equals(beaconId)) {
                dpi.setStatus(isOk);
            }
        }
    }

    /**
     * Set moving beacon
     *
     * @param beaconId MAC beacon
     * @param moving   boolean mobìving beacon
     */
    private void setMovingBeacon(String beaconId, int moving) {
        if (dpiDetailsList.size() > 0) {
            for (DpiDetails dpi : dpiDetailsList) {
                if (dpi.getAddress().equals(beaconId)) {
                    dpi.setMoving(moving);
                }
            }
        }
    }

    /**
     * check moving DPI
     *
     * @return true if moving
     */
    private boolean checkMoving() {
        if (dpiDetailsList.size() > 0) {
            for (DpiDetails dpi : dpiDetailsList) {
                if (!dpi.getName().equalsIgnoreCase(getString(R.string.label_right_shoe))
                        && !dpi.getName().equalsIgnoreCase(getString(R.string.label_left_shoe))
                        && !dpi.getName().equalsIgnoreCase(getString(R.string.label_trousers))) {

                    return dpi.isStatus() && dpi.getMoving() == 1;
                }

            }
        }
        return false;
    }

    /**
     * check status of beacon
     * if address is equals and alarm sended
     *
     * @param beaconId
     * @return
     */
    private boolean getStatusBeaconForResolution(String beaconId) {
        boolean dpiStatusOk = false;
        for (DpiDetails dpi : dpiDetailsList) {
            //controllo corrispondenza beacon e se il dpi ha inviato un allarme
            if (dpi.getAddress().equals(beaconId) && dpi.isAlarmSended()) {
                dpi.setAlarmSended(false);
                dpiStatusOk = true;
                break;
            }
        }
        return dpiStatusOk;
    }

    /**
     * check stato beacon and isAlarmSended
     *
     * @param beaconId MAC beacon
     */
    private boolean checkAlarmForSend(String beaconId) {
        boolean checkAlarmOk = false;
        for (DpiDetails dpi : dpiDetailsList) {
            if (!dpi.isAlarmSended() && dpi.getAddress().equals(beaconId)) {
                dpi.setAlarmSended(true);
                checkAlarmOk = true;
            }
        }
        return checkAlarmOk;
    }

    /**
     * Invio alert al BE
     *
     * @param alert
     */
    private void callNuovoAllarmeService(Alert alert) {
        apiService.nuovoAllarme(alert, getTokenOperatore(), new APIService.IAlertDPI() {
            @Override
            public void nuovoAlertDpi(Object response, boolean isError) {
                if (!isError) {
                    if (!response.equals("null")) {
                        Log.d(TAG, "nuovoAlertDpi: ALERT INVIATO");
                    } else {
                        Log.e(TAG, "nuovoAlertDpi errore [ ]");
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_service), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "nuovoAlertDpi errore");
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_service), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Invio alert al BE
     *
     * @param alert
     */
    private void callChiudiAllarme(Alert alert, String beaconId) {
        apiService.chiusuraAllarme(getTokenOperatore(), alert.getIdDpi(), alert.getIdIntervento(), idKit, new APIService.IChiusuraAlert() {
            @Override
            public void chiusuraAlert(Object response, boolean isError) {
                if (!isError) {
                    if (!response.equals("null")) {
                        Log.d(TAG, "callChiudiAllarme: ALERT CHIUSO");
                        //Set Status beacon OK
                        setStatusBeacon(beaconId, true);

                    } else {
                        Log.e(TAG, "callChiudiAllarme errore [ ]");
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_service), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "callChiudiAllarme errore");
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_service), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Invio alert Uomo A Terra al BE
     *
     * @param alert
     */
    private void callNuovoAllarmeUATService(Alert alert) {
        apiService.nuovoAllarmeUAT(alert, getTokenOperatore(), new APIService.IAlertUAT() {
            @Override
            public void nuovoAlertUAT(Object response, boolean isError) {
                if (!isError) {
                    if (!response.equals("null")) {
                        Log.d(TAG, "nuovoAlertUAT: ALERT UOMO A TERRA INVIATO");
                    } else {
                        Log.e(TAG, "nuovoAlertUAT errore [ ]");
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_service), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "nuovoAlertUAT errore ");
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_service), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Comunicazione alert al Portale
     */
    @Override
    public void sendToPortale() throws JSONException {
        Map<AlertType, JSONArray> alertTypeJsonArrayMap = getAlertDpiListJson();
        JSONArray alertDpiListJson = alertTypeJsonArrayMap.get(AlertType.ALERT);
        JSONArray alertUATListJson = alertTypeJsonArrayMap.get(AlertType.ALERT_UAT);
        JSONArray alertRisoltiJson = getAlertDpiRisoltiListJson();
        JSONArray interventiListJson = getInterventiListFromDB();
        JSONArray beaconListJson = getBeaconListJson();
        JSONArray azioniOperatoreJson = getAzioniOperatoreListJson();

        JSONObject body = new JSONObject();
        body.put("interventi", interventiListJson);
        body.put("allarmiDPI", alertDpiListJson);
        body.put("allarmiDPIRisolti", alertRisoltiJson);
        body.put("allarmiCadute", alertUATListJson);
        body.put("statiBeacon", beaconListJson);
        body.put("azioniOperatore", azioniOperatoreJson);

        callSyncDataService(body);
    }

    /**
     * call service sync data offline
     *
     * @param body
     */
    private void callSyncDataService(JSONObject body) {
        Log.i(TAG, "JSON OBJECT: " + String.valueOf(body));
        apiService.syncDatiApp(body, getTokenOperatore(), new APIService.ISync() {
            @Override
            public void offlineSyncData(Object response, boolean isError) {
                if (!isError) {
                    if ((Integer) response == 200) {
                        Log.i(TAG, "SYNC DATA: sincronizzazione dati avvenuta con Successo!!!");
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.sincronizzazione_effettuata), Toast.LENGTH_SHORT).show();
                        clearDBData();
                    } else {
                        Log.i(TAG, "offlineSyncData errore []");
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_service), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i(TAG, "offlineSyncData errore");
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_service), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * svuoto contenuto tabelle DB
     * INTERVENTO
     * ALERT
     * AZIONI OPERATORE
     * ALERT RISOLTO
     */
    private void clearDBData() {
        db.interventoDao().deleteAll();
        db.alertDao().deleteAll();
        db.azioneOperatoreDao().deleteAll();
        db.alertRisoltoDao().deleteAll();
    }

    private JSONArray getAzioniOperatoreListJson() {
        List<AzioneOperatore> azioneOperatoreList = db.azioneOperatoreDao().getAll();
        JSONArray azioniOperatoreJson = new JSONArray();
        if (azioneOperatoreList != null && azioneOperatoreList.size() > 0) {
            for (AzioneOperatore azione : azioneOperatoreList) {
                JSONObject azioniJson = new JSONObject();
                try {
                    if (azione.getIdAppIntervento() > 0) {
                        azioniJson.put("idAppIntervento", azione.getIdAppIntervento());
                    }
                    azioniJson.put("idTipoAzione", azione.getIdTipoAzione());
                    azioniJson.put("dataAzione", azione.getDataAzione());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                azioniOperatoreJson.put(azioniJson);
            }
        }
        return azioniOperatoreJson;
    }

    private JSONArray getBeaconListJson() {
        List<Beacon> beaconList = db.beaconDao().getAll();
        JSONArray beaconListJson = new JSONArray();
        for (Beacon beacon : beaconList) {
            JSONObject beaconJson = new JSONObject();
            try {
                beaconJson.put("idBeacon", beacon.getId());
                beaconJson.put("batteria", beacon.getLivelloBatteria());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            beaconListJson.put(beaconJson);
        }
        return beaconListJson;
    }

    private JSONArray getInterventiListFromDB() throws JSONException {
        JSONArray interventoListJson = new JSONArray();
        List<Intervento> listIntervento = db.interventoDao().getAll();
        for (Intervento intervento : listIntervento) {
            if (intervento != null) {
                JSONObject interventoJson = new JSONObject();
                try {
                    interventoJson.put("idAppIntervento", intervento.getIdAppIntervento());
                    interventoJson.put("idIntervento", intervento.getIdIntervento());
                    interventoJson.put("idSedeCommessa", intervento.getIdSedeCommessa());
                    interventoJson.put("idKit", intervento.getIdKit());
                    interventoJson.put("dataInizio", intervento.getDataInizio());
                    interventoJson.put("dataFine", intervento.getDataFine());
                    interventoJson.put("latitudine", intervento.getLatitudine());
                    interventoJson.put("longitudine", intervento.getLongitudine());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                interventoListJson.put(interventoJson);
            }
        }
        return interventoListJson;
    }

    private Map<AlertType, JSONArray> getAlertDpiListJson() throws JSONException {
        Map<AlertType, JSONArray> alertTypeToArray = new HashMap<>();
        List<Alert> alertList = db.alertDao().getAll();
        JSONArray alertListDpiJson = new JSONArray();
        JSONArray alertListUATJson = new JSONArray();
        for (Alert alert : alertList) {
            JSONObject alertJson = new JSONObject();
            alertJson.put("idAppAllarme", alert.getId());
            alertJson.put("idAllarme", 0);
            alertJson.put("idAppIntervento", alert.getIdIntervento() != 0 ? alert.getIdIntervento() : alert.getIdAppIntervento());
            alertJson.put("idIntervento", alert.getIdIntervento());
            alertJson.put("dataAllarme", alert.getDataAllarme());
            alertJson.put("latitudine", alert.getLatitude());
            alertJson.put("longitudine", alert.getLongitude());
            if (alert.getType().equals(AlertType.ALERT)) {
                alertJson.put("idDPI", alert.getIdDpi());
                alertListDpiJson.put(alertJson);
            } else if (alert.getType().equals(AlertType.ALERT_UAT)) {
                alertListUATJson.put(alertJson);
            }
        }
        alertTypeToArray.put(AlertType.ALERT, alertListDpiJson);
        alertTypeToArray.put(AlertType.ALERT_UAT, alertListUATJson);

        return alertTypeToArray;
    }

    private JSONArray getAlertDpiRisoltiListJson() throws JSONException {
        List<AlertRisolto> alertRisoltiList = db.alertRisoltoDao().getAll();
        JSONArray alertRisoltiDpiJson = new JSONArray();
        for (AlertRisolto alert : alertRisoltiList) {
            JSONObject alertJson = new JSONObject();
            alertJson.put("idDPI", alert.getIdDpi());
            alertJson.put("idAppIntervento", alert.getIdIntervento() != 0 ? alert.getIdIntervento() : alert.getIdAppIntervento());
            alertJson.put("idIntervento", alert.getIdIntervento());
            alertJson.put("idKit", idKit);
            alertRisoltiDpiJson.put(alertJson);
        }
        return alertRisoltiDpiJson;
    }

    @Override
    public void saveAlertInDB(Alert alert) {
        db.alertDao().insertAll(alert);
    }

    @Override
    public void saveInterventoInDB(Intervento intervento) {
        Intervento i = db.interventoDao().getIntervento(intervento.getIdAppIntervento());
        if (i != null) {
            intervento.setDataInizio(i.getDataInizio());
            db.interventoDao().updateIntervento(intervento);
        } else {
            db.interventoDao().insert(intervento);
        }
    }

    @Override
    public void saveAzioneOperatore(AzioneOperatore azioneOperatore) {
        db.azioneOperatoreDao().insert(azioneOperatore);
    }

    @Override
    public void showToast(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getTokenOperatore() {
        return db.operatoreDao().getAll().get(0).getToken();
    }

    private void setGreenIcon(String image) {
        ImageView imageView = ModelUtil.getInstance().getImageViewFromString(image, getImagesViewList());
        Log.i(TAG, "setGreenIcon: " + imageView);
        if (imageView != null) {
            imageView.setImageResource(android.R.drawable.presence_online);
        }
    }

    private void setRedIcon(String image) {
        ImageView imageView = ModelUtil.getInstance().getImageViewFromString(image, getImagesViewList());
        Log.i(TAG, "setRedIcon: " + imageView);
        if (imageView != null) {
            imageView.setImageResource(android.R.drawable.presence_busy);
        }
    }

    /**
     * preprocessing array valido
     *
     * @param beaconDataArray
     * @return
     */
    private List<Float> getPreprocessingArray(List<BeaconData> beaconDataArray) {
        Map<String, List<Float>> value2array = ArrayUtil.getInstance().getArrayValues(beaconDataArray);
        List<Float> preprocessingArray = new ArrayList<>();
        if (value2array != null && value2array.size() > 0) {
            List<Float> xArray = value2array.get("X");
            List<Float> yArray = value2array.get("Y");
            List<Float> zArray = value2array.get("Z");
            List<Float> cArray = value2array.get("C");

            //X
            preprocessingArray.add(MathUtil.getInstance().getAvg(xArray));
            preprocessingArray.add(MathUtil.getInstance().getSD(xArray));
            preprocessingArray.add(MathUtil.getInstance().getMin(xArray));
            preprocessingArray.add(MathUtil.getInstance().getMax(xArray));

            //Y
            preprocessingArray.add(MathUtil.getInstance().getAvg(yArray));
            preprocessingArray.add(MathUtil.getInstance().getSD(yArray));
            preprocessingArray.add(MathUtil.getInstance().getMin(yArray));
            preprocessingArray.add(MathUtil.getInstance().getMax(yArray));

            //Z
            preprocessingArray.add(MathUtil.getInstance().getAvg(zArray));
            preprocessingArray.add(MathUtil.getInstance().getSD(zArray));
            preprocessingArray.add(MathUtil.getInstance().getMin(zArray));
            preprocessingArray.add(MathUtil.getInstance().getMax(zArray));

            //C
            preprocessingArray.add(MathUtil.getInstance().getAvg(cArray));
            //preprocessingArray.add(MathUtil.getInstance().getSD(cArray));
            //preprocessingArray.add(MathUtil.getInstance().getMin(cArray));
            //preprocessingArray.add(MathUtil.getInstance().getMax(cArray));
        }

        return preprocessingArray;
    }

    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // Get GPS and network status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (forceNetwork) isGPSEnabled = false;

            if (!isNetworkEnabled && !isGPSEnabled) {
                // cannot get location
                //this.locationServiceAvailable = false;
            } else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0,
                            new LocationListener() {

                                public void onLocationChanged(Location location) {
                                    if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                    }
                                }

                                public void onProviderDisabled(String provider) {
                                }

                                public void onProviderEnabled(String provider) {
                                }

                                public void onStatusChanged(String provider, int status,
                                                            Bundle extras) {
                                }
                            });
                }
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000,
                            0, this);

                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }

        } catch (Exception ex) {
            Log.e(TAG, "Error creating location service: " + ex.getMessage());
        }

    }


    /**
     * Callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            stopService(new Intent(HomeActivity.this, DpiDetectorService.class));
            // cast the IBinder and get MyService instance
            DpiDetectorService.LocalBinder binder = (DpiDetectorService.LocalBinder) service;
            dpiDetectorService = binder.getService();
            dpiDetectorService.setCallbacks(HomeActivity.this); // register
            //startService(new Intent(getBaseContext(), DpiDetectorService.class));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            dpiDetectorService.setCallbacks(HomeActivity.this);
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void changePswBtnListener() {
        changePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChangePsw();
            }
        });
    }

    /**
     * change psw click event
     */
    private void goToChangePsw() {
        Intent i = new Intent(HomeActivity.this, ChangePswActivity.class);
        startActivity(i);
    }

    /**
     * send sms management
     */
    private void sendSMSMessage(boolean fromBtn) {
        String phoneNo;
        String message;
        List<Admin> adminList = db.adminDao().getAll();
        List<Operatore> operatoreList = db.operatoreDao().getAll();

        if (fromBtn) {
            message = getString(R.string.text_emergenza_uat_title_ok) + " " + getString(R.string.operatore_matricola).toLowerCase() + " " + operatoreList.get(0).getIdentificativo();
        } else {
            message = getString(R.string.text_emergenza_uat_title) + " " + getString(R.string.operatore_matricola).toLowerCase() + " " + operatoreList.get(0).getIdentificativo();
        }
        resetUATData();

        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            if (adminList.size() > 0) {
                for (int i = 0; i < adminList.size(); i++) {
                    Admin adminItem = adminList.get(i);
                    //Recupero della lista delle sedi commessa per ogni singolo admin trovato
                    List<UtenteSediCommesse> listUtentiSedeCommessa = db.utenteSediCommesseDao().getUtenteSediCommesseForAdmin(adminItem.getId());
                    if (listUtentiSedeCommessa != null && listUtentiSedeCommessa.size() > 0) {
                        for (UtenteSediCommesse ut : listUtentiSedeCommessa) {
                            //se l'id della sedeCommessa è lo stesso della sede commessa del task iniziato mando l'sms
                            if (taskIniziato != null && ut.getSedeCommessaId() == taskIniziato.getIdSedeCommessa()) {
                                if (adminItem.getRuoloId() == 2 && adminItem.getNumeroTelefono() != null) {
                                    phoneNo = adminItem.getNumeroTelefono();
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                                    Toast.makeText(getApplicationContext(), getString(R.string.sms_send) + adminItem.getCognome(),
                                            Toast.LENGTH_LONG).show();

                                }
                            }
                        }
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.sms_failed), Toast.LENGTH_LONG).show();
        }
    }


    /**
     * @param list list double
     * @return valore massimo
     */
    private double getMaxValueFromArray(ArrayList<Double> list) {
        double massimo;
        massimo = list.get(0);

        for (int i = 0; i <= list.size() - 1; i = i + 1) {
            if (list.get(i) > massimo) {
                massimo = list.get(i);
            }
        }
        System.out.println("Il massimo e' " + massimo);
        return massimo;
    }

    /**
     * This method identifies all the maxima within the signal.
     *
     * @return int[] The list of relative maxima identified
     */
    public double[] detectRelativeMaxima(ArrayList<Double> datiAccelerometro, int UTF, int distance) {
        ArrayList<Double> maxima_data = new ArrayList<>();
        int lastIndex = 0;
        for (int i = 1; i < datiAccelerometro.size() - 1; i++) {
            if ((datiAccelerometro.get(i - 1) < datiAccelerometro.get(i))
                    && (datiAccelerometro.get(i + 1) < datiAccelerometro.get(i))
                    && datiAccelerometro.get(i) > UTF * 0.8) {
                if (i <= distance && maxima_data.size() == 0) {
                    maxima_data.add(datiAccelerometro.get(i));
                }
                if (i >= lastIndex + distance) {
                    lastIndex = i;
                    maxima_data.add(datiAccelerometro.get(i));
                }
            }
        }
        return ArrayUtil.convertToPrimitiveDouble(maxima_data);
    }

    /**
     * reset dati Uomo a terra
     */
    private void resetUATData() {
        alarmUATSendend = false;
    }

    private boolean interventoIsStarted() {
        List<Task> taskList = db.taskDao().getAll();
        if (taskList != null && taskList.size() > 0) {
            for (Task task : taskList) {
                if (task != null && task.isStarted() && !task.isCompleted()) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<ImageView> getImagesViewList() {
        return homeFragment.getImagesView();
    }

    public class BeaconNoSignal implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(120000);//6000
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (serviceStarted) {
                for (DpiDetails dpi : dpiDetailsList) {
                    if (!dpi.isSignalIntercepted()) {
                        dpi.setSignalIntercepted(true);
                        setStatusBeacon(dpi.getAddress(), false);
                        Log.i(TAG, "BeaconNoSignal il beacon [" + dpi.getAddress() + "] non invia segnali");
                        if (checkAlarmForSend(dpi.getAddress())) {
                            dpiErrorManager.setNotification(dpi.getName(), true);
                            //Nuovo alert
                            getLocation();

                            Alert alert = new Alert("Alert: DPI non invia segnale ", getDpiIdFromSerialeBeacon(dpi.getAddress()), getIdInterventoFromDB(), getIdAppIntervento(), AlertType.ALERT, latitude, longitude);
                            if (NetworkState.getConnectivityStatus(getBaseContext())) {
                                callNuovoAllarmeService(alert);
                            }
                        }

                    }
                }
            }
        }
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        if (date1 != null) {
            long diffInMillies = date2.getTime() - date1.getTime();
            return timeUnit.convert(diffInMillies, TimeUnit.SECONDS);
        }
        return 0;
    }

    private String getDataNow() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = DateUtil.getDateFormatter();
        return formatter.format(c.getTime());
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(R.attr.progressBarStyle);
            progressDialog.setMessage(getString(R.string.txt_attendere));
            progressDialog.setMax(4);
            progressDialog.show();
            show();
        }
    }

    private void show() {
        ValueAnimator animator = ValueAnimator.ofInt(0, progressDialog.getMax());
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progressDialog.setProgress((Integer) animation.getAnimatedValue());

            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                hideProgressDialog();
                // start your activity here
            }
        });
        animator.start();
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
            progressDialog = null;
        }
    }


    /**
     * check beacon set output and iconImage only harness
     *
     * @param model        model
     * @param beaconId     address
     * @param x            x param
     * @param magnetometro x param
     */
    private void harnessOutput(DpiData model, String beaconId, double x, int mode, double magnetometro) {
        setSignalInterceptedBeacon(beaconId);

        if (mode == 1 && magnetometro >= 100) {
            dpiStatusOK(model, beaconId);
            Log.i("harnessOutput", "dispositivo " + "[ " + model.getModelName() + " indossato, indice X== " + x);
            //UOMO A TERRA
            //checkForUAT(model, x);
        } else {
            Log.i("harnessOutput", "ALLARME IMBRACATURA");
            dpiStatusKO(model, beaconId);
        }
    }
}

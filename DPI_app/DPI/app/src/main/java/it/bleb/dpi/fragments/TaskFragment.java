package it.bleb.dpi.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import it.bleb.dpi.DpiAppApplication;
import it.bleb.dpi.R;
import it.bleb.dpi.activities.HomeActivity;
import it.bleb.dpi.apiservices.APIService;
import it.bleb.dpi.database.entity.AzioneOperatore;
import it.bleb.dpi.database.entity.Intervento;
import it.bleb.dpi.database.entity.Operatore;
import it.bleb.dpi.database.entity.Task;
import it.bleb.dpi.utils.DateUtil;
import it.bleb.dpi.utils.DpiFeaturesHandler;
import it.bleb.dpi.utils.NetworkState;
import it.bleb.dpi.utils.TaskRecyclerViewAdapter;
import it.bleb.dpi.utils.TipoAzioneOperatoreEnum;

public class TaskFragment extends Fragment {

    private TaskRecyclerViewAdapter adapter;
    private DpiFeaturesHandler dpiFeaturesHandler;
    private DialogInterface.OnClickListener dialogListenerKO;
    private DialogInterface.OnClickListener logoutDialogListenerOK;
    private boolean isStarting;
    private Operatore operatore;
    private List<Task> taskDetailsList;
    private APIService apiService;
    private double latitude, longitude;
    private Activity activity;
    public TextView datiTest;

    public TaskFragment() {
        // Required empty public constructor
    }

    private void showAlert(String title, String msg, DialogInterface.OnClickListener listenerOK) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setMessage(msg).setCancelable(true);
        if (listenerOK != null) {
            builder.setNegativeButton(activity.getString(R.string.no), dialogListenerKO);
            builder.setPositiveButton(activity.getString(R.string.si), listenerOK);
        } else {
            builder.setPositiveButton(R.string.ok, dialogListenerKO);
        }
        builder.create().show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        if (getArguments() != null) {
            operatore = (Operatore) getArguments().getSerializable("operatoreInfo");
            taskDetailsList = (ArrayList<Task>) getArguments().getSerializable("taskList");
            latitude = getArguments().getDouble("latitude");
            longitude = getArguments().getDouble("longitude");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        TextView username = view.findViewById(R.id.username);
        Button logout = view.findViewById(R.id.logout);
        RecyclerView taskRecyclerView = view.findViewById(R.id.task_recycler_view);
        datiTest = view.findViewById(R.id.dati_test);
        if (!HomeActivity.DEBUG)
            datiTest.setVisibility(View.GONE);


        username.setText(operatore.getIdentificativo());

        apiService = new APIService(getContext());
        //change url service
        apiService.setUrl();


        adapter = new TaskRecyclerViewAdapter(taskDetailsList, getContext(), new TaskRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task item) {
                if (isStarting && !item.isStarted()) {
                    showAlert(activity.getString(R.string.attivita_in_corso_title), activity.getString(R.string.text_attivita_in_corso), null);
                } else if (isStarting && item.isStarted()) {
                    showAlert(activity.getString(R.string.conferma_title), activity.getString(R.string.text_conferma_termine_attivita), getTerminaAttivitaDialogListener(item));
                } else {
                    showAlert(activity.getString(R.string.conferma_title), activity.getString(R.string.text_conferma_inizio_attività), getIniziaAttivitaDialogListener(item));
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        taskRecyclerView.setLayoutManager(linearLayoutManager);
        taskRecyclerView.setHasFixedSize(true);
        adapter.notifyDataSetChanged();
        taskRecyclerView.setAdapter(adapter);
        TextView emptyList = view.findViewById(R.id.empty_list_task);

        if (!taskDetailsList.isEmpty()) {
            emptyList.setVisibility(View.GONE);
        } else {
            emptyList.setVisibility(View.VISIBLE);
        }

        //Set dialog onclick listeners
        setTaskOnClickListeners();

        //Logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStarting) {
                    showAlert(activity.getString(R.string.attivita_in_corso_title), activity.getString(R.string.logout_warning), null);
                } else {
                    showAlert(activity.getString(R.string.logout_title), activity.getString(R.string.text_conferma_logout), logoutDialogListenerOK);
                }
            }
        });
        isStarting = checkTaskIsStarted();
        if (isStarting) {
            Task taskStarted = getTaskStarted();
            if (taskStarted != null && dpiFeaturesHandler != null) {
                dpiFeaturesHandler.setKit(DpiAppApplication.DEBUG_MODE, taskStarted.getSettore());
                dpiFeaturesHandler.onScan();
            }
        } else {
            if (dpiFeaturesHandler != null)
                dpiFeaturesHandler.stopScan();
        }

        return view;
    }

    private Task getTaskStarted() {
        Task taskStarted = null;
        for (Task taskItem : taskDetailsList) {
            if (taskItem.isStarted()) {
                taskStarted = taskItem;
                break;
            }
        }
        return taskStarted;
    }

    private boolean checkTaskIsStarted() {
        boolean isStarted = false;
        for (Task taskItem : taskDetailsList) {
            if (taskItem.isStarted()) {
                isStarted = true;
                break;
            }
        }
        return isStarted;
    }


    private String getDataNow() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = DateUtil.getDateFormatter();
        Log.i("getDataNow", "getDataNow: " + c.getTime());
        return formatter.format(c.getTime());
    }


    private void callFineAttivitaService(Task item) {
        apiService.fineIntervento(item.getIdIntervento(), dpiFeaturesHandler.getTokenOperatore(), new APIService.IFineIntervento() {
            @Override
            public void fineIntervento(Object responseData, boolean isError) {
                if (!isError) {
                    if (!responseData.equals("null")) {
                        dpiFeaturesHandler.showToast(activity.getString(R.string.fine_intervento_OK));
                    } else {
                        dpiFeaturesHandler.showToast(activity.getString(R.string.error_service));
                    }
                } else {
                    dpiFeaturesHandler.showToast(activity.getString(R.string.error_service));
                }
            }
        });
    }

    private void callInizioAttivitaService(Task item) throws JSONException {
        apiService.inizioIntervento(item, latitude, longitude, dpiFeaturesHandler.getTokenOperatore(), new APIService.IInizioIntervento() {
            @Override
            public void inizioIntervento(Object responseData, boolean isError) {
                if (!isError) {
                    if (!responseData.equals("null")) {
                        JSONObject response = (JSONObject) responseData;
                        try {
                            item.setIdIntervento(response.getInt("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        dpiFeaturesHandler.updateTask(item);
                        dpiFeaturesHandler.showToast(activity.getString(R.string.attivita_iniziata_title));
                    } else {
                        dpiFeaturesHandler.showToast(activity.getString(R.string.error_service));
                    }
                } else {
                    dpiFeaturesHandler.showToast(activity.getString(R.string.error_service));
                }
            }
        });
    }


    /**
     * get listener Dialog Termina Attività
     */
    private DialogInterface.OnClickListener getTerminaAttivitaDialogListener(Task item) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isStarting = false;
                item.setStarted(false);
                item.setCompleted(true);
                dpiFeaturesHandler.updateTask(item);
                adapter.notifyDataSetChanged();
                if (NetworkState.getConnectivityStatus(getContext())) {
                    callFineAttivitaService(item);
                } else {
                    String dataFine = getDataNow();
                    Intervento intervento = new Intervento(item.getIdApp(), item.getIdIntervento() != 0 ? item.getIdIntervento() : 0, item.getIdSedeCommessa(), item.getIdKit(), item.getDataInizio(), dataFine, latitude, longitude);
                    dpiFeaturesHandler.saveInterventoInDB(intervento);
                    //Se l'inizio attività è avvenuto con linea, allora ho l'id dell'intervento restituito dal BE
                    //quindi setto l'intervento dell'azione operatore Fine Attività con l'id del BE
                    int id = item.getIdIntervento() != 0 ? item.getIdIntervento() : item.getIdApp();
                    AzioneOperatore azione = new AzioneOperatore(item.getIdApp(), TipoAzioneOperatoreEnum.FINE_ATTIVITA.getValue(), dataFine);
                    dpiFeaturesHandler.saveAzioneOperatore(azione);
                }
                dpiFeaturesHandler.stopScan();
            }
        };
    }

    /**
     * get listener Dialog Inizia Attività
     */
    private DialogInterface.OnClickListener getIniziaAttivitaDialogListener(Task item) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (item != null && dpiFeaturesHandler != null) {
                    isStarting = true;
                    item.setStarted(true);
                    item.setLatitudine(latitude);
                    item.setLongitudine(longitude);

                    adapter.notifyDataSetChanged();

                    dpiFeaturesHandler.setKit(DpiAppApplication.DEBUG_MODE, item.getSettore());

                    if (NetworkState.getConnectivityStatus(getContext())) {
                        try {
                            callInizioAttivitaService(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String dataInizio = getDataNow();
                        Intervento intervento = new Intervento(item.getIdApp(), 0, item.getIdSedeCommessa(), item.getIdKit(), dataInizio, null, latitude, longitude);
                        dpiFeaturesHandler.saveInterventoInDB(intervento);
                        AzioneOperatore azione = new AzioneOperatore(item.getIdApp(), TipoAzioneOperatoreEnum.INIZIO_ATTIVITA.getValue(), dataInizio);
                        dpiFeaturesHandler.saveAzioneOperatore(azione);
                    }
                    dpiFeaturesHandler.onScan();
                }

            }
        };
    }

    /**
     * Set listener Dialog base Inizia/Termina Attività e Logout
     */
    void setTaskOnClickListeners() {

        logoutDialogListenerOK = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (NetworkState.getConnectivityStatus(getContext())) {
                    dpiFeaturesHandler.stopScan();
                    dpiFeaturesHandler.logout();
                } else {
                    dpiFeaturesHandler.showToast(activity.getString(R.string.error_service));
                }
            }
        };

        dialogListenerKO = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
    }

    public void setCallbacks(DpiFeaturesHandler dpiHandler) {
        dpiFeaturesHandler = dpiHandler;
    }
}
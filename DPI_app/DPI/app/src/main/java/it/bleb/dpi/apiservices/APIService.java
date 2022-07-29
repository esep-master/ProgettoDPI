package it.bleb.dpi.apiservices;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;

import it.bleb.dpi.DpiAppApplication;
import it.bleb.dpi.activities.LoginActivity;
import it.bleb.dpi.database.entity.Alert;
import it.bleb.dpi.database.entity.Beacon;
import it.bleb.dpi.database.entity.Task;

public class APIService {
    //private String url = "http://192.168.5.233:8080/api";
    private String url = "http://77.39.130.80:6880/api";
    //private String url = "http://57.128.16.179:6880/api"; // EasyServizi
    private Context context;

    //Callback Login
    public interface ILogin {
        void authenticate(Object response, boolean isError);
    }

    //Callback Inizio Intervento
    public interface IInizioIntervento {
        void inizioIntervento(Object response, boolean isError);
    }

    //Callback Fine Intervento
    public interface IFineIntervento {
        void fineIntervento(Object response, boolean isError);
    }

    //Callback Alert DPI
    public interface IAlertDPI {
        void nuovoAlertDpi(Object response, boolean isError);
    }

    //Callback Alert UAT
    public interface IAlertUAT {
        void nuovoAlertUAT(Object response, boolean isError);
    }

    //Callback Sync
    public interface ISync {
        void offlineSyncData(Object response, boolean isError);
    }

    //Callback Beacon
    public interface IBeacon {
        void aggiornaStato(Object response, boolean isError);
    }

    //Callback Logout
    public interface ILogout {
        void logout(boolean isError);
    }

    //Callback ChangePsw
    public interface IChangePsw {
        void changePsw(boolean isError);
    }

    //Callback RecoveryPsw
    public interface IRecoveryPsw {
        void recoveryPsw(boolean isError);
    }

    //Callback Alert UAT
    public interface IChiusuraAlert {
        void chiusuraAlert(Object response, boolean isError);
    }


    public APIService(Context context) {
        this.context = context;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl() {
        this.url = DpiAppApplication.EASY_MODE ? "http://57.128.16.179:7580/api": this.url;
    }

    /**
     * Gestione API Login
     *
     * @param username
     * @param password
     * @param iLogin
     */
    public void doLogin(String username, String password, final ILogin iLogin) {
        String uri = url + "/auth/app";
        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject userJson = new JSONObject();
        if (password == null) {
            password = username;
        }
        try {
            userJson.put("matricola", username)
                    .put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("doLogin", " matricola [" + username + "]" + " password [" + password + "]");
        // Instantiate the RequestQueue.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, uri, userJson, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Integer code = (Integer) response.get("code");
                            if (code != 200) {
                                LoginActivity activity = (LoginActivity) context;
                                activity.inputLayout.setError((String) response.get("message"));
                            } else iLogin.authenticate(response.get("data"), false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.i("doLogin", "onErrorResponse: [" + error.toString() + "]");
                        iLogin.authenticate(error.toString(), true);
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    /**
     * Gestione API inizio Intervento
     *
     * @param task
     * @param latitudine
     * @param longitudine
     * @param iInizioIntervento
     */
    public void inizioIntervento(Task task, double latitudine, double longitudine, String token, final IInizioIntervento iInizioIntervento) throws JSONException {
        String uri = url + "/intervento/inizio";
        RequestQueue queue = Volley.newRequestQueue(context);
        if (task != null) {
            JSONObject inizioIntervento = new JSONObject();
            inizioIntervento.put("idSedeCommessa", task.getIdSedeCommessa());
            inizioIntervento.put("idKit", task.getIdKit());
            inizioIntervento.put("latitudine", latitudine);
            inizioIntervento.put("longitudine", longitudine);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, uri, inizioIntervento, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                iInizioIntervento.inizioIntervento(response.get("data"), false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            iInizioIntervento.inizioIntervento(error.toString(), true);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Accept", "application/json");
                    params.put("Content-Type", "application/json");
                    params.put("TokenAPP", token);
                    return params;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        }

    }


    /**
     * Gestione API fine Intevento
     *
     * @param idIntervento
     * @param iIntervento
     */
    public void fineIntervento(int idIntervento, String token, final IFineIntervento iIntervento) {
        String uri = url + "/intervento/fine/" + idIntervento;
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, uri, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            iIntervento.fineIntervento(response.get("data"), false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iIntervento.fineIntervento(error.toString(), true);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                params.put("TokenAPP", token);

                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    /**
     * Gestione API nuovo Alert
     *
     * @param alert
     * @param iAlertDPI
     */
    public void nuovoAllarme(Alert alert, String token, final IAlertDPI iAlertDPI) {
        String uri = url + "/allarme/dpi_non_indossato";
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject alertJson = new JSONObject();
        try {
            alertJson.put("idDPI", alert.getIdDpi());
            alertJson.put("idIntervento", alert.getIdIntervento());
            alertJson.put("latitudine", alert.getLatitude());
            alertJson.put("longitudine", alert.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, uri, alertJson, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            iAlertDPI.nuovoAlertDpi(response.get("data"), false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iAlertDPI.nuovoAlertDpi(error.toString(), true);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                params.put("TokenAPP", token);

                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    /***
     * Gestione API allarme Uomo A Terra
     * @param alert
     * @param iAlert
     */
    public void nuovoAllarmeUAT(Alert alert, String token, final IAlertUAT iAlert) {
        String uri = url + "/allarme/uomo_a_terra";
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject alertJson = new JSONObject();
        try {
            alertJson.put("idIntervento", alert.getIdIntervento());
            alertJson.put("latitudine", alert.getLatitude());
            alertJson.put("longitudine", alert.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, uri, alertJson, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            iAlert.nuovoAlertUAT(response.get("data"), false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iAlert.nuovoAlertUAT(error.toString(), true);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                params.put("TokenAPP", token);

                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public void chiusuraAllarme(String token, int idDPI, int idIntervento, int idKit, final IChiusuraAlert chiusuraAlert) {
        String uri = url + "/allarme/chiusura_operatore";
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject alertJson = new JSONObject();
        try {
            alertJson.put("idDPI", idDPI);
            alertJson.put("idIntervento", idIntervento);
            alertJson.put("idKit", idKit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("chiusuraAllarme", "alertJson: " + alertJson.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, uri, alertJson, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("chiusuraAllarme", "onResponse: " + response.get("data"));
                            if (response.get("code").equals(String.valueOf(200))) {
                                chiusuraAlert.chiusuraAlert(response.get("data"), false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        chiusuraAlert.chiusuraAlert(error.toString(), true);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                params.put("TokenAPP", token);

                return params;
            }
        };
        // Add the request to the RequestQueue.

        queue.add(jsonObjectRequest);
    }

    /**
     * Gestione API sincronizzazione dati offline
     *
     * @param iSync
     */
    public void syncDatiApp(JSONObject body, String token, final ISync iSync) {
        String uri = url + "/offline/sync";
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, uri, body, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            iSync.offlineSyncData(response.get("code"), false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iSync.offlineSyncData(error.toString(), true);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                params.put("TokenAPP", token);

                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public void aggiornaStatoBeacon(List<Beacon> beaconList, String token, final IBeacon iBeacon) throws JSONException {
        String uri = url + "/beacon/aggiorna_stato";
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONArray beaconListJson = new JSONArray();
        for (Beacon beacon : beaconList) {
            JSONObject beaconJson = new JSONObject();
            beaconJson.put("idBeacon", beacon.getId());
            beaconJson.put("batteria", beacon.getLivelloBatteria());
            beaconListJson.put(beaconJson);
        }
        JSONObject beaconListObj = new JSONObject();
        beaconListObj.put("beacon", beaconListJson);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, uri, beaconListObj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        iBeacon.aggiornaStato(response, false);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        iBeacon.aggiornaStato(error.toString(), true);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                params.put("TokenAPP", token);

                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    /**
     * Gestione API Logout
     */
    public void doLogout(String token, final ILogout iLogout) {
        String uri = url + "/operatore/logout";
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject logoutObj = new JSONObject();
        // Instantiate the RequestQueue.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, uri, logoutObj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean errorPresent = false;
                            if (200 != Integer.parseInt(response.get("code").toString())) {
                                errorPresent = true;
                            }
                            iLogout.logout(errorPresent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        iLogout.logout(true);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                params.put("TokenAPP", token);

                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    /**
     * Gestione API ChangePSW
     */
    public void doChangePsw(String oldPsw, String newPsw, String token, final IChangePsw iChangePsw) {
        String uri = url + "/operatore/cambia_password";
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject changePswObj = new JSONObject();
        try {
            changePswObj.put("oldPassword", oldPsw)
                    .put("newPassword", newPsw);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Instantiate the RequestQueue.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, uri, changePswObj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        iChangePsw.changePsw(false);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        iChangePsw.changePsw(true);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                params.put("TokenAPP", token);

                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    /**
     * Gestione API RecoveryPsw
     */
    public void doRecoveryPsw(String email, final IRecoveryPsw iRecoveryPsw) {
        String uri = url + "/auth/recovery/operatore";
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject recoveryPswObj = new JSONObject();
        try {
            recoveryPswObj.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("APIService", "doRecoveryPsw URL: " + uri);
        // Instantiate the RequestQueue.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, uri, recoveryPswObj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        iRecoveryPsw.recoveryPsw(false);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        iRecoveryPsw.recoveryPsw(true);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        Log.d("APIService", "jsonObjectRequest : " + jsonObjectRequest);
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}
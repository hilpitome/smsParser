package com.smsparser.smsparser.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


import com.smsparser.smsparser.models.MessagesOperatuerData;
import com.smsparser.smsparser.models.SmsData;
import com.smsparser.smsparser.utils.DatabaseHandler;
import com.smsparser.smsparser.utils.NetworkHelper;
import com.smsparser.smsparser.utils.PrefUtils;
import com.smsparser.smsparser.utils.SmsParserSecrets;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hilary on 1/13/18.
 */

public class ConnectivityReceiver extends BroadcastReceiver {
    private Context context;
    private DatabaseHandler databaseHandler;
    private String mUrl = SmsParserSecrets.mUrl;
    PrefUtils prefUtils;
    String currentObject = "";
    int currentSqliteId;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        databaseHandler = new DatabaseHandler(context);

        //Log.d("onReceive", NetworkHelper.getNetworkType(context));
        //Log.d("onReceive", String.valueOf(NetworkHelper.isNetworkConnected(context)));

        String networkType = NetworkHelper.getNetworkType(context);
        if (networkType == "WIFI" || networkType == "MOBILE")  {
            System.out.println("network is on");

            prefUtils = new PrefUtils(context);

            List<Object> offLineDataList = databaseHandler.getOfflineRapportAndOperetuerRecords();
            if(offLineDataList.size()>0){

                for (Object offlineData:offLineDataList) {
                   /* create a new instance of UpdateOnlineDatabaseTask everytime
                    * because you can only call execute once
                    */
                    UpdateOnlineDatabaseTask updateOnlineDatabaseTask = new UpdateOnlineDatabaseTask();
                    updateOnlineDatabaseTask.execute(offlineData);

                }

            } else {
                Log.e("offline", "no false");
            }



        }  else {
            System.out.println(networkType);
        }

    }

    private class UpdateOnlineDatabaseTask extends AsyncTask<Object, String, String> {


        @Override
        protected String doInBackground(Object... params) {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = null;

            String resp = "";
            Object object = params[0];


            if(object instanceof SmsData){

                currentObject = "smsData";

                SmsData smsData = (SmsData) params[0];
                currentSqliteId = smsData.getSqliteId();
                Log.e("Total_Caisse ", smsData.getDate());
                formBody =   new FormBody.Builder()
                        .add("date", smsData.getDate())
                        .add("SD_Number", smsData.getSdNumber())
                        .add("Cash_recu", smsData.getCashRecu())
                        .add("Vente_Serveur", smsData.getVenteServuer())
                        .add("Total_Caisse", smsData.getTotalCaisse())
                        .build();

            } else if (object instanceof MessagesOperatuerData){
                currentObject = "messagesOperatuerData";

                MessagesOperatuerData messagesOperatuerData = (MessagesOperatuerData) object;
                currentSqliteId = messagesOperatuerData.getSqliteId();
                formBody =   new FormBody.Builder()
                        .add("date", messagesOperatuerData.getDate())
                        .add("title", messagesOperatuerData.getTitle())
                        .add("description", messagesOperatuerData.getDescription())
                        .add("phone_number", messagesOperatuerData.getPhoneNumber())
                        .add("sim_number", prefUtils.getKeySimNumber())
                        .add("message", messagesOperatuerData.getMessage())
                        .build();
                Log.e("phone", messagesOperatuerData.getSimNumber());
            }

            Request request = new Request.Builder()
                    .url(mUrl)
                    .post(formBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                resp = response.body().string();

            } catch (IOException e) {
                e.printStackTrace();

            }


            return resp;

        }

//        @Override
//        protected String doInBackground(List<Agent>[] lists) {
//            OkHttpClient client = new OkHttpClient();
//            RequestBody formBody = null;
//            Request request=null;
//
//            String resp = "";
//            List<Agent> agents = lists[0];
//
//            for(Agent agent: agents){
//                formBody = new FormBody.Builder()
//                        .add("sd_number", agent.getSdNumber())
//                        .add("last_balance", String.valueOf(agent.getSdBalance()))
//                        .add("update", "1")
//                        .build();
//
//                request = new Request.Builder()
//                        .url(mUrl)
//                        .post(formBody)
//                        .build();
//            }
//
//            try {
//                Response response = client.newCall(request).execute();
//                resp = response.body().string();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//
//            }
//
//            return resp;
//        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println(s);


            if(s.equals("New record created successfully")){
                switch (currentObject){
                    case "smsData":
                        databaseHandler.updateIsOnlineRapportJournalierTrue(currentSqliteId);
                        break;
                    case "messagesOperatuerData":
                        databaseHandler.updateIsOnlineMessagesOperatuerTrue(currentSqliteId);
                        break;
                    default:
                        System.out.println("invalid case");
                        break;
                }
            }


        }

    }

}

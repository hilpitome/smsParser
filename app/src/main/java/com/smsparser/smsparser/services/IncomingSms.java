package com.smsparser.smsparser.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.smsparser.smsparser.models.MessagesOperatuerData;
import com.smsparser.smsparser.models.SmsData;
import com.smsparser.smsparser.utils.DatabaseHandler;
import com.smsparser.smsparser.utils.PrefUtils;
import com.smsparser.smsparser.utils.SmsParserSecrets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hilary on 11/15/17.
 */


public class IncomingSms extends BroadcastReceiver {
    /**
     * Constant TAG for logging key.
     */
    private static final String TAG = IncomingSms.class.getSimpleName();

    private String mUrl = "";

    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Context context;
    private DatabaseHandler databaseHandler;
    private PrefUtils prefUtils;
    private int currentSqliteId;
    private String currentObject;

    @Override
    public void onReceive(Context context, Intent intent) {
        String[] months = new String[]{"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};


        prefUtils = new PrefUtils(context);
        this.context = context;
        Bundle bundle = intent.getExtras();
        mUrl = SmsParserSecrets.mUrl;
        if (bundle != null) {
            Object[] pdu_Objects = (Object[]) bundle.get("pdus");
            if (pdu_Objects != null) {
                databaseHandler = new DatabaseHandler(context);

                for (Object aObject : pdu_Objects) {

                    SmsMessage currentSMS = getIncomingMessage(aObject, bundle);

                    String senderNo = currentSMS.getDisplayOriginatingAddress();

                    String message = currentSMS.getDisplayMessageBody();

                    if (message.toLowerCase().contains("rapport")) {

                        String[] messageArray = message.split("\\*");

                        String unformateDate =  messageArray[1];

                        // Changing ddmmyyyy to dd-mm-yyyy
                        String dd= unformateDate.substring(0,2);
                        String mm = unformateDate.substring(2,4);
                        String yyyy = unformateDate.substring(4,8);

                        String formatedDate = dd+"-"+mm+"-"+yyyy;

                        SmsData smsData = new SmsData(
                                formatedDate,
                                senderNo,
                                messageArray[2],
                                messageArray[3],
                                messageArray[4]
                        );



                        currentSqliteId = (int) databaseHandler.addParsedSmsData(smsData);
                        SendToMySqlTask sendToMySqlTask = new SendToMySqlTask();
                        sendToMySqlTask.execute(smsData);


                    } else if (message.toLowerCase().contains("la transaction")) {
                        String title = "", description = "", formatedDate = "", phoneNumber = "";

                        // assign the title
                        if (message.toLowerCase().contains("le destinataire n'est pas un client orange money.")) {
                            title = "Pas client Orange Money";
                            description = "Le Client n'as ps de compte Orange Money";
                        }
                        if (message.toLowerCase().contains("a ete annulee car il")) {
                            title = "Code secret en retard";
                            description = "Le Client n'a pas mis code secret secret a temps";
                        }
                        if (message.toLowerCase().contains("montant maximum cumule de transactions")) {
                            title = "Maximum de transactions par mois";
                            description = "Le Client a atteint son plafond mensuel de transactions";
                        }
                        if (message.toLowerCase().contains("depasse le solde minimal autorise")) {
                            title = "Depassement de plafond";
                            description = "Le Client a atteint le plafond autorise";
                        }

                        if (message.toLowerCase().contains("solde de votre compte ne vous permet")) {
                            title = "Solde Insuffisant";
                            description = "Le Client n'a pas un solde suffisant pour le montant de la transaction";
                        }
                        if (message.toLowerCase().contains("montant maximum autorise")){
                            title = "Total maximum par mois";
                            description = "Le cumul des montants de transactions a atteint le maximum pour le mois";
                        }

                        // split message/description to find date and phone number

                        String messageArray[] = message.split(" ");

                        for (int i = 0; i < messageArray.length; i++) {
                           if(messageArray[i].length()>2){
                               String substring = messageArray[i].substring(0, 2);
                               Log.e("substring", substring);
                               if (substring.equals("77") || substring.equals("78") || substring.equals("70") || substring.equals("76")) {
                                   phoneNumber = messageArray[i];
                                   Log.e("phone", phoneNumber);
                               }
                               if (substring.toLowerCase().equals("ci") || substring.toLowerCase().equals("co") || substring.toLowerCase().equals("mp") ) {
                                   Log.e("date", "here in date");
                                   String date = messageArray[i].substring(2, messageArray[i].length());
                                   String year = "20"+date.substring(0, 2);
                                   String monthNumber = date.substring(2,4);
                                   String day = date.substring(4,6);
                                   String hour = date.substring(7,9) + ":"+  date.substring(8,10);
                                   int monthIndex = Integer.valueOf(monthNumber)-1;
                                   String monthInWords = months[monthIndex];
                                   formatedDate = monthInWords+" "+day+" "+year+" "+hour;

                               }
                           }
                        }

                        if (phoneNumber.length()<1){
                            phoneNumber = "NA";
                        }
                        MessagesOperatuerData messagesOperatuerData = new MessagesOperatuerData();
                        messagesOperatuerData.setDate(formatedDate);
                        messagesOperatuerData.setTitle(title);
                        messagesOperatuerData.setDescription(description);
                        messagesOperatuerData.setPhoneNumber(phoneNumber);
                        messagesOperatuerData.setSimNumber(senderNo);
                        messagesOperatuerData.setMessage(message);

                        currentSqliteId = (int) databaseHandler.addMessagesOperatuer(messagesOperatuerData);
                        SendToMySqlTask sendToMySqlTask = new SendToMySqlTask();

                        sendToMySqlTask.execute(messagesOperatuerData);
                    }

                }


            }
        }
    }


    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;

    }

    private class SendToMySqlTask extends AsyncTask<Object, String, String>{

        @Override
        protected String doInBackground(Object... params) {
            String resp ="";
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = null;

            if(params[0] instanceof SmsData){
                currentObject = "smsData";
                SmsData smsData = (SmsData) params[0];
                Log.e("Total_Caisse ", smsData.getDate());
                formBody =   new FormBody.Builder()
                        .add("date", smsData.getDate())
                        .add("SD_Number", smsData.getSdNumber())
                        .add("Cash_recu", smsData.getCashRecu())
                        .add("Vente_Serveur", smsData.getVenteServuer())
                        .add("Total_Caisse", smsData.getTotalCaisse())
                        .build();

            } else if (params[0] instanceof MessagesOperatuerData){
                currentObject = "messagesOperatuerData";
                MessagesOperatuerData messagesOperatuerData = (MessagesOperatuerData) params[0];
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


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, s);
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            if(s.equals("New record created successfully")){
                switch (currentObject){
                    case "smsData":
                        System.out.println("I am here too");
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

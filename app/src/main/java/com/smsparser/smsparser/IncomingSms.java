package com.smsparser.smsparser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.smsparser.smsparser.models.MessagesOperatuerData;
import com.smsparser.smsparser.models.SmsData;
import com.smsparser.smsparser.utils.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Timestamp;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

//    private String mUrl = "http://telecomtransborder.com/caurix/API/SmsReports.php";
    private String mUrl = "http://192.168.1.3/index.php";

    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Context context;
    DatabaseHandler databaseHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdu_Objects = (Object[]) bundle.get("pdus");
            if (pdu_Objects != null) {
                databaseHandler = new DatabaseHandler(context);

                for (Object aObject : pdu_Objects) {

                    SmsMessage currentSMS = getIncomingMessage(aObject, bundle);

                    String senderNo = currentSMS.getDisplayOriginatingAddress();

                    String message = currentSMS.getDisplayMessageBody();
                    Date datetime = new Date();
                    String now = sdf.format(datetime);
                    if (message.toLowerCase().contains("rapport")) {
//                        Toast.makeText(context, message + " " + senderNo, Toast.LENGTH_LONG).show();

                        String[] messageArray = message.split("\\*");

                        SmsData smsData = new SmsData(
                                senderNo,
                                messageArray[1],
                                messageArray[2],
                                messageArray[3]
                        );

                        databaseHandler.addParsedSmsData(smsData);
                        SendToMySqlTask sendToMySqlTask = new SendToMySqlTask();
                        sendToMySqlTask.execute(smsData);


                    } else if (message.toLowerCase().contains("la transaction")) {
                        String title = "", description = message, date = "", phoneNumber = "";
//                        String titles[] = {
//                                "Pas client Orange Money",
//                                "Code secret en retard",
//                                "Maximum de transactions par mois",
//                                "Depassement de plafond",
//                                "Solde Insuffisant"
//                        };
//                        String patterns[] = {
//                                "le destinataire n'est pas un client orange money.",
//                                "a ete annulee car il",
//                                "montant maximum cumule de transactions par mois",
//                                "depasse le solde minimal autorise",
//                                "solde de votre compte ne vous permet pasd effectuer cette operation"
//                        };
                        // assign the title
                        if (message.toLowerCase().contains("le destinataire n'est pas un client orange money.")) {
                            title = "Pas client Orange Money";
                        }
                        if (message.toLowerCase().contains("a ete annulee car il")) {
                            Log.i("here", "right here here");
                            title = "Code secret en retard";
                        }
                        if (message.toLowerCase().contains("montant maximum cumule de transactions")) {
                            title = "Maximum de transactions par mois";
                        }
                        if (message.toLowerCase().contains("depasse le solde minimal autorise")) {
                            title = "Depassement de plafond";
                        }

                        if (message.toLowerCase().contains("solde de votre compte ne vous permet")) {

                            title = "Solde Insuffisant";
                        }

                        // split message/description to find date and phone number

                        String messageArray[] = description.split(" ");

                        for (int i = 0; i < messageArray.length; i++) {
                           if(messageArray[i].length()>2){
                               String substring = messageArray[i].substring(0, 2);
                               Log.e("substring", substring);
                               if (substring.equals("77") || substring.equals("78") || substring.equals("70") || substring.equals("76")) {
                                   phoneNumber = messageArray[i];
                                   Log.e("phone", phoneNumber);
                               }
                               if (substring.toLowerCase().equals("ci") || substring.toLowerCase().equals("co") || substring.toLowerCase().equals("mp") ) {
                                    Log.e("date",messageArray[i]);
                                   date = messageArray[i].substring(2, messageArray[i].length());
                               }
                           }
                        }

                        MessagesOperatuerData messagesOperatuerData = new MessagesOperatuerData();
                        messagesOperatuerData.setDate(date);
                        messagesOperatuerData.setTitle(title);
                        messagesOperatuerData.setDescription(description);
                        messagesOperatuerData.setPhoneNumber(phoneNumber);
                        messagesOperatuerData.setSimNumber(senderNo);

                        databaseHandler.addMessagesOperatuer(messagesOperatuerData);
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

                SmsData smsData = (SmsData) params[0];
                Log.e("Total_Caisse ", smsData.getTotalCaisse().getClass().getName());
                formBody =   new FormBody.Builder()
                        .add("SD_Number", smsData.getSdNumber())
                        .add("Cash_recu", smsData.getCashRecu())
                        .add("Vente_Serveur", smsData.getVenteServuer())
                        .add("Total_Caisse", smsData.getTotalCaisse())
                        .build();

            } else if (params[0] instanceof MessagesOperatuerData){
                MessagesOperatuerData messagesOperatuerData = (MessagesOperatuerData) params[0];
                formBody =   new FormBody.Builder()
                        .add("date", messagesOperatuerData.getDate())
                        .add("title", messagesOperatuerData.getTitle())
                        .add("description", messagesOperatuerData.getDescription())
                        .add("phone_number", messagesOperatuerData.getPhoneNumber())
                        .add("sim_number", messagesOperatuerData.getSimNumber())
                        .build();
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
            Log.i(TAG, s);
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }
    }


}

package com.smsparser.smsparser;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.smsparser.smsparser.models.MessagesOperatuerData;
import com.smsparser.smsparser.models.SmsData;
import com.smsparser.smsparser.utils.DatabaseHandler;
import com.smsparser.smsparser.utils.PrefUtils;
import com.smsparser.smsparser.utils.SmsParserSecrets;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
//    private String mUrl = "http://192.168.1.4/index.php";

    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Context context;
    DatabaseHandler databaseHandler;
    String[] months;
    ArrayList numbers;
        PrefUtils prefUtils;

    TelephonyManager telemananger;

    @Override
    public void onReceive(Context context, Intent intent) {
        months = new String[]{"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};
        telemananger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        numbers = new ArrayList<String>();
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
                            title = "Total maximum pa mois";
                            description = "Le cumul des montants de translation de transaction a atteint le maximum pour le mois";
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
                Log.e("Total_Caisse ", smsData.getTotalCaisse());
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
            Log.i(TAG, s);
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }
    }


}

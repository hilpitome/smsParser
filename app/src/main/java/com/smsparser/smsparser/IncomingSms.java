package com.smsparser.smsparser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.smsparser.smsparser.models.MessagesOperatuerData;
import com.smsparser.smsparser.models.SmsData;
import com.smsparser.smsparser.utils.DatabaseHandler;

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

/**
 * Created by hilary on 11/15/17.
 */

public class IncomingSms extends BroadcastReceiver {
    /**
     * Constant TAG for logging key.
     */
    private static final String TAG = IncomingSms.class.getSimpleName();

    private String mUrl = "http://p3nlmysqladm002.secureserver.net/grid55/221/index.php";
    private String username = "adilcaurix";
    private String password = "Farooq123#";
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdu_Objects = (Object[]) bundle.get("pdus");
            if (pdu_Objects != null) {

                for (Object aObject : pdu_Objects) {

                    SmsMessage currentSMS = getIncomingMessage(aObject, bundle);

                    String senderNo = currentSMS.getDisplayOriginatingAddress();

                    String message = currentSMS.getDisplayMessageBody();

                    if(message.toLowerCase().contains("rapport")){
                     Toast.makeText(context, message+" "+senderNo, Toast.LENGTH_LONG).show();
                        DatabaseHandler databaseHandler = new DatabaseHandler(context);
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


                    } else {
//                        MessagesOperatuerData messagesOperatuerData = new MessagesOperatuerData(
//                                String.valueOf(System.currentTimeMillis()),
//                                "title",
//                                message,
//                                senderNo);
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

    private class SendToMySqlTask extends AsyncTask<SmsData, String, String>{

        @Override
        protected String doInBackground(SmsData... params) {


            SmsData smsData = params[0];
            try {
                Class.forName("com.mysql.jdbc.Driver");

                Date date = new Date();
                String now = sdf.format(date);
                java.sql.Connection con = DriverManager.getConnection(mUrl, username, password);
                if(con!=null){
                    System.out.println("Connected to the database test1");
                    java.sql.Statement st = con.createStatement();
                    java.sql.ResultSet rs = st.executeQuery("INSERT INTO Daily_Report ( `SD_Number`, `Cash_recu`, `Vente_Serveur`,`Releve_Serveur`, `created_at`)" +
                            "   VALUES" +
                            "   ("+"'"+smsData.getSdNumber()+"',"+smsData.getCashRecu()+"',"+smsData.getVenteServuer()+"',"
                            +smsData.getReleveServuer()+"'"+"'"+now+"'"+");");
                } else {
                    System.out.println("no connection");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

//            URL url = null;
//            try {
//                url = new URL(mUrl);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("POST");
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (ProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }
    }
}

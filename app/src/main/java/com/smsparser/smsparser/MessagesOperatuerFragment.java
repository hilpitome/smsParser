package com.smsparser.smsparser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.smsparser.smsparser.models.MessagesOperatuerData;
import com.smsparser.smsparser.models.SmsData;
import com.smsparser.smsparser.utils.DatabaseHandler;

import java.util.List;

/**
 * Created by hilary on 11/19/17.
 */

public class MessagesOperatuerActivity extends AppCompatActivity {
    DatabaseHandler databaseHandler;
    TextView noTextsTv;
    RecyclerView recyclerView;
    List<MessagesOperatuerData> records;
    MessagesOperatuerAdapter messagesOperatuerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_operatuer);
        noTextsTv = (TextView) findViewById(R.id.no_sms_tv);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        databaseHandler = new DatabaseHandler(this);
        records = databaseHandler.getMessagesOperatuer();

        if(records.size()<1){
            // do nothing
        } else {
            noTextsTv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

            recyclerView.setLayoutManager(layoutManager);

            messagesOperatuerAdapter = new MessagesOperatuerAdapter(this, records);

            recyclerView.setAdapter(messagesOperatuerAdapter);


        }
    }
}

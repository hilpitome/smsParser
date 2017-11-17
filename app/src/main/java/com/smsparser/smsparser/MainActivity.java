package com.smsparser.smsparser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.smsparser.smsparser.models.SmsData;
import com.smsparser.smsparser.utils.DatabaseHandler;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    DatabaseHandler databaseHandler;
    TextView noTextsTv;
    RecyclerView recyclerView;
    List<SmsData> records;
    SmsAdapter smsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noTextsTv = (TextView) findViewById(R.id.no_sms_tv);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        databaseHandler = new DatabaseHandler(this);
        records = databaseHandler.getRecords();

        if(records.size()<1){
            // do nothing
        } else {
            noTextsTv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);


            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

            recyclerView.setLayoutManager(layoutManager);

            smsAdapter = new SmsAdapter(this, records);

            recyclerView.setAdapter(smsAdapter);


        }
    }
}

package com.smsparser.smsparser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smsparser.smsparser.models.SmsData;
import com.smsparser.smsparser.utils.DatabaseHandler;

import java.util.List;

/**
 * Created by hilary on 11/19/17.
 */

public class RapportSmsFragment extends Fragment {
    DatabaseHandler databaseHandler;
    TextView noTextsTv;
    RecyclerView recyclerView;
    List<SmsData> records;
    SmsAdapter smsAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHandler = new DatabaseHandler(getActivity());
        records = databaseHandler.getSmsRapportData();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_rapport_sms, container, false);
        intitalizeView(view);
        return view;
    }

    private void intitalizeView(View view) {
        noTextsTv = (TextView) view.findViewById(R.id.no_sms_tv);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        if(records.size()<1){
            // do nothing
        } else {
            noTextsTv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

            recyclerView.setLayoutManager(layoutManager);

            smsAdapter = new SmsAdapter(getActivity(), records);

            recyclerView.setAdapter(smsAdapter);


        }
    }

}

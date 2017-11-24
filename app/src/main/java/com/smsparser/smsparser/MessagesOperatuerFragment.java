package com.smsparser.smsparser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smsparser.smsparser.models.MessagesOperatuerData;
import com.smsparser.smsparser.utils.DatabaseHandler;

import java.util.List;

/**
 * Created by hilary on 11/19/17.
 */

public class MessagesOperatuerFragment extends Fragment {
    DatabaseHandler databaseHandler;
    TextView noTextsTv;
    RecyclerView recyclerView;
    List<MessagesOperatuerData> records;
    MessagesOperatuerAdapter messagesOperatuerAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHandler = new DatabaseHandler(getActivity());
        records = databaseHandler.getMessagesOperatuer();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages_operatuer, container, false);
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

            messagesOperatuerAdapter = new MessagesOperatuerAdapter(getActivity(), records);

            recyclerView.setAdapter(messagesOperatuerAdapter);


        }
    }

}

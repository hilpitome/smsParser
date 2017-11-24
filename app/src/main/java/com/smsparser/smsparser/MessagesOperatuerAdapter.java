package com.smsparser.smsparser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smsparser.smsparser.models.MessagesOperatuerData;

import java.util.List;

/**
 * Created by hilary on 11/19/17.
 */

public class MessagesOperatuerAdapter extends RecyclerView.Adapter<MessagesOperatuerAdapter.MyViewHolder>  {

    private List<MessagesOperatuerData> messagesOperatuerDataList;

        public MessagesOperatuerAdapter(Context context, List<MessagesOperatuerData> messagesOperatuerDataList){
            this.messagesOperatuerDataList = messagesOperatuerDataList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.layout_operateur_item_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            MessagesOperatuerData messagesOperatuerData = messagesOperatuerDataList.get(position);
                holder.dateText.setText(messagesOperatuerData.getDate());
                holder.titleText.setText(messagesOperatuerData.getTitle());
                holder.description.setText(messagesOperatuerData.getDescription());
                holder.sdNumber.setText(messagesOperatuerData.getPhoneNumber());
                holder.simNumber.setText(messagesOperatuerData.getSimNumber());


        }

        @Override
        public int getItemCount() {
            return messagesOperatuerDataList.size();
        }
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView dateText, titleText, description, sdNumber, simNumber;


            public MyViewHolder(View view) {
                super(view);
                dateText = (TextView) view.findViewById(R.id.date_text);
                titleText = (TextView) view.findViewById(R.id.title_text);
                description = (TextView) view.findViewById(R.id.desciption_text);
                sdNumber = (TextView) view.findViewById(R.id.phone_number_text);
                simNumber = (TextView) view.findViewById(R.id.sim_number);
            }
        }

}

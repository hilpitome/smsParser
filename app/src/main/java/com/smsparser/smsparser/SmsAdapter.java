package com.smsparser.smsparser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smsparser.smsparser.models.SmsData;

import java.util.List;

/**
 * Created by hilary on 11/15/17.
 */

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.MyViewHolder>  {
    private List<SmsData> smsDataList;

    public SmsAdapter(Context context, List<SmsData> smsDataList){
        this.smsDataList = smsDataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sms_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SmsData smsData = smsDataList.get(position);
        holder.sdNumber.setText(smsData.getSdNumber());
        holder.cashRevu.setText(smsData.getCashRecu());
        holder.venteServeur.setText(smsData.getVenteServuer());
        holder.releveServuer.setText(smsData.getReleveServuer());

    }

    @Override
    public int getItemCount() {
        return smsDataList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView sdNumber, cashRevu, venteServeur,releveServuer;


        public MyViewHolder(View view) {
            super(view);

            sdNumber = (TextView) view.findViewById(R.id.sd_number_text);
            cashRevu = (TextView) view.findViewById(R.id.cash_recu_text);
            venteServeur = (TextView) view.findViewById(R.id.vente_serveur_text);
            releveServuer = (TextView) view.findViewById(R.id.releve_serveur_text);

        }

    }
}

package com.smsparser.smsparser.models;

/**
 * Created by hilary on 11/15/17.
 */

public class SmsData {
    private String date, sdNumber, cashRecu, venteServuer, totalCaisse;
    public SmsData(String date, String sdNumber, String cashRecu, String venteServuer, String totalCaisse){
        this.date = date;
        this.sdNumber = sdNumber;
        this.cashRecu = cashRecu;
        this.venteServuer = venteServuer;
        this.totalCaisse  = totalCaisse;
    }

    public String getSdNumber() {
        return sdNumber;
    }

    public void setSdNumber(String sdNumber) {
        this.sdNumber = sdNumber;
    }

    public String getCashRecu() {
        return cashRecu;
    }

    public void setCashRecu(String cashRecu) {
        this.cashRecu = cashRecu;
    }

    public String getVenteServuer() {
        return venteServuer;
    }

    public void setVenteServuer(String venteServuer) {
        this.venteServuer = venteServuer;
    }

    public String getTotalCaisse() {
        return totalCaisse;
    }

    public void setTotalCaisse(String totalCaisse ) {
        this.totalCaisse  = totalCaisse;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

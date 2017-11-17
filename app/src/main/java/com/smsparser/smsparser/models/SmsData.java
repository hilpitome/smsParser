package com.smsparser.smsparser.models;

/**
 * Created by hilary on 11/15/17.
 */

public class SmsData {
    private String sdNumber, cashRecu, venteServuer, releveServuer;
    public SmsData(String sdNumber, String cashRecu, String venteServuer, String releveServuer){
        this.sdNumber = sdNumber;
        this.cashRecu = cashRecu;
        this.venteServuer = venteServuer;
        this.releveServuer = releveServuer;
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

    public String getReleveServuer() {
        return releveServuer;
    }

    public void setReleveServuer(String releveServuer) {
        this.releveServuer = releveServuer;
    }
}

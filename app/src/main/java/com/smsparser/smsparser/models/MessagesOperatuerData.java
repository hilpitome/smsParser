package com.smsparser.smsparser.models;

/**
 * Created by hilary on 11/16/17.
 */

public class MessagesOperatuerData {
    private String sdNumber, title, description, date;

    public MessagesOperatuerData(String date,String title, String description, String sdNumber){
        this.sdNumber = sdNumber;
        this.title = title;
        this.description = description;
        this.date=date;
    }

    public String getSdNumber() {
        return sdNumber;
    }

    public void setSdNumber(String sdNumber) {
        this.sdNumber = sdNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

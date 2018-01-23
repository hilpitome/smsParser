package com.smsparser.smsparser.models;

/**
 * Created by hilary on 11/16/17.
 */

public class MessagesOperatuerData {
    private String phoneNumber, title, description, date, simNumber, message, isOnline;

    private int sqliteId;

    public MessagesOperatuerData(){};

    public MessagesOperatuerData(String date,String title, String description, String phoneNumber, String simNumber, String message){

        this.date=date;
        this.phoneNumber = phoneNumber;
        this.simNumber = simNumber;
        this.message = message;
        this.title = title;
        this.description = description;
        this.isOnline = isOnline;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getSimNumber() {
        return simNumber;
    }

    public void setSimNumber(String simNumber) {
        this.simNumber = simNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public int getSqliteId() {
        return sqliteId;
    }

    public void setSqliteId(int sqliteId) {
        this.sqliteId = sqliteId;
    }
}

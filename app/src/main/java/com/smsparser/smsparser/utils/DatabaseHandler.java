package com.smsparser.smsparser.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.smsparser.smsparser.models.MessagesOperatuerData;
import com.smsparser.smsparser.models.SmsData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hilary on 11/15/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper{
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "rapportManager";
    // table names
    private static final String TABLE_RAPPORT_JOURNALIER = "rapport_journalier";
    private static final String TABLE_MESSAGES_OPERATUER = "messages_operatuer";

    // Common Table Columns names
    private static final String ID = "_id";
    private static final String SD_NUMBER = "sd_number";

    // rapport_journalier column names

    private static final String CASH_RECU = "cash_recu";
    private static final String VENTE_SERVEUR = "vente_serveur";
    private static final String TOTAL_CAISSE  = "total_caisse";

    // messages_operatuer column names
    private static final String DATE = "date";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String SIM_NUMBER = "sim_number";
    private static final String MESSAGE = "message";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_RAPPORT_TABLE = "CREATE TABLE " + TABLE_RAPPORT_JOURNALIER  + "("
                + ID + " INTEGER PRIMARY KEY," + SD_NUMBER + " TEXT,"
                + CASH_RECU + " TEXT," + VENTE_SERVEUR  + " TEXT," +TOTAL_CAISSE + " TEXT"+")";

        String CREATE_TABLE_MESSAGES_OPERATUER = "CREATE TABLE " + TABLE_MESSAGES_OPERATUER  + "("
                + ID + " INTEGER PRIMARY KEY," + DATE + " TEXT,"
                + PHONE_NUMBER+ " TEXT," + TITLE  + " TEXT," +DESCRIPTION+ " TEXT,"+ SIM_NUMBER+ " TEXT,"+MESSAGE+" TEXT"+")";
        sqLiteDatabase.execSQL(CREATE_RAPPORT_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE_MESSAGES_OPERATUER);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RAPPORT_JOURNALIER );
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES_OPERATUER);


        // Create tables again
        onCreate(sqLiteDatabase);
    }
    // add a new row
    public void addParsedSmsData(SmsData smsData){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SD_NUMBER, smsData.getSdNumber() );
        contentValues.put(CASH_RECU, smsData.getCashRecu());
        contentValues.put(TOTAL_CAISSE , smsData.getTotalCaisse());
        contentValues.put(VENTE_SERVEUR,smsData.getVenteServuer());

        db.insert(TABLE_RAPPORT_JOURNALIER , null, contentValues);
        db.close();
    }
    public List<SmsData> getSmsRapportData(){

        List<SmsData> records = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_RAPPORT_JOURNALIER ;
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            // Extract data.

            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String sdNumber = cursor.getString(cursor.getColumnIndex(SD_NUMBER));
            String cashRecu = cursor.getString(cursor.getColumnIndex(CASH_RECU));
            String venteServeur = cursor.getString(cursor.getColumnIndex(VENTE_SERVEUR));
            String totalCaisse  = cursor.getString(cursor.getColumnIndex(TOTAL_CAISSE));
            records.add(new SmsData(sdNumber, cashRecu, venteServeur, totalCaisse));
        }


        db.close();

        return records;
    }

    public void addMessagesOperatuer(MessagesOperatuerData messagesOperatuerData){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE, messagesOperatuerData.getDate());
        contentValues.put(TITLE, messagesOperatuerData.getTitle());
        contentValues.put(PHONE_NUMBER, messagesOperatuerData.getPhoneNumber());
        contentValues.put(DESCRIPTION, messagesOperatuerData.getDescription());
        contentValues.put(SIM_NUMBER, messagesOperatuerData.getSimNumber());
        contentValues.put(MESSAGE, messagesOperatuerData.getMessage());

        db.insert(TABLE_MESSAGES_OPERATUER , null, contentValues);
        db.close();
    }

    public List<MessagesOperatuerData> getMessagesOperatuer(){
        List<MessagesOperatuerData> records = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_MESSAGES_OPERATUER;

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            // Extract data.
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(PHONE_NUMBER));
            String date = cursor.getString(cursor.getColumnIndex(DATE));
            String title = cursor.getString(cursor.getColumnIndex(TITLE));
            String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
            String simCard = cursor.getString(cursor.getColumnIndex(SIM_NUMBER));
            String message = cursor.getString(cursor.getColumnIndex(MESSAGE));
            records.add(new MessagesOperatuerData(date, title, description, phoneNumber, simCard, message));
        }

        db.close();

        return  records;
    }

}

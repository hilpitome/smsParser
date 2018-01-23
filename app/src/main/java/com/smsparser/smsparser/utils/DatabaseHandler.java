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
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME = "rapportManager";
    // table names
    private static final String TABLE_RAPPORT_JOURNALIER = "rapport_journalier";
    private static final String TABLE_MESSAGES_OPERATUER = "messages_operatuer";

    // Common Table Columns names
    private static final String ID = "_id";
    private static final String SD_NUMBER = "sd_number";
    private static final String IS_ONLINE = "is_online";

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
                + ID + " INTEGER PRIMARY KEY," + DATE + " TEXT,"+ SD_NUMBER + " TEXT,"
                + CASH_RECU + " TEXT," + VENTE_SERVEUR  + " TEXT," +TOTAL_CAISSE + " TEXT,"+IS_ONLINE+" TEXT NOT NULL DEFAULT 'false'"+")";

        String CREATE_TABLE_MESSAGES_OPERATUER = "CREATE TABLE " + TABLE_MESSAGES_OPERATUER  + "("
                + ID + " INTEGER PRIMARY KEY," + DATE + " TEXT,"
                + PHONE_NUMBER+ " TEXT," + TITLE  + " TEXT," +DESCRIPTION+ " TEXT,"+ SIM_NUMBER+ " TEXT,"+MESSAGE+" TEXT,"+IS_ONLINE+" TEXT NOT NULL DEFAULT 'false'"+")";
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
    public long addParsedSmsData(SmsData smsData){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SD_NUMBER, smsData.getSdNumber() );
        contentValues.put(CASH_RECU, smsData.getCashRecu());
        contentValues.put(TOTAL_CAISSE , smsData.getTotalCaisse());
        contentValues.put(VENTE_SERVEUR,smsData.getVenteServuer());
        contentValues.put(DATE, smsData.getDate());

        long id = db.insert(TABLE_RAPPORT_JOURNALIER , null, contentValues);
        db.close();

        return id;
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
            String date  = cursor.getString(cursor.getColumnIndex(DATE));
            records.add(new SmsData(date, sdNumber, cashRecu, venteServeur, totalCaisse));
        }


        db.close();

        return records;
    }

    public long addMessagesOperatuer(MessagesOperatuerData messagesOperatuerData){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE, messagesOperatuerData.getDate());
        contentValues.put(TITLE, messagesOperatuerData.getTitle());
        contentValues.put(PHONE_NUMBER, messagesOperatuerData.getPhoneNumber());
        contentValues.put(DESCRIPTION, messagesOperatuerData.getDescription());
        contentValues.put(SIM_NUMBER, messagesOperatuerData.getSimNumber());
        contentValues.put(MESSAGE, messagesOperatuerData.getMessage());

        long id = db.insert(TABLE_MESSAGES_OPERATUER , null, contentValues);
        db.close();
        return id;
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
    public List<Object> getOfflineRapportAndOperetuerRecords(){

        List<Object> offlineTransferAndWithdrawalData = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_RAPPORT_JOURNALIER+" WHERE "+IS_ONLINE+" = ?", new String[] {"false"});
        Cursor cursor2 = sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_MESSAGES_OPERATUER+" WHERE "+IS_ONLINE+" = ?", new String[] {"false"});
        while (cursor.moveToNext()){

            SmsData smsData = new SmsData();
            String date = cursor.getString(cursor.getColumnIndex(DATE));
            String sdNumber = cursor.getString(cursor.getColumnIndex(SD_NUMBER));
            String cashRecu = cursor.getString(cursor.getColumnIndex(CASH_RECU));
            int venteServuer = cursor.getInt(cursor.getColumnIndex(VENTE_SERVEUR));
            int totalCaise = cursor.getInt(cursor.getColumnIndex(TOTAL_CAISSE));
            int sqliteId = cursor.getInt(cursor.getColumnIndex(ID));

            smsData.setDate(date);
            smsData.setSdNumber(sdNumber);
            smsData.setCashRecu(cashRecu);
            smsData.setVenteServuer(String.valueOf(venteServuer));
            smsData.setTotalCaisse(String.valueOf(totalCaise));
            smsData.setSqliteId(sqliteId);

            offlineTransferAndWithdrawalData.add(smsData);
        }

        while (cursor2.moveToNext()){
            MessagesOperatuerData messagesOperatuerData = new MessagesOperatuerData();

            String date = cursor2.getString(cursor2.getColumnIndex(DATE));
            String title = cursor2.getString(cursor2.getColumnIndex(TITLE));
            String description = cursor2.getString(cursor2.getColumnIndex(DESCRIPTION));
            String phoneNumber = cursor2.getString(cursor2.getColumnIndex(PHONE_NUMBER));
            String simNumber = cursor2.getString(cursor2.getColumnIndex(SIM_NUMBER));
            String message = cursor2.getString(cursor2.getColumnIndex(MESSAGE));
            int sqliteId = cursor2.getInt(cursor2.getColumnIndex(ID));

            messagesOperatuerData.setDate(date);
            messagesOperatuerData.setTitle(title);
            messagesOperatuerData.setDescription(description);
            messagesOperatuerData.setPhoneNumber(phoneNumber);
            messagesOperatuerData.setSimNumber(simNumber);
            messagesOperatuerData.setMessage(message);
            messagesOperatuerData.setSqliteId(sqliteId);

            offlineTransferAndWithdrawalData.add(messagesOperatuerData);




        }

        cursor.close();
        cursor2.close();

        sqLiteDatabase.close();

        return offlineTransferAndWithdrawalData;
    }

    public void updateIsOnlineRapportJournalierTrue(int id){
        Log.e("settrue", String.valueOf(id));
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(IS_ONLINE, "true");

        db.update(TABLE_RAPPORT_JOURNALIER, cv,ID+" = ?" ,new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateIsOnlineMessagesOperatuerTrue(int id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(IS_ONLINE, "true");

        db.update(TABLE_MESSAGES_OPERATUER, cv,ID+" = ?" ,new String[]{String.valueOf(id)});
        db.close();

    }




}
